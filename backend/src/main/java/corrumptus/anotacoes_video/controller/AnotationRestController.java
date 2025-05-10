package corrumptus.anotacoes_video.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import corrumptus.anotacoes_video.dto.anotation.AnotationResponseDTO;
import corrumptus.anotacoes_video.dto.anotation.NewAnotationDTO;
import corrumptus.anotacoes_video.entity.Anotation;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.mapper.AnotationMapper;
import corrumptus.anotacoes_video.mapper.UserMapper;
import corrumptus.anotacoes_video.mapper.VideoMapper;
import corrumptus.anotacoes_video.repository.AnotationRepository;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/anotation")
public class AnotationRestController {
    @Autowired
    private AnotationRepository anotationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @GetMapping
    public ResponseEntity<List<AnotationResponseDTO>> getVideoAnotations(
        @RequestParam("video") String videoId
    ) {
        List<AnotationResponseDTO> anotations = anotationRepository.findAllByVideoId(videoId)
            .stream().map(AnotationMapper::toResponse).toList();

        if (anotations.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok(anotations);
    }

    @PostMapping
    public ResponseEntity<AnotationResponseDTO> newAnotation(
        @RequestBody @Valid NewAnotationDTO request
    ) {
        Optional<User> user = userRepository.findById(request.userId());

        if (user.isEmpty())
            throw new EntityNotFoundException("User doesnt exists");

        Optional<Video> video = videoRepository.findById(request.videoId());

        if (video.isEmpty())
            throw new EntityNotFoundException("Video doesnt exists");

        if (request.videoInstant() > video.get().getTime())
            throw new IllegalArgumentException("Video has less time than the anotation video instant");

        Anotation newAnotation = anotationRepository.save(
            AnotationMapper.toEntity(request, user.get(), video.get())
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AnotationMapper.toResponse(newAnotation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnotation(@PathVariable("id") String id) {
        anotationRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
