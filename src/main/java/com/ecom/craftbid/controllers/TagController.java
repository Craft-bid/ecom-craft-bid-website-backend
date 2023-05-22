package com.ecom.craftbid.controllers;

import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @PostMapping
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable long id) {
        tagRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Tag updateTag(Tag tag) {
        return tagRepository.save(tag);
    }

}
