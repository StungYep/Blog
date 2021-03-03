package com.zhouyu.blog.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDTO {
    private String categoryName;    //大的分类
    private List<String> tags;      //分类下的tags
}
