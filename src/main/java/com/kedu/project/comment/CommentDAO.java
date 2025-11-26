package com.kedu.project.comment;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDAO {
    @Autowired
    private SqlSession mybatis;
    
    //1. 부모시퀀스+타입으로 댓글 전부 지우기
    public int deleteAllComment(Map<String, Object> params) {
    	return mybatis.delete("Comment.deleteAllComment", params);
    }
    
    
    //2. 보드 부모 시퀀스로 댓글 전부 가져오기
    public List<Map<String, Object>> getDetailBoardComments(Map<String, Object> params) {
    	return mybatis.selectList("Comment.getDetailBoardComments", params);
    }
    
    //3. 덧글 입력
    public int postComment(CommentDTO dto) {
    	return mybatis.insert("Comment.postComment", dto);
    }
    
    //4.사용자랑 타겟 시퀀스로 디티오 가져오기
    public CommentDTO findTargetDTO(Map<String, Object> params1) {
    	return mybatis.selectOne("Comment.findTargetDTO", params1);
    }
    
    //5.자식 코멘트 개수 가져오기
    public int getChildCount(int comment_seq) {
    	return mybatis.selectOne("Comment.getChildCount", comment_seq);
    }
    
    //6.자식이 있는 부모댓글 삭제 소프트
    public int softDeleteParent(int comment_seq) {
    	return mybatis.delete("Comment.softDeleteParent", comment_seq);
    }
    
    //7. ㄹㅇ 삭제
    public int strictDelete(int comment_seq) {
    	return mybatis.delete("Comment.strictDelete", comment_seq);
    }
    
    //8. 댓글 업데이트
    public int updateComment(CommentDTO dto) {
    	return mybatis.update("Comment.updateComment", dto);
    }

}
