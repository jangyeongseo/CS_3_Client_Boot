package com.kedu.project.baby;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kedu.project.user.UserDTO;

@Repository
public class BabyDAO {
    @Autowired
    private SqlSession mybatis;

    public List<Integer> getBabySeqList(UserDTO dto) {
        return mybatis.selectList("baby.getBabySeqList", dto);
    }

    public BabyDTO babyMypage(BabyDTO dto) {
        return mybatis.selectOne("baby.babyMypage", dto);
    }

    public int babyInsert(BabyDTO dto) {
        return mybatis.insert("baby.babyInsert", dto);
    }

    public List<BabyDTO> babyListByMypage(String family_code){
        return mybatis.selectList("baby.babyListByMypage", family_code);
    }

    public int babypageUpdate(BabyDTO dto){
        return mybatis.update("baby.babypageUpdate", dto);
    }
    
    
  //-----------지원 아기 시퀀스+부모 아이디로 출산예정일 or 생일 가져오기
    public String babyDueDate(Map<String, Object> params) {
    	return mybatis.selectOne("baby.babyDueDate", params);
    }

}
