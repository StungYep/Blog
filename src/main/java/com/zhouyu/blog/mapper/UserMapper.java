package com.zhouyu.blog.mapper;

import com.zhouyu.blog.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Controller;

@Controller
@Mapper
public interface UserMapper {

    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modify) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModify})")
    void insert(User user);

    @Select("Select * from user where token = #{token}")
    User findByToken(@Param("token") String token);   //参数不是Java类需要加参数@Param
}
