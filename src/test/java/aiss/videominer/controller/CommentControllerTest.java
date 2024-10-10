package aiss.videominer.controller;

import aiss.videominer.exception.CommentForbiddenException;
import aiss.videominer.exception.CommentNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Paths.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentController commentController;

    private Comment comment;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        comment = new Comment("1", "Test Comment", "2024-05-12", null);
        reset(commentRepository);
    }

    @Test
    void findAll_ShouldReturnAllComments() throws CommentNotFoundException {
        Page<Comment> page = new PageImpl<>(Arrays.asList(comment));
        when(commentRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        List<Comment> result = commentController.findAll(0, 10, null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(comment, result.get(0));
    }

    @Test
    void findOne_ShouldReturnComment() throws CommentNotFoundException, CommentForbiddenException {
        when(commentRepository.findById("1")).thenReturn(Optional.of(comment));

        Comment result = commentController.findOne("1");

        assertNotNull(result);
        assertEquals("Test Comment", result.getText());
    }

    @Test
    void findOne_ShouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentController.findOne("1"));
    }

    @Test
    void findOne_ShouldThrowExceptionWhenCommentIsForbidden() {
        // Arrange
        String commentId = "testCommentId";

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CommentNotFoundException.class, () -> {
            commentController.findOne(commentId);
        });
    }


    @Test
    void create_ShouldSaveAndReturnComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentController.create(new Comment("1", "New Comment", "2024-05-12", null));

        assertNotNull(result);
        assertEquals("Test Comment", result.getText());
    }

    @Test
    void update_ShouldUpdateComment() throws CommentNotFoundException {
        when(commentRepository.existsById("1")).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment("1", "Updated Comment", "2024-05-12", null));

        assertDoesNotThrow(() -> commentController.update(new Comment("1", "Updated Comment", "2024-05-12", null), "1"));
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.existsById("1")).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentController.update(new Comment("1", "Updated Comment", "2024-05-12", null), "1"));
    }

    @Test
    void delete_ShouldDeleteComment() throws CommentNotFoundException {
        when(commentRepository.existsById("1")).thenReturn(true);
        doNothing().when(commentRepository).deleteById("1");

        assertDoesNotThrow(() -> commentController.delete("1"));
    }

    @Test
    void delete_ShouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.existsById("1")).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> commentController.delete("1"));
    }
}
