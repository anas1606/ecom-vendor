package com.example.vendor.model;

import lombok.Data;

@Data
public class PageDetailModel {
    private Integer page;
    private Integer limit;
    private String sortorder;
    private String category;
    private String search;
}
