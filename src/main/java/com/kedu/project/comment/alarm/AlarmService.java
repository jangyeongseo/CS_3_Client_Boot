package com.kedu.project.comment.alarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.board.BoardDAO;
import com.kedu.project.comment.CommentDAO;
import com.kedu.project.comment.CommentDTO;
import com.kedu.project.comment.CommentService;
import com.kedu.project.file_info.FileService;
import com.kedu.project.report.ReportDAO;

@Service
public class AlarmService {
    @Autowired
    private AlarmDAO dao;

    @Autowired
    private BoardDAO boarddao;

    @Autowired
    private CommentDAO commentdao;

    @Autowired
    private ReportDAO reportdao;

    @Autowired
    private FileService fileService;

    @Autowired
    private CommentService commentService;

    public String getWriterId(CommentDTO dto) {
        if (dto.getParent_comment_seq() == null) {
            return boarddao.getBoardWriterId(dto.getBoard_seq());
        } else {
            return commentdao.getCommentWriterId(dto.getParent_comment_seq());
        }
    }

    public int saveAlarm(AlarmDTO dto) {
        return dao.saveAlarm(dto);
    }

    public List<AlarmDTO> getPendingAlarms(String userId) {
        return dao.getPendingAlarms(userId);
    }

    public int deleteAlarm(AlarmDTO dto) {
        return dao.deleteAlarm(dto);
    }

    public int admindeleteBoard(int board_seq, String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("board_seq", board_seq);
        commentdao.deleteAllComment(params);
        fileService.deleteAllFile(board_seq, id, "board");
        reportdao.admindeleteBoard(board_seq);
        return boarddao.admindeleteBoard(board_seq);
    }

    public String getWriterIdByBoardSeq(int board_seq) {
        return boarddao.getBoardWriterId(board_seq);
    }

    public void admindeleteCommnet(int comment_seq, String id) {
        reportdao.admindeleteCommnet(comment_seq);
        commentService.deleteComment(comment_seq, id);
    }

    public String getWriterIdByCommentSeq(int comment_seq) {
        return commentdao.getCommentWriterId(comment_seq);
    }
}
