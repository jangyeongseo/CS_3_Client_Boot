package com.kedu.project.comment;


import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    /*
        댓글  DTO
    */
    private int comment_seq;
    private int board_seq; // 부모 시퀀스 ( 게시글 번호 )
    private Integer parent_comment_seq;//부모 댓글 시퀀스 번호
    private String user_id;
    private int is_deleted;  // 삭제 여부
    private boolean is_reported; // 신고 횟수
    private Timestamp created_at; // 작성 날짜
    private String comment_content;//댓글 내용
}
