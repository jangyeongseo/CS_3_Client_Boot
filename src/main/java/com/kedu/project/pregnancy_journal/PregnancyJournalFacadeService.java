package com.kedu.project.pregnancy_journal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kedu.project.file_info.FileService;

@Service
@Transactional
public class PregnancyJournalFacadeService {

	@Autowired
	private PregnancyJournalService pregnancyJournalService;
	@Autowired
	private FileService fServ;

	public int postDiary(PregnancyJournalDTO dto, String imageSysListJson) {
		int target_seq = pregnancyJournalService.postDiary(dto);
		fServ.confirmImg(imageSysListJson, target_seq);

		return target_seq;
	}

	public int deleteTargetDTO(String id, int journal_seq) {
		pregnancyJournalService.deleteTargetDTO(id, journal_seq);
		return fServ.deleteAllFile(journal_seq, id, "diary/img");
	}

	public void updateJournal(PregnancyJournalDTO dto, String imageSysListJson) {
		pregnancyJournalService.updateJournal(dto);
		fServ.syncBoardImages(imageSysListJson, dto.getJournal_seq(), dto.getUser_id(), "diary/img");

	}

}
