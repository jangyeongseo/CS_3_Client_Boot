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
    
    
    //0.그룹아이디 있는지 확인 후
    public List<DailyRecordDTO> getSleepGroup(String sleep_group_id){
    	return dao.getSleepGroup(sleep_group_id);
    }
    
    //0-1. 받은 날짜기준으로 어제~내일까지의 sleep 기록들 리스트로 뽑아오기
    public List<DailyRecordDTO> getSleepRange(String baby_seq, String date, String id){
    	//1. 내아기 맞는지 확인
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(id); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(Integer.parseInt(baby_seq));
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);
    	
    	if(result != null) { //아기가 유효하다면
    		return dao.getSleepRange(Integer.parseInt(baby_seq), date);
    	}else {
    		return null;
    	}
    }
    
    
    
    //1. 하루 일기형 리스트 가져오기
    public List<DailyRecordDTO> getDailyRecord(String start, String end, String baby_seq, String id){
    	//1. 내아기 맞는지 확인
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(id); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(Integer.parseInt(baby_seq));
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);
    	
    	if(result != null) { //아기가 유효하다면
    		return dao.getDailyRecord(Integer.parseInt(baby_seq),start,end); //아기시퀀스, 시작일, 끝나는일 가져오기
    	}else {
    		return null;
    	}
    }
    
    //2. 타겟 날자 타입에 맞춰 데이터 가져오기
    public List<DailyRecordDTO> getTargetData(String date, String type, String baby_seq, String id){
    	//1. 내아기 맞는지 확인
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(id); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(Integer.parseInt(baby_seq));
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);
    	
    	if(result!=null) { // 아기가 유효하면
    		System.out.println(date+":"+type +":"+baby_seq);
    		return dao.getTargetData(date, type, Integer.parseInt(baby_seq)); //날짜, 타입, 애기시퀀스
    	}else {
    		return null;
    	}
    }
    
    //3. 데이터 입력
    public int postData(List<DailyRecordDTO> records, String id, String baby_seq) {
    	//1. 내아기 맞는지 확인
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(id); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(Integer.parseInt(baby_seq));
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);

    	if(result!=null) { // 아기가 유효하면
    		for (DailyRecordDTO dto : records) {
    	        dto.setBaby_seq(Integer.parseInt(baby_seq));
    	        dto.setUser_id(id);
    	    }
    		return dao.postData(records);
    	}else {
    		return 0;
    	}
    }
    
  //4. 데이터 수정
    public int updateData(List<DailyRecordDTO> records, String id, int record_seq, String baby_seq) {
        //1. 내아기 맞는지 확인
        //유저 아이디로 패밀리 코드 가져오기
        String familyCode = userdao.familyCode(id);
        //아기가 패밀리코드로 유효한지 확인
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if(result != null) { // 아기가 유효하면
            for (DailyRecordDTO dto : records) {
                dto.setBaby_seq(Integer.parseInt(baby_seq));
                dto.setUser_id(id);
                dto.setRecord_seq(record_seq); // ⭐ record_seq 설정
            }
            return dao.updateData(records);
        } else {
            return 0;
        }
    }

    //4-1. 수면 그룹 삭제
    public int deleteSleepGroup(String baby_seq, String group_id, String id) {
        //1. 내아기 맞는지 확인
        String familyCode = userdao.familyCode(id);
        BabyDTO bDto = new BabyDTO();
        bDto.setBaby_seq(Integer.parseInt(baby_seq));
        bDto.setFamily_code(familyCode);
        BabyDTO result = babydao.babyMypage(bDto);

        if(result != null) { // 아기가 유효하면
            return dao.deleteSleepGroup(baby_seq, group_id, id);
        } else {
            return 0;
        }
    }
    
    
}
