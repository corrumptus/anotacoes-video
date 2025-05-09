package corrumptus.anotacoes_video.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import corrumptus.anotacoes_video.entity.UserEntity;

public interface UserRepository extends Neo4jRepository<UserEntity, String> {
    
}