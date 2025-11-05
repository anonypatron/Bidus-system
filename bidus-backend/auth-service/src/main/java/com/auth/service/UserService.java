package com.auth.service;

import com.common.UpdateType;
import com.common.dto.user.UserUpdateDto;
import com.auth.dto.request.SignUpRequestDto;
import com.auth.dto.request.UpdateUserRequestInfoDto;
import com.auth.dto.response.UserInfo;
import com.auth.entity.User;
import com.auth.mapper.UserMapper;
import com.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final KafkaTemplate<String, UserUpdateDto> kafkaTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public UserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserInfo.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public boolean save(SignUpRequestDto dto) {
        // 현재 서비스 db 저장
        User user = userRepository.save(User.builder()
                    .email(dto.getEmail())
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .username(dto.getUsername())
                    .role(dto.getRole())
                    .build());

        // 사용자 생성 공지
        UserUpdateDto userUpdateDto = UserMapper.toUserUpdateDto(user, UpdateType.CREATE);
        kafkaTemplate.send("user-update-topic", userUpdateDto);

        return true;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserRequestInfoDto dto) {
        // 현재 서비스 db 업데이트
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        user.setUsername(dto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User updatedUser = userRepository.save(user);

        // 사용자 업데이트 공지
        UserUpdateDto userUpdateDto = UserMapper.toUserUpdateDto(updatedUser, UpdateType.UPDATE);
        kafkaTemplate.send("user-update-topic", userUpdateDto);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // 사용자 삭제 공지
        UserUpdateDto userUpdateDto = UserMapper.toUserUpdateDto(user, UpdateType.DELETE);
        kafkaTemplate.send("user-update-topic", userUpdateDto);

        // 현재 서비스에서도 삭제
        userRepository.deleteById(userId);
    }

}
