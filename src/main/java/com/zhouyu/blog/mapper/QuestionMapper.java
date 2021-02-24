package com.zhouyu.blog.mapper;

import com.zhouyu.blog.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Controller;

import java.util.List;

@Mapper
@Controller
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create, gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void Create(Question question);

    @Select("select * from question")
    List<Question> list();
}
