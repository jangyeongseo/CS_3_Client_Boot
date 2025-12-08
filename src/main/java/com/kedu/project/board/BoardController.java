package com.kedu.project.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kedu.project.comment.CommentService;
import com.kedu.project.config.PageNaviConfig;
import com.kedu.project.file_info.FileDTO;
import com.kedu.project.file_info.FileService;


@RequestMapping("/board")
@RestController
public class BoardController {
	
	
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardFacadeService boardFacadeService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CommentService commentService;
    
    
    
    //1. 보드 리스트 페이지 수와 필터에 맞게 보내주기
    @GetMapping
    public ResponseEntity <Map<String, Object>> getPagedFilteredBoardList(
    		@RequestParam(defaultValue="1") int page, //페이지 번호
    		@RequestParam(defaultValue="all") String board_type,//어떤 타입인지(필터)
    		@RequestParam(required=false) String target, //검색어
    		@AuthenticationPrincipal String id// 클라이언트 아이디
    		){
    	
        Map<String, Object> response = boardService.getPagedFilteredBoardList(page, board_type, target, id);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
    
    //2. 보드 작성
    @PostMapping(value="/write",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity <Void> write(
    	@RequestPart(required = false) MultipartFile[] files,
        @RequestParam("content") String content,
        @RequestParam("title") String title,
        @RequestParam("board_type") String board_type,
        @RequestParam("is_privated") boolean is_privated,
        @AuthenticationPrincipal String id,
        @RequestParam(value = "imageSysList", required = false) String imageSysListJson,
        @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
		System.out.println("작성"+id);
    	//1.파일과 보드를 facade layer로 보내서 트랜잭셔널 처리 : BoardDTO로 묶어서 files랑 같이 보냄
    	BoardDTO dto = BoardDTO.builder().user_id(id).title(title).content(content).board_type(board_type).is_privated(is_privated).build();
    	int target_seq;

    	if(files != null) {
    	    target_seq = boardFacadeService.postBoard(dto,files);
    	} else {
    	    target_seq = boardService.postBoard(dto);
    	}

    	fileService.confirmImg(imageSysListJson, target_seq);

    	if (thumbnail != null && !thumbnail.isEmpty()) {
    	    fileService.saveThumbnail(thumbnail, "board", target_seq, dto.getUser_id());
    	}
        
        return ResponseEntity.ok().build();
    }
    
    //3. 보드 디테일 가져오기
    @GetMapping("/detail")
    public ResponseEntity  <Map<String, Object>> getDetailBoard(
    		@RequestParam("seq") int board_seq,
    		@AuthenticationPrincipal String id
    		){
    	System.out.println("보기?"+id);
    	//0. 조회수 증가
    	boardService.increaseViewCount(board_seq);

    	//1. 보드 dto 가져오기 + 보드쓴 사람 닉네임 가져오기
    	Map<String, Object> result =boardService.getDetailBoard(board_seq);

    	//2. 첨부파일들 싹가져오기
    	List<FileDTO> files = fileService.getDetailBoardFile(board_seq, "board");
    	
    	//3. 댓글 싹 가져오기
    	List<Map<String, Object>> comments = commentService.getDetailBoardComments(board_seq);
    	
    	
    	//4. 클라이언트에게 보내기
    	Map<String, Object> response = new HashMap<>();
		response.put("boards", result);
		System.out.println("asdfasdf"+response.get("boards"));
		response.put("files", files);
		response.put("comments", comments);
		return ResponseEntity.ok(response);
    }
    
    
    //4. 보드 삭제하기
    @DeleteMapping("/delete")
    public ResponseEntity  <Void> deleteDetailBoard(
    		@RequestParam("seq") int board_seq, @AuthenticationPrincipal String id){    	
    	boardFacadeService.deleteBoard(board_seq, id, "board");
    	return ResponseEntity.ok().build();
    }
    
    //5. 보드 업데이트하기
    @PutMapping("/update")
    public ResponseEntity <Void> updateDetailBoard(
    		@AuthenticationPrincipal String id,
    	    @RequestParam("board_seq") int board_seq,
    	    @RequestParam("title") String title,
    	    @RequestParam("content") String content,
    	    @RequestParam("board_type") String board_type,
    	    @RequestParam("is_privated") boolean is_privated,

    	    @RequestParam(value = "files", required = false) MultipartFile[] files,
    	    @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
    	    @RequestParam(value = "deletedFiles", required = false) String deletedFilesJson,
    	    @RequestParam(value = "removeThumbnail", required = false) Boolean removeThumbnail,
    	    @RequestParam(value = "justChanged", required = false) Boolean justChanged,
    	    @RequestParam(value = "imageSysList", required = false) String imageSysListJson
    		){

    	boardFacadeService.updateBoard(
    			id,
                board_seq,
                title,
                board_type,
                is_privated,
                content,
                thumbnail,
                removeThumbnail,
                deletedFilesJson,
                files,
                justChanged,
                imageSysListJson
        );
    	return ResponseEntity.ok().build();
    }
    
}
