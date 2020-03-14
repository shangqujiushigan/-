package life.sdwy.community.service;

import life.sdwy.community.dto.PaginationDTO;
import life.sdwy.community.dto.QuestionDTO;
import life.sdwy.community.exception.CustomizeErrorCode;
import life.sdwy.community.exception.CustomizeException;
import life.sdwy.community.mapper.QuestionExtMapper;
import life.sdwy.community.mapper.QuestionMapper;
import life.sdwy.community.mapper.UserMapper;
import life.sdwy.community.model.Question;
import life.sdwy.community.model.QuestionExample;
import life.sdwy.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
        totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        if (page < 1)
            page = 1;
        paginationDTO.setPagination(totalPage, page);

        int offset = size * (page - 1);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOS);
        return paginationDTO;

    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(example);
        totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        if (page < 1)
            page = 1;

        // 无发布记录
        totalPage = totalPage == 0 ? 1 : totalPage;

        paginationDTO.setPagination(totalPage, page);

        int offset = size * (page - 1);
        QuestionExample exampleById = new QuestionExample();
        exampleById.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(exampleById, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOS);
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question =  questionMapper.selectByPrimaryKey(id);
        if(question == null)
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(userMapper.selectByPrimaryKey(question.getCreator()));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            // create
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else {
            // update
            question.setGmtModified(System.currentTimeMillis());
            int i = questionMapper.updateByPrimaryKeySelective(question);

            if(i != 1)
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
    }

    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}