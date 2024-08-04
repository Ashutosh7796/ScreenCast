package com.ScreenMirror.Handlers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ScreenCastServiceWithMouseTracking {
    private boolean streaming = false;
    private static final int FPS = 60;
    private static final int FRAME_DELAY_MS = 1000 / FPS;

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
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            Graphics2D g2d = screenFullImage.createGraphics();

            Point mousePos = MouseInfo.getPointerInfo().getLocation();

            if (screenRect.contains(mousePos)) {
                g2d.setColor(Color.RED);
                g2d.drawOval(mousePos.x - 10, mousePos.y - 10, 20, 20);
            }

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenFullImage, "jpg", baos);
            byte[] imageData = baos.toByteArray();
            ScreenMirrorWebSocketHandler.broadcast(imageData);

            try {
                Thread.sleep(FRAME_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopStreaming() {
        streaming = false;
    }
}
