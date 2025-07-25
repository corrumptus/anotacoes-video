package corrumptus.anotacoes_video.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import corrumptus.anotacoes_video.dto.video.NewVideoDTO;
import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.dto.video.VideoUpdateDTO;
import corrumptus.anotacoes_video.dto.video_permissions.VideoVisitantDBDTO;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.entity.Visibility;
import corrumptus.anotacoes_video.mapper.VideoMapper;
import corrumptus.anotacoes_video.repository.VideoRepository;
import corrumptus.anotacoes_video.repository.VideoVisibilityRepository;
import corrumptus.anotacoes_video.utils.mideahandling.VideoService;

@RestController
@RequestMapping("/video")
public class VideoRestController {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoVisibilityRepository visibilityRepository;

    @Autowired
    private VideoService videoService;

    private final String VIDEO_FOLDER = "videos";
    private final String THUMB_FOLDER = "thumb";

    private final long MAX_SIZE = 5l * 1024l * 1024l * 1024l;

    public VideoRestController() throws IOException {
        Path videoFolder = Paths.get(VIDEO_FOLDER);
        if (!Files.exists(videoFolder)) {
            Files.createDirectories(videoFolder);
        }

        Path thumbFolder = Paths.get(THUMB_FOLDER);
        if (!Files.exists(thumbFolder)) {
            Files.createDirectories(thumbFolder);
        }
    }

