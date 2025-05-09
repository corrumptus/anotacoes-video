package corrumptus.anotacoes_video.mapper;

import corrumptus.anotacoes_video.entity.UserEntity;
import corrumptus.anotacoes_video.model.User;

public class UserMapper {
    public static UserEntity toEntity(User model) {
        if (model == null)
            return null;

        return new UserEntity(
            model.getId(),
            model.getLogin(),
            model.getPassword(),
            model.getName(),
            model.getProfilePicPath()
        );
    }

    public static User toModel(UserEntity entity) {
        if (entity == null)
            return null;

        return new User(
            entity.getId(),
            entity.getLogin(),
            entity.getPassword(),
            entity.getName(),
            entity.getProfilePicPath()
        );
    }
}
