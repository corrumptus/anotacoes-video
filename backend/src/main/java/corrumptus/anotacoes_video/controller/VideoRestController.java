package corrumptus.anotacoes_video.controller;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import corrumptus.anotacoes_video.dto.video.NewVideoDTO;
import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.entity.UserEntity;
import corrumptus.anotacoes_video.entity.VideoEntity;
import corrumptus.anotacoes_video.mapper.UserMapper;
import corrumptus.anotacoes_video.mapper.VideoMapper;
import corrumptus.anotacoes_video.model.Video;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/video")
public class VideoRestController {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> getVideo(@PathVariable("id") String id) {
        Optional<VideoResponseDTO> response = videoRepository.findById(id)
            .map(VideoMapper::toResponseFromEntity);

        if (response.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(response.get());
    }

    @GetMapping
    public ResponseEntity<List<VideoResponseDTO>> getUserVideos(@RequestParam("user") String userId) {
        List<VideoResponseDTO> videos = videoRepository.findByOwner(userId)
            .stream().map(VideoMapper::toResponseFromEntity).toList();

        if (videos.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(videos);
    }

    @PostMapping
    public ResponseEntity<VideoResponseDTO> newVideo(
        @RequestBody NewVideoDTO request,
        UriComponentsBuilder uriBuilder
    ) throws Exception {
        Optional<UserEntity> owner = userRepository.findById(request.ownerId());

        if (owner.isEmpty())
            throw new EntityNotFoundException("User " + request.ownerId() + " doesnt exists");

        if (request.video().isEmpty())
            throw new IllegalArgumentException("Video is empty");

        if (
            request.video().getContentType() != null
            &&
            (
                request.video().getContentType() == "video/mp4"
                ||
                request.video().getContentType() == "video/webm"
            )
        )
            throw new IllegalArgumentException("Video must be of type mp4 or webm");

        int tamanhoMaximo = 100 * 1024 * 1024;

        if (request.video().getSize() > tamanhoMaximo)
            throw new IllegalArgumentException("Video is bigger than 100MB");

        String videoName = request.ownerId() + "-" + UUID.randomUUID();
        Path videoPath = Paths.get("videos", videoName);
        request.video().transferTo(videoPath.toFile());

        Video videoFromRequest = new Video(
            videoName,
            UserMapper.toModel(owner.get()),
            request.title(),
            request.description()
        );

        VideoEntity newVideo = videoRepository.save(VideoMapper.toEntity(videoFromRequest));
        URI uri = uriBuilder.path("/video/{id}").buildAndExpand(newVideo.getId()).toUri();

        return ResponseEntity
            .created(uri)
            .body(VideoMapper.toResponseFromEntity(newVideo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVideo(@PathVariable("id") String id) throws Exception {
        Optional<VideoEntity> video = videoRepository.findById(id);

        if (video.isEmpty())
            throw new EntityNotFoundException("Video doesnt exists");

        videoRepository.delete(video.get());

        Path path = Paths.get("videos", video.get().getPath());

        Files.delete(path);

        return ResponseEntity.noContent().build();
    }
}