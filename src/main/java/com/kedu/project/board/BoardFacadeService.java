package com.kedu.project.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kedu.project.comment.CommentService;
import com.kedu.project.file_info.FileService;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@Transactional
public class BoardFacadeService {
	@Autowired
	private BoardService bServ;
	@Autowired
	private FileService fServ;
	@Autowired
	private CommentService cServ;

	public int postBoard(BoardDTO dto, MultipartFile[] files ) {
		int targetSeq = bServ.postBoard(dto);
		fServ.insert(files,"board",targetSeq,dto.getUser_id()); 
		return targetSeq;
	}
	
	public int deleteBoard(int board_seq, String user_id, String target_type) {
		fServ.deleteAllFile(board_seq, user_id, target_type);
		cServ.deleteAllComment(board_seq);
		
		return bServ.deleteTargetBoard(board_seq, user_id);
	}
	
	public void updateBoard(
		    String id,
		    int board_seq,
		    String title,
		    String board_type,
		    boolean is_privated,
		    String content,
		    MultipartFile thumbnail,
		    Boolean removeThumbnail,  
		    String deletedFiles,
		    MultipartFile[] files,
		    Boolean justChanged,
		    String imageSysListJson
		) {
		BoardDTO bDto= BoardDTO.builder().user_id(id).board_seq(board_seq).title(title).board_type(board_type).is_privated(is_privated).content(content).build();
		bServ.updateBoard(bDto);
		
		if (Boolean.TRUE.equals(removeThumbnail)) {
		    fServ.deleteThumbnail(board_seq);
		}

		if (Boolean.TRUE.equals(justChanged)) {
		    fServ.replaceThumbnail(thumbnail, "board", board_seq, id);
		}
		
		else if (thumbnail != null && !thumbnail.isEmpty()) {
			fServ.replaceThumbnail(thumbnail, "board", board_seq, id);
		}
		
	    if (deletedFiles != null && !deletedFiles.isBlank()) {
	        try {
	            List<Integer> deleteSeqList = new ObjectMapper()
	                    .readValue(deletedFiles, new TypeReference<List<Integer>>() {});

	            for (Integer fileSeq : deleteSeqList) {
	                fServ.deleteFileBySeq(fileSeq);
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("삭제 파일 파싱 오류", e);
	        }
	    }

	    if (files != null && files.length > 0) {
	        fServ.insert(files, "board", board_seq, id);
	    }
	    
	    fServ.syncBoardImages(imageSysListJson, board_seq, id, "board/img");
	}
	
	
}
