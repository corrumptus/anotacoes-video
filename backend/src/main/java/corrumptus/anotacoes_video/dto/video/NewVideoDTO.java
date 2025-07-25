package corrumptus.anotacoes_video.dto.video;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewVideoDTO(
    @NotNull(message = "The title field is missing")
    @NotBlank(message = "The title field must be a non-empty string")
    String title,

    @NotNull(message = "The description field is missing")
    @NotBlank(message = "The description field must be a non-empty string")
    String description,

    @NotNull(message = "The video field is missing")
    MultipartFile video
) {
    
}
