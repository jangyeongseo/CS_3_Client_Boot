package com.kedu.project.board.alarm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.board.BoardDAO;
import com.kedu.project.board.BoardDTO;
import com.kedu.project.comment.CommentDAO;
import com.kedu.project.comment.CommentDTO;

@Service
public class AlarmService {
    @Autowired
    private BoardDAO boarddao;

    @Autowired
    private CommentDAO commentdao;

    // DB 조회 등으로 알람 대상 계산
    public String getWriterId(CommentDTO dto) {
        if (dto.getParent_comment_seq() == null) {
            return boarddao.getBoardWriterId(dto.getBoard_seq());
        }else{
            return commentdao.getCommentWriterId(dto.getParent_comment_seq());
        }
    }
}
