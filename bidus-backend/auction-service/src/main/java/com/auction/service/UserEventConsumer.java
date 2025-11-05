package com.auction.service;

import com.common.UpdateType;
import com.common.dto.user.UserUpdateDto;
import com.auction.entity.User;
import com.auction.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserEventConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = "user-update-topic", groupId = "auction-group")
    @Transactional
    public void userUpdate(UserUpdateDto dto) {
        UpdateType type = dto.getType();

        if (UpdateType.CREATE.equals(type)) {
            User user = User.builder()
                    .id(dto.getId())
                    .username(dto.getUsername())
                    .build();
            userRepository.save(user);
        }
        else if (UpdateType.UPDATE.equals(type)) {
            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("user not found"));
            user.setId(dto.getId());
            user.setUsername(dto.getUsername());
            userRepository.save(user);
        }
        else if (UpdateType.DELETE.equals(type)) {
            User user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("user not found"));
            userRepository.delete(user);
        }
        else {
            throw new IllegalStateException("user update type not supported");
        }

    }

}
