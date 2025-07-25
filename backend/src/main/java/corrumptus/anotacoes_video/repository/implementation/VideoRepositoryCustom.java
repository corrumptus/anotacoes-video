package corrumptus.anotacoes_video.repository.implementation;

import java.util.List;
import java.util.Optional;

import corrumptus.anotacoes_video.entity.Video;

public interface VideoRepositoryCustom {
    public Optional<Video> findById(String id);

    public List<Video> findByOwner(String id);

    public List<Video> findBySharedAccessUser(String id);
    
    public List<Video> findByKnowsPublicUser(String id);

    public Video sinkVideo(Video video) throws Exception;
}
