package com.kedu.project.daily_record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.user.UserController;

@RequestMapping("/dailyrecord")
@RestController
public class DailyRecordController {

    private final UserController userController;

    @Autowired
    private DailyRecordService dailyRecordService;


    DailyRecordController(UserController userController) {
        this.userController = userController;
    }    
    
    @GetMapping("/sleep-group")
    public ResponseEntity<Map<String, Object>>  getSleepGroup(
            @RequestParam("group_id") String group_id
    ) {
        List<DailyRecordDTO> rDTOList = dailyRecordService.getSleepGroup(group_id);
        Map result = new HashMap<>();
    	result.put("rDTOList", rDTOList);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/sleep-range")
    public ResponseEntity<Map<String, Object>>  getSleepRange(
            @RequestParam("baby_seq") String baby_seq,
            @RequestParam("date") String date,
            @AuthenticationPrincipal String id
    ) {
        List<DailyRecordDTO> rDTOList = dailyRecordService.getSleepRange(baby_seq, date, id);
        Map result = new HashMap<>();
    	result.put("rDTOList", rDTOList);
    	
        return ResponseEntity.ok(result);
    }
    
    @GetMapping
    public ResponseEntity<Map<String,Object>> getDailyRecord (
    		@RequestParam("start") String start,
    		@RequestParam("end") String end,
    		@RequestParam("baby_seq") String baby_seq,
    		@AuthenticationPrincipal String id
    		){
    	
    	List<DailyRecordDTO> rDTOList = dailyRecordService.getDailyRecord(start,end, baby_seq, id);
    	 
    	Map result = new HashMap<>();
    	result.put("rDTOList", rDTOList);
    	
    	return ResponseEntity.ok(result);
    }
    
    @GetMapping("/target")
    public ResponseEntity<Map<String, Object>> getTargetData(
    		@RequestParam("date") String date,
    		@RequestParam("type") String type,
    		@RequestParam("baby_seq") String baby_seq,
    		@AuthenticationPrincipal String id
    		){
    	String formattedDate = date.split("T")[0];
    	List<DailyRecordDTO> rDTOList =dailyRecordService.getTargetData(formattedDate,type, baby_seq, id);
    	
    	Map result = new HashMap<>();
    	result.put("rDTOList", rDTOList);
    	
    	return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> postData(
    		@RequestBody List<DailyRecordDTO> records,
    		@AuthenticationPrincipal String id,
    		@RequestParam("baby_seq") String baby_seq
    		){
    	int insertresult = dailyRecordService.postData(records,id,baby_seq);
    	Map result = new HashMap<>();
    	result.put("insertresult", insertresult);
    	
    	
    	return ResponseEntity.ok(result);
    }
    
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateData(
    		@RequestBody List<DailyRecordDTO> records,
    		@AuthenticationPrincipal String id,
    		@RequestParam("record_seq") String record_seq,
    		@RequestParam("baby_seq") String baby_seq
    		){
    	int updateresult = dailyRecordService.updateData(records,id,Integer.parseInt(record_seq) ,baby_seq);
    	Map result = new HashMap<>();
    	result.put("updateresult", updateresult);
    	
    	return ResponseEntity.ok(result);
    }
    
    
    @DeleteMapping("/sleep-group")
    public ResponseEntity<Map <String, Object>> deleteSleepGroup(
        @RequestParam("baby_seq") String baby_seq,
        @RequestParam("group_id") String group_id,
        @AuthenticationPrincipal String id
    ) {
        int result = dailyRecordService.deleteSleepGroup(baby_seq, group_id, id);
        return ResponseEntity.ok(Map.of("result", result));
    }
    
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteData(
    		@RequestParam("baby_seq") String baby_seq,
            @RequestParam("record_seq") String record_seq,
            @AuthenticationPrincipal String id
    		){
    	int result = dailyRecordService.deleteData(baby_seq,record_seq, id);
    	return ResponseEntity.ok(Map.of("result", result));
    }
    
}
