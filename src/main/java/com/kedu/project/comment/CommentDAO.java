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

  public int deleteAllComment(Map<String, Object> params) {
    return mybatis.delete("Comment.deleteAllComment", params);
  }

  public List<Map<String, Object>> getDetailBoardComments(Map<String, Object> params) {
    return mybatis.selectList("Comment.getDetailBoardComments", params);
  }

  public int postComment(CommentDTO dto) {
    return mybatis.insert("Comment.postComment", dto);
  }

  public CommentDTO findTargetDTO(Map<String, Object> params1) {
    return mybatis.selectOne("Comment.findTargetDTO", params1);
  }

  public CommentDTO findTargetPDTO(Map<String, Object> params1) {
    return mybatis.selectOne("Comment.findTargetPDTO", params1);
  }

  public int getChildCount(int comment_seq) {
    return mybatis.selectOne("Comment.getChildCount", comment_seq);
  }

  public int softDeleteParent(int comment_seq) {
    return mybatis.delete("Comment.softDeleteParent", comment_seq);
  }

  public int strictDelete(int comment_seq) {
    return mybatis.delete("Comment.strictDelete", comment_seq);
  }

  public int updateComment(CommentDTO dto) {
    return mybatis.update("Comment.updateComment", dto);
  }

  public String getCommentWriterId(int commentSeq) {
    return mybatis.selectOne("Comment.getCommentWriterId", commentSeq);
  }

  public String getCommentWriterIdByCommentSeq(int seq) {
    return mybatis.selectOne("Comment.getCommentWriterIdByCommentSeq", seq);
  }
}
