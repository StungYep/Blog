package com.zhouyu.blog.service;

import com.zhouyu.blog.dto.PaginationDTO;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.Question;
import com.zhouyu.blog.model.QuestionExample;
import com.zhouyu.blog.model.User;
import org.apache.ibatis.session.RowBounds;
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
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());    //总问题数, count(1) from question
        Integer totalPage = totalCount / size;         //用户问题总页数
        if(totalCount % size > 0) totalPage++;          //问题总页码数
        totalPage = Math.max(1, totalPage);

        PaginationDTO paginationDTO = new PaginationDTO();
        page = Math.min(Math.max(page, 1), totalPage);
        paginationDTO.setPagination(totalPage, page);   //设置需要显示的页码

        Integer offset = (page - 1) * size;
        //pageHelper实现分页, questions为当前页面的问题
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);        //设置当前页面需要显示的问题

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Integer userId, Integer page, Integer size) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);  //用户提问的问题总数,countByUserId

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage = totalCount / size;         //用户问题总页数
        if(totalCount % size > 0) totalPage++;
        totalPage = Math.max(1, totalPage);
        page = Math.min(Math.max(page, 1), totalPage);
        paginationDTO.setPagination(totalPage, page);   //设置需要显示的页码

        Integer offset = (page - 1) * size;
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size)); //当前页面的问题
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
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
        Question question = questionMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public Question createOrUpdate(Question question) {
        if(question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);   //创建问题
        } else {
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setDescription(question.getDescription());

            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            questionMapper.updateByExampleSelective(updateQuestion, questionExample);    //更新问题
        }
        return null;
    }
}
