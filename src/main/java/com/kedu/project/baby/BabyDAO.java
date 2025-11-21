package com.kedu.project.baby;

import java.util.List;

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

    public BabyDTO selectBabyInfo(int babySeq) {
        //  mybatis.selectOne(Mapper ID, Parameter) 호출
    	return mybatis.selectOne("baby.selectBabyInfo", babySeq);
    }
    
    
}
