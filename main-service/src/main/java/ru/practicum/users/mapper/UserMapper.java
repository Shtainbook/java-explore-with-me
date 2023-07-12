package ru.practicum.users.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.users.model.NewUserRequest;
import ru.practicum.users.model.UserDto;
import ru.practicum.users.model.UserShortDto;
import ru.practicum.users.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User element : users) {
            result.add(toUserDto(element));
        }
        return result;
    }

    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public List<UserShortDto> toUserShortDto(Iterable<User> users) {
        List<UserShortDto> result = new ArrayList<>();
        for (User element : users) {
            result.add(toUserShortDto(element));
        }
        return result;
    }
}