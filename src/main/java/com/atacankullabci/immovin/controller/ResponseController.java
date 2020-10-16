package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("callback")
//@CrossOrigin("http://localhost:4200")
@CrossOrigin("http://imovin.club")
public class ResponseController {

    private final CacheService cacheService;

    public ResponseController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping
    public void getURICode(@RequestParam(name = "code") String code, HttpServletResponse response) {
        this.cacheService.put(code);
        System.out.println(code);
        try {
            //response.sendRedirect("http://localhost:4200/?code=" + code);
            response.sendRedirect("http://imovin.club/?code=" + code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/caches")
    public ResponseEntity<List<String>> getAllInactiveCaches() {
        return ResponseEntity.ok().body(this.cacheService.getAllInactivatedCaches());
    }

    @GetMapping("/all/caches")
    public ResponseEntity<Map<String, Boolean>> getAllCaches() {
        return ResponseEntity.ok().body(this.cacheService.getCodeMap());
    }
}
