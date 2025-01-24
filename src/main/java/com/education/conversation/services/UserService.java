package com.education.conversation.services;

import com.education.conversation.dto.UserDto;
import com.education.conversation.entities.User;
import com.education.conversation.exceptions.ErrorResponseException;
import com.education.conversation.exceptions.ErrorStatus;
import com.education.conversation.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User create(UserDto userDto) {
        return userRepository.save(new User().setName(userDto.getName()));
    }

    public User findByIdOrThrowError(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(ErrorStatus.USER_NOT_FOUND));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
