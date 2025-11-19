package com.kedu.project.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BoardDAO {
    @Autowired
	private SqlSession mybatis;
    
    //1. 모든 타입에 대하여 토탈 카운트 계산
    public int getTotalAllCount () {
    	return mybatis.selectOne("Board.getTotalAllCount");
    }
    //1-1. 모든 타입에 대하여 + 검색어 토탈 카운트 계산
    public int getTotalAllFindTargetCount(String target) {
    	return mybatis.selectOne("Board.getTotalAllFindTargetCount", target);
    }
    
    //2. 특정 타입에 대하여 토탈 카운트 계산
    public int getTotalTypeCount (String board_type) {
    	return mybatis.selectOne("Board.getTotalTypeCount",board_type);
    }
    //2-1. 특정 타입에 대하여 + 검색어 토탈 카운트 계산
    public int getTotalTypeFindTargetCount(Map<String, Object> params) {
    	return mybatis.selectOne("Board.getTotalTypeFindTargetCount",params);
    }
    
    //3.페이징 계산된 게시글 가져오기
    public List<BoardDTO> getBoardListFromTo (Map<String, Object> params){
    	return mybatis.selectList("Board.getBoardListFromTo",params);
    }
    //3-1.페이징 계산된 게시글 가져오기 + 검색어
    public List<BoardDTO> getBoardListFromToWithTarget (Map<String, Object> params){
    	return mybatis.selectList("Board.getBoardListFromToWithTarget",params);
    }
    
    
    
}
