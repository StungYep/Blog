package com.zhouyu.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * github 登录的账号信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubUser {
    private String name;
    private long id;
    private String bio;
}
