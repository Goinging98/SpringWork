package com.multi.mvc.notice.controller;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.multi.mvc.board.model.vo.Board;
import com.multi.mvc.board.model.vo.Reply;
import com.multi.mvc.board.model.vo.Notice;
import com.multi.mvc.common.util.PageInfo;
import com.multi.mvc.member.model.vo.Member;
import com.multi.mvc.notice.model.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
// @RequestMapping("/board") // 상위 borad로 시작하는 url을 생략해서 사용할 수 있음.
public class NoticeController {

	@Autowired
	private NoticeService service;

	@Autowired
	private ResourceLoader resourceLoader; // 파일 다운로드 기능시 활용하는 loader
	
	
	@GetMapping("/notice/list")
	public String list(Model model, @RequestParam Map<String, Object> param) {
		log.info("notice list 요청, param : " + param);
		
		int page = 1;
		try {
			if(param.get("searchType") != null) {
				param.put((String) param.get("searchType"), param.get("searchValue"));
				// title - 아이폰
				// content - 삽니다
			}
			
			// page 파라메터를 숫자로 바꿔주는 코드, 항상 try 끝에 존재해야한다.
			page = Integer.parseInt((String) param.get("page"));
		} catch (Exception e) {}
		
		int boardCount = service.getBoardCount(param);
//		PageInfo pageInfo = new PageInfo(page, 10, boardCount, 15); // 게시글이 보여지는 갯수 = 15
		PageInfo pageInfo = new PageInfo(page, 10, boardCount, 10); // 게시글이 보여지는 갯수 = 10
		List<Board> list = service.getBoardList(pageInfo, param);
//		System.out.println("list : " + list);
		
		model.addAttribute("list", list);
		model.addAttribute("param", param);
		model.addAttribute("pageInfo", pageInfo);
		
		return "/notice/list";
	}
	
	@RequestMapping("/notice/view")
	public String view(Model model, @RequestParam("no") int no) {
		Board board = service.findByNo(no);
		if(board == null) {
			return "redirect:error";
		}
		model.addAttribute("board", board);
		model.addAttribute("replyList", board.getReplies());
		return "notice/view";
	}
	
}








