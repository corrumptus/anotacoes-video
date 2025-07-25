package corrumptus.anotacoes_video.dto.video_permissions;

import jakarta.validation.constraints.NotNull;

public record VideoAnotabilityUpdateDTO(
    @NotNull(message = "The visitantCanAnotate field is missing")
    boolean visitantCanAnotate
) {
    
}
