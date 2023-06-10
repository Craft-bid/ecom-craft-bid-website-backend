package com.ecom.craftbid.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponse {
    private List<ListingDTO> content;
    private Object pageable;
    private Object last;
    private Object totalPages;
    private Object totalElements;
    private Object size;
    private Object number;
    private Object sort;
    private Object numberOfElements;
    private Object first;
    private Object empty;
}



