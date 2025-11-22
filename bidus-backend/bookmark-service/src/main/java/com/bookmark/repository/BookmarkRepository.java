package com.bookmark.repository;

import com.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    void deleteByUserIdAndAuctionId(Long userId, Long auctionId);
    Set<Bookmark> findAllByUserId(Long userId);
}
