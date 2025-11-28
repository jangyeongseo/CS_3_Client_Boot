package com.kedu.project.pregnancy_journal;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/diary")
@RestController
public class PregnancyJournalController {
    @Autowired
    private PregnancyJournalService pregnancyJournalService;
    
    @PostMapping
    public ResponseEntity<Void> postDiary(
    		@RequestParam("title") String title,
    		@RequestParam("content") String content,
            @RequestParam(value = "imageSysList", required = false) String imageSysListJson,
            @RequestParam("pregnancy_week") String pregnancy_week,
            @RequestParam("babySeq") String babySeq,
            @AuthenticationPrincipal String id
    		){
    	PregnancyJournalDTO dto =PregnancyJournalDTO.builder().title(title).content(content)
    			.pregnancy_week(Integer.parseInt(pregnancy_week)).baby_seq(Integer.parseInt(babySeq)).user_id(id).build();
    	
    	pregnancyJournalService.postDiary(dto);
    	return ResponseEntity.ok().build();
    }
    
    
    
}
