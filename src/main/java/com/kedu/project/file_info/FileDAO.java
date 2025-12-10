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

    public List<FileDTO> getThumsFromTo(List<Integer> seqList) {
        return mybatis.selectList("File.getThumsFromTo", seqList);
    }

    public int insert(List<FileDTO> list) {
        return mybatis.insert("File.insert", list);
    }

    public int insertTemp(FileDTO dto) {
        return mybatis.insert("File.insertTemp", dto);
    }

    public int confirmImg(Map<String, Object> params) {
        return mybatis.update("File.confirmImg", params);
    }

    public int saveThumbnail(FileDTO dto) {
        return mybatis.insert("File.saveThumbnail", dto);
    }

    public String findOriNameBySysName(String sysname) {
        return mybatis.selectOne("File.findOriNameBySysName", sysname);
    }

    public List<FileDTO> getDetailBoardFile(Map<String, Object> params) {
        return mybatis.selectList("File.getDetailBoardFile", params);
    }

    public List<FileDTO> getFilesByParent(Map<String, Object> params) {
        return mybatis.selectList("File.getFilesByParent", params);
    }

    public int deleteAllFile(Map<String, Object> params) {
        return mybatis.delete("File.deleteAllFile", params);
    }

    public List<FileDTO> getTempFiles() {
        return mybatis.selectList("File.getTempFiles");
    }

    public int deleteFileBySysname(String sysname) {
        return mybatis.delete("File.deleteFileBySysname", sysname);
    }

    public List<FileDTO> getThumbnailsByBoardSeq(Map<String, Object> params) {
        return mybatis.selectList("File.getThumbnailsByBoardSeq", params);
    }

    public FileDTO getFileBySeq(int file_seq) {
        return mybatis.selectOne("File.getFileBySeq", file_seq);
    }

    public int deleteFileBySeq(int file_seq) {
        return mybatis.delete("File.deleteFileBySeq", file_seq);
    }

    public FileDTO findBySysname(String sysname) {
        return mybatis.selectOne("File.findBySysname", sysname);
    }
}
