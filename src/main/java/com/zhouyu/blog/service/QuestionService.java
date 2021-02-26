package com.zhouyu.blog.service;

import com.zhouyu.blog.dto.PaginationDTO;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.Question;
import com.zhouyu.blog.model.User;
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
        Integer totalPage = totalCount / size;         //用户问题总页数
        if(totalCount % size > 0) totalPage++;          //问题总页码数
        totalPage = Math.max(1, totalPage);

        System.out.println("totalpage = " + totalPage + "; totalcount = " + totalCount);

        PaginationDTO paginationDTO = new PaginationDTO();
        page = Math.min(Math.max(page, 1), totalPage);
        paginationDTO.setPagination(totalPage, page);   //设置需要显示的页码

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

    public PaginationDTO listByUserId(Integer userId, Integer page, Integer size) {
        Integer totalCount = questionMapper.countByUserId(userId);    //用户提问的问题总数
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage = totalCount / size;         //用户问题总页数
        if(totalCount % size > 0) totalPage++;
        totalPage = Math.max(1, totalPage);
        page = Math.min(Math.max(page, 1), totalPage);
        paginationDTO.setPagination(totalPage, page);   //设置需要显示的页码

        Integer offset = (page - 1) * size;
        List<Question> questions = questionMapper.ListByUserId(userId, offset, size);  //当前页面的问题
        System.out.println(questions);
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

    public QuestionDTO getById(Integer id) {
        QuestionDTO questionDTO = new QuestionDTO();
        Question question = questionMapper.getById(id);
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.findById(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }
}
