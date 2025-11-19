package com.kedu.project.growth_chart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chart")
@RestController
public class GrowthChartController {
	
    @Autowired
    private GrowthChartService growthChartService;
    
    // FETAL 성장차트 조회
    @GetMapping("/fetal/{babySeq}")
    public List<GrowthChartDTO> getFetalGrowth(@PathVariable int babySeq) {
        return growthChartService.getFetalGrowth(babySeq);
    }

    // INFANT 성장차트 조회
    @GetMapping("/infant/{babySeq}")
    public List<GrowthChartDTO> getInfantGrowth(@PathVariable int babySeq) {
        return growthChartService.getInfantGrowth(babySeq);
    }

    // 성장데이터 추가
    @PostMapping
    public ResponseEntity<String> insertGrowth(@RequestBody GrowthChartDTO dto) {
    	growthChartService.insertGrowth(dto);
        return ResponseEntity.ok("success");
    }

    // 성장데이터 수정
    @PutMapping("/{growthSeq}")
    public ResponseEntity<String> updateGrowth(@PathVariable int growthSeq, @RequestBody GrowthChartDTO dto) {
        dto.setGrowth_seq(growthSeq);
        growthChartService.updateGrowth(dto);
        return ResponseEntity.ok("success");
    }
    
}
