package corrumptus.anotacoes_video.dto.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewSharedUserDTO(
    @NotNull(message = "The userLogin field is missing")
    @NotBlank(message = "The userLogin field must be a non-empty string")
    String userLogin
) {
    
}
