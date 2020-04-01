package life.sdwy.community.controller;

import life.sdwy.community.dto.PaginationDTO;
import life.sdwy.community.mapper.UserMapper;
import life.sdwy.community.model.User;
import life.sdwy.community.service.QuestionService;
import life.sdwy.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "3") Integer size,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        PaginationDTO pagination = questionService.list(page,size);

        // 登录  删除
        User user = userMapper.selectByPrimaryKey(27L);
        request.setAttribute("user", user);
        response.addCookie(new Cookie("token", user.getToken()));

        model.addAttribute("pagination", pagination);
        return "index";
    }
}
