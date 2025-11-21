package com.kedu.project.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    
    
    
    //1. 보드 리스트 페이지 수와 필터에 맞게 보내주기
    @GetMapping
    public ResponseEntity <Map<String, Object>> getPagedFilteredBoardList(
    		@RequestParam(defaultValue="1") int page, //페이지 번호
    		@RequestParam(defaultValue="all") String board_type,//어떤 타입인지(필터)
    		@RequestParam(required=false) String target, //검색어
    		@AuthenticationPrincipal String id// 클라이언트 아이디
    		){
    	System.out.println(page);
    	System.out.println(board_type);
    	System.out.println(target);
    	System.out.println(id);
    	
    	
    	//0) 인증정보 조회
    	boolean is_privated=false;
    	if(!id.equals("anonymousUser")) { //로그인 했다면
    		is_privated=true;
    	}
    	
    	//1) 전체 게시글 개수 조회
    	int totalCount=0;
    	if(target!=null) {
    		totalCount = boardService.getTotalCountFindTarget(board_type,target,is_privated);
    	}else{
    		totalCount = boardService.getTotalCount(board_type, is_privated);
    		}
    	if(totalCount==0) {
    		return ResponseEntity.noContent().build();
    	}
    	
    	//2) 페이지당 데이터 수 10개
    	int count = PageNaviConfig.RECORD_COUNT_PER_PAGE; //몇개 가져올지
    	int offset = (page - 1) * count; // 몇번째 데이터부터 시작
    	
    	//3) 페이지에 맞는 데이터 꺼내기
    	List<BoardDTO> boards=null;
    	if(target!=null) {
    		boards=boardService.getBoardListFromToWithTarget(board_type,offset,count,target,is_privated);
    	}else{
    		boards= boardService.getBoardListFromTo(board_type,offset,count,is_privated); //보드 타입에 대하여, n번째부터 n개 가져오기
    		}
    	
    	//4) 페이지에 맞는 데이터의 썸네일 파일이 있다면 가져옴
    		List<Integer> seqList = boards.stream().map(BoardDTO ->BoardDTO.getBoard_seq()).toList();//boards의 seq만 꺼내서 재구성
    		List<FileDTO> thumbs= fileService.getThumsFromTo(seqList);
    		
    	//5) 클라이언트 전송
    		Map<String, Object> response = new HashMap<>();
    		response.put("boards", boards);
    		response.put("thumbs", thumbs);
    		response.put("totalCount", totalCount);
    		response.put("page", page);
    		response.put("count",count);
    	
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
    	//1.파일과 보드를 facade layer로 보내서 트랜잭셔널 처리 : BoardDTO로 묶어서 files랑 같이 보냄
    	// ** 나중에 진짜 아이디 넣어야 함
    	BoardDTO dto = BoardDTO.builder().user_id("test1").title(title).content(content).board_type(board_type).is_privated(is_privated).build();
    	int target_seq=0;
    	if(files!=null) {
    		target_seq= boardFacadeService.postBoard(dto,files); //첨부파일 있으면 트랜잭션 처리
    	}else {
    		target_seq= boardService.postBoard(dto);//없으면 게시물만 올림
    	}
    	
    	//2. 이미지로 첨부된것 있는지 확인
    	fileService.confirmImg(imageSysListJson, target_seq);
        
//        System.out.println(thumbnail.getOriginalFilename());
        //3.썸네일 있으면 저장
        if (thumbnail != null && !thumbnail.isEmpty()) {
            fileService.saveThumbnail(thumbnail,"board", target_seq, dto.getUser_id());
        }
        
        return ResponseEntity.ok().build();
    }
    
    
    
    
}
