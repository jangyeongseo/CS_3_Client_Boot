package com.kedu.project.pregnancy_journal;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PregnancyJournalDAO {
    @Autowired
	private SqlSession mybatis;    
    
    
    //1. 산모수첩 입력
    public int postDiary(PregnancyJournalDTO dto) {
    	return mybatis.insert("PregnancyJournal.postDiary", dto);
    }
}
