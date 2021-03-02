package com.zhouyu.blog.service;

import com.zhouyu.blog.dto.CommentDTO;
import com.zhouyu.blog.enums.CommentTypeEnum;
import com.zhouyu.blog.exception.CustomizeErrorCode;
import com.zhouyu.blog.exception.CustomizeException;
import com.zhouyu.blog.mapper.CommentMapper;
import com.zhouyu.blog.mapper.QuestionExtMapper;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    @Autowired
    private UserMapper userMapper;

    @Transactional    //申明事务
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
            //回复评论， 2
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null) {
                //评论不存在
                throw new CustomizeException((CustomizeErrorCode.COMMENT_NOT_FOUND));
            }
            commentMapper.insert(comment);
        } else {
            //回复问题， 1
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    //根据问题的ID
    public List<CommentDTO> listByQuestionId(Long id) {
        CommentExample commentExample = new CommentExample();
        //按照是评论问题的评论的问题ID来查
        commentExample.createCriteria().
                andParentIdEqualTo(id).andTypeEqualTo(CommentTypeEnum.Question.getType());
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size() == 0) {
            return new ArrayList<>();
        }
        //得到评论者的ID
        Set<Long> collect = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(collect);

        //根据评论ID找到其User
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换 comment 称为commentDTO
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            commentDTOS.add(commentDTO);
        }
        return commentDTOS;
    }
}
