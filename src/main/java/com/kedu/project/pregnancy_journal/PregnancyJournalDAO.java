package com.kedu.project.pregnancy_journal;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PregnancyJournalDAO {
    @Autowired
    private SqlSession mybatis;

    public int postDiary(PregnancyJournalDTO dto) {
        mybatis.insert("PregnancyJournal.postDiary", dto);
        return dto.getJournal_seq();
    }

    public List<PregnancyJournalDTO> getTargetWeek(Map<String, Object> params1) {
        return mybatis.selectList("PregnancyJournal.getTargetWeek", params1);
    }

    public Map<String, Object> getTargetDTO(int journal_seq) {
        return mybatis.selectOne("PregnancyJournal.getTargetDTO", journal_seq);
    }

    public int deleteTargetDTO(String id, int journal_seq) {
        return mybatis.delete("PregnancyJournal.deleteTargetDTO", Map.of(
                "user_id", id,
                "journal_seq", journal_seq));
    }

    public int updateJournal(PregnancyJournalDTO dto) {
        return mybatis.update("PregnancyJournal.updateJournal", dto);
    }

}
