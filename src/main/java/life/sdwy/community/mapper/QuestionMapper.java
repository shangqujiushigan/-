package life.sdwy.community.mapper;

import life.sdwy.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Select("SELECT count(1) FROM question")
    Integer count();

    @Select("SELECT count(1) FROM question WHERE creator = #{userId}")
    Integer countByUserId(@Param("userId") Integer userId);

    @Insert("INSERT INTO question(title,description,creator,tag,gmt_create,gmt_modified) VALUES " +
            "(#{title},#{description},#{creator},#{tag},#{gmtCreate},#{gmtModified})")
    public void create(Question question);

    @Select("SELECT * FROM question limit #{offset}, #{size}")
    public List<Question> list(@Param("offset") Integer offset, @Param("size") Integer size);

    @Select("SELECT * FROM question WHERE creator = #{userId} limit #{offset}, #{size}")
    public List<Question> listByUserId(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("size") Integer size);

}
