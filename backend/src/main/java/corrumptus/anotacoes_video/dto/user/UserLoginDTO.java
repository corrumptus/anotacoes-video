package corrumptus.anotacoes_video.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
    @NotBlank
    String login,

    @NotBlank
    String password
) {
    
}
