package com.example.vendor.model;

import lombok.Data;

@Data
public class PageResultModel {
    private Long totalCount;
    private int pageno;
    private int pagecount;
}
