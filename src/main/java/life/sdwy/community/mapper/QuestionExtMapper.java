package life.sdwy.community.mapper;

import life.sdwy.community.model.Question;
import life.sdwy.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);
    int incComment(Question record);
    List<Question> selectRelated(Question record);
}