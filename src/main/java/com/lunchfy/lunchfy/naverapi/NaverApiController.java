package com.lunchfy.lunchfy.naverapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class NaverApiController {

    private final NaverApiService myNaverApiService;

    @GetMapping("/test")
    public ResponseEntity search (@RequestParam String searchThing) {
        List<Place> list = myNaverApiService.placeResponse(searchThing);
        return new ResponseEntity(list, HttpStatus.OK);
    }

}

