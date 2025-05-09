package corrumptus.anotacoes_video.dto.video;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public record NewVideoDTO(
    MultipartFile video,

    @NotBlank
    String title,

    @NotBlank
    String description,
    
    @NotBlank
    String ownerId
) {
    
}
