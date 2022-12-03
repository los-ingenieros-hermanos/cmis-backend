package com.los.cmisbackend.util;

import java.io.IOException;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class Base64ImageEncoder {
	public String encodeImage(MultipartFile file){
	try {
		return Base64.getEncoder().encodeToString(file.getBytes());
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	}
	}
}
