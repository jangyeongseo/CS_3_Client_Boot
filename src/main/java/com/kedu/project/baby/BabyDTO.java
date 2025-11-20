package com.kedu.project.baby;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BabyDTO {
    /*
        클라이언트의 리퀘스트 값을 받을 DTO
    */

    private int baby_seq;
    private String name;
    private String gender; // 성별
    private String family_code; // 가족 코드
    private String image_name; // 이미지 종류
    private String status; // 출산 여부
    private String birth_date; // 출산예정일 또는 생일
    private String updated_at; // 최근 업데이트 날짜
}
