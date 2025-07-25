package corrumptus.anotacoes_video.utils.mideahandling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Service
public class VideoService {
    private long SECONDS_TO_MICROSECONDS = 1_000_000;
    private long MICROSECONDS_TO_SECONDS = 1_000_000;

    public long getDuration(File video) throws FrameGrabber.Exception {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video)) {
            grabber.start();
            long duration = grabber.getLengthInTime() / MICROSECONDS_TO_SECONDS;
            return duration;
        } catch (Exception e) {
            throw e;
        }
    }

    public void saveVideoThumbnail(File video, long duration, File thumb)
        throws FrameGrabber.Exception, IOException
    {
        try (
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(video);
            Java2DFrameConverter converter = new Java2DFrameConverter();
        ) {
            grabber.start();

            long randomTimeStamp =
                (long) Math.floor(Math.random() * (double) duration)
                *
                SECONDS_TO_MICROSECONDS;

            grabber.setTimestamp(randomTimeStamp);

            Frame frame = grabber.grabImage();
            BufferedImage img = converter.getBufferedImage(frame);
            ImageIO.write(img, "jpg", thumb);
        } catch (Exception e) {
            throw e;
        }
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.ofGigabytes(5));
        factory.setMaxRequestSize(DataSize.ofGigabytes(5));

        return factory.createMultipartConfig();
    }
}
