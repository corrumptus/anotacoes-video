package corrumptus.anotacoes_video.controller;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.mapper.VideoMapper;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/video")
public class VideoRestController {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    private String VIDEO_FOLDER = "videos";

    private int MAX_SIZE = 100 * 1024 * 1024;

    @GetMapping
    public ResponseEntity<List<VideoResponseDTO>> getUserVideos(@RequestParam("user") String userId) {
        List<VideoResponseDTO> videos = videoRepository.findByOwner(userId)
            .stream().map(VideoMapper::toResponse).toList();

        if (videos.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> getVideo(@PathVariable("id") String id) {
        Optional<VideoResponseDTO> response = videoRepository.findById(id)
            .map(VideoMapper::toResponse);

        if (response.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(response.get());
    }

    @PostMapping
    public ResponseEntity<VideoResponseDTO> newVideo(
        @RequestBody @Valid NewVideoDTO request,
        UriComponentsBuilder uriBuilder
    ) throws Exception {
        Optional<User> owner = userRepository.findById(request.ownerId());

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

        if (request.video().getSize() > MAX_SIZE)
            throw new IllegalArgumentException("Video is bigger than 100MB");

        String videoFileName = request.ownerId() + "-" + UUID.randomUUID();
        Path videoPath = Paths.get(VIDEO_FOLDER, videoFileName);
        request.video().transferTo(videoPath.toFile());

        Video newVideo = videoRepository.save(
            VideoMapper.toEntity(
                request,
                videoFileName,
                request.video().getContentType(),
                0,
                owner.get()
            )
        );
        URI uri = uriBuilder.path("/video/{id}").buildAndExpand(newVideo.getId()).toUri();

        return ResponseEntity
            .created(uri)
            .body(VideoMapper.toResponse(newVideo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVideo(@PathVariable("id") String id) throws Exception {
        Optional<Video> video = videoRepository.findById(id);

        if (video.isEmpty())
            throw new EntityNotFoundException("Video doesnt exists");

        videoRepository.delete(video.get());

        Path path = Paths.get(VIDEO_FOLDER, video.get().getPath());

        Files.delete(path);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/video")
    public ResponseEntity<Resource> getVideoResource(@PathVariable("id") String id) {
        Optional<Video> videoEntity = videoRepository.findById(id);

        if (videoEntity.isEmpty())
            throw new EntityNotFoundException("Video doesnt exists");

        Path path = Paths.get(VIDEO_FOLDER, videoEntity.get().getPath());

        File file = path.toFile();
        long fileLength = file.length();

        Resource video = new FileSystemResource(file);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, videoEntity.get().getType())
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
            .body(video);
    }
}