package com.kedu.project.growth_chart;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GrowthChartService {
	@Autowired
	private GrowthChartDAO growthChartDAO;

	private static final int MAX_DAYS_AGO = 30;

	@Transactional
	public void insertGrowth(List<GrowthChartDTO> dtoList) throws IllegalArgumentException, IllegalStateException {
		if (dtoList == null || dtoList.isEmpty()) {
			throw new IllegalArgumentException("입력할 데이터가 없습니다.");
		}

		GrowthChartDTO firstDto = dtoList.get(0);
		int babySeq = firstDto.getBaby_seq();

		LocalDate measureDate = firstDto.getMeasure_date().toLocalDateTime().toLocalDate();
		LocalDate today = LocalDate.now();

		long daysDifference = ChronoUnit.DAYS.between(measureDate, today);
		if (daysDifference > MAX_DAYS_AGO || daysDifference < 0) {
			throw new IllegalArgumentException("입력 가능한 날짜 범위를 초과했습니다. (7일 이내만 허용)");
		}

		Map<String, Object> countParams = new HashMap<>();
		countParams.put("baby_seq", babySeq);
		countParams.put("measureDate", Date.valueOf(measureDate));

		if (growthChartDAO.countByBabyAndDate(countParams) > 0) {
			throw new IllegalStateException("해당 날짜에 이미 측정 기록이 존재합니다. 중복 입력 불가.");
		}

		for (GrowthChartDTO dto : dtoList) {
			int result = growthChartDAO.insertMeasurement(dto);

			if (result != 1) {
				throw new IllegalStateException("데이터 저장 중 오류가 발생했습니다. 트랜잭션 롤백.");
			}
		}
	}

	public Map<String, Object> getActualDataByRange(int babyId, LocalDate startDate, LocalDate endDate) {

		Map<String, Object> daoParams = new HashMap<>();
		daoParams.put("baby_seq", babyId);
		daoParams.put("startDate", java.sql.Date.valueOf(startDate));
		daoParams.put("endDate", java.sql.Date.valueOf(endDate));

		List<GrowthChartDTO> records = growthChartDAO.selectLatestMeasurementsByDateRange(daoParams);

		if (records.isEmpty()) {
			return new HashMap<>();
		}

		Map<String, Object> actualDataMap = records.stream()
				.collect(Collectors.toMap(
						GrowthChartDTO::getMeasure_type,
						GrowthChartDTO::getMeasure_value));

		actualDataMap.put("measure_date", records.get(0).getMeasure_date());
		return actualDataMap;
	}

	public List<Map<String, Object>> getGrowthChartDetail(int babySeq) {
		List<Map<String, Object>> list = growthChartDAO.getGrowthChartDetail(babySeq);

		Map<Object, Map<String, Object>> grouped = new LinkedHashMap<>();

		for (Map<String, Object> row : list) {
			Object date = row.get("measure_date");
			String type = (String) row.get("measure_type");
			Float value = ((Number) row.get("measure_value")).floatValue();

			grouped.putIfAbsent(date, new LinkedHashMap<>());
			grouped.get(date).put("measure_date", date);
			grouped.get(date).put(type, value);
		}

		return new ArrayList<>(grouped.values());
	}

	public void updateGrowthChart(List<GrowthChartDTO> updates) {

		for (GrowthChartDTO dto : updates) {
			growthChartDAO.updateGrowthChart(dto);
		}

	}

}
