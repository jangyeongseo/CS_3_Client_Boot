package com.kedu.project.baby;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BabyDTO> babyMypage(@RequestBody BabyDTO dto, HttpServletRequest resp) {  
        String id = (String) resp.getAttribute("id");
        return ResponseEntity.ok(babyService.babyMypage(dto, id));
    }

}
