package com.firstgroup.movies.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.firstgroup.movies.domain.ImgVO;
import com.firstgroup.movies.domain.MemberVO;
import com.firstgroup.movies.domain.MoviesVO;
import com.firstgroup.movies.security.CustomUserDetailsService;
import com.firstgroup.movies.security.domain.CustomUser;
import com.firstgroup.movies.service.ActorServiceImpl;
import com.firstgroup.movies.service.ImgServiceImpl;
import com.firstgroup.movies.service.MemberServiceImpl;
import com.firstgroup.movies.service.MoviesService;
import com.firstgroup.movies.service.MoviesServiceImpl;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * Handles requests for the application home page.
 */
@Controller
@Log4j2
public class HomeController {

	@Setter(onMethod_ = @Autowired) //멤버 정보
	private MemberServiceImpl memberService;
	//MemberServiceImpl 타입의 객체를 생성

	@Setter(onMethod_ = @Autowired)
	private MoviesService movService; // 영화 정보

	@Setter(onMethod_ = @Autowired)
	private ActorServiceImpl actorService;
	
	@Setter(onMethod_ = @Autowired)
	private ImgServiceImpl imgService;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;


	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);
		
		List<MoviesVO> movieList = movService.getMovieList();
		 for(MoviesVO vo: movieList) {
	        vo.setImgList(imgService.findByBno("tbl_movies_img", vo.getMovBno()));
	      }
		model.addAttribute("movieList",movieList);
		
		return "home";
	}

	@GetMapping("/loginAuth") //로그인 페이지로 이동
	public void loginAuth(Model model) {
		log.info(model);
	}

	@GetMapping("/member/register") //회원 가입 페이지
	public String register() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //인증 확인 객체 생성
		if (authentication instanceof AnonymousAuthenticationToken) //AnonymousAuthenticationToken 확인되면 회원가입 페이지로
			return "/member/register";
		return "redirect:/";

	}

	@PostMapping("/member/register") 
	public String register(@Validated @RequestBody MemberVO memVo) { //객체 데이터를 반환
		log.info(memVo);
		memberService.register(memVo);
		for(ImgVO img : memVo.getImgList()) { //이미지 리스트돌려서 img 넣음
			img.setTblName("tbl_member_img"); //tbl_member_img 이름을 img.setTblName 넣음
			img.setBno(memVo.getMembno()); //memVo의 Membno 가져와서  img.setBno 넣음 
			imgService.insert(img);//imgService.insert를 사용해서 img 넣음
		}
		return "redirect:/loginAuth"; //로그인페이지로 이동
	}

	@GetMapping("/member/update") // 회원 정보 수정 페이지
	public String editPage(@AuthenticationPrincipal Model model) { //인증값 넣을 모델 생성
		CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//인증 과정을 거친 커스텀 유저 생성
		MemberVO memVo = memberService.getMember(user.getMember().getId());
		//memberService의 유저의 멤버,id 가져온 값을 getMember에 넣어서 memVo로 
		log.info(memVo);
		model.addAttribute("user", memVo); //찾은 값을 모델에 넣음
		return "/member/editPage"; //수정 페이지 이동

	}
	
	@PostMapping("/member/update") 
	public String edit(@RequestBody MemberVO memVo) { //회원 정보 수정
		CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		memVo.setId(user.getUsername()); //유저 이름 가져와서 memvo id에 넣음
		log.info(memVo);
		memberService.edit(memVo); //memberService 수정에 memVo 넣음
		log.info(user);
		ImgVO tmp = memVo.getImgList().get(0);
		tmp.setBno(memVo.getMembno());
		tmp.setTblName("tbl_member_img");
		imgService.delete(tmp);// imgService.delete에 찾은 tmp 값 넣음
		for (ImgVO vo : memVo.getImgList()) {
			vo.setBno(memVo.getMembno());
			vo.setTblName("tbl_member_img"); //이름 셋팅
			imgService.insert(vo); //이미지 넣음
		}
		sessionReset(user.getUsername());//?
		
		return "redirect:/"; 
	}
	
	@PostMapping("/member/delete")//회원정보 삭제
	@ResponseBody
	public Map<String, String> withDrawMember(@RequestBody Map<String,String> requestData,HttpServletRequest request) {
		Map<String, String> response = new HashMap<>(); //맵 생성
		CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logout(request);
		String result = requestData.get("bno"); //요청한번호 결과를 가져옴
	    Long bno = Long.parseLong(result); //String을 long으로 변환
		log.info("delete : " + bno);
		memberService.withdraw(bno,user.getUsername());//이름가져와서 memberService.withdraw로
		ImgVO vo = new ImgVO();
		vo.setBno(bno);//bno를 vo.setBno넣음
		vo.setTblName("tbl_member_img");//?
		
		imgService.delete(vo); //찾은값 imgService.delete
		
		response.put("status","success");
		response.put("message","회원 탈퇴가 성공적으로 처리되었습니다.");
		return response;
	}
	
	@GetMapping("/home")
	public void home(Model model) {
		log.info(model);
		List<MoviesVO> movieList = movService.getMovieList(); 
		 for(MoviesVO vo: movieList) {
	        vo.setImgList(imgService.findByBno("tbl_movies_img", vo.getMovBno()));
	      }
		model.addAttribute("movieList",movieList);
	}
	
	public void sessionReset(String username) { //인증정보 갱신
	    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
	    Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	    SecurityContextHolder.getContext().setAuthentication(newAuthentication);
	}
	
	public void logout(HttpServletRequest request) {//로그아웃
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();//인증 꺼내옴
	    if (auth != null) {
	        new SecurityContextLogoutHandler().logout(request, null, auth); //일치시 로그아웃
	    }
	}

}
