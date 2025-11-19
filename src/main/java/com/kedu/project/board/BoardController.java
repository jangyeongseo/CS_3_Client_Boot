package com.kedu.project.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kedu.project.config.PageNaviConfig;
import com.kedu.project.file_info.FileDTO;
import com.kedu.project.file_info.FileService;


@RequestMapping("/board")
@RestController
public class BoardController {
	
	
    @Autowired
    private BoardService boardService;
    @Autowired
    private FileService fileService;
    
    
    //1. 보드 리스트 페이지 수와 필터에 맞게 보내주기
    @GetMapping
    public ResponseEntity <Map<String, Object>> getPagedFilteredBoardList(
    		@RequestParam(defaultValue="1") int page,
    		@RequestParam(defaultValue="all") String board_type,
    		@RequestParam(required=false) String target
    		){
    	System.out.println(page);
    	System.out.println(board_type);
    	System.out.println(target);
    	
    	
    	//0) 인증정보 조회
    	//
    	
    	
    	//1) 전체 게시글 개수 조회
    	int totalCount=0;
    	if(target!=null) {
    		totalCount = boardService.getTotalCountFindTarget(board_type,target);
    	}else{
    		totalCount = boardService.getTotalCount(board_type);
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
    		boards=boardService.getBoardListFromToWithTarget(board_type,offset,count,target);
    	}else{
    		boards= boardService.getBoardListFromTo(board_type,offset,count); //보드 타입에 대하여, n번째부터 n개 가져오기
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
    
    
    
    
    
}
