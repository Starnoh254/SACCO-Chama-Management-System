package com.starnoh.sacco_management.dto;


import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MemberFilterRequest {

    private String status;
    private String search;
    private int page;
    private int size;
    private String sortBy;
    private String direction;
}
