const guid = () => {
    const s4 = () => {
      return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    };
  
    return (
      s4() +
      s4() +
      '-' +
      s4() +
      '-' +
      s4() +
      '-' +
      s4() +
      '-' +
      s4() +
      s4() +
      s4()
    );
  };

const downloadFile = (url, filename) => {
    $.ajax({             
        type: "GET",          
        url: url,  
        processData: true, // data 값을 쿼리 스트링으로 변환하지 않도록 설정
        cache: false, // 캐시 사용 안 함     
        xhrFields: {
            responseType: 'blob'  // 이 설정을 통해 Blob 형식으로 응답을 받음
        },
        data: {
            "filename": filename  // filename 파라미터 전달
        },                
        //timeout: 600000,       
        success: function (data) { 
            //console.log('data',data)
            var link = document.createElement('a');
            var objectUrl = window.URL.createObjectURL(data);
            link.href = objectUrl;
            link.download = filename;
            link.click()
            link.remove();
            window.URL.revokeObjectURL(objectUrl); // URLrevokeObjectURL() : URL로부터 blob URL을 해제
            //console.log('link',link)
                         
        },          
        error: function (e) {  
            console.log("ERROR : ", e);       
        }     
    });
}

const viewFile = (url, filename) => {
    $.ajax({             
        type: "GET",          
        url: url,  
        processData: true, // data 값을 쿼리 스트링으로 변환하지 않도록 설정
        cache: false, // 캐시 사용 안 함     
        // xhrFields: {
        //     responseType: 'blob'  // 이 설정을 통해 Blob 형식으로 응답을 받음
        // },
        data: {
            "filename": filename  // filename 파라미터 전달
        },                
        //timeout: 600000,       
        success: function (data) {
            let img;
            if (data.type.includes("pdf")) {
                img = document.createElement('iframe');
                img.style = 'width:100%;height:600px;'
            }else{
                img = document.createElement('img');
            }
            img.src = data.url

            let div = document.getElementById("image-area");
            div.innerHTML = '';
            div.appendChild(img);
        },          
        error: function (e) {  
            console.log("ERROR : ", e);       
        }     
    });
}
