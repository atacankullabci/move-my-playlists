package com.atacankullabci.immovin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("callback")
@CrossOrigin("http://localhost:4200")
public class ResponseController {

    @GetMapping
    public void getURICode(@RequestParam(name = "code") String code, HttpServletResponse response) {
        System.out.println(code);
        try {
            response.sendRedirect("http://imovin.club");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
