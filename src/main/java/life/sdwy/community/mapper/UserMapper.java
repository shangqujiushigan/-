package life.sdwy.community.mapper;

import life.sdwy.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO user(account_id,name,token,gmt_create,gmt_modified,avatar_url) VALUES " +
            "(#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    public void insert(User user);

    @Select("SELECT * FROM user WHERE token=#{token}")
    User findByToken(@Param("token") String token);

    @Select("SELECT * FROM user WHERE id=#{id}")
    User findById(@Param("id") Integer id);
}
