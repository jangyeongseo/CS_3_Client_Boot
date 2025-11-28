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

	private static final int MAX_DAYS_AGO = 7;


	@Transactional // 🚨 5개 DTO 중 하나라도 실패하면 전체 롤백
	public void insertGrowth(List<GrowthChartDTO> dtoList) throws IllegalArgumentException, IllegalStateException {

		if (dtoList == null || dtoList.isEmpty()) {
			throw new IllegalArgumentException("입력할 데이터가 없습니다.");
		}

		// 🚨 1. 핵심 검증 데이터 추출 (모든 DTO는 동일한 날짜와 baby_seq를 가짐)
		GrowthChartDTO firstDto = dtoList.get(0);
		int babySeq = firstDto.getBaby_seq();

		// DTO의 Timestamp를 Java의 LocalDate로 변환하여 검증에 사용
		// measure_date가 Timestamp 타입이므로 toLocalDate()를 사용
		LocalDate measureDate = firstDto.getMeasure_date().toLocalDateTime().toLocalDate(); 
		LocalDate today = LocalDate.now();

		// 2. 🛡️ 시간 잠금 검증 (Time Lock)
		// 입력 날짜가 오늘보다 7일 이상 과거인지 확인
		long daysDifference = ChronoUnit.DAYS.between(measureDate, today);
		if (daysDifference > MAX_DAYS_AGO || daysDifference < 0) { // 7일 초과 또는 미래 날짜 방지
			throw new IllegalArgumentException("입력 가능한 날짜 범위를 초과했습니다. (7일 이내만 허용)");
		}	

		// 3. 🚨 중복 데이터 검증 (Duplication Check)
		Map<String, Object> countParams = new HashMap<>();
		countParams.put("baby_seq", babySeq);
		countParams.put("measureDate", Date.valueOf(measureDate)); // DAO에 SQL Date로 전달

		if (growthChartDAO.countByBabyAndDate(countParams) > 0) {
			throw new IllegalStateException("해당 날짜에 이미 측정 기록이 존재합니다. 중복 입력 불가.");
		}

		// 4. 💾 5개 DTO를 DB에 개별 INSERT (트랜잭션 실행)
		for (GrowthChartDTO dto : dtoList) {
			int result = growthChartDAO.insertMeasurement(dto); 

			if (result != 1) {
				// 저장 실패 시 강제 예외 발생 -> 트랜잭션 롤백 유발
				throw new IllegalStateException("데이터 저장 중 오류가 발생했습니다. 트랜잭션 롤백."); 
			}
		}
	}


	public Map<String, Object> getActualDataByRange(int babyId, LocalDate startDate, LocalDate endDate) {

		// 1. 🔍 DAO 호출 준비
		System.out.println("babyId=" + babyId + ", start=" + startDate + ", end=" + endDate);
		
		
		Map<String, Object> daoParams = new HashMap<>();
		// DAO에 전달할 DB 타입(java.sql.Date)으로 변환
		daoParams.put("baby_seq", babyId);
		daoParams.put("startDate", java.sql.Date.valueOf(startDate)); 
		daoParams.put("endDate", java.sql.Date.valueOf(endDate));    

		// 2. DAO 호출 및 데이터 조회
		// 🚨 growthChartDAO.selectLatestMeasurementsByDateRange 메소드는 이미 구현되어 있어야 합니다.
		List<GrowthChartDTO> records = growthChartDAO.selectLatestMeasurementsByDateRange(daoParams);

		// 3. 📊 기록을 Map<String, Float>으로 가공 (React actualData props 형태)
		if (records.isEmpty()) {
			return new HashMap<>(); // 실측 데이터 없으면 빈 맵 반환
		}

		Map<String, Object> actualDataMap = records.stream()
				.collect(Collectors.toMap(
						GrowthChartDTO::getMeasure_type, 
						GrowthChartDTO::getMeasure_value
						));
		
		actualDataMap.put("measure_date", records.get(0).getMeasure_date());
		return actualDataMap;
	}

	public List<Map<String, Object>> getGrowthChartDetail(int babySeq) {
	    // DAO에서 바로 Map 리스트를 가져오도록 수정 (selectList 사용)
	    List<Map<String, Object>> list = growthChartDAO.getGrowthChartDetail(babySeq);

	    // measure_date 기준으로 그룹핑
	    Map<Object, Map<String, Object>> grouped = new LinkedHashMap<>();

	    for (Map<String, Object> row : list) {
	        Object date = row.get("measure_date"); // Timestamp나 Date 객체 그대로 사용
	        String type = (String) row.get("measure_type");
	        Float value = ((Number) row.get("measure_value")).floatValue();

	        grouped.putIfAbsent(date, new LinkedHashMap<>());
	        grouped.get(date).put("measure_date", date);
	        grouped.get(date).put(type, value);
	    }

	    return new ArrayList<>(grouped.values());
	}


}
