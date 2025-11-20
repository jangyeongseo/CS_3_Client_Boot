package com.kedu.project.baby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.user.UserDAO;

@Service
public class BabyService {
    @Autowired
    private BabyDAO dao;

    @Autowired
    private UserDAO userdao;

    public BabyDTO babyMypage(BabyDTO dto, String id){
        String parentsData = userdao.familyCode(id);
        dto.setFamily_code(parentsData);
        return dao.babyMypage(dto);
    }
}
