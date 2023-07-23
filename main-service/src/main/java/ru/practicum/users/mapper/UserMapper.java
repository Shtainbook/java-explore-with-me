package ru.practicum.users.mapper;

import ru.practicum.users.model.NewUserRequestDto;
import ru.practicum.users.model.User;
import ru.practicum.users.model.UserDto;
import ru.practicum.users.model.UserShortDto;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User element : users) {
            result.add(toUserDto(element));
        }
        return result;
    }

    public static User toUser(NewUserRequestDto newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static List<UserShortDto> toUserShortDto(Iterable<User> users) {
        List<UserShortDto> result = new ArrayList<>();
        for (User element : users) {
            result.add(toUserShortDto(element));
        }
        return result;
    }
}