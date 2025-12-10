package com.kedu.project.daily_record;



import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRecordDTO {
    private int record_seq; 
    private int baby_seq;
    private String user_id;
    private String record_type;
    private double amount_value; 
    private Timestamp created_at; 
    private String sleep_group_id; 
}
