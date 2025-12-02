package com.kedu.project.user;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.baby.BabyDTO;
import com.kedu.project.healthy_record.HealthyRecordDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/idChack")
    public ResponseEntity<Integer> idChack(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.idChack(dto));
    }

    @PostMapping("/nicknameChack")
    public ResponseEntity<Integer> nickNameChack(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.idChack(dto));
    }

    @PostMapping("/signup")
    public ResponseEntity<Integer> signup(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.signup(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO dto) {
        Map<String, String> map = userService.login(dto);
        if (map.get("babySeq") != null) {
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/pindIdByEmail")
    public ResponseEntity<String> pindIdByEmail(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.pindIdByEmail(dto));
    }

    @PostMapping("/pindPwByEmail")
    public ResponseEntity<Integer> pindPwByEmail(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.pindPwByEmail(dto));
    }

    @GetMapping("/mypage")
    public ResponseEntity<UserDTO> getMethodName(@AuthenticationPrincipal String id) {
        return ResponseEntity.ok(userService.mypage(id));
    }

    @PostMapping("/mypageUdate")
    public ResponseEntity<Integer> mypageUdate(@RequestBody UserDTO dto, @AuthenticationPrincipal String id) {
        dto.setUser_id(id);
        return ResponseEntity.ok(userService.mypageUdate(dto));
    }

    @GetMapping("/babyListByMypage")
    public ResponseEntity<List<BabyDTO>> babyListByMypage(@AuthenticationPrincipal String id) {
        return ResponseEntity.ok(userService.babyListByMypage(id));
    }

    @PostMapping("/changeBaby")
    public ResponseEntity<Integer> changeBaby(@RequestBody UserDTO dto, @AuthenticationPrincipal String id) {
        dto.setUser_id(id);
        return ResponseEntity.ok(userService.changeBaby(dto));
    }

    @GetMapping("/eventList")
    public ResponseEntity<List<HealthyRecordDTO>> eventList(@RequestParam("baby_seq") int babySeq,
            @AuthenticationPrincipal String id) {
        return ResponseEntity.ok(userService.eventList(babySeq, id));
    }

    @PostMapping("/eventDelete")
    public ResponseEntity<Integer> eventDelete(@RequestBody HealthyRecordDTO dto, @AuthenticationPrincipal String id) {
        dto.setUser_id(id);
        return ResponseEntity.ok(userService.eventDelete(dto));
    }

    @DeleteMapping("/secession")
    public ResponseEntity<Integer> secessionUser(@AuthenticationPrincipal String id) {
        return ResponseEntity.ok(userService.secessionUser(id));
    }
}
