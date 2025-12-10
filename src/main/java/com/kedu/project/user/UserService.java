package com.kedu.project.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.baby.BabyDAO;
import com.kedu.project.baby.BabyDTO;
import com.kedu.project.healthy_record.HealthyRecordDAO;
import com.kedu.project.healthy_record.HealthyRecordDTO;
import com.kedu.project.security.crypto.EncryptionUtil;
import com.kedu.project.security.jwt.JWTUtil;

@Service
public class UserService {
    @Autowired
    private UserDAO dao;

    @Autowired
    private BabyDAO babydao;

    @Autowired
    private HealthyRecordDAO healthyRecorddao;

    @Autowired
    private JWTUtil jwt;

    public int idChack(UserDTO dto) {
        return dao.idChack(dto);
    }

    public int nickNameChack(UserDTO dto) {
        return dao.nickNameChack(dto);
    }

    public int signup(UserDTO dto) {
        dto.setPassword(EncryptionUtil.encrypt(dto.getPassword()));
        String familyCode = dto.getFamily_code();
        if (familyCode == "") {
            dto.setFamily_code(familyCodeMake());
        }
        return dao.signup(dto);
    }

    public Map<String, String> login(UserDTO dto) {
        dto.setPassword(EncryptionUtil.encrypt(dto.getPassword()));
        String lastBabySeq = dao.login(dto);
        String token = jwt.createToken(dto.getUser_id());
        String familCode = dao.familyCode(dto.getUser_id());

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("babySeq", lastBabySeq);
        map.put("babyDueDate", babydao.babyDueDate(familCode, lastBabySeq));

        return map;
    }

    public String pindIdByEmail(UserDTO dto) {
        return dao.pindIdByEmail(dto);
    }

    public int pindPwByEmail(UserDTO dto) {
        dto.setPassword(EncryptionUtil.encrypt(dto.getPassword()));
        return dao.pindPwByEmail(dto);
    }

    public UserDTO mypage(String id) {
        UserDTO dto = new UserDTO();
        dto.setUser_id(id);
        return dao.userDataById(dto);
    }

    public int mypageUdate(UserDTO dto) {
        return dao.mypageUdate(dto);
    }

    public List<BabyDTO> babyListByMypage(String id) {
        String familyCode = dao.familyCode(id);
        List<BabyDTO> list = babydao.babyListByMypage(familyCode);
        LocalDate today = LocalDate.now();
        List<BabyDTO> result = new ArrayList<>();
        for (BabyDTO baby : list) {
            LocalDate birthDate = LocalDate.parse(baby.getBirth_date());
            if (birthDate.isAfter(today)) {
                if (!baby.getStatus().equals("fetus")) {
                    babydao.updateStatus(baby);
                    baby.setStatus("fetus");
                }
            } else {
                if (!baby.getStatus().equals("infant")) {
                    babydao.updateStatus(baby);
                    baby.setStatus("infant");
                }
            }
            result.add(baby);
        }
        return result;
    }

    public int changeBaby(UserDTO dto) {
        return dao.changeBaby(dto);
    }

    public List<HealthyRecordDTO> eventList(int babySeq, String id) {
        HealthyRecordDTO dto = new HealthyRecordDTO();
        dto.setBaby_seq(babySeq);
        dto.setUser_id(id);
        return healthyRecorddao.eventList(dto);
    }

    public int eventDelete(HealthyRecordDTO dto) {
        return healthyRecorddao.eventDelete(dto);
    }

    public int secessionUser(String id) {
        return dao.secessionUser(id);
    }

    public String familyCodeMake() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length = 5;
        while (true) {
            StringBuilder result = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int index = random.nextInt(characters.length());

                result.append(characters.charAt(index));
            }
            int exists = dao.familyCodeChack(result.toString());
            if (exists == 0) {
                return result.toString();
            }
        }
    }

}
