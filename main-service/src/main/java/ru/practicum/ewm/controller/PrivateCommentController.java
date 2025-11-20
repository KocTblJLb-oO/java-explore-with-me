package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.service.PrivateCommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final PrivateCommentService privateCommentService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return privateCommentService.addComment(userId, eventId, newCommentDto);
    }

    @GetMapping("{commentId}")
    public CommentDto getComment(@PathVariable Long userId,
                                 @PathVariable Long commentId) {
        return privateCommentService.getComment(userId, commentId);
    }

    @PatchMapping("{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return privateCommentService.updateComment(userId, commentId, newCommentDto);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        privateCommentService.deleteComment(userId, commentId);
    }
}