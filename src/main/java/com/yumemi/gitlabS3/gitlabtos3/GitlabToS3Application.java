package com.yumemi.gitlabS3.gitlabtos3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GitlabToS3Application {

	public static void main(String[] args) {
		SpringApplication.run(GitlabToS3Application.class, args);
	}

	@Autowired
	private GitlabToS3Service gitlabToS3Service;

	@RequestMapping("/run")
	public String run() throws Exception {
		return  gitlabToS3Service.uploadZipFile() ? "Success" : "Failure" ;
	}
}
