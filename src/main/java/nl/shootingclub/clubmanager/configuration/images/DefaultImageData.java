package nl.shootingclub.clubmanager.configuration.images;

import lombok.Getter;

import java.io.*;
import java.util.Base64;

@Getter
public enum DefaultImageData {
    PROFILE_PICTURE("defaultProfilePicture.jpeg", "DefaultProfilePicture"),
    ASSOCIATION_PICTURE("defaultAssociationPicture.png", "DefaultAssociationPicture");

    private final String location;
    private final String name;

    DefaultImageData(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public InputStream getImageFile() throws FileNotFoundException {
        return new FileInputStream(new File("src/main/resources/images/" + this.location));
    }

    public String getBase64EncodedImage() throws IOException {
        String base64Image = encodeToBase64(readInputStream(getImageFile()));
        String imageType = getFileType();
        return "data:image/" + imageType + ";base64," + base64Image;
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    private String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public String getFileType() {
        String[] parts = this.location.split("\\.");
        return parts[parts.length - 1];
    }
}