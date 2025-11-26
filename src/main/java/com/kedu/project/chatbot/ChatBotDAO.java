package com.kedu.project.chatbot;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChatBotDAO {
    @Autowired
	private SqlSession mybatis;    

    public ChatBotDTO answer(String trigger_text){
        return mybatis.selectOne("chatbot.answer", trigger_text);
    }
}
