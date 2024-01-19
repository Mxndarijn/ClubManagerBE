package nl.shootingclub.clubmanager.helper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageHelper {

    public static String scaleImage(String dataUrl, int scale) throws IOException {
        String base64Image = dataUrl.split(",")[1];

        // Decodeer de base64 string naar een byte array
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // Lees de byte array in als een BufferedImage
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        return bufferedImageToDataUrl(resizeImage(img, scale), "jpeg");

    }

    public static  String bufferedImageToDataUrl(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Schrijf de BufferedImage naar de ByteArrayOutputStream
        ImageIO.write(image, format, outputStream);

        // Converteer de ByteArrayOutputStream naar een byte-array
        byte[] imageBytes = outputStream.toByteArray();

        // Converteer de byte-array naar een Base64-gecodeerde string
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Formatteer en retourneer als Data URL
        return "data:image/" + format + ";base64," + base64Image;
    }

    public static  BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Behoud het aspectratio van de afbeelding
        double aspectRatio = (double) width / height;
        int newWidth, newHeight;

        if (width > height) {
            // Landschapsmodus
            newWidth = targetSize;
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            // Portretmodus of vierkant
            newHeight = targetSize;
            newWidth = (int) (newHeight * aspectRatio);
        }

        // Maak en vul een nieuwe BufferedImage
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}
