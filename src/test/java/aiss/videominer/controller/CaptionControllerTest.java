package aiss.videominer.controller;
import aiss.videominer.controller.CaptionController;
import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CaptionControllerTest {

    @Mock
    private CaptionRepository captionRepository;

    @InjectMocks
    private CaptionController captionController;

    private Caption caption;

    @BeforeEach
    void setUp() {
        caption = new Caption("1", "Test Caption", "en");
        reset(captionRepository);
    }

    @Test
    void findAll_ShouldReturnAllCaptions() {
        Page<Caption> page = new PageImpl<>(Arrays.asList(caption));
        when(captionRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        List<Caption> result = captionController.findAll(0, 10, null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(caption, result.get(0));
    }

    @Test
    void findOne_ShouldReturnCaption() throws CaptionNotFoundException {
        when(captionRepository.findById("1")).thenReturn(Optional.of(caption));

        Caption result = captionController.findOne("1");

        assertNotNull(result);
        assertEquals("Test Caption", result.getName());
    }

    @Test
    void findOne_ShouldThrowExceptionWhenCaptionNotFound() {
        when(captionRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(CaptionNotFoundException.class, () -> captionController.findOne("1"));
    }

    @Test
    void create_ShouldSaveAndReturnCaption() {
        when(captionRepository.save(any(Caption.class))).thenReturn(caption);

        Caption result = captionController.create(new Caption("1", "New Caption", "en"));

        assertNotNull(result);
        assertEquals("Test Caption", result.getName());
    }
    @Test
    void update_ShouldUpdateCaption() throws CaptionNotFoundException {
        // Arrange
        when(captionRepository.existsById("1")).thenReturn(true);
        when(captionRepository.save(any(Caption.class))).thenReturn(new Caption("1", "Updated Caption", "en"));

        // Act
        captionController.update(new Caption("1", "Updated Caption", "en"), "1");

        // Assert
        verify(captionRepository).save(any(Caption.class));
    }


    @Test
    void update_ShouldThrowExceptionWhenCaptionNotFound() {
        when(captionRepository.existsById("1")).thenReturn(false);

        assertThrows(CaptionNotFoundException.class, () -> captionController.update(new Caption("1", "Updated Caption", "en"), "1"));
    }

    @Test
    void delete_ShouldDeleteCaption() throws CaptionNotFoundException {
        when(captionRepository.existsById("1")).thenReturn(true);
        doNothing().when(captionRepository).deleteById("1");

        assertDoesNotThrow(() -> captionController.delete("1"));
    }

    @Test
    void delete_ShouldThrowExceptionWhenCaptionNotFound() {
        when(captionRepository.existsById("1")).thenReturn(false);

        assertThrows(CaptionNotFoundException.class, () -> captionController.delete("1"));
    }
}
