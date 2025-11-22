package com.bookmark.service;

import com.bookmark.entity.Bookmark;
import com.bookmark.repository.BookmarkRepository;
import com.common.error.code.ErrorCode;
import com.common.exception.auction.AuctionNotFoundException;
import com.common.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public Set<Long> getBookmarkedAuctionIdsByUserId(Long userId) {
        if (userId == null) {
            log.error("userId: {} is null", userId);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        Set<Bookmark> bookmarks = bookmarkRepository.findAllByUserId(userId);

        return bookmarks.stream()
                .map(Bookmark::getAuctionId)
                .collect(Collectors.toSet());
    }

    public void save(Long userId, Long auctionId) {
        existId(userId, auctionId);

        Bookmark bookmark = Bookmark.builder()
                .userId(userId)
                .auctionId(auctionId)
                .build();
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void delete(Long userId, Long auctionId) {
        existId(userId, auctionId);
        bookmarkRepository.deleteByUserIdAndAuctionId(userId, auctionId);
    }

    private void existId(Long userId, Long auctionId) {
        if (userId == null) {
            log.error("userId: {} is null", userId);
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        if (auctionId == null) {
            log.error("auctionId: {} is null", auctionId);
            throw new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND);
        }
    }

}
