package life.sdwy.community.controller;

import life.sdwy.community.dto.CommentCreateDTO;
import life.sdwy.community.dto.CommentDTO;
import life.sdwy.community.dto.ResultDTO;
import life.sdwy.community.enums.CommentTypeEnum;
import life.sdwy.community.exception.CustomizeErrorCode;
import life.sdwy.community.model.Comment;
import life.sdwy.community.model.User;
import life.sdwy.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");

        if(user == null)
            return ResultDTO.errorOf(CustomizeErrorCode.USER_NOT_LOGIN);

        if(commentCreateDTO == null || StringUtils.isEmpty(commentCreateDTO.getContent()))
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0);
        comment.setCommentCount(0);
        commentService.insert(comment);

        return ResultDTO.successOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> subComments(@PathVariable(name = "id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByQuestionId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.successOf(commentDTOS);
    }
}