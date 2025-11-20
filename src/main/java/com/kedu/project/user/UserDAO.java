package com.kedu.project.user;

import java.util.List;

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

    public String familyCode(String user_id){
        return mybatis.selectOne("user.selectFamilyCode", user_id);
    }
}
