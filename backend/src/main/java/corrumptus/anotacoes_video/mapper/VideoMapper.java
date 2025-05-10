package corrumptus.anotacoes_video.mapper;

import corrumptus.anotacoes_video.dto.video.NewVideoDTO;
import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.entity.VideoVisibility;

public class VideoMapper {
    public static Video toEntity(
        NewVideoDTO model,
        String path,
        String type,
        long duration,
        User user
    ) {
        if (model == null)
            return null;

        return new Video(
            null,
            path,
            user,
            model.title(),
            model.description(),
            type,
            duration,
            VideoVisibility.PRIVATE,
            false
        );
    }

    public static VideoResponseDTO toResponse(Video model) {
        if (model == null)
            return null;

        return new VideoResponseDTO(
            model.getId(),
            model.getOwner().getId(),
            model.getOwner().getName(),
            model.getOwner().getProfilePicPath(),
            model.getTitle(),
            model.getDescription(),
            model.getType(),
            model.getDuration(),
            model.getVisibility(),
            model.isVisitantCanAnotate()
        );
    }
}
