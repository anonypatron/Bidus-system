package com.auction.service;

import com.common.AuctionStatus;
import com.common.error.code.ErrorCode;
import com.common.exception.auction.AuctionNotFoundException;
import com.common.exception.bookmark.BookmarkAlreadyExistsException;
import com.common.exception.bookmark.BookmarkNotFoundException;
import com.common.exception.user.UserNotFoundException;
import com.auction.dto.request.AuctionCreateRequestDto;
import com.auction.dto.request.AuctionUpdateRequestDto;
import com.auction.dto.response.AuctionResponseDto;
import com.auction.entity.Auction;
import com.auction.entity.AuctionBookmark;
import com.auction.entity.Category;
import com.auction.entity.User;
import com.auction.repository.AuctionBookmarkRepository;
import com.auction.repository.AuctionRepository;
import com.auction.repository.CategoryRepository;
import com.auction.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuctionService {

    private final AuctionBookmarkRepository auctionBookmarkRepository;
    private final EventPublishService eventPublishService;
    private final CategoryRepository categoryRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    // 경매 등록
    @Transactional
    public AuctionResponseDto save(
            Long userId,
            AuctionCreateRequestDto dto,
            MultipartFile image
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));

        Auction savedAuction = auctionRepository.save(Auction.builder()
                .sellerId(userId)
                .sellerUserName(user.getUsername())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startPrice(dto.getStartPrice())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build());

        for (String categoryName : dto.getCategories()) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

            savedAuction.addCategory(category);
        }

        String imagePath = imageService.saveImagePath(image, savedAuction.getId());
        savedAuction.setImagePath(imagePath);
        auctionRepository.save(savedAuction);

        // 저장 후 kafka에 메시지 발행
        eventPublishService.publishAuctionCreatedEvent(savedAuction);
        return AuctionResponseDto.fromEntity(savedAuction, false);
    }

    @Transactional(readOnly = true)
    public AuctionResponseDto getAuction(Long userId, Long auctionId) {
        Auction auction = auctionRepository.findByIdWithCategories(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));

        if (userId == null) {
            return AuctionResponseDto.fromEntity(auction, false);
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return AuctionResponseDto.fromEntity(auction, false);
        }

        log.info("get auction: {}", auction.getId());
        log.info("get user: {}", user);
        boolean isBookmarked = auctionBookmarkRepository.existsByUserIdAndAuctionId(userId, auctionId);
        return AuctionResponseDto.fromEntity(auction, isBookmarked);
    }

    @Transactional(readOnly = true)
    public Page<AuctionResponseDto> getAuctions(
            Long userId,
            AuctionStatus status,
            int page,
            int size
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "endTime");

        if (userId == null) {
            Page<Auction> auctionPages = auctionRepository.findAllByStatusWithCategories(PageRequest.of(page, size, sort), status);
            return auctionPages.map(auction -> AuctionResponseDto.fromEntity(auction, false));
        }
        Page<Auction> auctionPages = auctionRepository.findAllByStatusWithCategories(PageRequest.of(page, size, sort), status);

        List<Long> auctionIds = auctionPages.stream()
                .map(Auction::getId)
                .collect(Collectors.toList());
        Set<Long> bookmarkedAuctionIds = auctionRepository.findBookmarkedAuctionIdsByUserIdAndAuctionIds(userId, auctionIds);

        return auctionPages.map(auction -> {
            boolean isBookmarked = bookmarkedAuctionIds.contains(auction.getId());
            return AuctionResponseDto.fromEntity(auction, isBookmarked);
        });
    }

    @Transactional
    public void update(
            Long userId,
            Long auctionId,
            AuctionUpdateRequestDto dto,
            MultipartFile image
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));

        if (user.getUsername().equals(auction.getSellerUserName())
                && AuctionStatus.SCHEDULED.equals(auction.getStatus())) {
            String newImagePath = auction.getImagePath();

            if (image != null) {
                newImagePath = imageService.updateImagePath(image, auction);
            }

            auction.getAuctionCategories().clear();
            for (String categoryName : dto.getCategories()) {
                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
                auction.addCategory(category);
            }

            auction.setImagePath(newImagePath);
            auction.setTitle(dto.getTitle());
            auction.setDescription(dto.getDescription());
            auction.setStartPrice(dto.getStartPrice());
            auction.setStartTime(dto.getStartTime());
            auction.setEndTime(dto.getEndTime());
            auctionRepository.save(auction);

            eventPublishService.publishAuctionSyncEvent(auction, dto.getCategories());
        }
        else {
            throw new RuntimeException("Auction status is " + auction.getStatus());
        }
    }

    @Transactional
    public void delete(Long userId, Long auctionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        if (!auction.getSellerUserName().equals(user.getUsername())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        if (!AuctionStatus.SCHEDULED.equals(auction.getStatus())) {
            throw new IllegalArgumentException("해당 경매를 제거할 수 없습니다.");
        }

        // 삭제한 후에도 보여주고 싶으면 주석처리, 필요없으면 주석 해제
        imageService.deleteImagePath(auction);
        deleteBookmark(userId, auctionId);
        auctionRepository.delete(auction);

        eventPublishService.publishAuctionDelete(auction);
    }

    @Transactional(readOnly = true)
    public List<AuctionResponseDto> getAuctionHistory(
            Long userId,
            String role,
            AuctionStatus status
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        List<Auction> auctions = null;

        if ("seller".equals(role)) {
            auctions = auctionRepository.findAllBySellerIdAndStatusWithCategories(userId, status);
        }
        else if ("winner".equals(role)) {
            auctions = auctionRepository.findAllByWinnerIdAndStatusWithCategories(userId, status);
        }
        else {
            return new ArrayList<>();
        }

        Set<Long> bookmarkedIds = auctionRepository.findAuctionIdsByUserId(userId);

        return auctions.stream()
                .map(auction -> AuctionResponseDto.fromEntity(
                        auction,
                        bookmarkedIds.contains(auction.getId())
                ))
                .collect(Collectors.toList());
    }

    public void addBookmark(Long userId, Long auctionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        if (auctionBookmarkRepository.findByUserIdAndAuctionId(userId, auctionId).isPresent()) {
            // 이미 북마크가 있는 상황임
            throw new BookmarkAlreadyExistsException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        AuctionBookmark bookmark = user.addBookmark(auction);
        auctionBookmarkRepository.save(bookmark);
    }

    @Transactional
    public void deleteBookmark(Long userId, Long auctionId) {
        AuctionBookmark bookmark = auctionBookmarkRepository.findByUserIdAndAuctionId(userId, auctionId)
                .orElseThrow(() -> new BookmarkNotFoundException(ErrorCode.BOOKMARK_NOT_FOUND));
        bookmark.getAuction().getAuctionBookmarks().remove(bookmark);
        bookmark.getUser().getAuctionBookmarks().remove(bookmark);

        auctionBookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public Page<AuctionResponseDto> getBookmarks(Long userId, AuctionStatus status, PageRequest pageRequest) {
        if (userId == null) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        Page<Auction> bookmarkedAuctions = auctionRepository.findBookmarkedAuctionsByUserIdAndStatus(userId, status, pageRequest);
        return bookmarkedAuctions.map(auction -> AuctionResponseDto.fromEntity(auction, true));
    }

}
