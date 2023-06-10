package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/public/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.ok(tags);
    }

    @PostMapping("/private/tags")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagRepository.save(tag);
        return ResponseEntity.ok(createdTag);
    }

    @DeleteMapping("/private/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/private/tags/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable long id, @RequestBody Tag tag) {
        if (tagRepository.existsById(id)) {
            Tag updatedTag = tagRepository.save(tag);
            return ResponseEntity.ok(updatedTag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
