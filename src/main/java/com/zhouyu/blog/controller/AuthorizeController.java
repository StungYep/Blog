package com.zhouyu.blog.controller;

import com.sun.deploy.net.HttpResponse;
import com.zhouyu.blog.dto.AccessTokenDTO;
import com.zhouyu.blog.dto.GithubUser;
import com.zhouyu.blog.mapper.UserMapper;
import com.zhouyu.blog.model.User;
import com.zhouyu.blog.provider.GithubProvider;
import com.zhouyu.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * github 登录认证
 */
@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")   //将配置文件注入clientId
    private String clientId;

    @Value("${github.client.secret}")
    private String ClientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();

        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(ClientSecret);
        accessTokenDTO.setRedirect_uri(redirectUri);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);   //提取出的Java对象

        if(githubUser != null && githubUser.getId() != null) {
            //做登录成功写cookie和session
            request.getSession().setAttribute("user", githubUser);
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatar_url());
            userService.createOrUpdate(user);   //将User写入数据库或更新用户

            //写入cookie
            response.addCookie(new Cookie("token", token));
            return "redirect:/";
        }
        else {
            //登录失败， 从新登录
            return "redirect:index";
        }
    }

    @GetMapping("/logout")
    public String Logout(HttpServletRequest request,
                         HttpServletResponse response) {
        request.getSession().removeAttribute("user");       //删除session
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);                                  //删除cookie
        response.addCookie(cookie);
        return "redirect:/";
    }
}
