package com.kedu.project.file_info;



import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDTO {
    private int file_seq;
    private String user_id;
    private String oriname; 
    private String sysname;
    private String target_type;
    private int target_seq; 
    private Timestamp created_at;
    
}