package com.kedu.project.chatbot;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/chatBoot")
@RestController
public class ChatBotController {
    @Autowired
    private ChatBotService chatBotService;

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/answer")
    public ResponseEntity<ChatBotDTO> answer(@RequestParam("trigger_text") String buttonText) {
        System.out.println(buttonText);
        return ResponseEntity.ok(chatBotService.answer(buttonText));
    }

    @PostMapping("/aiAnswer")
    public ResponseEntity<String> aiAnswer(@RequestBody Map<String, String> body, @AuthenticationPrincipal String id) {
        String question = body.get("question"); // 클라이언트에서 보낸 질문
        String answer = geminiService.generateText(question, id); // AI 호출
        return ResponseEntity.ok(answer);
    }

}
