package com.zhouyu.blog.service;

import com.zhouyu.blog.dto.PaginationDTO;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.Question;
import com.zhouyu.blog.model.User;
import jdk.management.resource.internal.TotalResourceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = questionMapper.count();    //总问题数
        PaginationDTO paginationDTO = new PaginationDTO();
        paginationDTO.setPagination(totalCount, page, size);   //设置需要显示的页码
        page = Math.min(Math.max(page, 1), paginationDTO.getTotalPage());

        Integer offset = (page - 1) * size;
        List<Question> questions = questionMapper.list(offset, size);  //当前页面的问题
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);        //设置当前页面需要显示的问题

        return paginationDTO;
    }
}
