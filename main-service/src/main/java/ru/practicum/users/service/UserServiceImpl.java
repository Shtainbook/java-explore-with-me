package ru.practicum.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.NewUserRequest;
import ru.practicum.users.model.User;
import ru.practicum.users.model.UserDto;
import ru.practicum.users.repository.UserRepository;
import ru.practicum.util.pageable.OffsetPageRequest;
import ru.practicum.util.validation.SizeValidator;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {

        User user = UserMapper.toUser(newUserRequest);
        userRepository.save(user);
        log.info("Вызов метода createUser с newUserRequest={}", newUserRequest);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size) {

        SizeValidator.validateSize(size);
        Pageable pageable = OffsetPageRequest.of(from, size);

        List<User> users;

        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findUsersByIdIn(ids, pageable);
        }
        log.info("Вызов метода getUsersByIds ids={}", ids);
        return UserMapper.toUserDto(users);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userExists(id);
        userRepository.deleteById(id);
        log.info("Вызов метода deleteUser по id={}", id);
    }

    @Override
    public void userExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }
        log.info("Вызов метода userExists по id={}", id);
    }

    @Override
    public User getUserModelById(Long id) {
        log.info("Вызов метода getUserModelById id={}", id);
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User", id));
    }
}