package corrumptus.anotacoes_video.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import corrumptus.anotacoes_video.dto.video.NewVideoDTO;
import corrumptus.anotacoes_video.dto.video.VideoResponseDTO;
import corrumptus.anotacoes_video.dto.video_permissions.VideoVisibilityResponseDTO;
import corrumptus.anotacoes_video.dto.video_permissions.VideoVisitantDBDTO;
import corrumptus.anotacoes_video.dto.video_permissions.VideoVisitantResponseDTO;
import corrumptus.anotacoes_video.entity.User;
import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.entity.Visibility;

public class VideoMapper {
    public static Map<String, Object> toMap(Video video) {
        Map<String, Object> videoMap = new HashMap<>();

        videoMap.put("id", video.getId());
        videoMap.put("path", video.getPath());
        videoMap.put("owner", UserMapper.toMap(video.getOwner()));
        videoMap.put("title", video.getTitle());
        videoMap.put("description", video.getDescription());
        videoMap.put("type", video.getType());
        videoMap.put("duration", video.getDuration());
        videoMap.put("visibility", video.getVisibility().toString());

        return videoMap;
    }

    public static Value toValue(Video video) {
        return Values.value(toMap(video));
    }

    public static Video toEntity(Map<String, Object> videoMap, Map<String, Object> ownerMap) {
        if (videoMap == null)
            return null;

        if (ownerMap == null)
            return null;

        User owner = UserMapper.toEntity(ownerMap);

        return new Video(
            (String) videoMap.get("id"),
            (String) videoMap.get("path"),
            owner,
            (String) videoMap.get("title"),
            (String) videoMap.get("description"),
            (String) videoMap.get("type"),
            (Long) videoMap.get("duration"),
            Visibility.valueOf((String) videoMap.get("visibility")),
            (Boolean) videoMap.get("canAnotate")
        );
    }

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
            Visibility.PRIVATE,
            null
        );
    }

    public static VideoResponseDTO toResponse(Video model, List<VideoVisitantDBDTO> visitants) {
        if (model == null)
            return null;

        List<VideoVisitantResponseDTO> canAnotateVisitants = null;
        List<VideoVisitantResponseDTO> cantAnotateVisitants = null;

        switch (model.getVisibility()) {
            case Visibility.PRIVATE:
                break;
            case Visibility.RESTRICTED:
                canAnotateVisitants =
                    visitants.stream()
                        .filter(v -> v.canAnotate())
                        .map(v -> new VideoVisitantResponseDTO(v.name()))
                        .toList();
                cantAnotateVisitants =
                    visitants.stream()
                        .filter(v -> !v.canAnotate())
                        .map(v -> new VideoVisitantResponseDTO(v.name()))
                        .toList();
                break;
            case Visibility.PUBLIC:
                if (model.getPublicCanAnotate() == true)
                    canAnotateVisitants =
                        visitants.stream()
                            .map(v -> new VideoVisitantResponseDTO(v.name()))
                            .toList();
                else
                    cantAnotateVisitants =
                        visitants.stream()
                            .map(v -> new VideoVisitantResponseDTO(v.name()))
                            .toList();
                break;
        }

        return new VideoResponseDTO(
            model.getId(),
            model.getOwner().getLogin(),
            model.getTitle(),
            model.getDescription(),
            model.getDuration(),
            new VideoVisibilityResponseDTO(
                model.getVisibility(),
                canAnotateVisitants,
                cantAnotateVisitants
            )
        );
    }
}
