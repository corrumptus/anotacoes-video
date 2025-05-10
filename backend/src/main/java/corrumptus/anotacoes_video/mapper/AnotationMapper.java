package corrumptus.anotacoes_video.mapper;

import corrumptus.anotacoes_video.dto.anotation.AnotationResponseDTO;
import corrumptus.anotacoes_video.dto.anotation.NewAnotationDTO;
import corrumptus.anotacoes_video.entity.Anotation;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;

public class AnotationMapper {
    public static Anotation toEntity(NewAnotationDTO dto, User user, Video video) {
        if (dto == null)
            return null;

        return new Anotation(
            null,
            user,
            video,
            dto.anotation(),
            dto.videoInstant()
        );
    }

    public static AnotationResponseDTO toResponse(Anotation entity) {
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
}
