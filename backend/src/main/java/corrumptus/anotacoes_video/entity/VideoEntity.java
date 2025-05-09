package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import corrumptus.anotacoes_video.model.VideoVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Node("VIDEO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoEntity {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    private String path;

    @Relationship(value = "OWNS")
    private UserEntity owner;

    private String title;

    private String description;

    private VideoVisibility visibility;

    private boolean visitantCanAnotate;
}
