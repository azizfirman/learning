package com.learning.springboot.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.learning.springboot.entity.Learning;
import com.learning.springboot.pojo.LearningPojo;
import com.learning.springboot.repository.LearningRepository;
import com.learning.springboot.util.Response;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class LearningService {
    @Autowired
    LearningRepository learningRepository;

    @Autowired
    TemplateEngine templateEngine;

    public ResponseEntity<Response> getData() {
        Response response = new Response();
        Map<String, Object> data = new HashMap<>();
        List<Learning> learning = learningRepository.findAll();

        data.put("learning", learning);
        data.put("total", learning.size());

        response.setData(data);
        response.setStatus(true);
        response.setMessage("success");

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Response> saveData(LearningPojo learningPojo) {
        Response response = new Response();

        if(!checkMandatory(learningPojo).isEmpty()){
            response.setData(null);
            response.setStatus(false);
            response.setMessage(checkMandatory(learningPojo));
        }
        else{
            Learning learning = new Learning();
            learning.setName(learningPojo.getName());
            learning.setEmail(learningPojo.getEmail());
            learning.setEmployeeID(learningPojo.getEmployeeId());
            learningRepository.save(learning);

            response.setData(learning);
            response.setStatus(true);
            response.setMessage("success");
        }

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Response> updateData(LearningPojo learningPojo) {
        Response response = new Response();

        try {
            UUID id = UUID.fromString(learningPojo.getId());
            Optional<Learning> learning = learningRepository.findById(id);

            if(learning.isPresent()){
                learning.get().setName(learningPojo.getName());
                learning.get().setEmail(learningPojo.getEmail());
                learning.get().setEmployeeID(learningPojo.getEmployeeId());
                learningRepository.save(learning.get());

                response.setData(learning.get());
                response.setStatus(true);
                response.setMessage("success");
            }
            else{
                response.setData(null);
                response.setStatus(false);
                response.setMessage("Data " + learningPojo.getId() +  " not found!");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            response.setData(null);
            response.setStatus(false);
            response.setMessage(learningPojo.getId() +  " is incorrect id!");
        }

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<Response> deleteData(String id) {
        Response response = new Response();

        try {
            UUID uuid = UUID.fromString(id);
            Optional<Learning> learning = learningRepository.findById(uuid);

            if(learning.isPresent()){
                learningRepository.delete(learning.get());

                response.setStatus(true);
                response.setMessage("success");
                response.setData(id + " deleted is successfully!");
            }
            else{
                response.setData(null);
                response.setStatus(false);
                response.setMessage("Data " + id +  " not found!");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            response.setData(null);
            response.setStatus(false);
            response.setMessage(id +  " is incorrect id!");
        }

        return ResponseEntity.ok().body(response);
    }

    public Map<String, String> checkMandatory(LearningPojo learningPojo) {
        Map<String, String> errorMessage = new HashMap<>();
        Field[] fields = LearningPojo.class.getDeclaredFields();

        for(Field field : fields){
            field.setAccessible(true);
            try {
                if(field.get(learningPojo).toString().isEmpty()){
                    errorMessage.put(field.getName(), field.getName() + " is mandatory!");
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return errorMessage;
    }

    public ResponseEntity<Object> generatePDF(String id, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            UUID uuid = UUID.fromString(id);
            Optional<Learning> learning = learningRepository.findById(uuid);

            if(learning.isPresent()){
                Context context = new Context();
                context.setVariable("name", learning.get().getName());
                context.setVariable("email", learning.get().getEmail());
                context.setVariable("employeeID", learning.get().getEmployeeID());

                String html = templateEngine.process("learning", context);

                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();

                renderer.createPDF(byteArrayOutputStream);
                byteArrayOutputStream.close();

                byte[] pdf = byteArrayOutputStream.toByteArray();

                httpServletResponse.setContentLength(pdf.length);
                httpServletResponse.setContentType("application/pdf");
                httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=Learning.pdf");

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdf);
                FileCopyUtils.copy(byteArrayInputStream, httpServletResponse.getOutputStream());
                return ResponseEntity.ok().build();
            }
            else{
                response.setData(null);
                response.setStatus(false);
                response.setMessage("Data " + id +  " not found!");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            response.setData(null);
            response.setStatus(false);
            response.setMessage(id +  " is incorrect id!");
        } catch (IOException ioException) {
            response.setData(null);
            response.setStatus(false);
            response.setMessage("Generate PDF is failed!");
        }
        return ResponseEntity.ok().body(response);
    }
}