package corrumptus.anotacoes_video.repository.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import corrumptus.anotacoes_video.entity.Video;
import corrumptus.anotacoes_video.mapper.VideoMapper;

@Repository
public class VideoRepositoryCustomImpl implements VideoRepositoryCustom {
    @Autowired
    private Neo4jClient client;

    @Override
    public Optional<Video> findById(String id) {
        String query = """
            MATCH (video:VIDEO) <- [:OWNS] - (owner:USER)
            WHERE video.id = $id
            RETURN video, owner
        """;
        return client.query(query)
            .bind(id).to("id")
            .fetchAs(Video.class)
            .mappedBy((__, queryResult) -> VideoMapper.toEntity(
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .one();
    }

    @Override
    public List<Video> findByOwner(String login) {
        String query = """
            MATCH (video:VIDEO) <- [:OWNS] - (owner:USER)
            WHERE owner.login = $login
            RETURN collect(video) as videos, owner
        """;

        return client.query(query)
            .bind(login).to("login")
            .fetchAs(List.class)
            .mappedBy((__, queryResult) -> queryResult.get("videos")
                .asList(v -> VideoMapper.toEntity(
                    v.asMap(),
                    queryResult.get("owner").asMap()
                ))
            )
            .one()
            .orElse(new ArrayList<Video>());
    }

    @Override
    public List<Video> findBySharedAccessUser(String login) {
        String query = """
            MATCH (video:VIDEO) <- [:SHARED_ACCESS] - (user:USER)
            WHERE user.login = $login
            MATCH (video) <- [:OWNS] - (owner:USER)
            RETURN video, owner
        """;

        return new ArrayList<Video>(client.query(query)
            .bind(login).to("login")
            .fetchAs(Video.class)
            .mappedBy((__, queryResult) -> VideoMapper.toEntity(
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .all());
    }

    @Override
    public List<Video> findByKnowsPublicUser(String login) {
        String query = """
            MATCH (video:VIDEO) <- [:KNOWS_PUBLIC] - (user:USER)
            WHERE user.login = $login
            MATCH (video) <- [:OWNS] - (owner:USER)
            RETURN video, owner
        """;

        return new ArrayList<Video>(client.query(query)
            .bind(login).to("login")
            .fetchAs(Video.class)
            .mappedBy((__, queryResult) -> VideoMapper.toEntity(
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .all());
    }

    @Override
    public Video sinkVideo(@NonNull Video video) throws Exception {
        String query = """
            MATCH (video:VIDEO { id: $video.id }) <- [:OWNS] - (owner:USER)
            SET
                video.title = $video.title,
                video.description = $video.description,
                video.visibility = $video.visibility
            RETURN video, owner
        """;

        return client.query(query)
            .bind(VideoMapper.toValue(video)).to("video")
            .fetchAs(Video.class)
            .mappedBy((__, queryResult) -> VideoMapper.toEntity(
                queryResult.get("video").asMap(),
                queryResult.get("owner").asMap()
            ))
            .one()
            .orElseThrow(() -> new Exception(""));
    }
}
