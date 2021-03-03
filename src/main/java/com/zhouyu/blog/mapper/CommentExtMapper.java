package com.zhouyu.blog.mapper;

import com.zhouyu.blog.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}