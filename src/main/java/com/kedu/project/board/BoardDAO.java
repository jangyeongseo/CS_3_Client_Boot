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

    // 1. 모든 타입에 대하여 토탈 카운트 계산
    public int getTotalAllCount(boolean is_privated) {
        return mybatis.selectOne("Board.getTotalAllCount", is_privated);
    }

    // 1-1. 모든 타입에 대하여 + 검색어 토탈 카운트 계산
    public int getTotalAllFindTargetCount(Map<String, Object> params) {
        return mybatis.selectOne("Board.getTotalAllFindTargetCount", params);
    }

    // 2. 특정 타입에 대하여 토탈 카운트 계산
    public int getTotalTypeCount(Map<String, Object> params) {
        return mybatis.selectOne("Board.getTotalTypeCount", params);
    }

    // 2-1. 특정 타입에 대하여 + 검색어 토탈 카운트 계산
    public int getTotalTypeFindTargetCount(Map<String, Object> params) {
        return mybatis.selectOne("Board.getTotalTypeFindTargetCount", params);
    }

    // 3.페이징 계산된 게시글 가져오기
    public List<BoardDTO> getBoardListFromTo(Map<String, Object> params) {
        return mybatis.selectList("Board.getBoardListFromTo", params);
    }

    // 3-1.페이징 계산된 게시글 가져오기 + 검색어
    public List<BoardDTO> getBoardListFromToWithTarget(Map<String, Object> params) {
        return mybatis.selectList("Board.getBoardListFromToWithTarget", params);
    }

    // 4. 보드 작성
    public int postBoard(BoardDTO dto) {
        mybatis.insert("Board.postBoard", dto); // 반환값은 1 (행 개수)
        return dto.getBoard_seq();
    }

    // 5. 보드 디테일
    public Map<String, Object> getDetailBoard(Map<String, Object> params) {
        return mybatis.selectOne("Board.getDetailBoard", params);
    }

    // 6. 보드 삭제
    public int deleteTargetBoard(Map<String, Object> params) {
        return mybatis.delete("Board.deleteTargetBoard", params);
    }

    // 7. 보드 수정
    public int updateBoard(BoardDTO dto) {
        return mybatis.update("Board.updateBoard", dto);
    }

    // 8. 조회수 증가
    public int increaseViewCount(int board_seq) {
        return mybatis.update("Board.increaseViewCount", board_seq);
    }
    
    //9. 댓글수 세기
    public int updateCommentCount(int board_seq) {
        return mybatis.update("Board.updateCommentCount", board_seq);
    }

    // 혜빈 추가
    public String getBoardWriterId(int board_seq) {
        return mybatis.selectOne("Board.getBoardWriterId", board_seq);
    }

    public int admindeleteBoard(int board_seq) {
        return mybatis.delete("Board.admindeleteBoard", board_seq);
    }

}
