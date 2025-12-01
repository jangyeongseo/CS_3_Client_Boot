package com.kedu.project.growth_chart;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GrowthChartDAO {
    @Autowired
	private SqlSession mybatis;    
   
    private static String NAMESPACE = "chart";
    

    public List<GrowthChartDTO> selectLatestMeasurementsByDateRange(Map<String, Object> params) {
        // 매퍼 ID: chart.selectLatestMeasurements
        return mybatis.selectList(NAMESPACE + ".selectLatestMeasurements", params);

    }
    
    // 혜빈 - 베이비 페이지 몸무게 출력
    public String getWeightByBabypage(int baby_seq){
        return mybatis.selectOne("chart.getWeightByBabypage", baby_seq);

    }
    
    
    public int countByBabyAndDate(Map<String, Object> params) {
        // 매퍼 ID: chart.countByBabyAndDate
        return mybatis.selectOne(NAMESPACE + ".countByBabyAndDate", params);
    }

    /**
     * 단일 측정치 삽입: DTO를 받아 DB에 저장합니다. (POST /chart)
     */
    public int insertMeasurement(GrowthChartDTO dto) {
        // 매퍼 ID: chart.insertMeasurement
        return mybatis.insert(NAMESPACE + ".insertMeasurement", dto);
    }
    
    public List<Map<String,Object>> getGrowthChartDetail(int baby_seq){
        return mybatis.selectList(NAMESPACE + ".selectDetail", baby_seq);
    }
    
    public void updateChart(int babySeq, String userId, String date, String type, float value) {
    	mybatis.update(NAMESPACE + ".updateChart",babySeq, userId, date, type, value);
    }
    
}
