package com.ecom.craftbid.services;

import com.ecom.craftbid.entities.listing.Tag;
import com.ecom.craftbid.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public void deleteTag(long id) {
        tagRepository.deleteById(id);
    }

    public Tag updateTag(long id, Tag tag) {
        if (tagRepository.existsById(id)) {
            tag.setId(id);
            return tagRepository.save(tag);
        } else {
            return null;
        }
    }
}
