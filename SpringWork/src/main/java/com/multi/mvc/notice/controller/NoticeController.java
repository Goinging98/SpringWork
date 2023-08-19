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

import com.multi.mvc.notice.model.vo.Notice;
import com.multi.mvc.notice.model.vo.NoticeRead;
import com.multi.mvc.common.util.PageInfo;
import com.multi.mvc.member.model.vo.Member;
import com.multi.mvc.notice.model.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
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
		
		int noticeCount = service.getNoticeCount(param);
		PageInfo pageInfo = new PageInfo(page, 10, noticeCount, 10); // 게시글이 보여지는 갯수 = 10
		List<Notice> list = service.selectNoticeList(pageInfo, param);
		
		model.addAttribute("list", list);
		model.addAttribute("param", param);
		model.addAttribute("pageInfo", pageInfo);
		
		return "/notice/list";
	}
	
	@RequestMapping("/notice/view")
	public String view(Model model, @RequestParam("no") int no,
			@SessionAttribute(name="loginMember", required = false) Member loginMember) {
		Notice notice = service.findByNo(no);
		if(notice == null) {
			return "redirect:error";
		}
		if(loginMember != null) {
			NoticeRead noticeRead = new NoticeRead(no, loginMember.getMno(), null, null, null);
			try {
				service.insertNoticeRead(noticeRead);
			} catch (Exception e) {
			}
		}
		
		List<NoticeRead> list = service.selectNoticeReadList(no);
		model.addAttribute("notice", notice);
		model.addAttribute("readList", list);
		return "notice/view";
	}
	
	
	
	
	@GetMapping("/notice/error")
	public String error() {
		return "/common/error";
	}
	
	@GetMapping("/notice/write")
	public String writeView() {
		return "/notice/write";
	}
	
	// 게시글 처리 + 파일 업로드
	@PostMapping("/notice/write")
	public String write(Model model, HttpSession session,
			@SessionAttribute(name="loginMember", required = false) Member loginMember,
			@ModelAttribute Notice notice,
			@RequestParam("upfile") MultipartFile upfile
			) {
		log.info("notice write 요청, notice : " + notice);
		
		// 보안코드 예시
		if(loginMember.getId().equals(notice.getWriterId()) == false) {
			model.addAttribute("msg","잘못된 접근입니다.");
			model.addAttribute("location","/");
			return "common/msg";
		}
		
		notice.setMno(loginMember.getMno());
		
		// 파일 저장 로직
		if(upfile != null && upfile.isEmpty() == false) {
			String rootPath = session.getServletContext().getRealPath("resources");
			String savePath = rootPath + "/upload/notice";
			String renamedFileName = service.saveFile(upfile, savePath); // 실제 파일 저장로직
			
			if(renamedFileName != null) {
				notice.setRenamedFileName(renamedFileName);
				notice.setOriginalFileName(upfile.getOriginalFilename());
			}
		}
		log.debug(" notice : " + notice);
		int result = service.saveNotice(notice);
		
		if(result > 0) {
			model.addAttribute("msg", "게시글이 등록 되었습니다.");
			model.addAttribute("location", "/notice/list");
		}else {
			model.addAttribute("msg", "게시글 작성에 실패하였습니다.");
			model.addAttribute("location", "/notice/list");
		}
		return "common/msg";
	}
	
	
	@GetMapping("/notice/update")
	public String updateView(Model model,
			@SessionAttribute(name="loginMember", required = false) Member loginMember,
			@RequestParam("no") int no
			) {
		Notice notice = service.findByNo(no);
		model.addAttribute("notice",notice);
		return "/notice/update";
	}
	
	@PostMapping("/notice/update")
	public String update(Model model, HttpSession session,
			@SessionAttribute(name="loginMember", required = false) Member loginMember,
			@ModelAttribute Notice notice,
			@RequestParam("upfile") MultipartFile upfile
			) {
		log.info("게시글 수정 요청");
		
		// 보안상의 코드라 프로젝트때는 없어도 된다. 잘못된 접근 체킹하는 예시
		if(loginMember.getId().equals(notice.getWriterId()) == false) {
			model.addAttribute("msg", "잘못된 접근입니다.");
			model.addAttribute("location", "/");
			return "common/msg";
		}
		
		notice.setMno(loginMember.getMno());
		
		// 파일 저장 로직
		if(upfile != null && upfile.isEmpty() == false) {
			String rootPath = session.getServletContext().getRealPath("resources");
			String savePath = rootPath + "/upload/notice";
			String renamedFileName = service.saveFile(upfile, savePath); // 실제 파일 저장로직
			
			// 기존파일이 있었다면 삭제
			if(notice.getRenamedFileName() != null) {
				service.deleteFile(savePath + "/" + notice.getRenamedFileName());
			}
			
			if(renamedFileName != null) {
				notice.setRenamedFileName(renamedFileName);
				notice.setOriginalFileName(upfile.getOriginalFilename());
			}
		}
		log.debug(" notice : " + notice);
		int result = service.saveNotice(notice);
		
		if(result > 0) {
			model.addAttribute("msg", "게시글이 수정이 완료 되었습니다.");
			model.addAttribute("location", "/notice/list");
		}else {
			model.addAttribute("msg", "게시글 수정에 실패하였습니다.");
			model.addAttribute("location", "/notice/list");
		}
		return "common/msg";
	}
	
	@RequestMapping("/notice/delete")
	public String deleteNotice(Model model,  HttpSession session,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			int no
			) {
		log.info("게시글 삭제 요청 no : " + no);
		
		String rootPath = session.getServletContext().getRealPath("resources");
		String savePath = rootPath +"/upload/notice";
		int result = service.deleteNotice(no, savePath);
		
		if(result > 0) {
			model.addAttribute("msg", "게시글 삭제가 정상적으로 완료되었습니다.");
		}else {
			model.addAttribute("msg", "게시글 삭제에 실패하였습니다.");
		}
		model.addAttribute("location", "/notice/list");
		return "/common/msg";
	}
	
	// 파일 저장코드
	@RequestMapping("/notice/fileDown")
	public ResponseEntity<Resource> fileDown(
			@RequestParam("originName") String originName,
			@RequestParam("reName") String reName,
			@RequestHeader(name= "user-agent") String userAgent){
		try {
			Resource resource = resourceLoader.getResource("resources/upload/notice/" + reName);
			String downName = null;
			
			// 인터넷 익스플로러 인 경우
			boolean isMSIE = userAgent.indexOf("MSIE") != -1 || userAgent.indexOf("Trident") != -1;

			if (isMSIE) { // 익스플로러 처리하는 방법
				downName = URLEncoder.encode(originName, "UTF-8").replaceAll("\\+", "%20");
			} else {    		
				downName = new String(originName.getBytes("UTF-8"), "ISO-8859-1"); // 크롬
			}
			
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + downName + "\"")
					.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM.toString())
					.body(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패했을 경우
	}
	
}



