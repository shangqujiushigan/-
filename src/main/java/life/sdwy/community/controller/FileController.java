package life.sdwy.community.controller;

import life.sdwy.community.dto.FileDTO;
import life.sdwy.community.provider.AliyunProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {

    @Autowired
    private AliyunProvider aliyunProvider;

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    @ResponseBody
    public FileDTO upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
        FileDTO fileDTO = new FileDTO();
        try {
            String url = aliyunProvider.uploadFile(file.getInputStream(), file.getOriginalFilename());
            fileDTO.setSuccess(1);
            fileDTO.setMessage("1");
            fileDTO.setUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileDTO;
    }
}
