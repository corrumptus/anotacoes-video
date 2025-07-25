package corrumptus.anotacoes_video.dto.video;

import corrumptus.anotacoes_video.dto.video_permissions.VideoVisibilityResponseDTO;

public record VideoResponseDTO(
    String id,
    String ownerName,
    String title,
    String description,
    long duration,
    VideoVisibilityResponseDTO visibility
) {
    
}
