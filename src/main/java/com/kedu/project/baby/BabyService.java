package com.kedu.project.baby;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.growth_chart.GrowthChartDAO;
import com.kedu.project.user.UserDAO;

@Service
public class BabyService {
    @Autowired
    private BabyDAO dao;

    @Autowired
    private UserDAO userdao;

    @Autowired
    private GrowthChartDAO growthChartdao;

    public BabyDTO babyMypage(int babySeq, String id) {
        BabyDTO dto = new BabyDTO();
        dto.setBaby_seq(babySeq);
        String parentsData = userdao.familyCode(id);
        dto.setFamily_code(parentsData);
        System.out.println(dto);
        String weight = growthChartdao.getWeightByBabypage(babySeq);
        dto = dao.babyMypage(dto);
        dto.setFamily_code(weight);
        return dto;
    }


    public int babyInsert(List<BabyDTO> dto, String id) {
        System.out.println(id);
        String familyCode = userdao.familyCode(id);
        System.out.println("십새야" + familyCode);
        int firstBaby = 0;
        int index = 0;
        for (BabyDTO baby : dto) {
            baby.setFamily_code(familyCode);
            String status = determineBabyStatus(baby.getBirth_date());
            baby.setStatus(status);
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

    
    public BabyDTO getBabyInfo(int babySeq, String id) {
        // DAO를 호출하여 DB에서 BabyDTO를 조회합니다.
        String familycode = userdao.familyCode(id);
        BabyDTO dto = new BabyDTO();
        dto.setBaby_seq(babySeq);
        dto.setFamily_code(familycode);
        BabyDTO babyInfo = dao.selectBabyInfo(dto);

        if (babyInfo == null) {
            // 정보가 없는 경우, 클라이언트가 에러 처리하도록 null을 반환합니다.
            // Controller가 404 응답을 반환하도록 설정되어 있습니다.
            return null;
        }
        
        return babyInfo; 
    }
    
    public int babypageUpdate(BabyDTO dto , String id){
        String familyCode = userdao.familyCode(id);
        dto.setFamily_code(familyCode);
        String status = determineBabyStatus(dto.getBirth_date());
        dto.setStatus(status);
        return dao.babypageUpdate(dto);
    }

    public String determineBabyStatus(String date) {
        LocalDate todayDate = LocalDate.now();
        LocalDate birthDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        if (birthDate.isBefore(todayDate)) {
            return "infant";
        } else {
            return "fetus";
        }
    }
    
    
    
    // //-----------지원 아기 시퀀스+부모 아이디로 출산예정일 or 생일 가져오기
    // public String babyDueDate(int baby_seq, String id) {
    // 	String familyCode = userdao.familyCode(id);  	
    // 	Map<String, Object> params = new HashMap<>();
    // 	params.put("baby_seq", baby_seq);
    // 	params.put("family_code", familyCode);
    	
    // 	return dao.babyDueDate(params);
    // }


}
