package com.ecom.craftbid.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class PhotosManager {

    // TODO: probably transfer to application.properties as: assets-dir=
    private final static String PHOTOS_PATH = "assets/photos/";

    /**
     * Save photo filename is constructed as follows:
     * listingId_originalFilename
     * If filename repeats, it is overwritten
     *
     * @param photos - array of photos to be saved
     * @return - list of paths to the saved photos
     */
    static public List<String> saveFiles(MultipartFile[] photos, long listingId) {
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile photo : photos) {
            try {
                String photoName = listingId + "_" + photo.getOriginalFilename();

                if (photo.isEmpty())
                    continue;

                String fileUrl = saveFile(photo, photoName);
                imagePaths.add(fileUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imagePaths;
    }

    static public String saveUserAvatar(MultipartFile photo, long userId) {
        String photoName = "user" + userId + "_" + photo.getOriginalFilename();
        return saveFile(photo, photoName);
    }

    private static String saveFile(MultipartFile photo, String photoName) {
        if (photo.isEmpty())
            throw new RuntimeException("Empty file");
        try {
            Path directoryPath = Path.of(PHOTOS_PATH);
            Path filePath = directoryPath.resolve(photoName);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(PHOTOS_PATH)
                    .path(photoName)
                    .toUriString();

        } catch (Exception e) {
            throw new RuntimeException("Could not store file " + photoName
                    + ". Please try again!", e);
        }

    }

    public static InputStream loadPhoto(String filename) throws IOException {
        return PhotosManager.class.getResourceAsStream(PHOTOS_PATH + filename);
    }
}
