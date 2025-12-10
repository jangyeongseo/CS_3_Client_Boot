package com.kedu.project.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private String user_id;
    private String email;
    private String password;
    private String contact;
    private String nickname;
    private String parent_role;
    private String birth_date;
    private String family_code;
    private String created_at;
    private int last_baby;
}
