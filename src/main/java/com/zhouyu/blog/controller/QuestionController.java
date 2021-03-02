package com.zhouyu.blog.controller;

import com.zhouyu.blog.dto.CommentCreateDTO;
import com.zhouyu.blog.dto.CommentDTO;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.service.CommentService;
import com.zhouyu.blog.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model) {
        QuestionDTO questionDTO = questionService.getById(id);
        List<CommentDTO> comments = commentService.listByQuestionId(id);
        //累加阅读数
        questionService.incView(id);
        model.addAttribute("comments", comments);
        model.addAttribute("question", questionDTO);
        return "question";
    }
}
