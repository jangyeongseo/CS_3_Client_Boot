package com.kedu.project.comment.alarm;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.comment.CommentDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

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

    private boolean isUserConnected(String userId) {
        return simpUserRegistry.getUser(userId) != null;
    }

    @MessageMapping("/notify/init")
    public void sendPendingAlarms(String userId) {
        List<AlarmDTO> pendingAlarms = alarmService.getPendingAlarms(userId);

        for (AlarmDTO alarm : pendingAlarms) {
            messagingTemplate.convertAndSendToUser(userId, "/queue/notify", alarm);
        }
    }

    @MessageMapping("/notify")
    public void handleNotify(CommentDTO dto) {
        String writerId = alarmService.getWriterId(dto);

        if (writerId.equals(dto.getUser_id()))
            return;

        AlarmDTO alarm = new AlarmDTO();
        alarm.setUser_id(writerId);
        alarm.setBoard_seq(String.valueOf(dto.getBoard_seq()));
        alarm.setComment_seq(dto.getParent_comment_seq());
        alarm.setType("C");

        int alarmSeq = alarmService.saveAlarm(alarm);
        alarm.setAlarm_seq(alarmSeq);
        messagingTemplate.convertAndSendToUser(writerId, "/queue/notify", alarm);

    }

    @PostMapping("/deleteAlarm")
    public ResponseEntity<?> deleteAlarm(@RequestBody AlarmDTO dto) {
        return ResponseEntity.ok(alarmService.deleteAlarm(dto));
    }

    @MessageMapping("/admin/notify/send/board")
    public void sendAdminDeleteBoard(@Payload int board_seq) {
        String writerId = alarmService.getWriterIdByBoardSeq(board_seq);
        alarmService.admindeleteBoard(board_seq, writerId);

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
