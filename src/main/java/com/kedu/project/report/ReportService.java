package com.kedu.project.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.board.BoardDAO;
import com.kedu.project.comment.CommentDAO;

@Service
public class ReportService {
    @Autowired
    private ReportDAO dao;

    @Autowired
    private CommentDAO commentdao;

    @Autowired
    private BoardDAO boarddao;

    public int reportBoard(ReportDTO dto) {
        String user_id = boarddao.getBoardWriterId(dto.getBoard_seq());
        dto.setUser_id(user_id);
        return dao.reportBoard(dto);
    }

    public int reportComment(ReportDTO dto) {
        String user_id = commentdao.getCommentWriterIdByCommentSeq(dto.getComment_seq());
        dto.setUser_id(user_id);
        return dao.reportComment(dto);
    }

}
