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

	@GetMapping
	public ResponseEntity<Map<String, Object>> getPagedFilteredBoardList(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "all") String board_type,
			@RequestParam(required = false) String target,
			@AuthenticationPrincipal String id) {

		Map<String, Object> response = boardService.getPagedFilteredBoardList(page, board_type, target, id);
		if (response == null) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(response);
	}

	
	@PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> write(
			@RequestPart(required = false) MultipartFile[] files,
			@RequestParam("content") String content,
			@RequestParam("title") String title,
			@RequestParam("board_type") String board_type,
			@RequestParam("is_privated") boolean is_privated,
			@AuthenticationPrincipal String id,
			@RequestParam(value = "imageSysList", required = false) String imageSysListJson,
			@RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {

		BoardDTO dto = BoardDTO.builder().user_id(id).title(title).content(content).board_type(board_type)
				.is_privated(is_privated).build();
		int target_seq;

		if (files != null) {
			target_seq = boardFacadeService.postBoard(dto, files);
		} else {
			target_seq = boardService.postBoard(dto);
		}

		fileService.confirmImg(imageSysListJson, target_seq);

		if (thumbnail != null && !thumbnail.isEmpty()) {
			fileService.saveThumbnail(thumbnail, "board", target_seq, dto.getUser_id());
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/detail")
	public ResponseEntity<Map<String, Object>> getDetailBoard(
			@RequestParam("seq") int board_seq,
			@AuthenticationPrincipal String id) {
		boardService.increaseViewCount(board_seq);

		Map<String, Object> result = boardService.getDetailBoard(board_seq);

		List<FileDTO> files = fileService.getDetailBoardFile(board_seq, "board");

		List<Map<String, Object>> comments = commentService.getDetailBoardComments(board_seq);

		Map<String, Object> response = new HashMap<>();
		response.put("boards", result);
		response.put("files", files);
		response.put("comments", comments);
		return ResponseEntity.ok(response);
	}

	// 4. 보드 삭제하기
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteDetailBoard(
			@RequestParam("seq") int board_seq, @AuthenticationPrincipal String id) {
		boardFacadeService.deleteBoard(board_seq, id, "board");
		return ResponseEntity.ok().build();
	}

	@PutMapping("/update")
	public ResponseEntity<Void> updateDetailBoard(
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
			@RequestParam(value = "imageSysList", required = false) String imageSysListJson) {

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
				imageSysListJson);
		return ResponseEntity.ok().build();
	}

}
