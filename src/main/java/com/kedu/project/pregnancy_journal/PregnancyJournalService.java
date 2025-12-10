package com.kedu.project.pregnancy_journal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public int postDiary(PregnancyJournalDTO dto) {
		String familyCode = userdao.familyCode(dto.getUser_id());
		BabyDTO bDto = new BabyDTO();
		bDto.setBaby_seq(dto.getBaby_seq());
		bDto.setFamily_code(familyCode);
		BabyDTO result = babydao.babyMypage(bDto);

		if (result != null) {
			return dao.postDiary(dto);
		} else {
			return 0;
		}
	}

	public List<PregnancyJournalDTO> getTargetWeek(int pregnancy_week, int baby_seq, String id) {
		String familyCode = userdao.familyCode(id);
		BabyDTO bDto = new BabyDTO();
		bDto.setBaby_seq(baby_seq);
		bDto.setFamily_code(familyCode);
		BabyDTO result = babydao.babyMypage(bDto);

		if (result != null) {
			Map<String, Object> params1 = new HashMap<>();
			params1.put("baby_seq", baby_seq);
			params1.put("pregnancy_week", pregnancy_week);
			return dao.getTargetWeek(params1);
		} else {
			return null;
		}
	}

	public Map<String, Object> getTargetDTO(String id, int journal_seq, int baby_seq) {
		String familyCode = userdao.familyCode(id);
		BabyDTO bDto = new BabyDTO();
		bDto.setBaby_seq(baby_seq);
		bDto.setFamily_code(familyCode);
		BabyDTO result = babydao.babyMypage(bDto);

		if (result != null) {
			Map<String, Object> finalResult = dao.getTargetDTO(journal_seq);
			return finalResult;
		}
		return null;
	}

	public int deleteTargetDTO(String id, int journal_seq) {
		return dao.deleteTargetDTO(id, journal_seq);
	}

	public int updateJournal(PregnancyJournalDTO dto) {
		return dao.updateJournal(dto);
	}

}
