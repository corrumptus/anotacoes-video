package corrumptus.anotacoes_video.mapper;

import corrumptus.anotacoes_video.dto.user.NewUserDTO;
import corrumptus.anotacoes_video.entity.User;

public class UserMapper {
    public static User toEntity(NewUserDTO dto, String profilePicPath) {
        if (dto == null)
            return null;

        return new User(
            null,
            dto.login(),
            dto.password(),
            dto.name(),
            profilePicPath
        );
    }
}
