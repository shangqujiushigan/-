package life.sdwy.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001, "你找的问题不在了，要不换个试试~"),
    TARGET_PARAM_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    USER_NOT_LOGIN(2003, "用户未登录，请先登录"),
    SYS_ERROR(2004, "服务冒烟啦，要不稍等再来试试~"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在了，要不换个试试~"),
    CONTENT_IS_EMPTY(2007, "输入内容不能为空"),
    READ_NOTIFICATION_FAIL(2008, "兄弟你这是读别人的信息呢?"),
    NOTIFICATION_NOT_FOUND(2009, "消息飞到火星去了~"),
    FILE_UPLOAD_FAIL(2010, "文件上传失败")
    ;

    private String message;
    private Integer code;
    CustomizeErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode(){
        return code;
    }
}
