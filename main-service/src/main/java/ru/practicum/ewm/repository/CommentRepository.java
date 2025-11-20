package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.CollectionUtils;
import ru.practicum.ewm.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    default List<Comment> findWithFilters(String commentText,
                                          List<Long> users,
                                          List<Long> events,
                                          List<Long> comments,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Pageable pageable) {
        var predicates = Specification.allOf(CommentRepository.Specs.text(commentText),
                CommentRepository.Specs.users(users),
                CommentRepository.Specs.events(events),
                CommentRepository.Specs.comments(comments),
                CommentRepository.Specs.after(rangeStart),
                CommentRepository.Specs.before(rangeEnd));
        return findAll(predicates, pageable).getContent();
    }

    List<Comment> findAllByEventId(Long eventId);

    List<Comment> findAllByAuthorId(Long authorId);

    class Specs {
        static Specification<Comment> text(String commentText) {
            return commentText == null ? null :
                    (entity, query, cb)
                            -> cb.or(cb.like(cb.lower(entity.get("commentText")),
                            "%" + commentText.toLowerCase() + "%"));
        }

        static Specification<Comment> users(List<Long> users) {
            return CollectionUtils.isEmpty(users) ? null :
                    (entity, query, cb)
                            -> entity.get("author").get("id").in(users);
        }

        static Specification<Comment> events(List<Long> events) {
            return CollectionUtils.isEmpty(events) ? null :
                    (entity, query, cb)
                            -> entity.get("event").get("id").in(events);
        }

        static Specification<Comment> comments(List<Long> comments) {
            return CollectionUtils.isEmpty(comments) ? null :
                    (entity, query, cb)
                            -> entity.get("id").in(comments);
        }

        static Specification<Comment> after(LocalDateTime rangeStart) {
            return rangeStart == null ? null :
                    (entity, query, cb)
                            -> cb.greaterThanOrEqualTo(entity.get("createdAt"), rangeStart);
        }

        static Specification<Comment> before(LocalDateTime rangeEnd) {
            return rangeEnd == null ? null :
                    (entity, query, cb)
                            -> cb.lessThanOrEqualTo(entity.get("createdAt"), rangeEnd);
        }
    }
}