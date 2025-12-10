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

    public int getTotalAllCount(boolean is_privated) {
        return mybatis.selectOne("Board.getTotalAllCount", is_privated);
    }

    public int getTotalAllFindTargetCount(Map<String, Object> params) {
        return mybatis.selectOne("Board.getTotalAllFindTargetCount", params);
    }

    public int getTotalTypeCount(Map<String, Object> params) {
        return mybatis.selectOne("Board.getTotalTypeCount", params);
    }

    public int getTotalTypeFindTargetCount(Map<String, Object> params) {
        return mybatis.selectOne("Board.getTotalTypeFindTargetCount", params);
    }

    public List<BoardDTO> getBoardListFromTo(Map<String, Object> params) {
        return mybatis.selectList("Board.getBoardListFromTo", params);
    }

    public List<BoardDTO> getBoardListFromToWithTarget(Map<String, Object> params) {
        return mybatis.selectList("Board.getBoardListFromToWithTarget", params);
    }

    public int postBoard(BoardDTO dto) {
        mybatis.insert("Board.postBoard", dto); 
        return dto.getBoard_seq();
    }

    public Map<String, Object> getDetailBoard(Map<String, Object> params) {
        return mybatis.selectOne("Board.getDetailBoard", params);
    }

    public int deleteTargetBoard(Map<String, Object> params) {
        return mybatis.delete("Board.deleteTargetBoard", params);
    }

    public int updateBoard(BoardDTO dto) {
        return mybatis.update("Board.updateBoard", dto);
    }

    public int increaseViewCount(int board_seq) {
        return mybatis.update("Board.increaseViewCount", board_seq);
    }
    
    public int updateCommentCount(int board_seq) {
        return mybatis.update("Board.updateCommentCount", board_seq);
    }

    public String getBoardWriterId(int board_seq) {
        return mybatis.selectOne("Board.getBoardWriterId", board_seq);
    }

    public int admindeleteBoard(int board_seq) {
        return mybatis.delete("Board.admindeleteBoard", board_seq);
    }

}
