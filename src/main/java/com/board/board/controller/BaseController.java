package com.board.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BaseController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
}
