package com.ecom.craftbid.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class SearchCriteriaDto {
    private String title;
    private String advertiserName;
    private String winnerName;
    private List<String> tagNames;
    private Date dateFrom;
    private Date dateTo;
}
