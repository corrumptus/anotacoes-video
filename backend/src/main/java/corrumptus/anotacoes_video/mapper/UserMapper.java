package corrumptus.anotacoes_video.mapper;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import corrumptus.anotacoes_video.dto.user.NewUserDTO;
import corrumptus.anotacoes_video.entity.User;

public class UserMapper {
    public static Map<String, Object> toMap(User user) {
        Map<String, Object> videoMap = new HashMap<>();

        videoMap.put("id", user.getId());
        videoMap.put("login", user.getLogin());
        videoMap.put("password", user.getPassword());
        videoMap.put("profilePicPath", user.getProfilePicPath());
        videoMap.put("profilePicType", user.getProfilePicType());

        return videoMap;
    }

    public static Value toValue(User user) {
        return Values.value(toMap(user));
    }

    public static User toEntity(Map<String, Object> map) {
        if (map == null)
            return null;

        return new User(
            (String) map.get("id"),
            (String) map.get("login"),
            (String) map.get("password"),
            (String) map.get("profilePicPath"),
            (String) map.get("profilePicType")
        );
    }

    public static User toEntity(NewUserDTO dto, String profilePicPath, String type) {
        if (dto == null)
            return null;

        return new User(
            null,
            dto.login(),
            dto.password(),
            profilePicPath,
            type
        );
    }

    public static NewUserDTO encodePassword(NewUserDTO dto, String encodedPassword) {
        return new NewUserDTO(dto.login(), encodedPassword, dto.profilePic());
    }
}
