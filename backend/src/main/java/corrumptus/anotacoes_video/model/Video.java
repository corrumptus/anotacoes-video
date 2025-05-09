package corrumptus.anotacoes_video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Video {
    private String id;
    private String path;
    private User owner;
    private String title;
    private String description;
    private VideoVisibility visibility;
    private boolean visitantCanAnotate;

    public Video(String path, User owner, String title, String description) {
        this.id = null;
        this.path = path;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.visibility = VideoVisibility.PRIVATE;
        this.visitantCanAnotate = false;
    }
}
