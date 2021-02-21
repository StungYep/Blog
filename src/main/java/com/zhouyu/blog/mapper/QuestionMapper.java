package com.zhouyu.blog.mapper;

import com.zhouyu.blog.dto.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Controller;

@Mapper
@Controller
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create, gmt_modified,creator,tag) values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void Create(Question question);
}
