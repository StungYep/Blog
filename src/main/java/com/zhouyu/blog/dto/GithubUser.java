package com.zhouyu.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * github 登录的账号信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatar_url;
}
