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
        return mybatis.selectList(NAMESPACE + ".selectLatestMeasurements", params);

    }

    public String getWeightByBabypage(int baby_seq) {
        return mybatis.selectOne("chart.getWeightByBabypage", baby_seq);

    }

    public int countByBabyAndDate(Map<String, Object> params) {
        return mybatis.selectOne(NAMESPACE + ".countByBabyAndDate", params);
    }

    public int insertMeasurement(GrowthChartDTO dto) {
        return mybatis.insert(NAMESPACE + ".insertMeasurement", dto);
    }

    public List<Map<String, Object>> getGrowthChartDetail(int baby_seq) {
        return mybatis.selectList(NAMESPACE + ".selectDetail", baby_seq);
    }

    public void updateGrowthChart(GrowthChartDTO dto) {
        mybatis.update(NAMESPACE + ".updateChart", dto);
    }

}
