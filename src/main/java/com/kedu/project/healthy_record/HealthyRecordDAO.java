package com.kedu.project.healthy_record;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HealthyRecordDAO {
    @Autowired
    private SqlSession mybatis;

    public int insert(HealthyRecordDTO dto) {
        return mybatis.insert("healthyRecor.insert", dto);
    }

    public int delete(HealthyRecordDTO dto) {
        return mybatis.delete("healthyRecor.delete", dto);
    }

    public List<HealthyRecordDTO> selectList(HealthyRecordDTO dto) {
        return mybatis.selectList("healthyRecor.selectList", dto);
    }

    public List<HealthyRecordDTO> eventList(HealthyRecordDTO dto) {
        return mybatis.selectList("healthyRecor.eventList", dto);
    }

    public int eventDelete(HealthyRecordDTO dto) {
        return mybatis.update("healthyRecor.eventDelete", dto);
    }
}
