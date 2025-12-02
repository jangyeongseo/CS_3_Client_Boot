package com.kedu.project.comment.alarm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.comment.CommentDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/alarm")
@RestController
public class NotifyController {

    private final SimpMessagingTemplate messagingTemplate;
    private final AlarmService alarmService;
    private final SimpUserRegistry simpUserRegistry;

    public NotifyController(SimpMessagingTemplate messagingTemplate,
            AlarmService alarmService,
            SimpUserRegistry simpUserRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.alarmService = alarmService;
        this.simpUserRegistry = simpUserRegistry;
    }

    // 유저 접속 여부 확인
    private boolean isUserConnected(String userId) {
        return simpUserRegistry.getUser(userId) != null;
    }

    @MessageMapping("/notify/init")
    public void sendPendingAlarms(String userId) {
        System.out.println("존나힘드네" + userId);
        List<AlarmDTO> pendingAlarms = alarmService.getPendingAlarms(userId);

        for (AlarmDTO alarm : pendingAlarms) {
            System.out.println(alarm);
            messagingTemplate.convertAndSendToUser(userId, "/queue/notify", alarm);
        }
    }

    @MessageMapping("/notify")
    public void handleNotify(CommentDTO dto) {
        System.out.println("쓰는이 : " + dto);
        String writerId = alarmService.getWriterId(dto);
        System.out.println("받는이 : " + writerId);

        if (writerId.equals(dto.getUser_id()))
            return;

        AlarmDTO alarm = new AlarmDTO();
        alarm.setUser_id(writerId);
        alarm.setBoard_seq(String.valueOf(dto.getBoard_seq()));
        alarm.setComment_seq(dto.getParent_comment_seq());
        alarm.setType("C");

        System.out.println("알람 보내기: " + dto.getUser_id() + " -> " + alarm.getType());

        System.out.println("진짜 개빡치네 " + alarm);

        int alarmSeq = alarmService.saveAlarm(alarm);
        alarm.setAlarm_seq(alarmSeq);
        messagingTemplate.convertAndSendToUser(writerId, "/queue/notify", alarm);

        // 선택적으로 브로드캐스트
        // messagingTemplate.convertAndSend("/topic/board/" + dto.getBoard_seq(),
        // alarm);
    }

    @PostMapping("/deleteAlarm")
    public ResponseEntity<?> deleteAlarm(@RequestBody AlarmDTO dto) {
        return ResponseEntity.ok(alarmService.deleteAlarm(dto));
    }

    @MessageMapping("/admin/notify/send/board")
    public void sendAdminDeleteBoard(@Payload int board_seq) {
        System.out.println(board_seq);
        String writerId = alarmService.getWriterIdByBoardSeq(board_seq);
        alarmService.admindeleteBoard(board_seq, writerId);
        System.out.println("어드민 : " + writerId);

        AlarmDTO alarm = new AlarmDTO();
        alarm.setUser_id(writerId);
        alarm.setBoard_seq(String.valueOf(board_seq));
        alarm.setComment_seq(null);
        alarm.setType("A");

        int alarmSeq = alarmService.saveAlarm(alarm);
        alarm.setAlarm_seq(alarmSeq);
        messagingTemplate.convertAndSendToUser(writerId, "/queue/notify", alarm);

    }

    @MessageMapping("/admin/notify/send/comment")
    public void sendAdminDeleteComment(@Payload int comment_seq) {
        System.out.println(comment_seq);
        String writerId = alarmService.getWriterIdByCommentSeq(comment_seq);
        alarmService.admindeleteCommnet(comment_seq, writerId);

        AlarmDTO alarm = new AlarmDTO();
        alarm.setUser_id(writerId);
        alarm.setBoard_seq(null);
        alarm.setComment_seq(comment_seq);
        alarm.setType("A");

        int alarmSeq = alarmService.saveAlarm(alarm);
        alarm.setAlarm_seq(alarmSeq);
        messagingTemplate.convertAndSendToUser(writerId, "/queue/notify", alarm);

    }

}
