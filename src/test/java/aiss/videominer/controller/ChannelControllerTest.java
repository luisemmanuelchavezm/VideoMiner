package aiss.videominer.controller;

import aiss.videominer.exception.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChannelControllerTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelController channelController;

    private Channel channel;

    @BeforeEach
    void setUp() {
        channel = new Channel("1", "Test Channel", null, "2024-05-12", "Test description");
    }

    @Test
    void findOne_ShouldReturnChannel() throws ChannelNotFoundException {
        // Arrange
        String channelId = "1";
        Channel expectedChannel = new Channel(channelId, "Test Channel", new ArrayList<>(), "2024-05-12", "Description");
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(expectedChannel));

        // Act
        Channel actualChannel = channelController.findOne(channelId);

        // Assert
        assertEquals(expectedChannel, actualChannel);
    }

    @Test
    void findOne_ShouldThrowChannelNotFoundException() {
        // Arrange
        String channelId = "1";
        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChannelNotFoundException.class, () -> {
            channelController.findOne(channelId);
        });
    }

    @Test
    void create_ShouldSaveChannel() {
        // Arrange
        Channel channel = new Channel("1", "Test Channel", new ArrayList<>(), "2024-05-12", "Description");
        when(channelRepository.save(any(Channel.class))).thenReturn(channel);

        // Act
        Channel savedChannel = channelController.create(channel);

        // Assert
        assertNotNull(savedChannel);
        assertEquals(channel.getId(), savedChannel.getId());
        assertEquals(channel.getName(), savedChannel.getName());
        assertEquals(channel.getDescription(), savedChannel.getDescription());
        assertEquals(channel.getCreatedTime(), savedChannel.getCreatedTime());
        assertEquals(channel.getVideos(), savedChannel.getVideos());
        verify(channelRepository).save(channel);
    }


    @Test
    void update_ShouldUpdateChannel() throws ChannelNotFoundException {
        // Given
        String channelId = "1";
        Channel channelToUpdate = new Channel("Updated Channel", "Updated Description", "2024-05-12", new ArrayList<>());

        // Mock the behavior of the repository
        when(channelRepository.existsById(channelId)).thenReturn(true);

        // When
        channelController.update(channelToUpdate, channelId);

        // Then
        verify(channelRepository).save(any(Channel.class));
    }



    @Test
    void update_ShouldThrowExceptionWhenChannelNotFound() {
        // Arrange
        when(channelRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        assertThrows(ChannelNotFoundException.class, () -> channelController.update(channel, "1"));

        // Verify
        verify(channelRepository).existsById("1");
        verify(channelRepository, never()).save(any(Channel.class));
    }

    @Test
    void delete_ShouldDeleteChannel() throws ChannelNotFoundException {
        // Arrange
        when(channelRepository.existsById("1")).thenReturn(true);
        doNothing().when(channelRepository).deleteById("1");

        // Act
        assertDoesNotThrow(() -> channelController.delete("1"));

        // Verify
        verify(channelRepository).deleteById("1");
    }
    @Test
    void delete_ShouldThrowExceptionWhenChannelNotFound() {
        when(channelRepository.existsById("1")).thenReturn(false);

        assertThrows(ChannelNotFoundException.class, () -> channelController.delete("1"));
    }
}
