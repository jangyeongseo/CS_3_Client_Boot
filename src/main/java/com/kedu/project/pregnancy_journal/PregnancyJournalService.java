package com.kedu.project.pregnancy_journal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.baby.BabyDAO;
import com.kedu.project.baby.BabyDTO;
import com.kedu.project.user.UserDAO;

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
    
}
