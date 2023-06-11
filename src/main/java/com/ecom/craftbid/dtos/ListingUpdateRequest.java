package com.ecom.craftbid.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingUpdateRequest {
    private String title;
    private String description;
    private Boolean ended;
    private long winnerId;
    private long advertiserId;
    private Date expirationDate;
    private Date creationDate;
}
