package com.zhouyu.blog.controller;

import com.zhouyu.blog.cache.TagCache;
import com.zhouyu.blog.dto.QuestionDTO;
import com.zhouyu.blog.mapper.QuestionMapper;
import com.zhouyu.blog.model.Question;
import com.zhouyu.blog.model.User;
import com.zhouyu.blog.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 发布问题
 */
@Controller
public class PublishController {

    @Autowired
    QuestionService questionService;

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Long id,  //问题的id
                       Model model) {
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title", question.getTitle());       //添加到Model中是为了回显到页面中去
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", question.getId());

        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "tag", required = false) String tag,
                            @RequestParam(value = "id", required = false) Long id,
                            HttpServletRequest request,
                            Model model) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());

        if(title == null || title == "") {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if(description == null || description == "") {
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }
        if(tag == null || tag == "") {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");
        if(user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(tag);
        if(!StringUtils.isBlank(invalid)) {
            model.addAttribute("error", "输入非法标签：" + invalid);
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
