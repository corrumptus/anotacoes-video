package corrumptus.anotacoes_video.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import corrumptus.anotacoes_video.dto.video.NewVideoDTO;
import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.entity.UserEntity;
import corrumptus.anotacoes_video.entity.VideoEntity;
import corrumptus.anotacoes_video.mapper.UserMapper;
import corrumptus.anotacoes_video.mapper.VideoMapper;
import corrumptus.anotacoes_video.model.Video;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.repository.VideoRepository;

@RestController
@RequestMapping("/video")
public class VideoRestController {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public Optional<VideoResponseDTO> getVideo(@PathVariable("id") String id) {
        return videoRepository.findById(id).map(VideoMapper::toResponseFromEntity);
    }

    @GetMapping
    public List<VideoResponseDTO> getUserVideos(@RequestParam(value = "user") String userId) {
        return videoRepository.findByOwner(userId)
            .stream().map(VideoMapper::toResponseFromEntity).toList();
    }

    @PostMapping
    public Optional<VideoResponseDTO> newVideo(@RequestBody NewVideoDTO request) {
        try {
            Optional<UserEntity> owner = userRepository.findById(request.ownerId());

            if (owner.isEmpty())
                throw new Exception("User " + request.ownerId() + " doesnt exists");

            if (request.video().isEmpty())
                throw new Exception("Video is empty");

            if (
                request.video().getContentType() != null
                &&
                (
                    request.video().getContentType() == "video/mp4"
                    ||
                    request.video().getContentType() == "video/webm"
                )
            )
                throw new Exception("Video must be of type mp4 or webm");

            int tamanhoMaximo = 100 * 1024 * 1024;

            if (request.video().getSize() > tamanhoMaximo)
                throw new Exception("Video is bigger than 100MB");

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

            return Optional.of(VideoMapper.toResponseFromEntity(newVideo));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteVideo(@PathVariable("id") String id) {
        videoRepository.deleteById(id);
    }
}