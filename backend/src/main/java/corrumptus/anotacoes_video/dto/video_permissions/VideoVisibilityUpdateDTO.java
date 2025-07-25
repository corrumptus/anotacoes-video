package corrumptus.anotacoes_video.dto.video_permissions;

import jakarta.validation.constraints.NotNull;

import corrumptus.anotacoes_video.entity.Visibility;

public record VideoVisibilityUpdateDTO(
    @NotNull(message = "The visibility field is missing")
    Visibility visibility
) {
    
}
