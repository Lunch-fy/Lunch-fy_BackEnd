package com.lunchfy.lunchfy.naverapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class NaverApiController {

    public NaverApiService myNaverApiService = new NaverApiService();
    @GetMapping("/test")
    public ResponseEntity search (@RequestParam String searchThing) {
        String list = myNaverApiService.placeResponse(searchThing);
        //List<Place> list = myNaverApiService.placeResponse(searchThing);
        return new ResponseEntity(list, HttpStatus.OK);
    }

}

