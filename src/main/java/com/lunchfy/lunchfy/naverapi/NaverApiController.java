package com.lunchfy.lunchfy.naverapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class NaverApiController {

    @GetMapping
    public ResponseEntity search (@RequestParam String SearchThing) {
        return new ResponseEntity(SearchThing, HttpStatus.OK);
    }

}
