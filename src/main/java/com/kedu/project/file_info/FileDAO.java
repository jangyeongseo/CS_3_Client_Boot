package com.kedu.project.file_info;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FileDAO {
    @Autowired
	private SqlSession mybatis;
    
    // 1. 보드 시퀀스로 썸네일 파일리스트 뽑아오기
    public List<FileDTO> getThumsFromTo (List<Integer> seqList){
    	return mybatis.selectList("File.getThumsFromTo", seqList);
    }
    
    //2. 인서트
    public int insert(List<FileDTO> list) {
    	return mybatis.insert("File.insert",list);
    }
    
    //2-1. 임시 인서트 (파일 미리보기용 url 위해서 따로 만듦) ** 해당 것 created_at을 null로 해놓아야지 구별 가능
    public int insertTemp(FileDTO dto) {
    	return mybatis.insert("File.insertTemp",dto);
    }
    
    //2-2. 파일 임시저장-> 찐 저장으로 바꾸기
    public int confirmImg(Map<String, Object>params) {
    	return mybatis.update("File.confirmImg", params);
    }
    
    //2-3. 썸네일 저장
    public int saveThumbnail(FileDTO dto) {
    	return mybatis.insert("File.saveThumbnail",dto);
    }
    
    //3. 시스네임으로 oriname 찾기
    public String findOriNameBySysName(String sysname) {
		return mybatis.selectOne("File.findOriNameBySysName", sysname);
	}

    
}
