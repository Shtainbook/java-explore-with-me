package ru.practicum.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comments.model.CommentLike;

import java.util.List;
import java.util.Optional;
@Repository
public interface LikeRepository extends JpaRepository<CommentLike, Long> {

    Boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    Optional<CommentLike> getByCommentIdAndUserId(Long commentId, Long userId);

    Integer countAllByCommentId(Long commentId);

    void deleteAllByCommentId(Long commentId);

    List<CommentLike> findAllByCommentIdIn(List<Long> comments);
}
