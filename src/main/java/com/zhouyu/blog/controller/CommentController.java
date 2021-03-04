package com.zhouyu.blog.controller;

import com.zhouyu.blog.dto.CommentCreateDTO;
import com.zhouyu.blog.dto.CommentDTO;
import com.zhouyu.blog.dto.ResultDTO;
import com.zhouyu.blog.enums.CommentTypeEnum;
import com.zhouyu.blog.exception.CustomizeErrorCode;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.model.Comment;
import com.zhouyu.blog.model.User;
import com.zhouyu.blog.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionMapper questionMapper;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            //用户未登录
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment, user);
        return ResultDTO.okof();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okof(commentDTOS);
    }
}
