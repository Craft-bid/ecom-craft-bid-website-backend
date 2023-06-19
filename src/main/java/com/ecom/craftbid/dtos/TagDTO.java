package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.listing.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDTO {
    private long id;
    private String name;

    public static TagDTO fromTag(Tag tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    public static List<TagDTO> fromTags(Collection<Tag> tags) {
        if (tags == null)
            return new ArrayList<>();

        return tags.stream()
                .map(TagDTO::fromTag)
                .collect(Collectors.toList());
    }

    public static Tag toTag(TagDTO tagDTO) {
        return Tag.builder()
                .name(tagDTO.getName())
                .build();
    }
}