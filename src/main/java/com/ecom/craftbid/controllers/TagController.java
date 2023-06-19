package com.ecom.craftbid.controllers;

import com.ecom.craftbid.dtos.TagDTO;
import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/public/tags")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        List<TagDTO> ntags = new ArrayList<>();
        for (Tag t : tags) {
            ntags.add(new TagDTO(t.getId(), t.getName()));
        }
        return ResponseEntity.ok(ntags);
    }

    @PostMapping("/private/tags")
    public ResponseEntity<Tag> createTag(@RequestBody String name) {
        Tag createdTag = tagService.createTag(name);
        return ResponseEntity.ok(createdTag);
    }

    @DeleteMapping("/private/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/private/tags/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable long id, @RequestBody Tag tag) {
        Tag updatedTag = tagService.updateTag(id, tag);
        if (updatedTag != null) {
            return ResponseEntity.ok(updatedTag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
