package corrumptus.anotacoes_video.dto.video;

import corrumptus.anotacoes_video.entity.Visibility;
import jakarta.validation.constraints.NotNull;

public record VideoVisibilityUpdateDTO(
    @NotNull(message = "The visibility field is missing")
    Visibility visibility
) {
    
}
