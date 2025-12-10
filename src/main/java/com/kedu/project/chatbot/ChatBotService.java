package com.kedu.project.chatbot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatBotService {
    @Autowired
    private ChatBotRepository chatBotRepository;

    public ChatBotDTO answer(String buttonText) {
        List<ChatBot> chatBots = chatBotRepository.findByTriggerText(buttonText);

        ChatBot chatBot = chatBots.get(0);
        ChatBotDTO dto = new ChatBotDTO();
        dto.setTrigger_text(chatBot.getTriggerText());
        dto.setResponse_text(chatBot.getResponseText());
        return dto;
    }
}
