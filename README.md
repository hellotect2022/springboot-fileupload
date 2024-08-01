# 서버에서 파일을 다운로드 하려면

- 단순히 파일을 응답으로 보내주는 것 외에 **헤더에** **`Content-Disposition`** **필드가 필요**
- **`attachment;` 다운로드를 강제**
- **`inline` : 컨텐츠를 display**

```java
response.setHeader("Content-Disposition","attachment; filename=\"+filename+\"")
```

## SpringBoot 파일 다운로드 코드

```java
@RequestMapping(value = "/test/download", method = RequestMethod.GET)
    public void testDownload(@RequestParam("filename") String filename, HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException{
        String path = "D:/study_in_covision/vanilaHtml/file/";
        File file = new File(path+filename);
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        System.out.println("file-lenght="+file.length());
        **response.setContentType("application/octet-stream;charset=utf-8");**
        **response.setContentLength((int) file.length());**
        response.setHeader("Access-Control-Allow-Origin", "*");
        **response.setHeader("Content-Disposition",getDisposition(filename, check_browser(request)));// 파일 강제 다운로드
        response.setHeader("Content-Transfer-Encoding", "binary");**

        try (FileInputStream inputStream = new FileInputStream(file)) {
            **byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer))!= -1) {
                response.getOutputStream().write(buffer,0,read);
            }
            response.getOutputStream().flush();**
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(e);
        }
        System.out.println("파일 다운로드 끝");
    }
```

**Content-Type : `application/octet-stream;charset=utf-8` ⇒ Stream 형식으로 응답**

**Conatent-Length** : **(int) file.length() ⇒ 응답 받을 컨텐츠의 byte 길이값을 알아야 Client 가 쪼개서 들어오는 응답을 받을 수 있음**

**Content-Disposition :** "**`Content-Disposition","attachment; filename=\"+filename+\"`**" ⇒ 브라우저에서 파일 강제 다운로드

**Content-Transfer-Encoding : `binary` ⇒ 서버에서 이진데이타로 Stream 을 보냄**

```java
try (FileInputStream inputStream = new FileInputStream(file)) {
            **byte[] buffer = new byte[4096];  서버에서 한번에 받을 데이타 사이즈는 4096 byte (= 4kb)
            int read;
            while ((read = inputStream.read(buffer))!= -1) { 4kb 씩 스트림에서 읽어 buffer 에저장 read 에는 읽어드린 사이즈
                response.getOutputStream().write(buffer,0,read); 시작 : 0 , read 길이까지 buffer 데이타 쓰기
            }
            response.getOutputStream().flush();  : 쓴 데이타 밀어넣기 함**
        }
```

# 2. 자바스크립트로 브라우저에서 파일을 다운로드 하는방법

## Blob

Blob 객체는 파일류의 불변하는 미가공 데이타 

텍스트 && 이진데이타(binary) 형태로 읽을 수 있음, ReadableStream 

File 은 Blob 인터페이스를 상속해 확장한 것 

## AJAX 로 요청 보내서 파일 다운로드 가 안되는 이유

**AJAX 요청은** 

- 비동기적으로 데이터를 받아와서 페이지 일부를 업데이트 ,
- 백그라운드에서 데이터를 처리하는데 사용됨

### 문제의 원인

1. 파일 다운로드 방식 **아래 2가지만 허용됨**
- 브라우저는 **주소창에 URL 입력**
- **폼 제출 요청**에서 파일이 반환될때

 

1. **이유 :  브라우저 보안 제한**
- **스크립트가 사용자 상호작용 없이** 파일을 다운로드 하거나 저장을 막기위한 브라우저 보안 조치

### 해결 방법 link(<a>) 태그를 활용

<a> 태그를 사용하여  javascript 가 다운로드를 트리거 하게끔 한다. 

```jsx
$.ajax({
    url: 'http://localhost:9000/test/download?filename=mr-0.png',
    method: 'GET',
    **xhrFields: {
        responseType: 'blob'  // 이 설정을 통해 Blob 형식으로 응답을 받음
    },**
    success: function(data) {
        **var link = document.createElement('a');
        link.href = window.URL.createObjectURL(data);
        link.download = 'mr-0.png';
        link.click();**
    },
    error: function(error) {
        console.log('Error:', error);
    }
});
```

**`window.URL.createObjectURL(data);` : url 을 만드는 web api** 

- 파라미터로 받은 객체를 가리키는 URL 을 DOMstring(UTF-16) 문자열로 리턴
- 해당 document 내에서만 local 하게 유효함 , document 창이 닫히면 자동으로 무효화 됨

**`window.URL.revokeObjectURL(downloadUrl);` : 생성된 URL 을 해제함 브라우저 메모리에서 삭제함**

**`window.open(url)` : URL 을 통해 이미지 디스플레이** 

**`<a target=_blank>`  : URL 을 통해 이미지 디스플레이** 

<aside>
🚫 **blob 이 stream 데이타면 미리 보기 안됨/ stream 은 이진데이타로 모든 컨텐츠의 기본값임 
필요하면 content-type을  `application/octect-stream` 이 아닌 `image/jpeg`나 `application/pdf`  로 지정이 필요함**

</aside>

# 3. 왜 react 에서는 자동으로 서버에서 요청한 url 과 filename 으로 파일 다운로드가 되었는가

react 에서 사용하는 npm 모듈 **`js-file-download` 안에 위에서 설명한 로직이 구현되어있다.**
