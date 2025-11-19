package com.kedu.project.growth_chart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrowthChartService {
    @Autowired
    private GrowthChartDAO growthChartDAO;

    public List<GrowthChartDTO> getFetalGrowth(int babySeq) {
        return growthChartDAO.getFetalGrowthList(babySeq);
    }

    public List<GrowthChartDTO> getInfantGrowth(int babySeq) {
        return growthChartDAO.getInfantGrowthList(babySeq);
    }

    public void insertGrowth(GrowthChartDTO dto) {
        growthChartDAO.insertGrowth(dto);
    }

    public void updateGrowth(GrowthChartDTO dto) {
        growthChartDAO.updateGrowth(dto);
    }
    
    
    
}
