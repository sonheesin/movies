package com.firstgroup.movies.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.firstgroup.movies.domain.DirectorVO;
import com.firstgroup.movies.domain.ImgVO;
import com.firstgroup.movies.service.DirectorServiceImpl;
import com.firstgroup.movies.service.ImgServiceImpl;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RestController 				// @Controller + @ResponseBody (주용도는 Json 형태로 객체 데이터를 반환)
@RequestMapping("/director/*") 	// 들어온 요청 url을 지정
@Log4j2 						// 콘솔에 로그 출력을 위한 어노테이션
public class DirectorRESTController {
	
	@Setter(onMethod_ = @Autowired) // 해당 변수에 자동으로 주입해달라고 스프링에게 요청하는 표시
	private DirectorServiceImpl service; 
	//DirectorServiceImpl 타입의 객체를 생성
	
	@Setter(onMethod_ = @Autowired)
	private ImgServiceImpl imgService;
	
	@GetMapping("/list") // director 리스트
	
	/*	//Model 방식(파라미터 방식)은 메서드에 파라미터로 넣어주고 String 형태로 반환한다.
	Model에 값을 넣을 때 addAttribute() 사용

	ModelAndVIew 방식(컴포넌트 방식)은 ModelAndView 객체를 생성해서 객체 형태로 반환한다.

	말 그대로 Model + View를 합쳐놓은 방식이다.
	값을 넣을 때는 addObject()를 사용하고, 값을 보낼 View를 세팅하는 것은 setViewName()을 사용한다.*/

	public ModelAndView directorList(Model model) { 
		log.info("REST directorList...........");
		ModelAndView mv = new ModelAndView(); //
		mv.setViewName("/director/directorList"); // mv에  값을 보낼 View를 넣음
		
		 List<DirectorVO> dirList = service.directorList();  //service의 directorList를  리스트사용한 dirlist에 넣음 
		 for(DirectorVO vo : dirList) {
			 // dirList에서 차례대로 객체를 꺼내서 DirectorVO vo에다가 dirList에 더이상 꺼낼 객체가 없을 때 까지
			 vo.setImgList(imgService.findByBno("tbl_director_img", vo.getDirBno()));
			 } //vo의 setImgList에 imgService의 findByBno 찾아서 tbl_director_img인 vo.getDirBno에 넣음
		 log.info(dirList);
		 mv.addObject("directorList", dirList); //위에서 찾은 값을 mv에 넣음
		 
		return mv;
	}
	@GetMapping
	public ModelAndView register() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/director/register");
		return mv;
	}
	
	@PostMapping (value = "/register",produces="application/text; charset=UTF-8")
	public String register(@RequestBody DirectorVO dirVo,Model model,RedirectAttributes rttr)throws Exception{
		log.info("register 문제 :" + dirVo);
		log.info(model);
		service.insertDirector(dirVo);
		log.info(dirVo);
		for(ImgVO img : dirVo.getImgList()) {
	    	img.setBno(dirVo.getDirBno());
	    	img.setTblName("tbl_Director_img");
			log.info(img);
		    imgService.insert(img);
		}
		//처리 결과에 다른 응답 데이터 설정하기
		return dirVo.getDirName();
		
		
	}
	
	@GetMapping("/modify/{dirBno}") // 감독 조회
	public ModelAndView getDirector(@PathVariable Long dirBno,Model model) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/director/modify");
		log.info("/director/getDirector");
		DirectorVO dir = service.getDirector(dirBno);
		dir.setImgList(imgService.findByBno("tbl_director_img", dirBno));
		model.addAttribute("dirVo", dir);
		
		return mv;
	}
	
	@PostMapping(value="/modify")//감독 수정
	public String modify(@RequestBody DirectorVO dirVo, Model model, RedirectAttributes rttr) {
		log.info("modify:"+dirVo);
		service.modify(dirVo);
		log.info(dirVo);
		ImgVO tmp = new ImgVO();
		tmp.setTblName("tbl_director_img");
		tmp.setBno(dirVo.getDirBno());
		imgService.delete(tmp);
		 for(ImgVO img : dirVo.getImgList()) {
	        	img.setBno(dirVo.getDirBno());
	        	img.setTblName("tbl_director_img");
	        	log.info(img);
	            imgService.insert(img);
		
		}
		 return dirVo.getDirName();
		
	}
	
	@GetMapping("/delete/{dirBno}")
	public ModelAndView delete(@PathVariable Long dirBno ,HttpServletResponse response) {
	    // movBno를 사용하여 영화 삭제 로직을 수행
	    log.info("삭제할 영화 게시물 번호: " + dirBno);
	    service.remove(dirBno);
		ImgVO tmp = new ImgVO();
		tmp.setTblName("tbl_director_img");
		tmp.setBno(dirBno);
		imgService.delete(tmp);
	    ModelAndView mv = new ModelAndView();
		mv.setViewName("/director/directorList");
		
		try {
			response.sendRedirect("/director/list");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mv;
	}
	
}
