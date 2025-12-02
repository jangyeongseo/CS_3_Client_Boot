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
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class BoardFacadeService {
	@Autowired
	private BoardService bServ;
	@Autowired
	private FileService fServ;
	@Autowired
	private CommentService cServ;

	//보드 작성
	public int postBoard(BoardDTO dto, MultipartFile[] files ) {
		int targetSeq = bServ.postBoard(dto);//발행한 시퀀스 번호 가져와야함
		fServ.insert(files,"board",targetSeq,dto.getUser_id()); //파일데이터, 타겟 타입, 타겟 시퀀스, 유저 아이디
		return targetSeq;
	}
	
	//보드 삭제
	public int deleteBoard(int board_seq, String user_id, String target_type) {
		//1. 보드에 딸린 첨부파일 삭제(db+gcs다)+2.보드에 딸린 이미지 파일 삭제(db+gcs다)
		fServ.deleteAllFile(board_seq, user_id, target_type);

		//2.보드에 딸린 댓글 전체 삭제
		cServ.deleteAllComment(board_seq);
		
		//3.보드 삭제
		return bServ.deleteTargetBoard(board_seq, user_id);
	}
	
	//보드 업데이트	
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
		//1. board db내용 수정
		BoardDTO bDto= BoardDTO.builder().user_id(id).board_seq(board_seq).title(title).board_type(board_type).is_privated(is_privated).content(content).build();
		bServ.updateBoard(bDto);
		
		// 2. 썸네일 처리
		// 1) 이미지 없음 → 썸네일 삭제
		if (Boolean.TRUE.equals(removeThumbnail)) {
		    fServ.deleteThumbnail(board_seq);
		}

		// 2) 새 이미지 없음 + 순서만 바뀜 → 첫 이미지로 썸네일 변경
		if (Boolean.TRUE.equals(justChanged)) {
		    fServ.replaceThumbnail(thumbnail, "board", board_seq, id);
		}
		
		// 3) 새 이미지가 추가되고, 그게 첫번째 → 새 이미지로 썸네일
		else if (thumbnail != null && !thumbnail.isEmpty()) {
			fServ.replaceThumbnail(thumbnail, "board", board_seq, id);
		}
		
	    // 3. 삭제된 파일 처리 (file_seq 기준)
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

	    // 4. 신규 파일 업로드
	    if (files != null && files.length > 0) {
	        fServ.insert(files, "board", board_seq, id);
	    }
	    
	    // 5.에디터 내 이미지타입 싱크
	    fServ.syncBoardImages(imageSysListJson, board_seq, id, "board/img");
	}
	
	
}
