package com.kedu.project.baby;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/baby")
@RestController
public class BabyController {
    @Autowired
    private BabyService babyService;

    @GetMapping("/babyMypage")
    public ResponseEntity<BabyDTO> babyMypage(@RequestParam("baby_seq") int babySeq,
            @AuthenticationPrincipal String id) {
        System.out.println(id);
        System.out.println(babySeq);
        return ResponseEntity.ok(babyService.babyMypage(babySeq, id));
    }

    @PostMapping("/insert")
    public ResponseEntity<Integer> babyInsert(@RequestBody List<BabyDTO> dto, @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(babyService.babyInsert(dto, id));
    }

    @PostMapping("/babypageUpdate")
    public ResponseEntity<Integer> babypageUpdate(@RequestBody BabyDTO dto, @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(babyService.babypageUpdate(dto, id));
    }
    
  //-----------지원 아기인덱스에서 아기 dto 꺼내오기

    
    
//   
//     @GetMapping("/date")
//     public ResponseEntity<String> babyDueDate(@RequestParam("seq") int baby_seq, @AuthenticationPrincipal String id) {
//         return ResponseEntity.ok(babyService.babyDueDate(baby_seq, id));
//     }

}
