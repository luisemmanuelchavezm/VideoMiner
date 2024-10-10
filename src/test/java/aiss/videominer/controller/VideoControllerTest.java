package aiss.videominer.controller;

import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoControllerTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoController videoController;

    private Video video;

    @BeforeEach
    void setUp() {
        video = new Video("1", "Test Video", "Description", "2024-05-12", new ArrayList<>(), new ArrayList<>());
        reset(videoRepository);
    }

    @Test
    void findAll_ShouldReturnAllVideos() throws VideoNotFoundException {
        Page<Video> page = new PageImpl<>(Arrays.asList(video));
        when(videoRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        List<Video> result = videoController.findAll(0, 10, null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(video, result.get(0));
    }

    @Test
    void findOne_ShouldReturnVideo() throws VideoNotFoundException {
        when(videoRepository.findById("1")).thenReturn(Optional.of(video));

        Video result = videoController.findOne("1");

        assertNotNull(result);
        assertEquals("Test Video", result.getName());
    }

    @Test
    void findOne_ShouldThrowExceptionWhenVideoNotFound() {
        when(videoRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(VideoNotFoundException.class, () -> videoController.findOne("1"));
    }

    @Test
    void create_ShouldSaveAndReturnVideo() {
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        Video result = videoController.create(new Video("1", "New Video", "Description", "2024-05-12", new ArrayList<>(), new ArrayList<>()));

        assertNotNull(result);
        assertEquals("Test Video", result.getName());
    }

    @Test
    void update_ShouldUpdateVideo() throws VideoNotFoundException {
        when(videoRepository.existsById("1")).thenReturn(true);
        when(videoRepository.save(any(Video.class))).thenReturn(new Video("1", "Updated Video", "Updated Description", "2024-05-12", new ArrayList<>(), new ArrayList<>()));

        assertDoesNotThrow(() -> videoController.update(new Video("1", "Updated Video", "Updated Description", "2024-05-12", new ArrayList<>(), new ArrayList<>()), "1"));
        verify(videoRepository).save(any(Video.class));
    }

    @Test
    void update_ShouldThrowExceptionWhenVideoNotFound() {
        when(videoRepository.existsById("1")).thenReturn(false);

        assertThrows(VideoNotFoundException.class, () -> videoController.update(new Video("1", "Updated Video", "Updated Description", "2024-05-12", new ArrayList<>(), new ArrayList<>()), "1"));
    }

    @Test
    void delete_ShouldDeleteVideo() throws VideoNotFoundException {
        when(videoRepository.existsById("1")).thenReturn(true);
        doNothing().when(videoRepository).deleteById("1");

        assertDoesNotThrow(() -> videoController.delete("1"));
    }

    @Test
    void delete_ShouldThrowExceptionWhenVideoNotFound() {
        when(videoRepository.existsById("1")).thenReturn(false);

        assertThrows(VideoNotFoundException.class, () -> videoController.delete("1"));
    }
}
