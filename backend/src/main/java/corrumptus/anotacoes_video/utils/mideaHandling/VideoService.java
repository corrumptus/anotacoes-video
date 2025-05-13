package corrumptus.anotacoes_video.utils.mideaHandling;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class VideoService {
    public long getVideoDuration(MultipartFile file) throws IOException {
        // Salvar o arquivo temporariamente
        Path tempFile = Files.createTempFile("video", ".tmp");
        file.transferTo(tempFile.toFile());

        // Inicializar o FFmpeg
        AVFormatContext formatContext = avformat.avformat_alloc_context();

        // Abrir o arquivo de vídeo
        if (avformat.avformat_open_input(formatContext, tempFile.toString(), null, null) != 0) {
            throw new IOException("Não foi possível abrir o arquivo de vídeo.");
        }

        // Obter informações sobre o vídeo
        if (avformat.avformat_find_stream_info(formatContext, (PointerPointer<Pointer>) null) < 0) {
            throw new IOException("Não foi possível obter informações do arquivo de vídeo.");
        }

        // Obter a duração do vídeo (em microsegundos)
        long durationInMicroseconds = formatContext.duration();
        
        // Converter a duração para segundos
        long durationInSeconds = durationInMicroseconds / 1000000;

        // Apagar o arquivo temporário após o processamento
        Files.delete(tempFile);

        return durationInSeconds;
    }
}
