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
    private int comment_seq;
    private int board_seq;
    private Integer parent_comment_seq;
    private String user_id;
    private int is_deleted; 
    private boolean is_reported; 
    private Timestamp created_at;
    private String comment_content;
}
