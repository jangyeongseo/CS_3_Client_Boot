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

    public BabyDTO selectBabyInfo(BabyDTO dto) {
    	return mybatis.selectOne("baby.selectBabyInfo", dto);
    }
    
   
    public List<BabyDTO> babyListByMypage(String family_code){
        return mybatis.selectList("baby.babyListByMypage", family_code);
    }

    public int babypageUpdate(BabyDTO dto) {
        return mybatis.update("baby.babypageUpdate", dto);
    }

    public int updateStatus(BabyDTO dto){
        return mybatis.update("baby.updateStatus", dto);
    }

    public String babyDueDate(String familCode, String babySeq) {
        return mybatis.selectOne("baby.babyDueDate", Map.of(
                "family_code", familCode,
                "baby_seq", babySeq));
    }
}
