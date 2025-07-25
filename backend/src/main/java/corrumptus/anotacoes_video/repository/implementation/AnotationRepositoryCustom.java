package corrumptus.anotacoes_video.repository.implementation;

import java.util.List;
import java.util.Optional;

import corrumptus.anotacoes_video.entity.Anotation;

public interface AnotationRepositoryCustom {
    public <A extends Anotation> A save(A anotation);

    public Optional<Anotation> findById(String id);

    public List<Anotation> findAllByVideoId(String videoId);

    public Anotation sinkAnotation(Anotation anotation) throws Exception;

    public void deleteById(String videoId, String anotationId);
}
