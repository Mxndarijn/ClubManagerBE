package nl.shootingclub.clubmanager.helper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageHelper {

    /**
     * Scales the given image data URL to the specified scale value.
     *
     * @param dataUrl The image data URL to be scaled.
     * @param scale   The scale value to resize the image (e.g., 2 for doubling the size, 0.5 for halving the size).
     * @return The scaled image data URL.
     * @throws IOException If an I/O error occurs while reading or writing the image.
     */
    public static String scaleImage(String dataUrl, int scale) throws IOException {
        String base64Image = dataUrl.split(",")[1];

        // Decodeer de base64 string naar een byte array
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // Lees de byte array in als een BufferedImage
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

        return bufferedImageToDataUrl(resizeImage(img, scale), "jpeg");

    }

    /**
     * Converts a BufferedImage to a Data URL.
     *
     * @param image the BufferedImage to convert
     * @param format the image format to use in the Data URL (e.g., "jpeg", "png")
     * @return the Data URL representation of the BufferedImage
     * @throws IOException if an I/O error occurs during the conversion
     */
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

    /**
     * Resizes the given BufferedImage to the specified target size while maintaining the aspect ratio.
     *
     * @param originalImage The original BufferedImage to be resized.
     * @param targetSize The target size for the resized image (either the width or height, depending on the aspect ratio).
     * @return The resized BufferedImage.
     */
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
