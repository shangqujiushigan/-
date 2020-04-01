package life.sdwy.community.dto;

import life.sdwy.community.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private String content;
    private Integer commentCount;
    private Integer likeCount;
    private Long gmtCreate;
    private Long gmtModified;
    private User user;
}
