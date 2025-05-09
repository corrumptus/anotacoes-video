package corrumptus.anotacoes_video.mapper;

import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.entity.VideoEntity;
import corrumptus.anotacoes_video.model.Video;

public class VideoMapper {
    public static VideoEntity toEntity(Video model) {
        if (model == null)
            return null;

        return new VideoEntity(
            model.getId(),
            model.getPath(),
            UserMapper.toEntity(model.getOwner()),
            model.getTitle(),
            model.getDescription(),
            model.getVisibility(),
            model.isVisitantCanAnotate()
        );
    }

    public static Video toModel(VideoEntity entity) {
        if (entity == null)
            return null;

        return new Video(
            entity.getId(),
            entity.getPath(),
            UserMapper.toModel(entity.getOwner()),
            entity.getTitle(),
            entity.getDescription(),
            entity.getVisibility(),
            entity.isVisitantCanAnotate()
        );
    }

    public static VideoResponseDTO toResponseFromModel(Video model) {
        if (model == null)
            return null;

        return new VideoResponseDTO(
            model.getId(),
            model.getPath(),
            model.getOwner().getId(),
            model.getOwner().getName(),
            model.getOwner().getProfilePicPath(),
            model.getTitle(),
            model.getDescription(),
            model.getVisibility(),
            model.isVisitantCanAnotate()
        );
    }
    
    public static VideoResponseDTO toResponseFromEntity(VideoEntity entity) {
        if (entity == null)
            return null;

        return new VideoResponseDTO(
            entity.getId(),
            entity.getPath(),
            entity.getOwner().getId(),
            entity.getOwner().getName(),
            entity.getOwner().getProfilePicPath(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getVisibility(),
            entity.isVisitantCanAnotate()
        );
    }
}
