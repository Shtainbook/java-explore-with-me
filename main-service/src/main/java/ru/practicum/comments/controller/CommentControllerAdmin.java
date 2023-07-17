package ru.practicum.comments.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.model.CommentDto;
import ru.practicum.comments.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
public class CommentControllerAdmin {

    private CommentService commentService;

    @Autowired
    public CommentControllerAdmin(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        log.info("Запрос GET '/admin/comments/{}' для метода getCommentById с данными: commentId={}", commentId, commentId);
        return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Запрос DELETE '/admin/comments/{}' для метода to deleteCommentByAdmin с данными: commentId={}", commentId, commentId);
        commentService.deleteCommentByAdmin(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}