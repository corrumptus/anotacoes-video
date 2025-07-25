package corrumptus.anotacoes_video.dto.anotation;

public record AnotationResponseDTO(
    String id,
    String userId,
    String userName,
    String videoId,
    String anotation,
    long videoInstant
) {
    
}
