package corrumptus.anotacoes_video.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.repository.implementation.VideoRepositoryCustom;

public interface VideoRepository extends Neo4jRepository<Video, String>, VideoRepositoryCustom {
    @Query("""
        MATCH (video:VIDEO)
        WHERE video.id = $id
        DETACH DELETE video
    """)
    public void deleteById(@Param("id") @NonNull String id);
}
