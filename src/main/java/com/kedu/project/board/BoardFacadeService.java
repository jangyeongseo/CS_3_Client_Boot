package com.kedu.project.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kedu.project.file_info.FileService;

@Service
@Transactional
public class BoardFacadeService {
	@Autowired
	private BoardService bServ;
	@Autowired
	private FileService fServ;
	

	//보드 작성
	public int postBoard(BoardDTO dto, MultipartFile[] files ) {
		int targetSeq = bServ.postBoard(dto);//발행한 시퀀스 번호 가져와야함
		fServ.insert(files,"board",targetSeq,dto.getUser_id()); //파일데이터, 타겟 타입, 타겟 시퀀스, 유저 아이디
		return targetSeq;
	}
	
	
	
}
