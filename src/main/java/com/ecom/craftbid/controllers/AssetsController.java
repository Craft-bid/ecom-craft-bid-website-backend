package com.ecom.craftbid.controllers;

import com.ecom.craftbid.utils.PhotosManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;


@Controller
@RequestMapping("/assets")
public class AssetsController {
    @GetMapping("/photos/{filename}")
    public ResponseEntity<InputStreamResource> getPhoto(@PathVariable String filename) throws IOException {
        InputStream photoResource = PhotosManager.loadPhoto(filename); //getClass().getResourceAsStream("/assets/photos/" +filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(photoResource));

    }
}
