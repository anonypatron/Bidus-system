package com.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String username;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuctionBookmark> auctionBookmarks = new HashSet<>();

    @Builder
    public User(Long id, String username){
        this.id = id;
        this.username = username;
    }

    public AuctionBookmark addBookmark(Auction auction){
        AuctionBookmark auctionBookmark = AuctionBookmark.builder()
                .user(this)
                .auction(auction)
                .build();
        this.auctionBookmarks.add(auctionBookmark);
        auction.getAuctionBookmarks().add(auctionBookmark);

        return auctionBookmark;
    }

    @Deprecated
    public void removeBookmark(Auction auction) {
        this.auctionBookmarks.stream()
                .filter(auctionBookmark -> auctionBookmark.getAuction().equals(auction))
                .findFirst()
                .ifPresent(bookmark -> {
                    this.auctionBookmarks.remove(bookmark);
                    auction.getAuctionBookmarks().remove(bookmark);
                });
    }

}
