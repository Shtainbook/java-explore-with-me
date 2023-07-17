package ru.practicum.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comments.model.CommentDislike;

import java.util.List;
import java.util.Optional;
@Repository
public interface DislikeRepository extends JpaRepository<CommentDislike, Long> {

    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Optional<CommentDislike> getByCommentIdAndUserId(Long commentId, Long userId);

    Integer countAllByCommentId(Long commentId);

    void deleteAllByCommentId(Long commentId);

    List<CommentDislike> findAllByCommentIdIn(List<Long> comments);
}
