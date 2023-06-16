package com.ecom.craftbid.utils;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class PhotosManager {

    // TODO: probably transfer to application.properties as: assets-dir=
    private final static String PHOTOS_PATH = "/assets/photos/";

    /**
     * Save photo filename is constructed as follows:
     * originalFilename_listingTitle_listingId
     * If filename repeats, it is overwritten
     *
     * @param photos - array of photos to be saved
     * @return - list of paths to the saved photos
     */
    static public List<String> saveFiles(MultipartFile[] photos, String listingTitle, long listingId) {
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile photo : photos) {
            if (photo.isEmpty())
                continue;

            try {
                String photoName = photo.getOriginalFilename() + "_" + listingTitle + "_" + listingId;
                Path path = Path.of(PHOTOS_PATH + photoName);
                Files.copy(photo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(PHOTOS_PATH)
                        .path(photoName)
                        .toUriString();

                imagePaths.add(fileUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imagePaths;
    }
}
