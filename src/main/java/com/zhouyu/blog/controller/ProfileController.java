package com.zhouyu.blog.controller;

import com.zhouyu.blog.dto.PaginationDTO;
import com.zhouyu.blog.model.User;
import com.zhouyu.blog.service.NotificationService;
import com.zhouyu.blog.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            return "redirect:/";
        }

        if(action.equals("questions")) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO paginationDTO = questionService.listByUserId(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        }
        else if(action.equals("replies")) {
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "我的回复");
            PaginationDTO paginationDTO = notificationService.list(user.getId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        }

        return "profile";
    }
}
