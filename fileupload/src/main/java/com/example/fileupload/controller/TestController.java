package com.example.fileupload.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
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
        
    };

    @RequestMapping(value = "/test/download", method = RequestMethod.POST)
    public String testDownload(@RequestParam("filename") String filename, HttpServletResponse response){
        String path = "D:/study_in_covision/vanilaHtml/file/";
        File file = new File(path+filename);
        if (!file.exists()) return "File Not Found";
        
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setContentLength(Integer.valueOf((int) file.length()));
        response.setHeader("Access-Control-Allow-Origin", "*");
        //response.setHeader("Content-Disposition", FileHelper.getDisposition(fileDTO.getFileName(), FileHelper.getBrowser(request.getHeader("User-Agent"))));
        response.setHeader("Content-Transfer-Encoding", "binary");

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[11410];
            int read;
            while ((read = inputStream.read(buffer))!= -1) {
                response.getOutputStream().write(buffer,0,read);
                response.getOutputStream().flush();
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        return "File Download Success";

    }

    // @RequestMapping(value = "/test/view", method = RequestMethod.GET, produces=MediaType.IMAGE_PNG_VALUE)
    // public void testView(@RequestParam("filename") String filename, HttpServletResponse response){
    //     response.setContentType("application/octet-stream");
    //     response.setHeader("Content-Disposition", "attachment; filename="+filename);
}
