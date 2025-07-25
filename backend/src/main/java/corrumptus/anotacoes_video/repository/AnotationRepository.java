package corrumptus.anotacoes_video.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import corrumptus.anotacoes_video.entity.Anotation;
import corrumptus.anotacoes_video.repository.implementation.AnotationRepositoryCustom;

public interface AnotationRepository extends Neo4jRepository<Anotation, String>, AnotationRepositoryCustom {

}
