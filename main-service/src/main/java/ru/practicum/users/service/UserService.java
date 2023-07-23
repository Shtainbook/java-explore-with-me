package ru.practicum.users.service;

import ru.practicum.users.model.NewUserRequestDto;
import ru.practicum.users.model.User;
import ru.practicum.users.model.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserRequest);

    List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long id);

    void userExists(Long id);

    User getUserModelById(Long id);
}