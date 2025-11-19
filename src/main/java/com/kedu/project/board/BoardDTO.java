package com.kedu.project.board;

import java.security.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BoardDTO {
    /*
        커뮤니티 DTO 
    */
    private int board_seq;
    private String board_type; // 글 타입
    private String user_id;
    private String title;
    private String content;
    private int is_reported; // 신고 횟수
    private boolean is_privated; // 공개 여부
    private int view_count;
    private Timestamp created_at; // 작성일
}
 