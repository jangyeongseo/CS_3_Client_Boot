package com.kedu.project.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kedu.project.config.PageNaviConfig;
import com.kedu.project.file_info.FileDTO;

import com.kedu.project.file_info.FileService;

@Service
public class BoardService {
    @Autowired
    private BoardDAO dao;
    @Autowired
	private FileService fServ;
    
    public Map<String, Object> getPagedFilteredBoardList(
            int page,
            String board_type,
            String target,
            String id
    ) {
        boolean is_privated = (id != null && !id.equals("anonymousUser"));
        int totalCount;
        if (target != null) {
            totalCount = getTotalCountFindTarget(board_type, target, is_privated);
        } else {
            totalCount = getTotalCount(board_type, is_privated);
        }

        if (totalCount == 0) {
            return null;
        }

        int count = PageNaviConfig.RECORD_COUNT_PER_PAGE;
        int offset = (page - 1) * count;

        List<BoardDTO> boards;
        if (target != null) {
            boards = getBoardListFromToWithTarget(board_type, offset, count, target, is_privated);
        } else {
            boards = getBoardListFromTo(board_type, offset, count, is_privated);
        }

        List<Integer> seqList = boards.stream()
                .map(BoardDTO::getBoard_seq)
                .toList();

        List<FileDTO> thumbs = fServ.getThumsFromTo(seqList);

        Map<String, Object> response = new HashMap<>();
        response.put("boards", boards);
        response.put("thumbs", thumbs);
        response.put("totalCount", totalCount);
        response.put("page", page);
        response.put("count", count);

        return response;
    }

    public int getTotalCount (String board_type, boolean is_privated) {
    	if(board_type.equals("all")) {
    		return dao.getTotalAllCount(is_privated);
    	}else {
    		Map <String, Object> params = new HashMap<>();
    		params.put("is_privated", is_privated);
    		params.put("board_type", board_type);
    		return dao.getTotalTypeCount(params);
    	}
    }
    
    public int getTotalCountFindTarget(String board_type, String target, boolean is_privated) {
    	if(board_type.equals("all")) {
    		Map <String, Object> params = new HashMap<>();
    		params.put("target", target);
    		params.put("is_privated", is_privated);
    		return dao.getTotalAllFindTargetCount(params);
    	}else {
    		Map <String, Object> params = new HashMap<>();
    		params.put("board_type", board_type);
    		params.put("target", target);
    		params.put("is_privated", is_privated);
    		return dao.getTotalTypeFindTargetCount(params);
    	}
    }
    
    public List<BoardDTO> getBoardListFromTo (String board_type, int offset, int count,boolean is_privated ){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_type",board_type);
    	params.put("offset", offset);
    	params.put("count", count);
    	params.put("is_privated", is_privated);
    	
    	return dao.getBoardListFromTo(params);
    }
    
    public List<BoardDTO> getBoardListFromToWithTarget(String board_type, int offset, int count, String target,boolean is_privated ){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_type",board_type);
    	params.put("offset", offset);
    	params.put("count", count);
    	params.put("target", target);
    	params.put("is_privated", is_privated);
    	
    	return dao.getBoardListFromToWithTarget(params);
    }
    
    public int postBoard(BoardDTO dto) {
    	return dao.postBoard(dto);
    }
    
    public Map<String, Object> getDetailBoard(int board_seq){
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_seq",board_seq);
    	return dao.getDetailBoard(params);
    }
    
    public int deleteTargetBoard(int board_seq, String user_id) {
    	Map<String, Object> params= new HashMap<>();
    	params.put("board_seq",board_seq);
    	params.put("user_id",user_id);
    	
    	return dao.deleteTargetBoard(params);
    }
    
    public int updateBoard(BoardDTO dto) {
    	return dao.updateBoard(dto);
    }
    
    public int increaseViewCount(int board_seq) {
    	return dao.increaseViewCount(board_seq);
    }
    
}
