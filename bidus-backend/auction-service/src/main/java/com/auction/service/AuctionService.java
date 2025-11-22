package com.auction.service;

import com.auction.dto.request.AuctionCreateRequestDto;
import com.auction.dto.request.AuctionUpdateRequestDto;
import com.auction.dto.response.AuctionPriceDto;
import com.auction.dto.response.AuctionResponseDto;
import com.auction.entity.Auction;
import com.auction.entity.Category;
import com.auction.entity.User;
import com.auction.repository.AuctionRepository;
import com.auction.repository.CategoryRepository;
import com.auction.repository.UserRepository;
import com.common.AuctionStatus;
import com.common.error.code.ErrorCode;
import com.common.exception.auction.AuctionNotFoundException;
import com.common.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuctionService {

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
        return AuctionResponseDto.fromEntity(savedAuction);
    }

    @Transactional(readOnly = true)
    public AuctionResponseDto getAuction(Long auctionId) {
        Auction auction = auctionRepository.findByIdWithCategories(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));

        return AuctionResponseDto.fromEntity(auction);
    }

    @Transactional(readOnly = true)
    public Page<AuctionResponseDto> getAuctions(
            AuctionStatus status,
            Pageable pageable
    ) {
        Page<Auction> auctionPages = auctionRepository.findAllByStatusWithCategories(pageable, status);
        return auctionPages.map(AuctionResponseDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<AuctionResponseDto> getAuctionsByIds(Set<Long> ids, AuctionStatus status, Pageable pageable) {
        Page<Auction> auctionPages = auctionRepository.findByIdInAndStatus(ids, status, pageable);
        return auctionPages.map(AuctionResponseDto::fromEntity);
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

        return auctions.stream()
                .map(AuctionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AuctionResponseDto> getAuctionCurrentBidding(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND));
        List<Auction> auctions = auctionRepository.findCurrentBiddingByUserId(userId);

        return auctions.stream()
                .map(AuctionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public AuctionPriceDto getAuctionPrice(Long id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        return new AuctionPriceDto(auction.getCurrentPrice());
    }

}
