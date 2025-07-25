package corrumptus.anotacoes_video.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import corrumptus.anotacoes_video.dto.anotation.AnotationUpdateDTO;

@RelationshipProperties
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Anotation {
    @RelationshipId
    @GeneratedValue
    private String id;

    private User user;

    @TargetNode
    private Video video;

    private String anotation;

    private long videoInstant;

    public void update(AnotationUpdateDTO dto) {
        if (dto.anotation() != null) {
            this.anotation = dto.anotation();
        }

        if (dto.videoInstant() != null) {
            this.videoInstant = dto.videoInstant();
        }
    }
}
