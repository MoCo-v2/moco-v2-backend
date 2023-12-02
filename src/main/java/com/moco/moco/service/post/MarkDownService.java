package com.moco.moco.service.post;

import org.springframework.stereotype.Service;

import io.github.furstenheim.CopyDown;

@Service
public class MarkDownService {

	/* Html -> MarkDown */
	public String convertHtmlToMarkDown(String Content) {
		CopyDown converter = new CopyDown();
		return converter.convert(Content);
	}
}
