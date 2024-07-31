package com.example.fileupload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(){
        return "Hello";
    }

    @RequestMapping(value = "/test/upload", method = RequestMethod.POST)
    public String testUpload(@RequestParam("file") MultipartFile[] multipartFile, HttpServletResponse response){
        String path = "D:/study_in_covision/vanilaHtml/file/";
        try {
            File file = new File(path);
            if (!file.exists()){
                file.mkdirs();
            }

            for (MultipartFile multiFile : multipartFile) {

                System.out.println(
                    String.format("multipartFile.getContentType() : %s,\r\n" +
                        "multipartFile.getOriginalFilename() : %s ,\r\n" +
                        "multipartFile.getBytes() : %s",
                        multiFile.getContentType().toString(),
                        multiFile.getOriginalFilename(),
                        multiFile.getBytes().length
                    )
                );
        
                FileOutputStream outputStream = new FileOutputStream(path+multiFile.getOriginalFilename());
                outputStream.write(multiFile.getBytes());
                outputStream.close();
            }
        return "File Upload Success";
        
        }catch (Exception e){
            System.out.println(e);
            return "File Upload Failed";
        }
        
    }
}
