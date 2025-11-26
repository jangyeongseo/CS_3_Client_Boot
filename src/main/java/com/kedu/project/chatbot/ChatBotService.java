package com.kedu.project.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatBotService {
    @Autowired
    private ChatBotDAO dao;

    public ChatBotDTO answer(String buttonText){
        System.out.println(dao.answer(buttonText));
        return dao.answer(buttonText);
    }
}
