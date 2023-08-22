package com.multi.mvc.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.multi.mvc.board.model.vo.Board;
import com.multi.mvc.common.util.PageInfo;
import com.multi.mvc.member.model.service.MemberService;
import com.multi.mvc.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j // log4j 선언 대신에 작성해주는 lombok 어노테이션
@Controller
@SessionAttributes("loginMember") // loginMember를 model에서 취급할때 세션으로 처리하는 어노테이션
public class MemberController {
	
	@Autowired
	private MemberService service;

	// action : /mvc/login
	// 파라메터 : memberId, memberPwd
//	@RequestMapping(name="/login", method = RequestMethod.POST)
	@PostMapping("/login")
	String login(Model model, String memberId, String memberPwd) {
		log.info("@@@Login : " + memberId +", "+ memberPwd);
		
		Member loginMember = service.login(memberId, memberPwd);
		
		// 로그인이 성공한 케이스
		if(loginMember != null) {
			model.addAttribute("loginMember", loginMember); // 세션으로 저장되는 코드, @SessionAttributes 사용
			return "redirect:/"; // home으로 보내는 방법
		}else {
			model.addAttribute("msg", "아이디와 패스워드를 확인해주세요.");
			model.addAttribute("location", "/");
			return "common/msg";
		}
	}
	
	@RequestMapping("/logout")
	public String logout(SessionStatus staus) { // staus: 세션의 상태확인 가능한 메소드
		log.info("staus : " + staus.isComplete());
		staus.setComplete(); // 세션을 종료 시키는 메소드
		log.info("staus : " + staus.isComplete());
		return "redirect:/";
	}
	

	// 회원가입 페이지를 연결시켜주는 핸들러
	@GetMapping("/member/enroll")
	public String enrollPage() { // xxxPage = 단순 html을 연결만 시켜주는 핸들러 메소드
		log.info("회원가입 페이지 요청");
		return "member/enroll";
	}
	
	
	// 실제 회원가입을 시켜주는 핸들러
	@PostMapping("/member/enroll")
	public String enroll(Model model, @ModelAttribute Member member) { // @ModelAttribute 생략 가능!
		log.info("회원가입 요청");
		
		int result = service.save(member);
		
		if(result > 0) {
			model.addAttribute("msg", "회원가입 성공하였습니다.");
			model.addAttribute("location", "/");
		} else { 
			model.addAttribute("msg", "회원가입에 실패하였습니다. 입력정보 확인하세요.");
			model.addAttribute("location", "/member/enroll");
		}
		
		return "common/msg";
	}

	// AJAX로 ID 검사부
	@RequestMapping("/member/idCheck")
	public ResponseEntity<Map<String, Object>> idCheck(String id){
		log.info("아이디 중복 확인 : " +  id);
		
		boolean result = service.validate(id);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("validate", result); // json으로 변경되어 리턴될 예정
		
		return new ResponseEntity<Map<String,Object>>(jsonMap, HttpStatus.OK);
	}
	
	

	// 회원가입 페이지를 연결시켜주는 핸들러
	@GetMapping("/member/view")
	public String memberView(Model model) {
		log.info("회원 정보 보기 및 수정");
		// session으로 처리할 예정으로 아래 코드는 불필요
		// 원래는 수정 할 정보를 조회해서 model에 담아서 view로 보내야한다.
//		Member member = service.findMember();
//		model.addAttribute("member", member);
		return "member/view";
	}
	
	// 회원정보 수정
	@PostMapping("/member/update")
	public String update(Model model,
			@ModelAttribute Member updateMember, // request에서 온 값
			@SessionAttribute(name="loginMember", required = false) Member loginMember // 세션값
			) {
		log.info("update 요청, updateMember : " + updateMember);
		log.info("update 요청, loginMember : " + loginMember);
		
		// 보안코드 작성부! + 체크부 작성, 예시
		if(loginMember == null || loginMember.getId().equals(updateMember.getId()) == false) {
			model.addAttribute("msg","잘못 된 접근입니다.");
			model.addAttribute("location","/");
			return "common/msg";
		}
		
		updateMember.setMno(loginMember.getMno()); // update가 되는 코드
		int result = service.save(updateMember);
		
		if(result > 0) {
			loginMember = service.findById(loginMember.getId());
			model.addAttribute("loginMember", loginMember); // 세션을 업데이트 하는 코드
			model.addAttribute("msg", "회원정보를 수정하였습니다.");
			model.addAttribute("location","/member/view");
		} else {
			model.addAttribute("msg", "회원정보 수정에 실패하였습니다.");
			model.addAttribute("location","/member/view");
		}
		return "common/msg";
	}
	
	
	@GetMapping("/member/updatePwd")
	public String updatePwdPage() {
		return "/member/updatePwd";
	}
	
	@PostMapping("/member/updatePwd")
	public String updatePwd(Model model, 
			String password,
			@SessionAttribute(name="loginMember", required = false) Member loginMember // 세션값
			) {
		int result = service.updatePwd(loginMember, password);
		
		if(result > 0) {
			model.addAttribute("msg", "비밀번호 수정에 성공하였습니다.");
		}else {
			model.addAttribute("msg", "비밀번호를 변경 할수 없습니다.");
		}
		model.addAttribute("script","self.close();");
		
		return "/common/msg";
	}
	
	@RequestMapping("/member/delete")
	public String delete(Model model, 
			@SessionAttribute(name="loginMember", required = false) Member loginMember // 세션값
			) {
		int result = service.delete(loginMember.getMno());
		
		if(result > 0) {
			model.addAttribute("msg", "회원탈퇴에 성공하였습니다.");
			model.addAttribute("location", "/logout");
		}else {
			model.addAttribute("msg", "회원탈퇴를 할수 없습니다.");
			model.addAttribute("location", "/member/view");
		}
		
		return "/common/msg";
	}
	

	@RequestMapping(value = "/admin/member", method = RequestMethod.GET)
	public String memberlist(Locale locale, Model model) {
		log.info("@@@@@@@@@@@ selectAll : " + service.findAll());
		int page = 1;
		
		int memberCount = service.getMemberCount();
		PageInfo pageInfo = new PageInfo(page, 10, memberCount, 10); // 게시글이 보여지는 갯수 = 10
		List<Member> list = service.findAll();
		System.out.println("list : " + list);
		
		model.addAttribute("list", list);
		model.addAttribute("pageInfo", pageInfo);
		
		return "/member/list";
	}
}



















