package com.kedu.project.board;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    private int board_seq;
    private String board_type;
    private String user_id;
    private String title;
    private String content;
    private int is_reported;
    private boolean is_privated;
    private int view_count;
    private Timestamp created_at;
}