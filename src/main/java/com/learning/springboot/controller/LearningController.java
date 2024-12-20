package com.learning.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.learning.springboot.pojo.LearningPojo;
import com.learning.springboot.service.LearningService;
import com.learning.springboot.util.Response;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/learning")
public class LearningController {

	@Autowired
	LearningService learningService;

	@GetMapping("get")
	public ResponseEntity<Response> get() {
		return learningService.getData();
	}

	@PostMapping("post")
	public ResponseEntity<Response> post(@RequestBody LearningPojo learningPojo) {
		return learningService.saveData(learningPojo);
	}

	@PutMapping("put")
	public ResponseEntity<Response> put(@RequestBody LearningPojo learningPojo) {
		return learningService.updateData(learningPojo);
	}

	@DeleteMapping("delete")
	public ResponseEntity<Response> delete(@RequestParam String id) {
		return learningService.deleteData(id);
	}

	@GetMapping("generate-pdf/{id}")
	public ResponseEntity<Object> generatePDF(@PathVariable String id, HttpServletResponse httpServletResponse) {
		return learningService.generatePDF(id, httpServletResponse);
	}
}