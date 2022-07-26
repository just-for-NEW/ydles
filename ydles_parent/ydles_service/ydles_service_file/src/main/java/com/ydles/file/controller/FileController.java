package com.ydles.file.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.file.util.FastDFSClient;
import com.ydles.file.util.FastDFSFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
public class FileController {
    @PostMapping("upload")
    public Result upload(MultipartFile file){
        // 文件上传

        try {
            // 1.补全属性
            // String name = file.getName(); // 不对,无法得到正确文件名，这是form表单name file
            // 下面正确得到全名 文件名+后缀名
            String originalFilename = file.getOriginalFilename();
            // 得到文件内容
            byte[] fileBytes = file.getBytes();
            // 文件拓展名 截取上面全名的后缀名
            String substring = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            // 2.创建所需实体类
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename,fileBytes,substring);
            // 3.调用工具类上传
            String[] upload = FastDFSClient.upload(fastDFSFile);

            String url = FastDFSClient.getTrackerUrl() + upload[0]+"/"+upload[1];

            return new Result(true, StatusCode.OK,"上传成功",url);
        }catch (Exception e){
            return new Result(false, StatusCode.ERROR,"上传失败"+e.getMessage());
        }

    }
}
