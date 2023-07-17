package ru.practicum.comments.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.model.CommentDto;
import ru.practicum.comments.model.NewCommentDto;
import ru.practicum.comments.model.UpdateCommentRequestDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
public class CommentControllerPrivate {

    private CommentService commentService;

    @Autowired
    public CommentControllerPrivate(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Запрос POST '/users/{}/events/{}/comments' для метода createComment с данными eventId={}", userId, eventId, eventId);
        return new ResponseEntity<>(commentService.createComment(userId, eventId, newCommentDto), HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/comments/{commentId}/like")
    public ResponseEntity<?> addLikeToComment(@PathVariable Long userId,
                                              @PathVariable Long commentId) {
        log.info("Запрос POST '/users/{}/comments/{}/like' для метода addLikeToComment с данными commentId={}", userId, commentId, commentId);
        commentService.addLikeToComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/comments/{commentId}/dislike")
    public ResponseEntity<?> addDislikeToComment(@PathVariable Long userId,
                                                 @PathVariable Long commentId) {
        log.info("Запрос POST '/users/{}/comments/{}/dislike' для метода addDislikeToComment с данными commentId={}", userId, commentId, commentId);
        commentService.addDislikeToComment(userId, commentId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateCommentById(@PathVariable Long userId,
                                                        @PathVariable Long commentId,
                                                        @Valid @RequestBody UpdateCommentRequestDto updateCommentRequest) {
        log.info("Запрос PATCH '/users/{}/comments/{}' для метода updateComment с данными commentId={}", userId, commentId, commentId);
        return new ResponseEntity<>(commentService.updateComment(userId, commentId, updateCommentRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<?> deleteCommentByUser(@PathVariable Long userId,
                                                 @PathVariable Long commentId) {
        log.info("Запрос DELETE '/users/{}/comments/{}' для метода deleteCommentByUser с данными commentId={}", userId, commentId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}