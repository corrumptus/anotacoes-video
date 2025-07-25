package corrumptus.anotacoes_video.dto.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewUserDTO(
    @NotNull(message = "The login field is missing")
    @NotBlank(message = "The login field must be a non-empty string")
    String login,

    @NotNull(message = "The password field is missing")
    @NotBlank(message = "The password field must be a non-empty string")
    String password,

    MultipartFile profilePic
) {
    
}
