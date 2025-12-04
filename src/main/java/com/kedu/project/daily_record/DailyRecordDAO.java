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
    
    //0.그룹아이디로 같은 그룹아이디 리스트 가져오기
    public List<DailyRecordDTO> getSleepGroup(String sleep_group_id){
    	return mybatis.selectList("DailyRecord.getSleepGroup", sleep_group_id);
    }
    
    //0-1. 받은 날짜기준으로 어제~내일까지의 sleep 기록들 리스트로 뽑아오기
    public List<DailyRecordDTO> getSleepRange(int baby_seq, String date){
    	return mybatis.selectList("DailyRecord.getSleepRange",Map.of(
                "date", date,
                "baby_seq",baby_seq));
    }
    
    
    //1. 하루 일기형 리스트 가져오기
    public List<DailyRecordDTO> getDailyRecord(int baby_seq,String start, String end){
    	return mybatis.selectList("DailyRecord.getDailyRecord",Map.of(
                "start", start,
                "end", end,
                "baby_seq",baby_seq));
    }
    
    //2. 타겟 날자 타입에 맞춰 데이터 가져오기
    public List<DailyRecordDTO> getTargetData(String date,String type, int baby_seq){
    	System.out.println("dao :"+date+":"+type +":"+baby_seq);
    	List<DailyRecordDTO> result= mybatis.selectList("DailyRecord.getTargetData",Map.of(
                "created_at", date,
                "record_type", type,
                "baby_seq",baby_seq));
    	return result;
    }
    
    //3. 데이터 입력
    public int postData (List<DailyRecordDTO> list) {
    	return mybatis.insert("DailyRecord.postData", list);
    }
    
    //4. 업데이트
    public int updateData(List<DailyRecordDTO> list) {
    	int result= mybatis.update("DailyRecord.updateData",Map.of(
                "list", list));
    	return result;
    }
  //4-1. 수면 그룹 삭제
    public int deleteSleepGroup(String baby_seq, String group_id, String user_id) {
        return mybatis.delete("DailyRecord.deleteSleepGroup", Map.of(
            "baby_seq", baby_seq,
            "group_id", group_id,
            "user_id", user_id
        ));
    }

}
