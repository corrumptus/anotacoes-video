package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import corrumptus.anotacoes_video.dto.video.VideoUpdateDTO;

@Node("VIDEO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Video {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String path;

    @Relationship(value = "OWNS", direction = Direction.INCOMING)
    private User owner;

    private String title;

    private String description;

    private String type;

    private long duration;

    @Setter
    private Visibility visibility;

    @Setter
    private Boolean publicCanAnotate;

    public void update(VideoUpdateDTO dto) {
        if (dto.title() != null)
            this.title = dto.title();

        if (dto.description() != null)
            this.description = dto.description();
    }
}
