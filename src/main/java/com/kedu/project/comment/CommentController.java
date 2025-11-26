package com.kedu.project.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/comment")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;    
    
    
    //1. 댓글 입력하기
    @PostMapping
    public ResponseEntity<Void> postComment(@RequestBody CommentDTO dto, @AuthenticationPrincipal String id){
    	commentService.postComment(dto,id);
    	
    	return ResponseEntity.ok().build();
    }
    
    //2.댓글 삭제
    @DeleteMapping("/{comment_seq}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("comment_seq") int comment_seq,
            @AuthenticationPrincipal String id) {
        commentService.deleteComment(comment_seq, id);
        return ResponseEntity.ok().build();
    }
    
    //3. 댓글 수정
    @PutMapping("/{comment_seq}")
    public ResponseEntity<Void> updateComment(
    		@PathVariable("comment_seq") int comment_seq,
    		@AuthenticationPrincipal String id,
    		@RequestBody CommentDTO dto
    		){
    	dto.setUser_id(id);
    	dto.setComment_seq(comment_seq);
    	commentService.updateComment(dto);

    	return ResponseEntity.ok().build();
    }
    
    
}
