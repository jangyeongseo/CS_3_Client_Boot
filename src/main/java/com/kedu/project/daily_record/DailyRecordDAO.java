package com.kedu.project.daily_record;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DailyRecordDAO {
    @Autowired
	private SqlSession mybatis;   
    
    public List<DailyRecordDTO> getSleepGroup(String sleep_group_id){
    	return mybatis.selectList("DailyRecord.getSleepGroup", sleep_group_id);
    }
    
    public List<DailyRecordDTO> getSleepRange(int baby_seq, String date){
    	return mybatis.selectList("DailyRecord.getSleepRange",Map.of(
                "date", date,
                "baby_seq",baby_seq));
    }
    
    
    public List<DailyRecordDTO> getDailyRecord(int baby_seq,String start, String end){
    	return mybatis.selectList("DailyRecord.getDailyRecord",Map.of(
                "start", start,
                "end", end,
                "baby_seq",baby_seq));
    }
    
    public List<DailyRecordDTO> getTargetData(String date,String type, int baby_seq){
    	System.out.println("dao :"+date+":"+type +":"+baby_seq);
    	List<DailyRecordDTO> result= mybatis.selectList("DailyRecord.getTargetData",Map.of(
                "created_at", date,
                "record_type", type,
                "baby_seq",baby_seq));
    	return result;
    }
    
    public int postData (List<DailyRecordDTO> list) {
    	return mybatis.insert("DailyRecord.postData", list);
    }
    
    public int updateData(List<DailyRecordDTO> list) {
    	int result= mybatis.update("DailyRecord.updateData",Map.of(
                "list", list));
    	return result;
    }

    public int deleteSleepGroup(String baby_seq, String group_id, String user_id) {
        return mybatis.delete("DailyRecord.deleteSleepGroup", Map.of(
            "baby_seq", baby_seq,
            "group_id", group_id,
            "user_id", user_id
        ));
    }
    
    public int deleteData(String baby_seq, String record_seq, String user_id) {
    	return mybatis.delete("DailyRecord.deleteData", Map.of(
                "baby_seq", baby_seq,
                "record_seq", record_seq,
                "user_id", user_id
            ));
    }
}
