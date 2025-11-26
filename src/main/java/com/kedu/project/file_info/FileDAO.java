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
    
    //4. 타겟 시퀀스+ 타겟 타입으로 해당하는 파일 배열 가져오기
    public List<FileDTO> getDetailBoardFile(Map<String, Object>params){
    	return mybatis.selectList("File.getDetailBoardFile", params);
    }
    
    //5.부모 시퀀스에 따라서 딸려있는 첨부파일+이미지들 전부 가져오기
    public List<FileDTO> getFilesByParent(Map<String, Object>params) {
    	return mybatis.selectList("File.getFilesByParent", params);
    }
    //5-1.부모 시퀀스에 따라서 딸려있는 첨부파일+이미지들 전부 지우기
    public int deleteAllFile(Map<String, Object>params) {
    	return mybatis.delete("File.deleteAllFile", params);
    }
    
    //6.created_at + target_seq 가 null인 파일 목록 가져오기
    public List<FileDTO> getTempFiles(){
    	return mybatis.selectList("File.getTempFiles");
    }
    
    //7. sysname으로 삭제하기
    public int deleteFileBySysname(String sysname) {
    	return mybatis.delete("File.deleteFileBySysname",sysname);
    }
    
    //8. 보드시퀀스로 썸네일 가져오기
    public List<FileDTO> getThumbnailsByBoardSeq (Map<String, Object> params){
    	return mybatis.selectList("File.getThumbnailsByBoardSeq", params);
    }
    
    //9. 파일 시퀀스로 dto 가져오기
    public FileDTO getFileBySeq(int file_seq) {
    	return mybatis.selectOne("File.getFileBySeq", file_seq);
    }
    
    //10. 파일 시퀀스로 db에서 삭제하기
    public int deleteFileBySeq(int file_seq) {
    	return mybatis.delete("File.deleteFileBySeq",file_seq);
    }
    
    //11. 시스네임을 dto 가져오기
    public FileDTO findBySysname(String sysname) {
    	return mybatis.selectOne("File.findBySysname",sysname);
    }
}
