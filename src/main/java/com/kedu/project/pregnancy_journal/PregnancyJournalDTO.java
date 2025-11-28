package com.kedu.project.pregnancy_journal;



import java.sql.Timestamp;

import com.kedu.project.board.BoardDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PregnancyJournalDTO {
    private int journal_seq;
    private int baby_seq;
    private String user_id;
    private int pregnancy_week;
    private Timestamp record_datetime;
    private String title;
    private String content;
}
 