package corrumptus.anotacoes_video.dto.anotation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record NewAnotationDTO(
    @NotBlank
    String userId,

    @NotBlank
    String videoId,

    @NotBlank
    String anotation,

    @NotNull
    @PositiveOrZero
    long videoInstant
) {
    
}
