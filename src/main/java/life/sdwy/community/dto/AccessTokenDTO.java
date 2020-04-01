package life.sdwy.community.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class AccessTokenDTO {
    @JSONField(name = "client_id")
    private String clientId;

    @JSONField(name = "client_secret")
    private String clientSecret;

    @JSONField(name = "code")
    private String code;

    @JSONField(name = "redirect_uri")
    private String redirectUri;

    @JSONField(name = "state")
    private String state;
}
