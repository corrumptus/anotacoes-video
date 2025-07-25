package corrumptus.anotacoes_video.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import corrumptus.anotacoes_video.dto.anotation.AnotationResponseDTO;
import corrumptus.anotacoes_video.dto.anotation.AnotationUpdateDTO;
import corrumptus.anotacoes_video.dto.anotation.NewAnotationDTO;
import corrumptus.anotacoes_video.entity.Anotation;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.entity.Visibility;
import corrumptus.anotacoes_video.mapper.AnotationMapper;
import corrumptus.anotacoes_video.repository.AnotationRepository;
import corrumptus.anotacoes_video.repository.VideoRepository;
import corrumptus.anotacoes_video.repository.VideoVisibilityRepository;

@RestController
@RequestMapping("/video/{videoId}/anotation")
public class AnotationRestController {
    @Autowired
    private AnotationRepository anotationRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoVisibilityRepository visibilityRepository;

    @GetMapping
    public ResponseEntity<List<AnotationResponseDTO>> getVideoAnotations(
        @PathVariable("videoId") String videoId
    ) {
        List<AnotationResponseDTO> anotations = anotationRepository.findAllByVideoId(videoId)
            .stream().map(AnotationMapper::toResponse).toList();

        return ResponseEntity.ok(anotations);
    }

    @PostMapping
    public ResponseEntity<AnotationResponseDTO> newAnotation(
        @PathVariable("videoId") String videoId,
        @RequestBody @Valid NewAnotationDTO request,
        @AuthenticationPrincipal User user
    ) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (request.videoInstant() > video.getDuration())
            throw new IllegalArgumentException("Video has less time than the anotation video instant");

        if (
            video.getVisibility() == Visibility.PRIVATE
            &&
            !video.getOwner().getLogin().equals(user.getLogin())
        )
            throw new AccessDeniedException("Only owners can create a new anotation");

        if (
            video.getVisibility() == Visibility.RESTRICTED
            &&
            !visibilityRepository.getUserCanAnotateRestricted(video.getId(), user.getLogin())
        )
            throw new AccessDeniedException(String.format("User %s can't anotate in this video", user.getLogin()));

        if (
            video.getVisibility() == Visibility.PUBLIC
            &&
            !video.getPublicCanAnotate()
        )
            throw new AccessDeniedException("Users can't anotate in this video");

        Anotation newAnotation = anotationRepository.save(
            AnotationMapper.toEntity(request, user, video)
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(AnotationMapper.toResponse(newAnotation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateAnotation(
        @PathVariable("id") String id,
        @PathVariable("videoId") String videoId,
        @RequestBody AnotationUpdateDTO request,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException, Exception {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        Anotation anotation = anotationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Anotation doesn't exist"));

        if (!anotation.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("Only owners can update anotations");

        if (request.anotation() == null || request.anotation().isBlank())
            throw new IllegalArgumentException("");

        if (request.videoInstant() > video.getDuration())
            throw  new IllegalArgumentException("Video has less time than the anotation video instant");

        anotation.update(request);

        Anotation sinkedAnotation = anotationRepository.sinkAnotation(anotation);

        return ResponseEntity.ok(AnotationMapper.toResponse(sinkedAnotation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAnotation(
        @PathVariable("id") String id,
        @PathVariable("videoId") String videoId,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        Anotation anotation = anotationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Anotation doesn't exist"));

        if (
            !video.getOwner().getLogin().equals(user.getLogin())
            &&
            !anotation.getUser().getLogin().equals(user.getLogin())
        )
            throw new AccessDeniedException("Only video owner or anotation owner can delete anotations");

        anotationRepository.deleteById(videoId, id);

        return ResponseEntity.noContent().build();
    }
}
