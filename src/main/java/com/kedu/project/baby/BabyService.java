package com.kedu.project.baby;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.user.UserDAO;

@Service
public class BabyService {
    @Autowired
    private BabyDAO dao;

    @Autowired
    private UserDAO userdao;

    public BabyDTO babyMypage(BabyDTO dto, String id) {
        String parentsData = userdao.familyCode(id);
        dto.setFamily_code(parentsData);
        return dao.babyMypage(dto);
    }

    public int babyInsert(List<BabyDTO> dto, String id) {
        System.out.println(id);
        String parentsData = userdao.familyCode(id);
        System.out.println("십새야"+parentsData);
        int firstBaby = 0;
        int index = 0;
        for (BabyDTO baby : dto) {
            baby.setFamily_code(parentsData);
            LocalDate todayDate = LocalDate.now();
            String birthDateString = baby.getBirth_date();
            LocalDate birthDate = LocalDate.parse(birthDateString, DateTimeFormatter.ISO_LOCAL_DATE);
            if (birthDate.isBefore(todayDate)) {
                // 이 로직은 출생일이 오늘보다 과거일 경우(이미 태어났을 경우) 실행됩니다.
                baby.setStatus("infant");
            }else{
                baby.setStatus("fetus");
            }
            System.out.println(baby);
            dao.babyInsert(baby);
            int generatedSeq = baby.getBaby_seq();
            if (index == 0) {
                firstBaby = generatedSeq;
            }
            index++;
        }
        userdao.updateLastBabySeq(firstBaby, id);
        return firstBaby;
    }
}
