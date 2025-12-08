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

	// baby info 조회

	@GetMapping("/{babySeq}")
	public ResponseEntity<BabyDTO> getBabyInfoForChart(@PathVariable int babySeq, @AuthenticationPrincipal String id) {
		System.out.println("아기 시퀀스" + babySeq);
		// BabyService를 호출하여 BabyDTO (status, birthDate) 반환
		// Service는 int를 사용하므로 Long으로 변환할 필요 없음
		BabyDTO babyInfo = babyService.getBabyInfo(babySeq, id);

		System.out.println(babyInfo);

		if (babyInfo == null) {
			// DB에 아기가 없으면 404 NOT FOUND를 반환하여 안전하게 처리합니다.
			return ResponseEntity.notFound().build();
		}
		// 200 OK와 함께 BabyDTO를 반환
		return ResponseEntity.ok(babyInfo);
	}

	// total chart 데이터 조회

	@GetMapping("/total")
	public ResponseEntity<Map<String, Object>> getTotalChartData(
			@RequestParam("babyId") int babyId,
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		try {
			// DB에서 주차 범위에 맞는 데이터 조회
			Map<String, Object> actualDataMap = growthChartService.getActualDataByRange(babyId, startDate, endDate);
			// 결과를 항상 0으로 채워서 반환할 최종 Map
			Map<String, Float> resultMap = new LinkedHashMap<>();
			LocalDate date = startDate;
			int week = 1;
			// 📌 기간 내 주차 Loop → 값 없다면 0으로 채움
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
		System.err.println("도달했나요?"+dtoList);
		try {
			growthChartService.insertGrowth(dtoList); // 내부에서 주차/중복 검증 포함
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
		System.out.println("업데이트 찍히나요?" + updates);
		growthChartService.updateGrowthChart(updates);
		return ResponseEntity.ok("updated");
	}

}
