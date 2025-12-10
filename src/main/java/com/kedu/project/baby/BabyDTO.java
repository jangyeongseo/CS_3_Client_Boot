package com.kedu.project.baby;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BabyDTO {

    private int baby_seq;
    private String name;
    private String gender; 
    private String family_code; 
    private String image_name; 
    private String status;
    private String birth_date;
    private String updated_at; 
}
