package corrumptus.anotacoes_video.mapper;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import corrumptus.anotacoes_video.dto.anotation.AnotationResponseDTO;
import corrumptus.anotacoes_video.dto.anotation.NewAnotationDTO;
import corrumptus.anotacoes_video.entity.Anotation;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;

public class AnotationMapper {
    public static Map<String, Object> toMap(Anotation anotation) {
        Map<String, Object> anotationMap = new HashMap<>();

        anotationMap.put("id", anotation.getId());
        anotationMap.put("anotation", anotation.getAnotation());
        anotationMap.put("videoInstant", anotation.getVideoInstant());
        anotationMap.put("video", VideoMapper.toMap(anotation.getVideo()));
        anotationMap.put("user", UserMapper.toMap(anotation.getUser()));

        return anotationMap;
    }

    public static Value toValue(Anotation anotation, String id) {
        Map<String, Object> map = toMap(anotation);

        map.replace("id", id);

        return Values.value(map);
    }
    
    public static Anotation toEntity(
        Map<String, Object> anotationMap,
        Map<String, Object> userMap,
        Map<String, Object> videoMap,
        Map<String, Object> videoOwnerMap
    ) {
        if (anotationMap == null)
            return null;

        if (userMap == null)
            return null;

        if (videoMap == null)
            return null;

        if (videoOwnerMap == null)
            return null;

        User user = UserMapper.toEntity(userMap);

        Video video = VideoMapper.toEntity(videoMap, videoOwnerMap);

        return new Anotation(
            (String) anotationMap.get("id"),
            user,
            video,
            (String) anotationMap.get("anotation"),
            (Long) anotationMap.get("videoInstant")
        );
    }
    
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
            entity.getUser().getLogin(),
            entity.getVideo().getId(),
            entity.getAnotation(),
            entity.getVideoInstant()
        );
    }
}
