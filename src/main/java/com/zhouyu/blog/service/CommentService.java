package com.zhouyu.blog.service;

import com.zhouyu.blog.enums.CommentTypeEnum;
import com.zhouyu.blog.exception.CustomizeErrorCode;
import com.zhouyu.blog.exception.CustomizeException;
import com.zhouyu.blog.mapper.CommentMapper;
import com.zhouyu.blog.mapper.QuestionExtMapper;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.model.Comment;
import com.zhouyu.blog.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 评论异常处理
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public void insert(Comment comment) {
        if(comment.getParentId() == null || comment.getParentId() == 0) {
            //问题不存在了
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }

        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            //评论类型错误或不存在
            throw new CustomizeException((CustomizeErrorCode.TYPE_PARAM_WRONG));
        }

        if(comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null) {
                //评论不存在
                throw new CustomizeException((CustomizeErrorCode.COMMENT_NOT_FOUND));
            }
            commentMapper.insert(comment);
        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }
}
