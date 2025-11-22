package com.auction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String username;

    @Builder
    public User(Long id, String username){
        this.id = id;
        this.username = username;
    }

}
