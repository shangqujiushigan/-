package life.sdwy.community.service;

import life.sdwy.community.dto.NotificationDTO;
import life.sdwy.community.dto.PaginationDTO;
import life.sdwy.community.enums.NotificationStatusEnum;
import life.sdwy.community.enums.NotificationTypeEnum;
import life.sdwy.community.exception.CustomizeErrorCode;
import life.sdwy.community.exception.CustomizeException;
import life.sdwy.community.mapper.CommentMapper;
import life.sdwy.community.mapper.NotificationMapper;
import life.sdwy.community.mapper.QuestionMapper;
import life.sdwy.community.mapper.UserMapper;
import life.sdwy.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private CommentMapper commentMapper;


    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(example);
        totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        if (page < 1)
            page = 1;

        // 无发布记录
        totalPage = totalPage == 0 ? 1 : totalPage;

        paginationDTO.setPagination(totalPage, page);

        int offset = size * (page - 1);
        NotificationExample exampleById = new NotificationExample();
        exampleById.createCriteria().andReceiverEqualTo(userId);
        exampleById.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(exampleById, new RowBounds(offset, size));
        if(notifications.size() == 0)
            return paginationDTO;

        Set<Long> disUserIds = notifications.stream().map(notify -> notify.getNotifier()).collect(Collectors.toSet());
        ArrayList<Long> userIds = new ArrayList<>(disUserIds);

        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setNotifierName(userMap.get(notification.getNotifier()).getName());
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            String outerTitle="";
            if(notification.getType() == NotificationTypeEnum.REPLY_QUESTION.getType()) {
                QuestionExample questionExample = new QuestionExample();
                questionExample.createCriteria().andIdEqualTo(notification.getOuterId());
                outerTitle = questionMapper.selectByExample(questionExample).get(0).getTitle();
            }else if(notification.getType() == NotificationTypeEnum.REPLY_COMMENT.getType()){
                CommentExample commentExample = new CommentExample();
                commentExample.createCriteria().andIdEqualTo(notification.getOuterId());
                Comment comment = commentMapper.selectByExample(commentExample).get(0);

                QuestionExample questionExample = new QuestionExample();
                questionExample.createCriteria().andIdEqualTo(comment.getParentId());
                outerTitle = questionMapper.selectByExample(questionExample).get(0).getTitle();
            }
            notificationDTO.setOuterTitle(outerTitle);
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if(notification == null)
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        if(!notification.getReceiver().equals(user.getId()))
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        return notificationDTO;
    }
}
