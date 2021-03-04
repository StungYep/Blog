package com.zhouyu.blog.service;

import com.zhouyu.blog.dto.PaginationDTO;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.exception.CustomizeErrorCode;
import com.zhouyu.blog.exception.CustomizeException;
import com.zhouyu.blog.mapper.QuestionExtMapper;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.Question;
import com.zhouyu.blog.model.QuestionExample;
import com.zhouyu.blog.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

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
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);        //设置当前页面需要显示的问题

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {
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
        paginationDTO.setData(questionDTOList);        //设置当前页面需要显示的问题

        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        QuestionDTO questionDTO = new QuestionDTO();
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null) {   //创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setLikeCount(0);
            question.setViewCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {        //更新问题
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setDescription(question.getDescription());

            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if(updated != 1) {
                //更新失败
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {
        /**
         *   Question question = questionMapper.selectByPrimaryKey(id);
         *   updateQuestion.setViewCount(question.getViewCount() + 1);
         *   上面这种更新会导致并发错误
         */
        Question question = new Question();
        question.setViewCount(1);
        question.setId(id);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if(StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regxpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regxpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());

        return questionDTOS;
    }
}
