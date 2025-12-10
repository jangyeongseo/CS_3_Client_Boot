package com.kedu.project.daily_record;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.baby.BabyDAO;
import com.kedu.project.baby.BabyDTO;
import com.kedu.project.user.UserDAO;

@Service
public class DailyRecordService {
    @Autowired
    private DailyRecordDAO dao;
    @Autowired
    private UserDAO userdao;
    @Autowired
    private BabyDAO babydao;

    public List<DailyRecordDTO> getSleepGroup(String sleep_group_id) {
        return dao.getSleepGroup(sleep_group_id);
    }

    public List<DailyRecordDTO> getSleepRange(String baby_seq, String date, String id) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            return dao.getSleepRange(Integer.parseInt(baby_seq), date);
        } else {
            return null;
        }
    }

    public List<DailyRecordDTO> getDailyRecord(String start, String end, String baby_seq, String id) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            return dao.getDailyRecord(Integer.parseInt(baby_seq), start, end); // 아기시퀀스, 시작일, 끝나는일 가져오기
        } else {
            return null;
        }
    }

    public List<DailyRecordDTO> getTargetData(String date, String type, String baby_seq, String id) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            return dao.getTargetData(date, type, Integer.parseInt(baby_seq)); // 날짜, 타입, 애기시퀀스
        } else {
            return null;
        }
    }

    public int postData(List<DailyRecordDTO> records, String id, String baby_seq) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            for (DailyRecordDTO dto : records) {
                dto.setBaby_seq(Integer.parseInt(baby_seq));
                dto.setUser_id(id);
            }
            return dao.postData(records);
        } else {
            return 0;
        }
    }

    public int updateData(List<DailyRecordDTO> records, String id, int record_seq, String baby_seq) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            for (DailyRecordDTO dto : records) {
                dto.setBaby_seq(Integer.parseInt(baby_seq));
                dto.setUser_id(id);
                dto.setRecord_seq(record_seq);
            }
            return dao.updateData(records);
        } else {
            return 0;
        }
    }

    public int deleteSleepGroup(String baby_seq, String group_id, String id) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            return dao.deleteSleepGroup(baby_seq, group_id, id);
        } else {
            return 0;
        }
    }

    public int deleteData(String baby_seq, String record_seq, String id) {
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if (result != null) {
            return dao.deleteData(baby_seq, record_seq, id);
        } else {
            return 0;
        }
    }

}
