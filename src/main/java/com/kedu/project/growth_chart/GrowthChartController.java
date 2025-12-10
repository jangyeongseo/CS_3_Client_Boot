package com.kedu.project.growth_chart;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.baby.BabyDTO;
import com.kedu.project.baby.BabyService;

@RequestMapping("/chart")
@RestController
public class GrowthChartController {

	@Autowired
	private GrowthChartService growthChartService;

	@Autowired
	private BabyService babyService;

	@GetMapping("/{babySeq}")
	public ResponseEntity<BabyDTO> getBabyInfoForChart(@PathVariable int babySeq, @AuthenticationPrincipal String id) {
		BabyDTO babyInfo = babyService.getBabyInfo(babySeq, id);

		if (babyInfo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(babyInfo);
	}

	@GetMapping("/total")
	public ResponseEntity<Map<String, Object>> getTotalChartData(
			@RequestParam("babyId") int babyId,
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		try {
			Map<String, Object> actualDataMap = growthChartService.getActualDataByRange(babyId, startDate, endDate);
			Map<String, Float> resultMap = new LinkedHashMap<>();
			LocalDate date = startDate;
			int week = 1;
			while (!date.isAfter(endDate)) {
				String weekKey = "Week " + week;
				Float value = (Float) actualDataMap.getOrDefault(weekKey, 0f);
				resultMap.put(weekKey, value);
				date = date.plusDays(7);
				week++;
			}
			return ResponseEntity.ok(actualDataMap);

		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(null);
		}
	}

	@PostMapping("/insert")
	public ResponseEntity<?> insertGrowthData(@RequestBody List<GrowthChartDTO> dtoList) {
		try {
			growthChartService.insertGrowth(dtoList);
			return ResponseEntity.ok().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/detail")
	public List<Map<String, Object>> getChart(@RequestParam int babySeq) {
		return growthChartService.getGrowthChartDetail(babySeq);
	}

	@PutMapping("/update")
	public ResponseEntity<?> updateGrowthChart(
			@RequestBody List<GrowthChartDTO> updates) {
		growthChartService.updateGrowthChart(updates);
		return ResponseEntity.ok("updated");
	}

}
