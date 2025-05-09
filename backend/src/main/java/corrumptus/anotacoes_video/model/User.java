package corrumptus.anotacoes_video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private String id;
    private String login;
    private String password;
    private String name;
    private String profilePicPath;

    public User(String login, String password, String name, String profilePicPath) {
        this.id = null;
        this.login = login;
        this.password = password;
        this.name = name;
        this.profilePicPath = profilePicPath;
    }
}
