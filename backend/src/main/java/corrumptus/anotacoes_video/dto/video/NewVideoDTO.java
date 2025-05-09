package corrumptus.anotacoes_video.dto.video;

import org.springframework.web.multipart.MultipartFile;

public record NewVideoDTO(
    MultipartFile video,
    String title,
    String description,
    String ownerId
) {
    
}
