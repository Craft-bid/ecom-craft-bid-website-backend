package com.ecom.craftbid.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingCreateRequest {
    private String title;
    private String description;
    private long advertiserId;
    private boolean ended = false;
}
