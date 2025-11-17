package com.kedu.project.chatbot;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="chatbot_responses")
@Getter
@Setter
@NoArgsConstructor
public class ChatBotDataEntity {
    @Id
    private int id;
    private String trigger_text;
    private String response_text;
}
