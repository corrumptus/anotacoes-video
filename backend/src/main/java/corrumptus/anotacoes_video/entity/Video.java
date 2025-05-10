package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Node("VIDEO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String path;

    @Relationship(value = "OWNS")
    private User owner;

    private String title;

    private String description;

    private String type;

    private long duration;

    private VideoVisibility visibility;

    private boolean visitantCanAnotate;

    public void setVisibility(VideoVisibility visibility) {
        this.visibility = visibility;
    }

    public void setVisitantCanAnotate(boolean visitantCanAnotate) {
        this.visitantCanAnotate = visitantCanAnotate;
    }
}
