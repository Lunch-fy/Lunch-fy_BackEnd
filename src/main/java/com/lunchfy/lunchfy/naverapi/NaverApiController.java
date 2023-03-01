package com.lunchfy.lunchfy.naverapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lunchfy")
public class NaverApiController {
    private ByteBuffer buffer;
    private String encode;
    private final NaverApiService myNaverApiService;

    @GetMapping("/search")
    public ResponseEntity search (@RequestParam String searchThing) {
        List<Place> list = myNaverApiService.placeResponse(searchThing);
        return new ResponseEntity(list, HttpStatus.OK);
    }


}
