package corrumptus.anotacoes_video.dto.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public record NewUserDTO(
    @NotBlank
    String login,

    @NotBlank
    String password,

    @NotBlank
    String name,

    MultipartFile profilePic
) {
    
}
