package com.kedu.project.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.baby.BabyDAO;
import com.kedu.project.security.crypto.EncryptionUtil;
import com.kedu.project.security.jwt.JWTUtil;

@Service
public class UserService {
    @Autowired
    private UserDAO dao;

    @Autowired
    private BabyDAO babydao;

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
        // 유저 정보 포장
        UserDTO user = dao.userDataById(dto);
        System.out.println(user.getUser_id());
        // 애기 시퀀스 ( return용 )
        String babySeq = String.valueOf(user.getLast_baby());
        System.out.println(babySeq);
        // 유저의 애기 시퀀스 리스트
        List<Integer> babySeqList = babydao.getBabySeqList(user);
        String token = jwt.createToken(dto.getUser_id(), babySeqList);

        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("babySeq", babySeq);

        return map;
    }

    public String pindIdByEmail(UserDTO dto) {
        return dao.pindIdByEmail(dto);
    }

    public int pindPwByEmail(UserDTO dto) {
        dto.setPassword(EncryptionUtil.encrypt(dto.getPassword()));
        return dao.pindPwByEmail(dto);
    }

    public String familyCodeMake() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length = 5;
        while (true) {
            StringBuilder result = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                // characters 문자열 내에서 랜덤한 인덱스를 선택
                int index = random.nextInt(characters.length());

                // 해당 인덱스의 문자를 StringBuilder에 추가
                result.append(characters.charAt(index));
            }
            int exists = dao.familyCodeChack(result.toString());
            if (exists == 0) {
                return result.toString();
            }
        }
    }

}
