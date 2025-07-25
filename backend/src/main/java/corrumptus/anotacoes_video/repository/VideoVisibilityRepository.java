package corrumptus.anotacoes_video.repository;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import corrumptus.anotacoes_video.entity.VideoVisibility;
import corrumptus.anotacoes_video.repository.implementation.VideoVisibilityRepositoryCustom;

public interface VideoVisibilityRepository extends Neo4jRepository<VideoVisibility, String>, VideoVisibilityRepositoryCustom {
    @Query("""
        MATCH (user:USER {id: $userId}), (video:VIDEO {id: $videoId})
        OPTIONAL MATCH (video) <- [k:KNOWS_PUBLIC] - (user)
        RETURN k IS NOT NULL
    """)
    public boolean userKnowsPublic(@Param("userId") String userId, @Param("videoId") String videoId);

    @Query("""
        MATCH (user:USER {id: $userId}), (video:VIDEO {id: $videoId})
        CREATE (video) <- [:KNOWS_PUBLIC] - (user)
    """)
    public void userDiscoveredPublic(@Param("userId") String userId, @Param("videoId") String videoId);

    @Query("""
        MATCH (user:USER {id: $userId}), (video:VIDEO {id: $videoId})
        OPTIONAL MATCH (video) <- [s:SHARED_ACCESS] - (user)
        RETURN s IS NOT NULL
    """)
    public boolean userHasShared(@Param("userId") String userId, @Param("videoId") String videoId);

    @Query("""
        MATCH (:VIDEO { id: $videoId }) <- [s:SHARED_ACCESS] - (:USER { login: $userLogin })
        SET s.canAnotate = $canAnotate
    """)
    public void changeRestrictedCanAnotate(@Param("videoId") String videoId, @Param("userLogin") String userLogin, @Param("canAnotate") boolean canAnotate);

    @Query("""
        MATCH (v:VIDEO { id: $id })
        SET v.canAnotate = $canAnotate
    """)
    public void changePublicCanAnotate(@Param("id") String id, @Param("canAnotate") boolean canAnotate);

    @Query("""
        MATCH (v:VIDEO { id: $videoId }), (u:USER { login: $userLogin })
        CREATE (v) <- [:SHARED_ACCESS { canAnotate: false }] - (u)
    """)
    public void addUserInSharedVideo(@Param("videoId") String videoId, @Param("userLogin") String userLogin);

    @Query("""
        MATCH (v:VIDEO { id: $videoId }), (u:USER { id: $userId })
        CREATE (v) <- [:KNOWS_PUBLIC] - (u)
    """)
    public void addUserKnowsPublic(@Param("videoId") String videoId, @Param("userId") String userId);

    @Query("""
        MATCH (:VIDEO { id: $videoId }) <- [k:SHARED_ACCESS] - (:USER { login: $userLogin })
        DELETE k
    """)
    public void removeUserInSharedVideo(@Param("videoId") String videoId, @Param("userLogin") String userLogin);

    @Query("""
        MATCH (video:VIDEO { id: $id }) <- [a:ACCESS] - (user:USER)
        RETURN
            a.id as id,
            collect(video) as video,
            collect(user) as user,
            a.canAnotate as canAnotate
    """)
    public List<VideoVisibility> findAllByVideoId(@Param("id") String id);

    @Query("""
        MATCH (:VIDEO) <- [a:ACCESS { id: $id }] - (:USER)
        SET a.canAnotate = $canAnotate
        RETURN
            a.id as id,
            collect(video) as video,
            collect(user) as user,
            a.canAnotate as canAnotate
    """)
    public VideoVisibility updateCanAnotate(@Param("id") String id, @Param("canAnotate") boolean canAnotate);

    @Query("""
        MATCH (:VIDEO { id: $videoId }) <- [s:SHARED_ACCESS] - (:USER { login: $userLogin })
        RETURN s.canAnotate
    """)
    public boolean getUserCanAnotateRestricted(@Param("videoId") String videoId, @Param("userLogin") String userLogin);
}
