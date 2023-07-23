package ru.practicum.users.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.model.NewUserRequestDto;
import ru.practicum.users.model.UserDto;
import ru.practicum.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class UserControllerAdmin {

    private final UserService userService;

    @Autowired
    public UserControllerAdmin(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequestDto newUserRequest) {
        log.info("Запрос POST '/admin/users' для метода createUser с newUserRequest={}", newUserRequest);
        return new ResponseEntity<>(userService.createUser(newUserRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsersByIds(
            @RequestParam(required = false) List<Long> ids,
            @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос GET '/admin/users' для метода getUsersByIds с параметрами: ids={}, from={}, size={}",
                ids, from, size);
        return new ResponseEntity<>(userService.getUsersByIds(ids, from, size), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.info("Запрос DELETE '/admin/users/{}' для метода deleteUser с userId={}", userId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}