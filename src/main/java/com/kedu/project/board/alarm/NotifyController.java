package com.kedu.project.board.alarm;

import org.springframework.stereotype.Controller;
import com.kedu.project.comment.CommentDTO;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import java.security.Principal;

@Controller
public class NotifyController {

    private final SimpMessagingTemplate messagingTemplate;
    private final AlarmService alarmService; // 중간 서비스

    public NotifyController(SimpMessagingTemplate messagingTemplate, AlarmService alarmService) {
        this.messagingTemplate = messagingTemplate;
        this.alarmService = alarmService;
    }

    // STOMP 메시지 처리 메서드
    @MessageMapping("/notify")
    public void handleNotify(CommentDTO dto, Principal principal) {
        String writerId = alarmService.getWriterId(dto);

        if (writerId.equals(principal.getName())) {
            return; // 자기 자신에게는 알람 보내지 않음
        }

        AlarmDTO alarm = new AlarmDTO();
        alarm.setUser_id(writerId); // 알람 받을 사람
        alarm.setBorad_seq(String.valueOf(dto.getBoard_seq()));
        alarm.setComment_seq(String.valueOf(dto.getComment_seq()));
        alarm.setType("NEW_COMMENT");

        // 4. STOMP 전송
        messagingTemplate.convertAndSendToUser(
                writerId,
                "/queue/notify",
                alarm);

        // 5. 게시글 구독자 전체 브로드캐스트 (선택)
        messagingTemplate.convertAndSend(
                "/topic/board/" + dto.getBoard_seq(),
                alarm);
    }
}
