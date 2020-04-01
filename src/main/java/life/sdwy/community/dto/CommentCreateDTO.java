package life.sdwy.community.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class CommentCreateDTO {
    @JSONField(name = "parent_id")
    private Long parentId;

    @JSONField(name = "content")
    private String content;

    @JSONField(name = "type")
    private Integer type;
}
