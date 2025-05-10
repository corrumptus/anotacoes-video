package corrumptus.anotacoes_video.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import corrumptus.anotacoes_video.entity.Anotation;

public interface AnotationRepository extends Neo4jRepository<Anotation, String> {
    @Override
    @Query("""
        MATCH
            (user:USER { id: $anotation.user.id }),
            (video:VIDEO { id: $anotation.video.id })
        CREATE (user) - [anotation:ANOTATION {
            id: $anotation.id,
            anotation: $anotation.anotation,
            videoInstant: $anotation.videoInstant
        }] -> (video)
        RETURN
            anotation.id as id,
            collect(user) as user,
            collect(video) as video,
            anotation.anotation as anotation,
            anotation.videoInstant as videoInstant
    """)
    @NonNull
    public <A extends Anotation> A save(@Param("anotation") @NonNull A anotation);

    @Query("""
       MATCH (user:USER) - [anotation:ANOTATION] -> (video:VIDEO { id: $videoId })
       RETURN
            anotation.id as id,
            collect(user) as user,
            collect(video) as video,
            anotation.anotation as anotation,
            anotation.videoInstant as videoInstant
    """)
    public List<Anotation> findAllByVideoId(@Param("videoId") String videoId);

    @Query("""
        MATCH (:USER) - [anotation:ANOTATION { id: $id }] -> (:VIDEO)
        DELETE anotation
    """)
    public void deleteById(@Param("id") @NonNull String id);
}
