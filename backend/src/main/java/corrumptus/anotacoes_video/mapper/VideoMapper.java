package corrumptus.anotacoes_video.mapper;

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
}
