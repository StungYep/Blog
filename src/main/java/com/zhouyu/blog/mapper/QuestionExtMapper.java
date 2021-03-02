package com.zhouyu.blog.mapper;

import com.zhouyu.blog.model.Question;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
}