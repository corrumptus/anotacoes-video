package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RelationshipProperties
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class VideoVisibility {
    @RelationshipId
    private String id;

    private User user;

    @TargetNode
    private Video video;

    private boolean canAnotate;
}
