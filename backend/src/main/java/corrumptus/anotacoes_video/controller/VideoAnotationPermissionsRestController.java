package corrumptus.anotacoes_video.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import corrumptus.anotacoes_video.dto.video.NewSharedUserDTO;
import corrumptus.anotacoes_video.dto.video.VideoCanAnotateUpdateDTO;
import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.dto.video.VideoVisibilityUpdateDTO;
import corrumptus.anotacoes_video.dto.video_permissions.VideoVisibilityResponseDTO;
import corrumptus.anotacoes_video.dto.video_permissions.VideoVisitantDBDTO;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.entity.Visibility;
import corrumptus.anotacoes_video.mapper.VideoMapper;
import corrumptus.anotacoes_video.repository.UserRepository;
import corrumptus.anotacoes_video.repository.VideoRepository;
import corrumptus.anotacoes_video.repository.VideoVisibilityRepository;

@RestController
@RequestMapping("/video/{id}/visibility")
public class VideoAnotationPermissionsRestController {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoVisibilityRepository visibilityRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<VideoVisibilityResponseDTO> addUserWhenRestricted(
        @PathVariable("id") String id,
        @AuthenticationPrincipal User user,
        @RequestBody @Valid NewSharedUserDTO request
    ) throws AccessDeniedException {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (!video.getOwner().getLogin().equals(user.getLogin()))
            throw new AccessDeniedException("Only owners can share videos");

        if (video.getVisibility() != Visibility.RESTRICTED)
            throw new IllegalArgumentException("Only videos in restricted mode can be shared");

        userRepository.findByLogin(request.userLogin())
            .orElseThrow(() -> new EntityNotFoundException(String.format("User %s doesn't exist", request.userLogin())));

        visibilityRepository.addUserInSharedVideo(id, request.userLogin());

        return ResponseEntity.ok(getVideoResponse(video).visibility());
    }

    @PutMapping
    @Transactional
    public ResponseEntity<VideoVisibilityResponseDTO> updateVideoVisibility(
        @PathVariable("id") String id,
        @RequestBody VideoVisibilityUpdateDTO request,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException, Exception {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (!video.getOwner().getLogin().equals(user.getLogin()))
            throw new AccessDeniedException("Only owners can update videos");

        video.setVisibility(request.visibility());

        switch (request.visibility()) {
            case Visibility.PRIVATE:
                visibilityRepository.turnVideoRelashionshipPrivate(video.getId());
                break;
            case Visibility.RESTRICTED:
                visibilityRepository.turnVideoRelashionshipRestricted(video.getId());
                break;
            case Visibility.PUBLIC:
                visibilityRepository.turnVideoRelashionshipPublic(video.getId());
                break;
        }

        Video sinked = videoRepository.sinkVideo(video);

        return ResponseEntity.ok(getVideoResponse(sinked).visibility());
    }

    @PutMapping("/canAnotate")
    public ResponseEntity<VideoVisibilityResponseDTO> updateVideoCanAnotate(
        @PathVariable("id") String id,
        @RequestBody VideoCanAnotateUpdateDTO request,
        @AuthenticationPrincipal User user
    ) throws AccessDeniedException {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (!video.getOwner().getLogin().equals(user.getLogin()))
            throw new AccessDeniedException("Only owners can update videos");

        if (video.getVisibility() == Visibility.PRIVATE)
            throw new IllegalArgumentException("Private videos cant change the anotability");

        if (
            video.getVisibility() == Visibility.RESTRICTED
            &&
            request.visitant() == null
        )
            throw new IllegalArgumentException("Must specify the visitant");

        if (
            video.getVisibility() == Visibility.PUBLIC
            &&
            request.visitant() != null
        )
            throw new IllegalArgumentException("Cannot specify a visitant in public mode");

        if (video.getVisibility() == Visibility.RESTRICTED)
            visibilityRepository.changeRestrictedCanAnotate(video.getId(), request.visitant(), request.canAnotate());

        if (video.getVisibility() == Visibility.PUBLIC) {
            video.setPublicCanAnotate(request.canAnotate());
            visibilityRepository.changePublicCanAnotate(video.getId(), request.canAnotate());
        }

        return ResponseEntity.ok(getVideoResponse(video).visibility());
    }

    @DeleteMapping("/users/{login}")
    public ResponseEntity<VideoVisibilityResponseDTO> deleteUser(
        @PathVariable("id") String id,
        @AuthenticationPrincipal User user,
        @PathVariable("login") String deleteName
    ) throws AccessDeniedException {
        Video video = videoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Video doesn't exist"));

        if (!video.getOwner().getLogin().equals(user.getLogin()))
            throw new AccessDeniedException("Only owners can remove users from the share list");

        if (video.getVisibility() != Visibility.RESTRICTED)
            throw new IllegalArgumentException("Only videos in restricted mode have managable list of users");

        userRepository.findByLogin(deleteName)
            .orElseThrow(() -> new EntityNotFoundException(String.format("User %s doesn't exist", deleteName)));

        visibilityRepository.removeUserInSharedVideo(id, deleteName);

        return ResponseEntity.ok(getVideoResponse(video).visibility());
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
