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
    public int getTotalCount (String board_type, boolean is_privated) {
    	if(board_type.equals("all")) {
    		return dao.getTotalAllCount(is_privated);
    	}else {
    		Map <String, Object> params = new HashMap<>();
    		params.put("is_privated", is_privated);
    		params.put("board_type", board_type);
    		return dao.getTotalTypeCount(params);
    	}
    }
    
    // 1-1. 특정 타입+ 검색어기준으로 토탈카운트 계산
    public int getTotalCountFindTarget(String board_type, String target, boolean is_privated) {
    	if(board_type.equals("all")) {
    		Map <String, Object> params = new HashMap<>();
    		params.put("target", target);
    		params.put("is_privated", is_privated);
    		return dao.getTotalAllFindTargetCount(params);
    	}else {
    		Map <String, Object> params = new HashMap<>();
    		params.put("board_type", board_type);
    		params.put("target", target);
    		params.put("is_privated", is_privated);
    		return dao.getTotalTypeFindTargetCount(params);
    	}
    }
    
    
    //2. 페이징 계산된 게시글 가져오기
    public List<BoardDTO> getBoardListFromTo (String board_type, int offset, int count,boolean is_privated ){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_type",board_type);
    	params.put("offset", offset);
    	params.put("count", count);
    	params.put("is_privated", is_privated);
    	
    	return dao.getBoardListFromTo(params);
    }
    //2-2. 페이징 계산된 게시글 + 검색어 기준
    public List<BoardDTO> getBoardListFromToWithTarget(String board_type, int offset, int count, String target,boolean is_privated ){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_type",board_type);
    	params.put("offset", offset);
    	params.put("count", count);
    	params.put("target", target);
    	params.put("is_privated", is_privated);
    	
    	return dao.getBoardListFromToWithTarget(params);
    }
    
    
    //3.보드 작성
    public int postBoard(BoardDTO dto) {
    	return dao.postBoard(dto);
    }
    
    
}
