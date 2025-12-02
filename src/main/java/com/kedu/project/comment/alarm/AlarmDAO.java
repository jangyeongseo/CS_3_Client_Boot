package com.kedu.project.comment.alarm;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AlarmDAO {
    @Autowired
    private SqlSession mybatis;

    public int saveAlarm(AlarmDTO dto){
        mybatis.insert("alarm.saveAlarm", dto);
        return dto.getAlarm_seq();
    }

    public List<AlarmDTO> getPendingAlarms(String user_id){
        return mybatis.selectList("alarm.getPendingAlarms", user_id);
    }

    public int deleteAlarm(AlarmDTO dto){
        return mybatis.delete("alarm.deleteAlarm", dto);
    }

}
