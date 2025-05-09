package corrumptus.anotacoes_video.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import corrumptus.anotacoes_video.entity.VideoEntity;

public interface VideoRepository extends Neo4jRepository<VideoEntity, String> {
    @Query("""
        MATCH (video:VIDEO) <- [:OWNS] - (owner:USER)
        WHERE owner.id = $id
        RETURN video
    """)
    public List<VideoEntity> findByOwner(@Param("id") String id);

    @Query("""
        MATCH (video:VIDEO)
        WHERE video.id = $id
        DETACH DELETE video
    """)
    public void deleteById(@Param("id") @NonNull String id);
}
