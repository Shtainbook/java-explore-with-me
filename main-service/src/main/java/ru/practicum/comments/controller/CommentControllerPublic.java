package ru.practicum.comments.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.model.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/{eventId}")
public class CommentControllerPublic {

    private final CommentService commentService;

    @Autowired
    public CommentControllerPublic(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDto>> getAllCommentsByEventId(@PathVariable Long eventId,
                                                                    @RequestParam(defaultValue = "0") Integer from,
                                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос GET '/events/{}/comments' для метода getAllCommentsByEventId с данными eventId={}", eventId, eventId);
        return new ResponseEntity<>(commentService.getAllCommentsByEventId(eventId, from, size), HttpStatus.OK);
    }

    @GetMapping("/comments/count")
    public ResponseEntity<Long> getCommentsCountByEventId(@PathVariable Long eventId) {
        log.info("Запрос GET '/events/{eventId}/comments/count' для метода getCommentsCountByEventId с данными eventId={}", eventId);
        return new ResponseEntity<>(commentService.getCommentsCountByEventId(eventId), HttpStatus.OK);
    }
}