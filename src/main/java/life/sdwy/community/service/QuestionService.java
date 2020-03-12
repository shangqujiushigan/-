package life.sdwy.community.service;

import life.sdwy.community.dto.PaginationDTO;
import life.sdwy.community.dto.QuestionDTO;
import life.sdwy.community.mapper.QuestionMapper;
import life.sdwy.community.mapper.UserMapper;
import life.sdwy.community.model.Question;
import life.sdwy.community.model.User;
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

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = questionMapper.count();
        totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        if (page < 1)
            page = 1;
        paginationDTO.setPagination(totalPage, page);

        List<Question> questionList = questionMapper.list(size*(page-1), size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questionList) {
            User user = userMapper.findById(question.getCreator());
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
        Integer totalCount = questionMapper.countByUserId(userId);
        totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page > totalPage)
            page = totalPage;
        if (page < 1)
            page = 1;
        paginationDTO.setPagination(totalPage, page);


        List<Question> questionList = questionMapper.listByUserId(userId,size*(page-1), size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        for (Question question : questionList) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOS);
        return paginationDTO;
    }
}