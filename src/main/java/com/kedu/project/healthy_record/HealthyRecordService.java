package com.kedu.project.healthy_record;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.baby.BabyDAO;
import com.kedu.project.baby.BabyDTO;
import com.kedu.project.user.UserDAO;

@Service
public class HealthyRecordService {
    @Autowired
    private HealthyRecordDAO dao;

    @Autowired
    private UserDAO userdao;

    @Autowired
    private BabyDAO babydao;

    public BabyDTO getBabyData(int babySeq, String id) {
        BabyDTO dto = new BabyDTO();
        dto.setFamily_code(userdao.familyCode(id));
        dto.setBaby_seq(babySeq);

        return babydao.babyMypage(dto);
    }

    public int insert(HealthyRecordDTO dto) {
        return dao.insert(dto);
    }

    public int delete(HealthyRecordDTO dto) {
        return dao.delete(dto);
    }

    public List<HealthyRecordDTO> selectList(int babySeq, String id){
        HealthyRecordDTO dto = new HealthyRecordDTO();
        dto.setBaby_seq(babySeq);
        dto.setUser_id(id);
        return dao.selectList(dto);
    }
}
