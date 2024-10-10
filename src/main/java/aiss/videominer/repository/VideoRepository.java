package aiss.videominer.repository;

import aiss.videominer.model.Channel;
import aiss.videominer.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, String> {
    Page<Video> findByName(String name, Pageable pageable);
    Page<Video> findByNameContaining(String containing, Pageable pageable);
}
