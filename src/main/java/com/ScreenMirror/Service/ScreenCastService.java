package com.ScreenMirror.Service;

import com.ScreenMirror.Handlers.ScreenMirrorWebSocketHandler;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class ScreenCastService {
    private boolean streaming = false;
    private static final int FPS = 60;
    private static final int FRAME_DELAY_MS = 1000 / FPS;
    static {
        System.setProperty("java.awt.headless", "false");
    }

    public void startStreaming() throws IOException, AWTException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new AWTException("Headless environment detected. Screen capture not possible.");
        }

        if (streaming) {
            throw new IllegalStateException("Streaming is already in progress.");
        }
        streaming = true;

        new Thread(() -> {
            try {
                captureAndStream();
            } catch (Exception e) {
                e.printStackTrace();
                stopStreaming();
            }
        }).start();
    }

    private void captureAndStream() throws AWTException, IOException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        while (streaming) {
            long startTime = System.currentTimeMillis();

            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenFullImage, "jpg", baos);
            byte[] imageData = baos.toByteArray();
            ScreenMirrorWebSocketHandler.broadcast(imageData);

            long elapsedTime = System.currentTimeMillis() - startTime;
            long sleepTime = FRAME_DELAY_MS - elapsedTime;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void stopStreaming() {
        streaming = false;
    }
}
