package corrumptus.anotacoes_video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Anotation {
    private String id;
    private User user;
    private Video video;
    private String anotation;
    private long videoInstant;

    public Anotation(User user, Video video, String anotation, long videoInstant) {
        this.id = null;
        this.user = user;
        this.video = video;
        this.anotation = anotation;
        this.videoInstant = videoInstant;
    }
}
