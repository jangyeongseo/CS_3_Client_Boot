package com.kedu.project.file_info;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    @Autowired
    private FileDAO dao;

    //1. 보드 시퀀스로 썸네일 파일리스트 뽑아오기
    public List<FileDTO> getThumsFromTo (List<Integer> seqList) {
        if(seqList == null || seqList.isEmpty()) { 
            return List.of(); // 빈 배열이면 아예 DAO 호출 차단
        }
        return dao.getThumsFromTo(seqList);
    }
    

}
