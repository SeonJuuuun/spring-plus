package org.example.expert.domain.user.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.request.UserSearchRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.dto.response.UserSearchResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final String REDIS_KEY_PREFIX = "user:nickname:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    public UserSearchResponse searchNickname(final UserSearchRequest userSearchRequest) {
        final User user = userRepository.findByNickname(userSearchRequest.nickname())
                .orElseThrow(() -> new InvalidRequestException("닉네임이 유효하지 않습니다."));

        return UserSearchResponse.from(user.getNickname());
    }

//    public UserSearchResponse searchNickname(final UserSearchRequest userSearchRequest) {
//        final String nickname = userSearchRequest.nickname();
//        final String redisKey = REDIS_KEY_PREFIX + nickname;
//
//        final UserSearchResponse cachedResponse = (UserSearchResponse) redisTemplate.opsForValue().get(redisKey);
//        if (cachedResponse != null) {
//            return cachedResponse;
//        }
//
//        final User user = userRepository.findByNickname(nickname)
//                .orElseThrow(() -> new InvalidRequestException("닉네임이 유효하지 않습니다."));
//
//        final UserSearchResponse response = UserSearchResponse.from(user.getNickname());
//        redisTemplate.opsForValue().set(redisKey, response, 10, TimeUnit.MINUTES);
//
//        return response;
//    }
}
