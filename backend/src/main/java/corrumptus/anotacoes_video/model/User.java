package corrumptus.anotacoes_video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private String id;
    private String name;
    private String password;
    private String profilePicPath;

    public User(String name, String password, String profilePicPath) {
        this.id = null;
        this.name = name;
        this.password = password;
        this.profilePicPath = profilePicPath;
    }
}
