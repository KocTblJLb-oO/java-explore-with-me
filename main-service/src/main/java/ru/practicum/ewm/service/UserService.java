package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        int idsCount = (ids == null) ? 0 : ids.size();
        log.info("getUsers. Получение пользователей. Количество ИД: {}, смещение: {}, количество: {}", idsCount, from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findByIdIn(ids, pageable);
        }
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto registerUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ValidationException("Пользователь с e-mail " + newUserRequest.getEmail() + " существует.",
                    HttpStatus.CONFLICT);
        }

        User user = userMapper.toUser(newUserRequest);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("deleteUser. Удаление пользователя: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не существует", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }
}
