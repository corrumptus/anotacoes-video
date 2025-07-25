package corrumptus.anotacoes_video.repository.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;

import corrumptus.anotacoes_video.dto.video_permissions.VideoVisitantDBDTO;

public class VideoVisibilityRepositoryCustomImpl implements VideoVisibilityRepositoryCustom {
    @Autowired
    private Neo4jClient client;
    
    @Override
    public List<VideoVisitantDBDTO> getVisitantsRestricted(String id) {
        String query = """
            MATCH (video:VIDEO) <- [s:SHARED_ACCESS] - (user:USER)
            WHERE video.id = $id
            RETURN user.login as name, s.canAnotate as canAnotate
        """;

        return new ArrayList<>(
            client.query(query)
            .bind(id).to("id")
            .fetchAs(VideoVisitantDBDTO.class)
            .mappedBy((__, queryResult) -> new VideoVisitantDBDTO(
                queryResult.get("name").asString(),
                queryResult.get("canAnotate").asBoolean()
            ))
            .all()
        );
    }

    @Override
    public List<VideoVisitantDBDTO> getVisitantsPublic(String id) {
        String query = """
            MATCH (video:VIDEO) <- [:KNOWS_PUBLIC] - (user:USER)
            WHERE video.id = $id
            RETURN user.login as name, video.canAnotate as canAnotate
        """;

        return new ArrayList<>(
            client.query(query)
            .bind(id).to("id")
            .fetchAs(VideoVisitantDBDTO.class)
            .mappedBy((__, queryResult) -> new VideoVisitantDBDTO(
                queryResult.get("name").asString(),
                queryResult.get("canAnotate").asBoolean()
            ))
            .all()
        );
    }

    @Override
    public void turnVideoRelashionshipPrivate(String id) {
        String query1 = """
            MATCH (:VIDEO { id: $id }) <- [k:KNOWS_PUBLIC] - (:USER)
            DELETE k
        """;

        String query2 = """
            MATCH (:VIDEO { id: $id }) <- [s:SHARED_ACCESS] - (:USER)
            DELETE s
        """;

        client.query(query1)
            .bind(id).to("id")
            .run();

        client.query(query2)
            .bind(id).to("id")
            .run();
    }

    @Override
    public void turnVideoRelashionshipRestricted(String id) {
        String query1 = """
            MATCH (v:VIDEO { id: $id }) <- [k:KNOWS_PUBLIC] - (:USER)
            DELETE k
        """;

        String query2 = """
            MATCH (v:VIDEO { id: $id })
            REMOVE v.canAnotate
        """;

        client.query(query1)
            .bind(id).to("id")
            .run();

        client.query(query2)
            .bind(id).to("id")
            .run();
    }

    @Override
    public void turnVideoRelashionshipPublic(String id) {
        String query1 = """
            MATCH (v:VIDEO { id: $id }) <- [s:SHARED_ACCESS] - (u:USER)
            DELETE s
            CREATE (v) <- [:KNOWS_PUBLIC] - (u)
        """;

        String query2 = """
            MATCH (v:VIDEO { id: $id })
            SET v.canAnotate = false
        """;

        client.query(query1)
            .bind(id).to("id")
            .run();

        client.query(query2)
            .bind(id).to("id")
            .run();
    }
}
