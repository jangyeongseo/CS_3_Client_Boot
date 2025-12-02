package com.kedu.project.pregnancy_journal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.baby.BabyDAO;
import com.kedu.project.baby.BabyDTO;
import com.kedu.project.user.UserDAO;
import com.kedu.project.user.UserDTO;

@Service
public class PregnancyJournalService {
    @Autowired
    private PregnancyJournalDAO dao;
    @Autowired
    private UserDAO userdao;
    @Autowired
    private BabyDAO babydao;
    
    // 1. 산모수첩 입력
    public int postDiary (PregnancyJournalDTO dto) {
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(dto.getUser_id()); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(dto.getBaby_seq());
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);
    	
    	if(result != null) { //아기가 유효하다면
    		return dao.postDiary(dto);
    	}else {
    		return 0;
    	}
    }
    
    
    //2. 타켓 week + 아기 시퀀스로 주별 산모수첩 가져오기
    public List<PregnancyJournalDTO> getTargetWeek(int pregnancy_week, int baby_seq, String id){
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(id); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(baby_seq);
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);
    	
    	if(result != null) { //아기가 유효하다면
    		Map<String, Object> params1 = new HashMap<>();
    		params1.put("baby_seq", baby_seq);
    		params1.put("pregnancy_week", pregnancy_week);
    		return dao.getTargetWeek(params1);
    	}else {
    		return null;
    	}
    }
    
    //3. 타겟 시퀀스로 산모수첩dto 가져오기
    public Map<String, Object> getTargetDTO(String id, int journal_seq, int baby_seq) {
    	//유저 아이디로 패밀리 코드 가져오기
    	String familyCode = userdao.familyCode(id); 
    	//아기가 패밀리코드로 유효한지 확인
    	BabyDTO bDto = new BabyDTO();
    	bDto.setBaby_seq(baby_seq);
    	bDto.setFamily_code(familyCode);
    	BabyDTO result =babydao.babyMypage(bDto);
    	
    	if(result!=null) {
    		Map<String, Object> finalResult = dao.getTargetDTO(journal_seq);
    		return finalResult;
    	}
    	return null;
    }
    
    //4.삭제
    public int deleteTargetDTO(String id, int journal_seq) {
    	return dao.deleteTargetDTO(id, journal_seq);
    }
    
    //5. 업데이트
    public int updateJournal(PregnancyJournalDTO dto) {
    	return dao.updateJournal(dto);
    }
    
}
