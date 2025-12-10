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

	public int deleteAllComment(int board_seq) {
		Map<String, Object> params = new HashMap<>();
		params.put("board_seq", board_seq);

		return dao.deleteAllComment(params);
	}

	public List<Map<String, Object>> getDetailBoardComments(int board_seq) {
		Map<String, Object> params = new HashMap<>();
		params.put("board_seq", board_seq);

		return dao.getDetailBoardComments(params);
	}

	public int postComment(CommentDTO dto, String id) {
		dto.setUser_id(id);
		int result = dao.postComment(dto);
		boardDao.updateCommentCount(dto.getBoard_seq());
		return result;
	}

	public void deleteComment(int comment_seq, String user_id) {
		Map params1 = new HashMap();
		params1.put("comment_seq", comment_seq);
		params1.put("user_id", user_id);
		CommentDTO targetdto = dao.findTargetDTO(params1);

		if (targetdto == null) {
			throw new RuntimeException("잘못된 접근입니다");
		}

		if (targetdto.getParent_comment_seq() == null) {
			int childCount = dao.getChildCount(comment_seq);
			if (childCount > 0) {
				dao.softDeleteParent(comment_seq);
			} else {
				dao.strictDelete(comment_seq);
			}

		} else {
			dao.strictDelete(comment_seq);
			Map params2 = new HashMap();
			params2.put("comment_seq", targetdto.getParent_comment_seq());
			CommentDTO parentdto = dao.findTargetPDTO(params2);
			if (parentdto.getIs_deleted() == 1) {
				int childCount = dao.getChildCount(parentdto.getComment_seq());

				if (childCount < 1) {
					dao.strictDelete(parentdto.getComment_seq());
				}
			}
		}
		boardDao.updateCommentCount(targetdto.getBoard_seq());
	}

	public int updateComment(CommentDTO dto) {
		return dao.updateComment(dto);
	}

}
