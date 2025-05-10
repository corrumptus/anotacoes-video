package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RelationshipProperties
@AllArgsConstructor
@Getter
public class AnotationEntity {
    @RelationshipId
    private String id;

    private UserEntity user;

    @TargetNode
    private VideoEntity video;

    private String anotation;

    private long videoInstant;
}
