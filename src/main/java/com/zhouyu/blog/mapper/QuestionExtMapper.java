package com.zhouyu.blog.mapper;

import com.zhouyu.blog.model.Question;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);

    int incCommentCount(Question record);

    List<Question> selectRelated(Question record);
}