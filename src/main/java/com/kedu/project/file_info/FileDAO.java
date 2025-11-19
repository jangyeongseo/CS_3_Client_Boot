package com.kedu.project.file_info;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FileDAO {
    @Autowired
	private SqlSession mybatis;
    
    //1. 보드 시퀀스로 썸네일 파일리스트 뽑아오기
    public List<FileDTO> getThumsFromTo (List<Integer> seqList){
    	return mybatis.selectList("File.getThumsFromTo", seqList);
    }
    
    
}
