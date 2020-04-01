package life.sdwy.community.service;

import life.sdwy.community.dto.CommentDTO;
import life.sdwy.community.enums.CommentTypeEnum;
import life.sdwy.community.enums.NotificationStatusEnum;
import life.sdwy.community.enums.NotificationTypeEnum;
import life.sdwy.community.exception.CustomizeErrorCode;
import life.sdwy.community.exception.CustomizeException;
import life.sdwy.community.mapper.*;
import life.sdwy.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0)
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType()))
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);

        if (comment.getType().equals(CommentTypeEnum.COMMENT.getType())) {
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null)
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            commentMapper.insert(comment);

            // 增加子评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incComment(parentComment);

            // 创建通知
            createNotify(comment, dbComment.getCommentator(), NotificationTypeEnum.REPLY_COMMENT, dbComment.getParentId());

        } else {
            // 发表评论
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null)
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incComment(question);

            // 创建通知
            createNotify(comment, question.getCreator(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }

    }

    private void createNotify(Comment comment, Long receiver, NotificationTypeEnum notificationType, Long outerId) {
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterId(outerId);
//        notification.setOuterId(comment.getParentId());
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByQuestionId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0)
            return new ArrayList<>();

        // 获取去重评论人
        Set<Long> commentators = comments.stream().map(Comment::getCommentator).collect(Collectors.toSet());
        UserExample userExample = new UserExample();
        List<Long> userIds = new ArrayList<>(commentators);

        //获取评论人并转为map
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        //将comment转换为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
