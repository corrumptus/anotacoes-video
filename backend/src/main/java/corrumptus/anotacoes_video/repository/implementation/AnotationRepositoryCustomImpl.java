package corrumptus.anotacoes_video.repository.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.lang.NonNull;

import corrumptus.anotacoes_video.entity.Anotation;
import corrumptus.anotacoes_video.mapper.AnotationMapper;

public class AnotationRepositoryCustomImpl implements AnotationRepositoryCustom {
    @Autowired Neo4jClient client;

    @Override
    @NonNull
    public <A extends Anotation> A save(@NonNull A anotation) {
        String query = """
            MATCH
                (user:USER { id: $anotation.user.id }),
                (video:VIDEO { id: $anotation.video.id }),
                (video) <- [:OWNS] - (owner:USER)
            CREATE (user) - [anotation:ANOTATION {
                id: $anotation.id,
                anotation: $anotation.anotation,
                videoInstant: $anotation.videoInstant
            }] -> (video)
            RETURN anotation, user, video, owner
        """;

        String newId = UUID.randomUUID().toString();

        return (A) client.query(query)
            .bind(AnotationMapper.toValue(anotation, newId)).to("anotation")
            .fetchAs(Anotation.class)
            .mappedBy((__, queryResult) -> AnotationMapper.toEntity(
                queryResult.get("anotation").asMap(),
                queryResult.get("user").asMap(),
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .one()
            .orElse(null);
    }

    @Override
    public Optional<Anotation> findById(String id) {
        String query = """
            MATCH (video:VIDEO) <- [anotation:ANOTATION] - (user:USER)
            WHERE anotation.id = $id
            MATCH (video) <- [:OWNS] - (owner:USER)
            RETURN anotation, user, video, owner
        """;

        return client.query(query)
            .bind(id).to("id")
            .fetchAs(Anotation.class)
            .mappedBy((__, queryResult) -> AnotationMapper.toEntity(
                queryResult.get("anotation").asMap(),
                queryResult.get("user").asMap(),
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .one();
    }

    @Override
    public List<Anotation> findAllByVideoId(String id) {
        String query = """
            MATCH (video:VIDEO) <- [anotation:ANOTATION] - (user:USER)
            WHERE video.id = $id
            MATCH (video) <- [:OWNS] - (owner:USER)
            RETURN anotation, user, video, owner
        """;

        return new ArrayList<Anotation>(
            client.query(query)
                .bind(id).to("id")
                .fetchAs(Anotation.class)
                .mappedBy((__, queryResult) -> AnotationMapper.toEntity(
                    queryResult.get("anotation").asMap(),
                    queryResult.get("user").asMap(),
                    queryResult.get("video").asMap(),
                    queryResult.get("owner").asMap()
                ))
                .all()
        );
    }

    @Override
    public Anotation sinkAnotation(@NonNull Anotation anotation) throws Exception {
        String query = """
            MATCH
                (video:VIDEO { id: $anotation.video.id })
                    <- [anotation:ANOTATION { id: $anotation.id }] -
                    (user:USER),
                (video) <- [:OWNS] - (owner:USER)
            SET
                anotation.anotation = $anotation.anotation,
                anotation.videoInstant = $anotation.videoInstant
            RETURN anotation, user, video, owner
        """;
    
        return client.query(query)
            .bind(AnotationMapper.toValue(anotation, anotation.getId())).to("anotation")
            .fetchAs(Anotation.class)
            .mappedBy((__, queryResult) -> AnotationMapper.toEntity(
                queryResult.get("anotation").asMap(),
                queryResult.get("user").asMap(),
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .one()
            .orElseThrow(() -> new Exception(""));
    }

    @Override
    public void deleteById(@NonNull String videoId, @NonNull String anotationId) {
        String query = """
            MATCH (:VIDEO { id: $videoId }) <- [a:ANOTATION { id: $anotationId }] - (:USER)
            DELETE a
        """;

        client.query(query)
            .bind(anotationId).to("anotationId")
            .bind(videoId).to("videoId")
            .run();
    }    
}
