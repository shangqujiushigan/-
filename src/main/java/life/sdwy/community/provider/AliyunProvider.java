package life.sdwy.community.provider;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import life.sdwy.community.exception.CustomizeErrorCode;
import life.sdwy.community.exception.CustomizeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Component
public class AliyunProvider {
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    public String uploadFile(InputStream fileInputStream, String fileName) {
        if(fileInputStream == null)
            return null;
        String generateFileName;
        String[] split = fileName.split("\\.");
        if(split.length>=1)
            generateFileName = UUID.randomUUID().toString()+"."+split[split.length-1];
        else
            return null;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, generateFileName, fileInputStream);

        // 上传文件。
        PutObjectResult result = ossClient.putObject(putObjectRequest);
        if(result != null){
            // 设置URL过期时间为1小时。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            URL url = ossClient.generatePresignedUrl(bucketName, generateFileName, expiration);
            ossClient.shutdown();
            return url.toString();
        }else
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
    }

}
