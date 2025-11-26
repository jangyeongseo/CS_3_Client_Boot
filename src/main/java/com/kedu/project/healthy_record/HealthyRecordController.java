package com.kedu.project.healthy_record;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.baby.BabyDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/checkList")
@RestController
public class HealthyRecordController {
    @Autowired
    private HealthyRecordService healthyRecordService;

    @GetMapping("/getBabyData")
    public ResponseEntity<BabyDTO> getBabyData(@RequestParam("baby_seq") int babySeq,
            @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(healthyRecordService.getBabyData(babySeq, id));
    }

    @PostMapping("/insert")
    public ResponseEntity<Integer> insert(@RequestBody HealthyRecordDTO dto, @AuthenticationPrincipal String id) {
        dto.setUser_id(id);
        return ResponseEntity.ok(healthyRecordService.insert(dto));
    }

    @PostMapping("/delete")
    public ResponseEntity<Integer> delete(@RequestBody HealthyRecordDTO dto, @AuthenticationPrincipal String id) {
        dto.setUser_id(id);
        System.out.println(dto);
        return ResponseEntity.ok(healthyRecordService.delete(dto));
    }

    @GetMapping("/selectList")
    public ResponseEntity<List<HealthyRecordDTO>> selectList(@RequestParam("baby_seq") int babySeq,
            @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(healthyRecordService.selectList(babySeq, id));
    }

}
