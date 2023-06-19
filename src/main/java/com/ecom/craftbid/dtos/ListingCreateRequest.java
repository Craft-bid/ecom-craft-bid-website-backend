package com.ecom.craftbid.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingCreateRequest {
    private String title;
    private String description;
    private long advertiserId;
    private Boolean ended = false;
}
