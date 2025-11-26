package com.kedu.project.chatbot;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatBotDTO {
    private int id;
    private String trigger_text;
    private String response_text;
    
}
