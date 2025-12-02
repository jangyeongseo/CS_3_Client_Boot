package com.kedu.project.user;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {
    @Autowired
    private SqlSession mybatis;

    public int idChack(UserDTO dto) {
        return mybatis.selectOne("user.idChack", dto.getUser_id());
    }

    public int nickNameChack(UserDTO dto) {
        return mybatis.selectOne("user.nickNameChack", dto.getNickname());
    }

    public int familyCodeChack(String family_code) {
        return mybatis.selectOne("user.familyCodeChack", family_code);
    }

    public int signup(UserDTO dto) {
        return mybatis.insert("user.signup", dto);
    }

    public String login(UserDTO dto) {
        return mybatis.selectOne("user.login", dto);
    }

    public UserDTO userDataById(UserDTO dto) {
        return mybatis.selectOne("user.userDataById", dto);
    }

    public String pindIdByEmail(UserDTO dto) {
        return mybatis.selectOne("user.pindIdByEmail", dto);
    }

    public int pindPwByEmail(UserDTO dto) {
        return mybatis.update("user.pindPwByEmail", dto);
    }

    public String familyCode(String user_id) {
        return mybatis.selectOne("user.selectFamilyCode", user_id);
    }

    public int updateLastBabySeq(int last_baby, String id) {
        return mybatis.update("user.updateLastBabySeq", Map.of(
                "last_baby", last_baby,
                "user_id", id));
    }

    public int mypageUdate(UserDTO dto) {
        return mybatis.update("user.mypageUdate", dto);
    }

    public int changeBaby(UserDTO dto){
        return mybatis.update("user.changeBaby", dto);
    }   
    
    //---------------------------------지원
    public List<UserDTO> getMyfamily (String family_code){
    	return mybatis.selectList("user.getMyfamily",family_code);
    }
   
    public int secessionUser(String user_id){
        return mybatis.delete("user.secessionUser", user_id);
    }

}
