package com.zhouyu.blog.service;

import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.User;
import com.zhouyu.blog.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.isEmpty()) {
            //插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }
        else {
            //更新
            User dbuser = users.get(0);

            User updateuser = new User();   //需要更新的现在的
            updateuser.setGmtModified(System.currentTimeMillis());
            updateuser.setAvatarUrl(user.getAvatarUrl());
            updateuser.setName(user.getName());
            updateuser.setToken(user.getToken());

            UserExample example = new UserExample();  //需要更新的原来的
            example.createCriteria().andIdEqualTo(dbuser.getId());

            //不明白的话看源码就行了

            userMapper.updateByExampleSelective(updateuser, example);
        }
    }
}
