package corrumptus.anotacoes_video.repository.implementation;

import java.util.List;

import corrumptus.anotacoes_video.dto.video_permissions.VideoVisitantDBDTO;

public interface VideoVisibilityRepositoryCustom {
    public List<VideoVisitantDBDTO> getVisitantsRestricted(String id);

    public List<VideoVisitantDBDTO> getVisitantsPublic(String id);

    public void turnVideoRelashionshipPrivate(String id);

    public void turnVideoRelashionshipRestricted(String id);

    public void turnVideoRelashionshipPublic(String id);
}
