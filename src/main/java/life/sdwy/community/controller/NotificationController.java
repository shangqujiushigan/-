package life.sdwy.community.controller;

import life.sdwy.community.dto.NotificationDTO;
import life.sdwy.community.enums.NotificationTypeEnum;
import life.sdwy.community.model.User;
import life.sdwy.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,
                          HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(user == null)
            return "redirect:/";

        NotificationDTO notificationDTO = notificationService.read(id, user);
        if(notificationDTO.getType() == NotificationTypeEnum.REPLY_QUESTION.getType() || notificationDTO.getType() == NotificationTypeEnum.REPLY_COMMENT.getType())
            return "redirect:/question/"+notificationDTO.getOuterId();

        return "redirect:/";
    }
}
