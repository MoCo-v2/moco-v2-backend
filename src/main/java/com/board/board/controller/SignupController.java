package com.board.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.board.config.auth.CheckUseremailValidator;
import com.board.board.config.auth.CustomAuthFailureHandler;
import com.board.board.dto.UserDto;
import com.board.board.service.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SignupController {
	private final UserService userService;
	private final CheckUseremailValidator checkUseremailValidator;
	private final HttpSession httpSession;

	/* 커스텀 유효성 검증을 위해 추가 */
	@InitBinder //: 특정 컨트롤러에서 바인딩 또는 검증 설정을 변경하고 싶을 때 사용
	public void validatorBinder(
		WebDataBinder binder) {  //WebDataBinder : Http 요청 정보를 컨트롤러 메소드의 파라미터나 모델에 바인딩할 때 사용되는 바인딩 객체
		binder.addValidators(checkUseremailValidator);
	}

	/* 회원가입 페이지 */
	@GetMapping("/signup")
	public String sign_up_page(Model model) {
		model.addAttribute("userDto", new UserDto.Request()); //빈객체 전달
		return "login/signup";
	}

	/* 회원가입화면 아이디 중복확인 (email) */
	@GetMapping("/id/check")
	public ResponseEntity<?> checkEmailDuplication(@RequestParam(value = "email") String email) throws
		CustomAuthFailureHandler.BadRequestExection {
		if (userService.checkUseremailDuplication(email)) {
			throw new CustomAuthFailureHandler.BadRequestExection("이미 사용중인 아이디 입니다.");
		}

		return ResponseEntity.ok("사용 가능한 아이디 입니다.");
	}

	/* 별명 중복 체크 */
	@GetMapping("/name/check")
	public ResponseEntity<?> checkNameDupulication(@RequestParam(value = "nickname") String name) throws
		CustomAuthFailureHandler.BadRequestExection {
		if (userService.checkUsernameDuplication(name)) {
			throw new CustomAuthFailureHandler.BadRequestExection("이미 사용중인 별명 입니다.");
		}

		return ResponseEntity.ok("사용 가능한 별명 입니다.");
	}

	// /* SNS로그인 사용자 회원가입 처리 (실제로는 이름만 Update) */
	// @GetMapping("/signup/name/edit")
	// public String nickNameUpdate(@RequestParam String name, @LoginUser SessionUser sessionUser, Model model) {
	// 	String email = sessionUser.getEmail();
	// 	String picture = sessionUser.getPicture();
	//
	// 	/* 별명 중복 가능성 */
	// 	if (userService.checkUsernameDuplication(name)) {
	// 		model.addAttribute("error", "이미 존재하는 별명입니다.");
	// 		model.addAttribute("email", email);
	// 		model.addAttribute("nickname", name);
	// 		return "login/OauthNameCheck";
	// 	}
	//
	// 	User user = userService.nameUpdate(email, name, picture);
	// 	httpSession.setAttribute("user", new SessionUser(user)); // SessionUser (직렬화된 dto 클래스 사용)
	//
	// 	return "redirect:/";
	// }
}
