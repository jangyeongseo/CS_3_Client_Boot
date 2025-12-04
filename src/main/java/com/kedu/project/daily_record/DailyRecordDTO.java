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
    /*
        하루기록 DTO
    */    
    private int record_seq; 
    private int baby_seq;
    private String user_id;
    private String record_type;  // "milk", "toilet/poop", "toilet/pee", "baby_food", "sleep", "temperature"
    private int amount_value; // 양 또는 횟수 또는 시간
    private Timestamp created_at; // 생성 날짜
    private String sleep_group_id; //수면에만 추가, 다른애들은 null
}
