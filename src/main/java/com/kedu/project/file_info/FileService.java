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
import org.springframework.scheduling.annotation.Scheduled;
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
        List<FileDTO> result =dao.getThumsFromTo(seqList);
        
         return result;
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
                    .target_seq(target_seq)
                    .build();

            return dao.saveThumbnail(dto);
        } catch (Exception e) {
            throw new RuntimeException("임시 파일 업로드 중 오류", e);
        }
    } 

    // 2-4. board/img 업데이트용 싱크 로직
    public void syncBoardImages(String imageSysListJson, int targetSeq, String userId, String target_type) {
        // 0. 이미지 리스트 없음 → 기존 이미지를 전부 삭제
        if (imageSysListJson == null || imageSysListJson.isBlank()) {
            Map<String, Object> params = new HashMap<>();
            params.put("target_seq", targetSeq);
            params.put("target_type", target_type);
            params.put("user_id", userId);

            List<FileDTO> currentImgs = dao.getFilesByParent(params);

            if (currentImgs != null) {
                for (FileDTO img : currentImgs) {
                    String objectName = img.getTarget_type() + "/" + img.getSysname();
                    storage.delete(BlobId.of(bucketName, objectName));
                    dao.deleteFileBySysname(img.getSysname());
                }
            }
            return;
        }

        // 1. 프론트에서 받은 새로운 sysname 리스트
        List<String> newSysList = gson.fromJson(
                imageSysListJson,
                new TypeToken<List<String>>() {}.getType()
        );

        // 2. 현재 DB에 묶인 이미지를 가져옴
        Map<String, Object> params = new HashMap<>();
        params.put("target_seq", targetSeq);
        params.put("target_type", target_type);
        params.put("user_id", userId);
        List<FileDTO> currentImgs = dao.getFilesByParent(params);

        
        // 3. DB에는 있는데 newSysList에는 없는 이미지 → 삭제
        if (currentImgs != null) {
            for (FileDTO img : currentImgs) {
                if (!newSysList.contains(img.getSysname())) {
                    String objectName = img.getTarget_type() + "/" + img.getSysname();
                    storage.delete(BlobId.of(bucketName, objectName));
                    dao.deleteFileBySysname(img.getSysname());
                }
            }
        }


        // 4. 남아 있는 이미지가 없다면 confirmImg 불필요 → 종료
        if (newSysList.isEmpty()) {
            return;
        }


        // 5. 임시파일(board/img, seq=null) 중 newSysList에 포함된 애들만 확정
        Map<String, Object> confirmParams = new HashMap<>();
        confirmParams.put("imageSysList", newSysList);
        confirmParams.put("target_seq", targetSeq);

        dao.confirmImg(confirmParams);
    }

    
    // 3. 파일 다운로드용 링크 받기 : 시스네임으로 구별
    public Map<String, Object> getFileStream(String sysname, String file_type) {
        Map<String, Object> result = new HashMap<>();
        
        if (file_type.endsWith("/")) {
            file_type = file_type.substring(0, file_type.length() - 1);
        }

        String objectPath = file_type + "/" + sysname;
        Blob blob = storage.get(bucketName, objectPath);
        if (blob == null) return null;

        String oriName = dao.findOriNameBySysName(sysname);
        InputStream inputStream = new ByteArrayInputStream(blob.getContent());

        result.put("oriName", oriName);
        result.put("stream", inputStream);
        return result;
    }
    
    //4. 타겟 시퀀스+ 타겟 타입으로 해당하는 파일 배열 가져오기
    public List<FileDTO> getDetailBoardFile(int target_seq, String target_type) {
    	 Map<String, Object> params = new HashMap<>();
    	 params.put("target_seq", target_seq);
    	 params.put("target_type", target_type+"/file");
    	 
    	 return dao.getDetailBoardFile(params);
    }

    //5.부모 시퀀스에 따라서 딸려있느 첨부파일+이미지들 전부 지우기
    public int deleteAllFile(int target_seq, String user_id, String target_type) {
    	// 1. 파일이 없다면 리턴
    	Map<String, Object> params1 = new HashMap<>();
    	params1.put("target_seq", target_seq);
    	params1.put("target_type", target_type);
    	params1.put("user_id", user_id);
    	List<FileDTO> files = dao.getFilesByParent(params1);
    	
        if (files == null || files.isEmpty()) return 0;
        
        // 2. GCS에서도 파일 삭제
        for (FileDTO f : files) {
            try {
                String objectName = f.getTarget_type() + "/" + f.getSysname();
                storage.delete(BlobId.of(bucketName, objectName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    	
        // 2. DB에서도 파일 삭제
    	Map<String, Object> params = new HashMap<>();
    	params.put("target_seq", target_seq);
    	params.put("user_id", user_id);
    	params.put("target_type", target_type);
    	
    	return dao.deleteAllFile(params);
    }
    
    //6. 새벽 4시마다 미리보기용 임시파일 db+gcs정리
    @Scheduled (cron="0 49 08 * * *")
    public void cleanUpTemp() {
    	// 1. created_at + target_seq 가 null인 파일 목록 가져오기
    	List<FileDTO> files =dao.getTempFiles();
        if (files == null || files.isEmpty()) {
            return;
        }
        
        //2. gcs에서 삭제 + db삭제
        for(FileDTO file :files) {
        	try {
        		String objectPath = file.getTarget_type()+"/"+file.getSysname();
        		boolean deleted = storage.delete(BlobId.of(bucketName, objectPath));//gcs 삭제
        		
        		if(deleted) {
        			dao.deleteFileBySysname(file.getSysname());//db 삭제
        			System.out.println("gcs삭제 성공"+objectPath);
        		}else {
        			System.out.println("gcs삭제 실패"+objectPath);
        		}
        		
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
        
    }
    
    //7. 썸네일 삭제
    public void deleteThumbnail(int board_seq) {
        // 1. DB에서 board_seq + target_type = 'board/thumb' 인 파일 조회
    	Map<String, Object> params = new HashMap<>();
    	params.put("target_seq", board_seq);
    	params.put("target_type", "board/thumb");
        List<FileDTO> thumbs = dao.getThumbnailsByBoardSeq(params); 

        if (thumbs == null || thumbs.isEmpty()) return;

        for (FileDTO t : thumbs) {
            // GCS 삭제
            String objectName = t.getTarget_type() + "/" + t.getSysname(); // board/thumb/xxx
            storage.delete(BlobId.of(bucketName, objectName));
            // DB 삭제
            dao.deleteFileBySysname(t.getSysname()); // 시스네임으로 db삭제
        }
    }
    
    //8.썸네일 교체
    public void replaceThumbnail(MultipartFile file, String targetType, int boardSeq, String userId) {
        // 기존 썸네일 있으면 삭제
        deleteThumbnail(boardSeq);
        // 새 썸네일 저장 (이미 있는 saveThumbnail 재사용)
        saveThumbnail(file, targetType, boardSeq, userId);
    }
    
    //9. 시퀀스번호로 삭제
    public void deleteFileBySeq(int file_seq) {
        // 1. 파일 정보 조회
        FileDTO file = dao.getFileBySeq(file_seq);
        if (file == null) return;
        // 2. GCS 경로 생성
        String objectName = file.getTarget_type() + "/" + file.getSysname();
        // 3. GCS 삭제
        storage.delete(BlobId.of(bucketName, objectName));
        // 4. DB 삭제
        dao.deleteFileBySeq(file_seq);
    }
    
    //9. 썸네일 순서가 변경되엇을시 다시 썸네일 부여
    public void replaceThumbnailBySysname(String sysname, String target_type, int target_seq, String user_id) {
        // 1. 기존 썸네일 삭제 (DB + GCS)
        deleteThumbnail(target_seq);

        // 2. 기존 이미지 파일 정보 조회
        FileDTO original = dao.findBySysname(sysname);
        if (original == null) return;

        // 3. 썸네일용 DTO로 재삽입
        FileDTO thumb = FileDTO.builder()
                .oriname(original.getOriname())
                .sysname(original.getSysname())
                .user_id(user_id)
                .target_type(target_type + "/thumb")
                .target_seq(target_seq)
                .build();

        dao.saveThumbnail(thumb);
    }

}
