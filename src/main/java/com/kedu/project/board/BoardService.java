package com.kedu.project.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    @Autowired
    private BoardDAO dao;
    
    
    //1. 토탈 카운트 계산
    public int getTotalCount (String board_type) {
    	if(board_type.equals("all")) {
    		return dao.getTotalAllCount();
    	}else {
    		return dao.getTotalTypeCount(board_type);
    	}
    }
    
    // 1-1. 특정 타입+ 검색어기준으로 토탈카운트 계산
    public int getTotalCountFindTarget(String board_type, String target) {
    	if(board_type.equals("all")) {
    		return dao.getTotalAllFindTargetCount(target);
    	}else {
    		Map <String, Object> params = new HashMap<>();
    		params.put("board_type", board_type);
    		params.put("target", target);
    		return dao.getTotalTypeFindTargetCount(params);
    	}
    }
    
    
    //2. 페이징 계산된 게시글 가져오기
    public List<BoardDTO> getBoardListFromTo (String board_type, int offset, int count){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_type",board_type);
    	params.put("offset", offset);
    	params.put("count", count);
    	
    	return dao.getBoardListFromTo(params);
    }
    //2-2. 페이징 계산된 게시글 + 검색어 기준
    public List<BoardDTO> getBoardListFromToWithTarget(String board_type, int offset, int count, String target){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_type",board_type);
    	params.put("offset", offset);
    	params.put("count", count);
    	params.put("target", target);
    	
    	return dao.getBoardListFromToWithTarget(params);
    }
    
    
    
}
