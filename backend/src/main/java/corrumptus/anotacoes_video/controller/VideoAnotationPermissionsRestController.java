package corrumptus.anotacoes_video.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import corrumptus.anotacoes_video.dto.video_permissions.UpdateVideoAnotability;
import corrumptus.anotacoes_video.dto.video_permissions.UpdateVideoVisibility;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.entity.VideoVisibility;
import corrumptus.anotacoes_video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/video-permissions")
public class VideoAnotationPermissionsRestController {
    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/{id}/visibility")
    public ResponseEntity<Object> changeVideoVisibility(
        @PathVariable("id") String videoId,
        @AuthenticationPrincipal User user,
        @RequestBody UpdateVideoVisibility request
    ) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new EntityNotFoundException("Video doesnt exists"));

        if (video.getOwner() != user)
            throw new AccessDeniedException("User cant change the visibility of this video");

        video.setVisibility(request.visibility());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/anotable")
    public ResponseEntity<Object> makeNonPrivateVideoAnotable(
        @PathVariable("id") String videoId,
        @AuthenticationPrincipal User user,
        @RequestBody UpdateVideoAnotability request
    ) {
        Video video = videoRepository.findById(videoId)
            .orElseThrow(() -> new EntityNotFoundException("Video doesnt exists"));

        if (video.getOwner() != user)
            throw new AccessDeniedException("User cant change the visibility of this video");

        if (video.getVisibility() == VideoVisibility.PRIVATE)
            throw new IllegalArgumentException("Cant change the video anotability");

        video.setVisitantCanAnotate(request.visitantCanAnotate());

        return ResponseEntity.ok().build();
    }
}
