package com.kedu.project.file_info;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/file")
@RestController
public class FileController {
	@Autowired
    private FileService fileService;
	
	
	//다운로드용 gcs링크
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(String sysname, String file_type) throws Exception {
        Map<String, Object> data = fileService.getFileStream(sysname, file_type);
        if (data == null) return ResponseEntity.notFound().build();

        String oriName = (String) data.get("oriName");
        InputStream stream = (InputStream) data.get("stream");
        String encoded = URLEncoder.encode(oriName, "UTF-8").replaceAll("\\+", "%20");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", encoded);

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(stream));
    }
    
    //미리보기용 컨트롤러
    @PostMapping("/tempupload")
    public ResponseEntity<?> tempUpload(
        @RequestPart("file") MultipartFile file,
        @RequestParam("target_type") String targetType,
        @AuthenticationPrincipal String id
    ) {
    	//나중에 지워야함
    	id="test1";
    	
        String url = fileService.uploadTempFile(file, targetType, id);
        return ResponseEntity.ok(Map.of("url", url));
    }
	
	
	
}
