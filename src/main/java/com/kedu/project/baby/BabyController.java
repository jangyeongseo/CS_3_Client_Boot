package com.kedu.project.baby;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/baby")
@RestController
public class BabyController {
    @Autowired
    private BabyService babyService;

    @PostMapping("/babyMypage")
    public ResponseEntity<BabyDTO> babyMypage(@RequestBody BabyDTO dto, @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(babyService.babyMypage(dto, id));
    }

    @PostMapping("/insert")
    public ResponseEntity<Integer> babyInsert(@RequestBody List<BabyDTO> dto, @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(babyService.babyInsert(dto, id));
    }

}
