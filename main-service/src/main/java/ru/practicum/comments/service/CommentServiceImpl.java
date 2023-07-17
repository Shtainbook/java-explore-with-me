package ru.practicum.comments.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.*;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.comments.repository.DislikeRepository;
import ru.practicum.comments.repository.LikeRepository;
import ru.practicum.events.service.EventService;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationRequestException;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserService;
import ru.practicum.util.pageable.OffsetPageRequest;
import ru.practicum.util.validation.SizeValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final EventService eventService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final LikeRepository likesRepository;
    private final DislikeRepository dislikeRepository;

    @Autowired
    public CommentServiceImpl(EventService eventService, UserService userService, CommentRepository commentRepository, LikeRepository likesRepository, DislikeRepository dislikeRepository) {
        this.eventService = eventService;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.likesRepository = likesRepository;
        this.dislikeRepository = dislikeRepository;
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {

        Comment comment = CommentMapper.toComment(newCommentDto);

        comment.setEvent(eventService.getEventModelById(eventId));
        comment.setAuthor(userService.getUserModelById(userId));

        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setLikesCount(likesRepository.countAllByCommentId(comment.getId()));
        commentDto.setDislikesCount(dislikeRepository.countAllByCommentId(comment.getId()));
        log.info("Вызов метода createComment с newCommentDto={} для eventId={} для userId={}", newCommentDto, eventId, userId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void addLikeToComment(Long userId, Long commentId) {
        Comment comment = getCommentModelById(commentId);
        User user = userService.getUserModelById(userId);

        if (comment.getAuthor().equals(user)) {
            throw new ConflictException("Ты не можешь сам себя лайкать.");
        }

        if (likesRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new ConflictException("Пользователь уже лайкнул комментарий.");
        }

        if (dislikeRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            deleteDislike(userId, commentId);
        }

        CommentLike commentLike = CommentLike.builder()
                .commentId(commentId)
                .userId(user.getId())
                .createdOn(LocalDateTime.now())
                .build();
        log.info("Вызов метода addLikeToComment для userId={} для commentId={}", userId, commentId);
        likesRepository.save(commentLike);
    }

    @Override
    @Transactional
    public void addDislikeToComment(Long userId, Long commentId) {
        Comment comment = getCommentModelById(commentId);
        User user = userService.getUserModelById(userId);

        if (comment.getAuthor().equals(user)) {
            throw new ConflictException("Ты не можешь сам себя дислайкать.");
        }

        if (dislikeRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            throw new ConflictException("Пользователь уже дислайкнул комментарий.");
        }

        if (likesRepository.existsByCommentIdAndUserId(commentId, user.getId())) {
            deleteLike(userId, commentId);
        }

        CommentDislike commentDislike = CommentDislike.builder()
                .commentId(commentId)
                .userId(user.getId())
                .createdOn(LocalDateTime.now())
                .build();
        log.info("Вызов метода addDislikeToComment для userId={} для commentId={}", userId, commentId);
        dislikeRepository.save(commentDislike);
    }

    @Override
    @Transactional
    public void deleteLike(Long userId, Long commentId) {
        commentExists(commentId);
        userService.userExists(userId);
        CommentLike like = getLikeByCommentIdAndUserId(commentId, userId);
        log.info("Вызов метода deleteLike для userId={} для commentId={}", userId, commentId);
        likesRepository.deleteById(like.getId());
    }

    @Override
    @Transactional
    public void deleteDislike(Long userId, Long commentId) {
        commentExists(commentId);
        userService.userExists(userId);
        CommentDislike dislike = getDislikeByCommentIdAndUserId(commentId, userId);
        log.info("Вызов метода deleteDislike для userId={} для commentId={}", userId, commentId);
        dislikeRepository.deleteById(dislike.getId());
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequestDto updateCommentRequest) {
        Comment comment = getCommentModelById(commentId);
        User user = userService.getUserModelById(userId);

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ConflictException("Только автор комментария может редактировать комментарий.");
        }

        if (updateCommentRequest.getCommentText() != null && !updateCommentRequest.getCommentText().isBlank()) {
            comment.setCommentText(updateCommentRequest.getCommentText());
        }

        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setLikesCount(likesRepository.countAllByCommentId(commentId));
        commentDto.setDislikesCount(dislikeRepository.countAllByCommentId(commentId));
        log.info("Вызов метода updateComment для userId={} для commentId={} для updateCommentRequest={}", userId, commentId, updateCommentRequest);
        return commentDto;
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("Getting comment with id={}", commentId);
        CommentDto commentDto = CommentMapper.toCommentDto(getCommentModelById(commentId));
        commentDto.setLikesCount(likesRepository.countAllByCommentId(commentId));
        commentDto.setDislikesCount(dislikeRepository.countAllByCommentId(commentId));
        log.info("Вызов метода getCommentById для commentId={}", commentId);
        return commentDto;
    }

    @Override
    public List<CommentDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size) {
        SizeValidator.validateSizeAndFrom(size, from);

        eventService.eventExists(eventId);

        List<Comment> comments = commentRepository.findAllByEventId(eventId, OffsetPageRequest.of(from, size));
        List<CommentDto> commentDtos = CommentMapper.toCommentDto(comments);

        List<Long> commentIds = commentDtos.stream()
                .map(CommentDto::getId)
                .collect(Collectors.toList());

        Map<Long, List<CommentLike>> commentLikesMap = likesRepository.findAllByCommentIdIn(commentIds)
                .stream()
                .collect(Collectors.groupingBy(CommentLike::getCommentId));

        Map<Long, List<CommentDislike>> commentDislikesMap = dislikeRepository.findAllByCommentIdIn(commentIds)
                .stream()
                .collect(Collectors.groupingBy(CommentDislike::getCommentId));

        for (CommentDto element : commentDtos) {
            Long commentId = element.getId();

            List<CommentLike> commentLikes = commentLikesMap.getOrDefault(commentId, Collections.emptyList());
            Integer likesCount = commentLikes.size();

            List<CommentDislike> commentDislikes = commentDislikesMap.getOrDefault(commentId, Collections.emptyList());
            Integer dislikesCount = commentDislikes.size();

            element.setLikesCount(likesCount);
            element.setDislikesCount(dislikesCount);
        }
        log.info("Вызов метода getAllCommentsByEventId для eventId={}", eventId);
        return commentDtos;
    }

    @Override
    public Long getCommentsCountByEventId(Long eventId) {
        eventService.eventExists(eventId);

        log.info("Вызов метода countByEventId с eventId={}", eventId);
        return commentRepository.countByEventId(eventId);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        Comment comment = getCommentModelById(commentId);
        User user = userService.getUserModelById(userId);

        if (!comment.getAuthor().equals(user)) {
            throw new ValidationRequestException("Только автор может удалить свой комментарий.");
        }

        log.info("Вызов метода deleteAllByCommentId с commentId={}, deleteAllByCommentId с commentId={}, deleteById с commentId={} для id={} ", commentId, commentId, commentId, userId);
        likesRepository.deleteAllByCommentId(commentId);
        dislikeRepository.deleteAllByCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        commentExists(commentId);

        log.info("Вызов метода deleteAllByCommentId с commentId={}, deleteAllByCommentId с commentId={}, deleteById с commentId={}", commentId, commentId, commentId);
        likesRepository.deleteAllByCommentId(commentId);
        dislikeRepository.deleteAllByCommentId(commentId);
        commentRepository.deleteById(commentId);
    }


    private Comment getCommentModelById(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Comment", id));
    }

    private void commentExists(Long id) {
        log.info("Проверка комментария с  id={} на существование", id);

        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("User", id);
        }
    }

    private CommentLike getLikeByCommentIdAndUserId(Long commentId, Long userId) {
        return likesRepository.getByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new NotFoundException("Лайк не найден.", commentId));
    }

    private CommentDislike getDislikeByCommentIdAndUserId(Long commentId, Long userId) {
        return dislikeRepository.getByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new NotFoundException("Дислайк не найден.", commentId));
    }
}