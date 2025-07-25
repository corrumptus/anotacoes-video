package corrumptus.anotacoes_video.dto.anotation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record NewAnotationDTO(
    @NotNull(message = "The anotation field is missing")
    @NotBlank(message = "The anotation field must be a non-empty string")
    String anotation,

    @NotNull(message = "The videoInstant field must be a number")
    @PositiveOrZero(message = "The videoInstant field must not be negative")
    long videoInstant
) {
    
}
