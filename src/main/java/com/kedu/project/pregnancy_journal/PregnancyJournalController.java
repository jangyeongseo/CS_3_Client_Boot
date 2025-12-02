package com.kedu.project.pregnancy_journal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/diary")
@RestController
public class PregnancyJournalController {
    @Autowired
    private PregnancyJournalService pregnancyJournalService;
    @Autowired
    private PregnancyJournalFacadeService pregnancyJournalFService;
    
    
    //인서트
    @PostMapping
    public  ResponseEntity<Integer> postDiary(
    		@RequestParam("title") String title,
    		@RequestParam("content") String content,
            @RequestParam(value = "imageSysList", required = false) String imageSysListJson,
            @RequestParam("pregnancy_week") String pregnancy_week,
            @RequestParam("babySeq") String babySeq,
            @AuthenticationPrincipal String id
    		){
    	PregnancyJournalDTO dto =PregnancyJournalDTO.builder().title(title).content(content)
    			.pregnancy_week(Integer.parseInt(pregnancy_week)).baby_seq(Integer.parseInt(babySeq)).user_id(id).build();
    	
    	
    	return ResponseEntity.ok(pregnancyJournalFService.postDiary(dto,imageSysListJson));
    }
    
    //주차별 리스트 받아오기
    @GetMapping("/week/{pregnancy_week}")
    public ResponseEntity <Map<String, Object>> getTargetWeek(
    		@PathVariable int pregnancy_week,
    		@RequestHeader("BABY") int babySeq,
            @AuthenticationPrincipal String id){
    	
    	List <PregnancyJournalDTO> list = pregnancyJournalService.getTargetWeek(pregnancy_week, babySeq, id);
    	Map<String, Object> result = new HashMap<>();
    	result.put("list", list);
    	
    	return ResponseEntity.ok(result);
    }
    
    //시퀀스로 dto 찾기
    @GetMapping("/{journal_seq}")
    public ResponseEntity<Map<String, Object>> getTargetDTO(
    		@AuthenticationPrincipal String id,
    		@PathVariable int journal_seq,
    		@RequestHeader("BABY") int babySeq
    		){
    	return ResponseEntity.ok(pregnancyJournalService.getTargetDTO(id, journal_seq,babySeq));
    }
    
    //시퀀스로 dto 지우기
    @DeleteMapping("/{journal_seq}")
    public ResponseEntity<Integer> deleteTargetDTO(
    		@AuthenticationPrincipal String id,
    		@PathVariable int journal_seq
    		){
    	return ResponseEntity.ok(pregnancyJournalFService.deleteTargetDTO(id, journal_seq));
    }
    
    //산모수첩 업데이트
    //5. 보드 업데이트하기
    @PutMapping
    public ResponseEntity <Void> updateDetailBoard(
    		@RequestParam("title") String title,
    		@RequestParam("content") String content,
            @RequestParam(value = "imageSysList", required = false) String imageSysListJson,
            @RequestParam("pregnancy_week") String pregnancy_week,
            @RequestParam("baby_seq") String baby_seq,
            @RequestParam("journal_seq") String journal_seq,
            @AuthenticationPrincipal String id
    		){
    	PregnancyJournalDTO dto =PregnancyJournalDTO.builder().title(title).content(content)
    			.journal_seq(Integer.parseInt(journal_seq))
    			.pregnancy_week(Integer.parseInt(pregnancy_week))
    			.baby_seq(Integer.parseInt(baby_seq))
    			.user_id(id).build();
    	pregnancyJournalFService.updateJournal(dto,imageSysListJson);
    	
    	return ResponseEntity.ok().build();
    }
    
}