    @GetMapping("/self")
    public ResponseEntity<List<VideoResponseDTO>> getUserVideos(
        @AuthenticationPrincipal User user
    ) {
        List<VideoResponseDTO> videos = videoRepository.findByOwner(user.getLogin())
            .stream().map(v -> getVideoResponse(v)).toList();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<VideoResponseDTO>> getUserSharedVideos(
        @AuthenticationPrincipal User user
    ) {
        List<VideoResponseDTO> videos = videoRepository.findBySharedAccessUser(user.getLogin())
            .stream().map(v -> getVideoResponse(v)).toList();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/public")
    public ResponseEntity<List<VideoResponseDTO>> getUserPublicVideos(
        @AuthenticationPrincipal User user
    ) {
        List<VideoResponseDTO> videos = videoRepository.findByKnowsPublicUser(user.getLogin())
            .stream().map(v -> getVideoResponse(v)).toList();

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/{id}/infos")
    public ResponseEntity<VideoResponseDTO> getVideoInfos(
        @PathVariable("id") String id,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException {
        VideoResponseDTO videoResponse = videoRepository.findById(id)
            .map(v -> getVideoResponse(v))
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (
            videoResponse.visibility().type() == Visibility.PRIVATE
            &&
            !videoResponse.ownerName().equals(user.getLogin())
        )
            throw new AccessDeniedException("User cant access this video");

        if (
            videoResponse.visibility().type() == Visibility.RESTRICTED
            &&
            !videoResponse.ownerName().equals(user.getLogin())
            &&
            !visibilityRepository.userHasShared(user.getId(), videoResponse.id())
        )
            throw new AccessDeniedException("User cant access this video");

        if (
            videoResponse.visibility().type() == Visibility.PUBLIC
            &&
            !videoResponse.ownerName().equals(user.getLogin())
            &&
            !visibilityRepository.userKnowsPublic(user.getId(), videoResponse.id())
        )
            visibilityRepository.userDiscoveredPublic(user.getId(), videoResponse.id());

        return ResponseEntity.ok(videoResponse);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getVideoResource(
        @PathVariable("id") String id,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (
            video.getVisibility() == Visibility.PRIVATE
            &&
            !video.getOwner().getId().equals(user.getId())
        )
            throw new AccessDeniedException("User cant access this video");

        if (
            video.getVisibility() == Visibility.RESTRICTED
            &&
            !video.getOwner().getId().equals(user.getId())
            &&
            !visibilityRepository.userHasShared(user.getId(), video.getId())
        )
            throw new AccessDeniedException("User cant access this video");

        if (
            video.getVisibility() == Visibility.PUBLIC
            &&
            !video.getOwner().getId().equals(user.getId())
            &&
            !visibilityRepository.userKnowsPublic(user.getId(), video.getId())
        )
            visibilityRepository.userDiscoveredPublic(user.getId(), video.getId());

        File videoFile = Paths.get(
            VIDEO_FOLDER,
            video.getPath() + getExtension(video.getType())
        ).toFile();

        Resource videoResource = new FileSystemResource(videoFile);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, video.getType())
            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(videoFile.length()))
            .header(HttpHeaders.ACCEPT_RANGES, "bytes")
            .body(videoResource);
    }

    @GetMapping("/{id}/thumb")
    public ResponseEntity<Resource> getVideoThumb(
        @PathVariable("id") String id,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (
            video.getVisibility() == Visibility.PRIVATE
            &&
            !video.getOwner().getId().equals(user.getId())
        )
            throw new AccessDeniedException("User cant access this video");

        if (
            video.getVisibility() == Visibility.RESTRICTED
            &&
            !video.getOwner().getId().equals(user.getId())
            &&
            !visibilityRepository.userHasShared(user.getId(), video.getId())
        )
            throw new AccessDeniedException("User cant access this video");

        if (
            video.getVisibility() == Visibility.PUBLIC
            &&
            !video.getOwner().getId().equals(user.getId())
            &&
            !visibilityRepository.userKnowsPublic(user.getId(), video.getId())
        )
            visibilityRepository.userDiscoveredPublic(user.getId(), video.getId());

        File file = Paths.get(THUMB_FOLDER, video.getPath() + ".jpg").toFile();

        Resource videoFile = new FileSystemResource(file);

        return ResponseEntity.ok().body(videoFile);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VideoResponseDTO> newVideo(
        @ModelAttribute @Valid NewVideoDTO request,
        @AuthenticationPrincipal User user,
        UriComponentsBuilder uriBuilder
    ) throws IllegalStateException, IOException {
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

        String baseFileName = user.getLogin() + "-" + UUID.randomUUID();
        String videoFileName = baseFileName + getExtension(request.video().getContentType());
        String thumbFileName = baseFileName + ".jpg";
        Path videoPath = Paths.get(VIDEO_FOLDER, videoFileName);

        Files.copy(
            request.video().getInputStream(),
            videoPath,
            StandardCopyOption.REPLACE_EXISTING
        );

        long duration = videoService.getDuration(videoPath.toFile());

        videoService.saveVideoThumbnail(videoPath.toFile(), duration, Paths.get(THUMB_FOLDER, thumbFileName).toFile());

        Video newVideo = videoRepository.save(
            VideoMapper.toEntity(
                request,
                baseFileName,
                request.video().getContentType(),
                duration,
                user
            )
        );

        URI uri = uriBuilder.path("/video/{id}/file")
            .buildAndExpand(newVideo.getId()).toUri();

        return ResponseEntity
            .created(uri)
            .body(getVideoResponse(newVideo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoUpdateDTO> updateVideo(
        @PathVariable("id") String id,
        @RequestBody VideoUpdateDTO request,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException, Exception {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (!video.getOwner().getLogin().equals(user.getLogin()))
            throw new AccessDeniedException("Only owners can update videos");

        video.update(request);

        Video sinked = videoRepository.sinkVideo(video);

        return ResponseEntity.ok(new VideoUpdateDTO(sinked.getTitle(), sinked.getDescription()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteVideo(@PathVariable("id") String id)
        throws IOException
    {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        videoRepository.delete(video);

        Path videoPath = Paths.get(VIDEO_FOLDER, video.getPath() + getExtension(video.getType()));
        Path thumbPath = Paths.get(THUMB_FOLDER, video.getPath() + ".jpg");

        Files.delete(videoPath);
        Files.delete(thumbPath);

        return ResponseEntity.noContent().build();
    }

    private String getExtension(String fileType) {
        return switch (fileType) {
            case "video/mp4" -> ".mp4";
            case "video/webm" -> ".webm";
            default -> throw new IllegalArgumentException("Supported file type: video/mp4 or video/webm");
        };
    }

    private VideoResponseDTO getVideoResponse(Video v) {
        List<VideoVisitantDBDTO> visitants = List.of();

        if (v.getVisibility() == Visibility.RESTRICTED)
            visitants = visibilityRepository.getVisitantsRestricted(v.getId());

        if (v.getVisibility() == Visibility.PUBLIC)
            visitants = visibilityRepository.getVisitantsPublic(v.getId());

        return VideoMapper.toResponse(v, visitants);
    }
}