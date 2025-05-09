package corrumptus.anotacoes_video.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import corrumptus.anotacoes_video.entity.UserEntity;

public interface UserRepository extends Neo4jRepository<UserEntity, String> {
    @Query("""
        MATCH (user:USER)
        WHERE user.login = $login
        RETURN user
    """)
    Optional<UserDetails> findByLogin(@Param("login") String login);
}