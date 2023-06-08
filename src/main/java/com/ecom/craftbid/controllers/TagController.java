package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/public/tags")
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @PostMapping("/private/tags")
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @DeleteMapping("/private/tags/{id}")
    public void deleteTag(@PathVariable long id) {
        tagRepository.deleteById(id);
    }

    @PutMapping("/private/tags/{id}")
    public Tag updateTag(Tag tag) {
        return tagRepository.save(tag);
    }

}
