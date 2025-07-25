package corrumptus.anotacoes_video.dto.video_permissions;

import java.util.List;

import corrumptus.anotacoes_video.entity.Visibility;

public record VideoVisibilityResponseDTO(
    Visibility type,
    List<VideoVisitantResponseDTO> canAnotateVisitants,
    List<VideoVisitantResponseDTO> canNotAnotateVisitants
) {
    
}
