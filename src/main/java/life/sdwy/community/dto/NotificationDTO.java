package life.sdwy.community.dto;

import life.sdwy.community.enums.NotificationStatusEnum;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Integer type;
    private String typeName;
    private Integer notifier;
    private String notifierName;
    private Long outerId;
    private String outerTitle;

    public boolean isUnread(){
        return status == NotificationStatusEnum.UNREAD.getStatus();
    }
}
