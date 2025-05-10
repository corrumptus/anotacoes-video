package corrumptus.anotacoes_video.mapper;

import corrumptus.anotacoes_video.dto.anotation.AnotationResponseDTO;
import corrumptus.anotacoes_video.entity.AnotationEntity;
import corrumptus.anotacoes_video.model.Anotation;

public class AnotationMapper {
    public static Anotation toModel(AnotationEntity entity) {
        if (entity == null)
            return null;

        return new Anotation(
            entity.getId(),
            UserMapper.toModel(entity.getUser()),
            VideoMapper.toModel(entity.getVideo()),
            entity.getAnotation(),
            entity.getVideoInstant()
        );
    }

    public static AnotationEntity toEntity(Anotation model) {
        if (model == null)
            return null;

        return new AnotationEntity(
            model.getId(),
            UserMapper.toEntity(model.getUser()),
            VideoMapper.toEntity(model.getVideo()),
            model.getAnotation(),
            model.getVideoInstant()
        );
    }

    public static AnotationResponseDTO toResponseFromEntity(AnotationEntity entity) {
        if (entity == null)
            return null;

        return new AnotationResponseDTO(
            entity.getId(),
            entity.getUser().getId(),
            entity.getVideo().getId(),
            entity.getAnotation(),
            entity.getVideoInstant()
        );
    }

    public static AnotationResponseDTO toResponseFromModel(Anotation model) {
        if (model == null)
            return null;

        return new AnotationResponseDTO(
            model.getId(),
            model.getUser().getId(),
            model.getVideo().getId(),
            model.getAnotation(),
            model.getVideoInstant()
        );
    }
}
