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
    private String bucketName;
    @Autowired
    private Storage storage;
    @Autowired
    private Gson gson;

    public List<FileDTO> getThumsFromTo(List<Integer> seqList) {
        if (seqList == null || seqList.isEmpty()) {
            return List.of();
        }
        List<FileDTO> result = dao.getThumsFromTo(seqList);

        return result;
    }

    public int insert(MultipartFile[] files, String target_type, int target_seq, String user_id) {
        if (files == null || files.length == 0)
            return 0;

        List<FileDTO> list = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String oriname = file.getOriginalFilename();
                String sysname = UUID.randomUUID() + "_" + oriname;
                String objectName = target_type + "/file/" + sysname;

                BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName))
                        .setContentType(file.getContentType()).build();

                try (InputStream is = file.getInputStream()) {
                    storage.createFrom(blobInfo, is);
                }

                FileDTO dto = FileDTO.builder()
                        .oriname(oriname)
                        .sysname(sysname)
                        .user_id(user_id)
                        .target_type(target_type + "/file")
                        .target_seq(target_seq)
                        .build();

                list.add(dto);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("파일 업로드 중 오류 발생", e);
            }
        }
        return dao.insert(list);
    }

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
                    .target_type(target_type + "/img")
                    .build();

            dao.insertTemp(dto);

            return "https://storage.googleapis.com/" + bucketName + "/" + objectName;

        } catch (Exception e) {
            throw new RuntimeException("임시 파일 업로드 중 오류", e);
        }
    }

    public int confirmImg(String imageSysListJson, int target_seq) {
        if (imageSysListJson == null || imageSysListJson.isBlank()) {
            return 0;
        }
        List<String> imageSysList = gson.fromJson(
                imageSysListJson,
                new TypeToken<List<String>>() {
                }.getType());
        if (imageSysList.isEmpty()) {
            return 0;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("imageSysList", imageSysList);
        params.put("target_seq", target_seq);

        return dao.confirmImg(params);
    }

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
                    .target_type(target_type + "/thumb")
                    .target_seq(target_seq)
                    .build();

            return dao.saveThumbnail(dto);
        } catch (Exception e) {
            throw new RuntimeException("임시 파일 업로드 중 오류", e);
        }
    }

    public void syncBoardImages(String imageSysListJson, int targetSeq, String userId, String target_type) {
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

        List<String> newSysList = gson.fromJson(
                imageSysListJson,
                new TypeToken<List<String>>() {
                }.getType());

        Map<String, Object> params = new HashMap<>();
        params.put("target_seq", targetSeq);
        params.put("target_type", target_type);
        params.put("user_id", userId);
        List<FileDTO> currentImgs = dao.getFilesByParent(params);

        if (currentImgs != null) {
            for (FileDTO img : currentImgs) {
                if (!newSysList.contains(img.getSysname())) {
                    String objectName = img.getTarget_type() + "/" + img.getSysname();
                    storage.delete(BlobId.of(bucketName, objectName));
                    dao.deleteFileBySysname(img.getSysname());
                }
            }
        }

        if (newSysList.isEmpty()) {
            return;
        }

        Map<String, Object> confirmParams = new HashMap<>();
        confirmParams.put("imageSysList", newSysList);
        confirmParams.put("target_seq", targetSeq);

        dao.confirmImg(confirmParams);
    }

    public Map<String, Object> getFileStream(String sysname, String file_type) {
        Map<String, Object> result = new HashMap<>();

        if (file_type.endsWith("/")) {
            file_type = file_type.substring(0, file_type.length() - 1);
        }

        String objectPath = file_type + "/" + sysname;
        Blob blob = storage.get(bucketName, objectPath);
        if (blob == null)
            return null;

        String oriName = dao.findOriNameBySysName(sysname);
        InputStream inputStream = new ByteArrayInputStream(blob.getContent());

        result.put("oriName", oriName);
        result.put("stream", inputStream);
        return result;
    }

    public List<FileDTO> getDetailBoardFile(int target_seq, String target_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("target_seq", target_seq);
        params.put("target_type", target_type + "/file");

        return dao.getDetailBoardFile(params);
    }

    public int deleteAllFile(int target_seq, String user_id, String target_type) {
        Map<String, Object> params1 = new HashMap<>();
        params1.put("target_seq", target_seq);
        params1.put("target_type", target_type);
        params1.put("user_id", user_id);
        List<FileDTO> files = dao.getFilesByParent(params1);

        if (files == null || files.isEmpty())
            return 0;

        for (FileDTO f : files) {
            try {
                String objectName = f.getTarget_type() + "/" + f.getSysname();
                storage.delete(BlobId.of(bucketName, objectName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("target_seq", target_seq);
        params.put("user_id", user_id);
        params.put("target_type", target_type);

        return dao.deleteAllFile(params);
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void cleanUpTemp() {
        List<FileDTO> files = dao.getTempFiles();
        if (files == null || files.isEmpty()) {
            return;
        }

        for (FileDTO file : files) {
            try {
                String objectPath = file.getTarget_type() + "/" + file.getSysname();
                boolean deleted = storage.delete(BlobId.of(bucketName, objectPath));// gcs 삭제

                if (deleted) {
                    dao.deleteFileBySysname(file.getSysname());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void deleteThumbnail(int board_seq) {
        Map<String, Object> params = new HashMap<>();
        params.put("target_seq", board_seq);
        params.put("target_type", "board/thumb");
        List<FileDTO> thumbs = dao.getThumbnailsByBoardSeq(params);

        if (thumbs == null || thumbs.isEmpty())
            return;

        for (FileDTO t : thumbs) {
            String objectName = t.getTarget_type() + "/" + t.getSysname(); // board/thumb/xxx
            storage.delete(BlobId.of(bucketName, objectName));
            dao.deleteFileBySysname(t.getSysname());
        }
    }

    public void replaceThumbnail(MultipartFile file, String targetType, int boardSeq, String userId) {
        deleteThumbnail(boardSeq);
        saveThumbnail(file, targetType, boardSeq, userId);
    }

    public void deleteFileBySeq(int file_seq) {
        FileDTO file = dao.getFileBySeq(file_seq);
        if (file == null)
            return;
        String objectName = file.getTarget_type() + "/" + file.getSysname();
        storage.delete(BlobId.of(bucketName, objectName));
        dao.deleteFileBySeq(file_seq);
    }

    public void replaceThumbnailBySysname(String sysname, String target_type, int target_seq, String user_id) {
        deleteThumbnail(target_seq);

        FileDTO original = dao.findBySysname(sysname);
        if (original == null)
            return;

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
