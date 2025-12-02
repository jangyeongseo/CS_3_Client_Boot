package com.kedu.project.comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.board.BoardDAO;

@Service
public class CommentService {
    @Autowired
    private CommentDAO dao;
    @Autowired
    private BoardDAO boardDao;
    
    //1. 보드 부모시퀀스로 댓글 전부 지우기
    public int deleteAllComment (int board_seq) {
    	Map<String, Object> params = new HashMap<>();
    	params.put("board_seq", board_seq);
    	
    	return dao.deleteAllComment(params);
    }
    
    //2. 보드 부모 시퀀스로 댓글 전부 가져오기
    public List<Map<String, Object>> getDetailBoardComments (int board_seq){
    	Map<String, Object> params = new HashMap<>();
    	params.put("board_seq", board_seq);
    	
    	return dao.getDetailBoardComments(params);
    }
    
    //3. 댓글 작성
    public int postComment(CommentDTO dto, String id) {
    	dto.setUser_id(id);
    	int result = dao.postComment(dto);
    	// 댓글 개수 → board.is_reported 에 반영
    	boardDao.updateCommentCount(dto.getBoard_seq());
    	return result;
    }
    
    //4. 댓글 삭제
    public void deleteComment(int comment_seq, String user_id) {
    	//1. 타겟 디티오 찾기
    	Map params1 = new HashMap();
    	params1.put("comment_seq", comment_seq);
    	params1.put("user_id", user_id);
    	CommentDTO targetdto = dao.findTargetDTO(params1);
    	
    	if (targetdto == null) {
            throw new RuntimeException("잘못된 접근입니다");
        }
    	
    	//2. 해당 댓글이 부모 댓글일 경우
    	if(targetdto.getParent_comment_seq() ==null) {
    		//자식댓글 개수 가져오기
    		int childCount = dao.getChildCount(comment_seq);
    		if(childCount>0) {//자식이 있다면
    			dao.softDeleteParent(comment_seq);
    		}else {//자식이 있다면
    			dao.strictDelete(comment_seq);
    		}
    	
    	}else {//3. 해당 댓글이 자식 댓글일 경우
    		dao.strictDelete(comment_seq); //자식댓글은 바로 삭제하고
    		System.out.println("ddd"+comment_seq);
    		Map params2 = new HashMap();
    		params2.put("comment_seq", targetdto.getParent_comment_seq());
    		// params2.put("user_id", user_id);
    		CommentDTO parentdto = dao.findTargetPDTO(params2); //부모 디티오 가져와서
    		System.out.println("dddddddddd"+parentdto);
    		if(parentdto.getIs_deleted()==1){//부모 디티오의 is_delete가 1 이면
    			int childCount =dao.getChildCount(parentdto.getComment_seq());//남아있는 자식 댓글을 조회하고
    			
    			if(childCount<1) {//남아있는 자식 댓글수가 0이면 찐삭
    				dao.strictDelete(parentdto.getComment_seq());
    			}
    		}
    	}
    	boardDao.updateCommentCount(targetdto.getBoard_seq());
    }
    
    //5. 댓글 수정
    public int updateComment(CommentDTO dto) {
    	return dao.updateComment(dto);
    }
    
}
