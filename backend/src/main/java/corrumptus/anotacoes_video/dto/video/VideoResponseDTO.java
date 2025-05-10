package corrumptus.anotacoes_video.dto.video;

import corrumptus.anotacoes_video.entity.VideoVisibility;

public record VideoResponseDTO(
    String id,
    String path,
    String ownerId,
    String ownerName,
    String ownerProfilePicPath,
    String title,
    String description,
    VideoVisibility visibility,
    boolean visitantCanAnotate
) {
    
}
