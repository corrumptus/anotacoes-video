package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RelationshipProperties
@AllArgsConstructor
@Getter
public class Anotation {
    @RelationshipId
    private String id;

    private User user;

    @TargetNode
    private Video video;

    private String anotation;

    private long videoInstant;
}
