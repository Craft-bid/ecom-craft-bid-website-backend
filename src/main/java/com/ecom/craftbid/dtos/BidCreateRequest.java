package com.ecom.craftbid.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BidCreateRequest {
    private long price;
    private String description;
    private long daysToDeliver;
    private long bidderId;
    private long listingId;

    private Date creationDate = Calendar.getInstance().getTime();
}
