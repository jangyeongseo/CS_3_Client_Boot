package com.kedu.project.growth_chart;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GrowthChartDAO {
    @Autowired
	private SqlSession mybatis;    
    
    private static String NAMESPACE = "chart";
    
    
 // FETAL 조회
    public List<GrowthChartDTO> getFetalGrowthList(int babySeq) {
        return mybatis.selectList(NAMESPACE + ".getFetalGrowthList", babySeq);
    }

    // INFANT 조회
    public List<GrowthChartDTO> getInfantGrowthList(int babySeq) {
        return mybatis.selectList(NAMESPACE + ".getInfantGrowthList", babySeq);
    }

    // INSERT
    public int insertGrowth(GrowthChartDTO dto) {
        return mybatis.insert(NAMESPACE + ".insertGrowth", dto);
    }

    // UPDATE
    public int updateGrowth(GrowthChartDTO dto) {
        return mybatis.update(NAMESPACE + ".updateGrowth", dto);
    }
    
}
