package com.kedu.project.file_info;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service
public class FileService {
    @Autowired
    private FileDAO dao;
    @Value("${spring.cloud.gcp.storage.bucket:hwi_study}")  
    private String bucketName; // application.properties에서 가져옴
    @Autowired
    private Storage storage;
    @Autowired
    private Gson gson;
    
    //1. 보드 시퀀스로 썸네일 파일리스트 뽑아오기
    public List<FileDTO> getThumsFromTo (List<Integer> seqList) {
        if(seqList == null || seqList.isEmpty()) { 
            return List.of(); // 빈 배열이면 아예 DAO 호출 차단
        }
        return dao.getThumsFromTo(seqList);
    }
    
    //2. 파일 인서트 : 파일데이터, 타겟 타입, 타겟 시퀀스, 유저 아이디
    // ** 미리 보기 화면이 아닌 실제 파일로 올릴때 사용
    public int insert(MultipartFile[] files, String target_type,int target_seq,String user_id) {
    	if (files == null || files.length == 0) return 0;
    	
    	List<FileDTO> list= new ArrayList<>();
    	for(MultipartFile file:files) {
    		//1. gcs업로드
    		try {
    			String oriname = file.getOriginalFilename();
    			String sysname =UUID.randomUUID() + "_" + oriname;
    			String objectName = target_type + "/file/" + sysname; // board면 board/sysname이런식으로(gcs 폴더로 나눠서 쓰기 위함)
    			
    			BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                        .setContentType(file.getContentType()).build();
    			
    			try (InputStream is = file.getInputStream()){
    				storage.createFrom(blobInfo,is);
    			}
    		
    			FileDTO dto= FileDTO.builder()
   				     .oriname(oriname)
   				     .sysname(sysname)
   				     .user_id(user_id)
   				     .target_type(target_type+"/file")
   				     .target_seq(target_seq)
   				     .build();
    			
    			list.add(dto);
    		
    		}catch(Exception e) {
    			e.printStackTrace();
    			throw new RuntimeException("파일 업로드 중 오류 발생", e);
    		}
    	}
    	//2. db에 한번에 넘기기
    	return dao.insert(list);
    }
    
    //2-1. 파일 인서트 미리보기 : 파일데이터, 타겟 타입, 타겟 시퀀스, 유저 아이디 
    //**단, created_at, target_seq는 null 로 넣어서 구별함
    public String uploadTempFile(MultipartFile file, String target_type, String user_id) {
        try {
            String oriname = file.getOriginalFilename();
            String sysname = UUID.randomUUID() + "_" + oriname;
            String objectName = target_type + "/img/" + sysname;

            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                    .setContentType(file.getContentType()).build();

            try (InputStream is = file.getInputStream()) {
                storage.createFrom(blobInfo, is);
            }

            FileDTO dto = FileDTO.builder()
                    .oriname(oriname)
                    .sysname(sysname)
                    .user_id(user_id)
                    .target_type(target_type+"/img")
                    .build();

            dao.insertTemp(dto);

            // 프론트에서 img src에 바로 쓰는 URL 반환
            return "https://storage.googleapis.com/" + bucketName + "/" + objectName;

        } catch (Exception e) {
            throw new RuntimeException("임시 파일 업로드 중 오류", e);
        }
    }
    
  //2-2. 파일 임시저장-> 찐 저장으로 바꾸기
    public int confirmImg(String imageSysListJson, int target_seq) {
        if (imageSysListJson == null || imageSysListJson.isBlank()) {
            return 0;
        }
        List<String> imageSysList = gson.fromJson(
                imageSysListJson,
                new TypeToken<List<String>>() {}.getType()
        );
        if (imageSysList.isEmpty()) {
            return 0;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("imageSysList", imageSysList);
        params.put("target_seq", target_seq);

        return dao.confirmImg(params);
    }
    //2-3. 썸네일 사진 저장
    public int saveThumbnail(MultipartFile file, String target_type, int target_seq, String user_id) {
        try {
            String oriname = file.getOriginalFilename();
            String sysname = UUID.randomUUID() + "_" + oriname;
            String objectName = target_type + "/thumb/" + sysname;

            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                    .setContentType(file.getContentType()).build();

            try (InputStream is = file.getInputStream()) {
                storage.createFrom(blobInfo, is);
            }

            FileDTO dto = FileDTO.builder()
                    .oriname(oriname)
                    .sysname(sysname)
                    .user_id(user_id)
                    .target_type(target_type+"/thumb")
                    .build();

            return dao.saveThumbnail(dto);
        } catch (Exception e) {
            throw new RuntimeException("임시 파일 업로드 중 오류", e);
        }
    } 
    
    
    
    
    // 3. 파일 다운로드용 링크 받기 : 시스네임으로 구별
    public Map<String, Object> getFileStream(String sysname, String file_type) {
        Map<String, Object> result = new HashMap<>();
        String objectPath = file_type + sysname;
        Blob blob = storage.get(bucketName, objectPath);
        if (blob == null) return null;

        String oriName = dao.findOriNameBySysName(sysname);
        InputStream inputStream = new ByteArrayInputStream(blob.getContent());

        result.put("oriName", oriName);
        result.put("stream", inputStream);
        return result;
    }
    
    

    
    
}
