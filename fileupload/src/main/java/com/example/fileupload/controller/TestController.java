package com.example.fileupload.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "Hello";
    }

    @RequestMapping(value = "/test/upload", method = RequestMethod.POST)
    public String testUpload(@RequestParam("file") MultipartFile[] multipartFile, HttpServletResponse response) {
        String path = "D:/study_in_covision/vanilaHtml/file/";
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }

            for (MultipartFile multiFile : multipartFile) {

                System.out.println(
                        String.format("multipartFile.getContentType() : %s,\r\n" +
                                "multipartFile.getOriginalFilename() : %s ,\r\n" +
                                "multipartFile.getBytes() : %s",
                                multiFile.getContentType().toString(),
                                multiFile.getOriginalFilename(),
                                multiFile.getBytes().length));
                
                FileOutputStream outputStream = new FileOutputStream(path + multiFile.getOriginalFilename());
                outputStream.write(multiFile.getBytes());
                outputStream.close();
            }
            return "File Upload Success";

        } catch (Exception e) {
            System.out.println(e);
            return "File Upload Failed";
        }

    };

    @RequestMapping(value = "/test/download", method = RequestMethod.GET)
    public void testDownload(@RequestParam("filename") String filename, HttpServletResponse response,
            HttpServletRequest request) throws UnsupportedEncodingException {
        String path = "D:/study_in_covision/vanilaHtml/file/";
        File file = new File(path + filename);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        System.out.println("file-lenght=" + file.length());
        response.setContentType("application/octet-stream;charset=utf-8");
        // response.setContentType("text/plain;charset=utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Disposition", getDisposition(filename, check_browser(request)));// 파일 강제 다운로드
        response.setHeader("Content-Transfer-Encoding", "binary");

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, read);
            }
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(e);
        }
        System.out.println("파일 다운로드 끝");
    }

    @RequestMapping(value = "/test/view", method = RequestMethod.GET)
    public @ResponseBody HashMap<String,String> testView(@RequestParam("filename") String filename, HttpServletResponse response) throws IOException {
        String path = "D:/study_in_covision/vanilaHtml/file/";
        File file = new File(path + filename);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        String mimeType = Files.probeContentType(file.toPath());
        System.out.println("MIME Type: " + mimeType);

        byte[] imageBytes = Files.readAllBytes(file.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        System.out.println("imageBytes : "+imageBytes);
        System.out.println("base64Image : "+base64Image);
        String returnString = "data:" + mimeType + ";base64," + base64Image;
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("url", returnString);
        map.put("type", mimeType);
        return map;

    }

    private String check_browser(HttpServletRequest request) {
        String browser = "";
        String header = request.getHeader("User-Agent");
        // 신규추가된 indexof : Trident(IE11) 일반 MSIE로는 체크 안됨
        if (header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1) {
            browser = "ie";
        }
        // 크롬일 경우
        else if (header.indexOf("Chrome") > -1) {
            browser = "chrome";
        }
        // 오페라일경우
        else if (header.indexOf("Opera") > -1) {
            browser = "opera";
        }
        // 사파리일 경우
        else if (header.indexOf("Apple") > -1) {
            browser = "sarari";
        } else {
            browser = "firfox";
        }
        return browser;
    }

    private String getDisposition(String down_filename, String browser_check) throws UnsupportedEncodingException {
        String prefix = "attachment;filename=";
        String encodedfilename = null;
        System.out.println("browser_check:" + browser_check);
        if (browser_check.equals("ie")) {
            encodedfilename = URLEncoder.encode(down_filename, "UTF-8").replaceAll("\\+", "%20");
        } else if (browser_check.equals("chrome")) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < down_filename.length(); i++) {
                char c = down_filename.charAt(i);
                if (c > '~') {
                    sb.append(URLEncoder.encode("" + c, "UTF-8"));
                } else {
                    sb.append(c);
                }
            }
            encodedfilename = sb.toString();
        } else {
            encodedfilename = "\"" + new String(down_filename.getBytes("UTF-8"), "8859_1") + "\"";
        }
        return prefix + encodedfilename;
    }
}
