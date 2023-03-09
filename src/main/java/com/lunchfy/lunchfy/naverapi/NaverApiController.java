package com.lunchfy.lunchfy.naverapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lunch-fy")
public class NaverApiController {
    private ByteBuffer buffer;
    private String encode;
    private final NaverApiService myNaverApiService;

    @GetMapping("/search-loc")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity search (@RequestParam String location, @RequestParam String tag) {
        List<Place> list = myNaverApiService.locCombineData(location, tag);
        System.out.println("search-loc 실행됨");
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @GetMapping("/search-key")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity search2 (@RequestParam String key, @RequestParam String tag) {
        List<Place> list = myNaverApiService.keyCombineData(key, tag);
        System.out.println("search-loc 실행됨");
        return new ResponseEntity(list ,  HttpStatus.OK);
    }

    @GetMapping("/xy-key")
    public String[] xyTest(@RequestParam String key)
    {
        String[] xy = new String[2];
        xy = myNaverApiService.parsingLocationXY(myNaverApiService.placeResponse(key));
        return xy;
    }

    @GetMapping("/xy-loc")
    public String[] xyTest2(@RequestParam String location)
    {
        String[] xy = new String[2];
        xy = myNaverApiService.parsingLocationXY(myNaverApiService.locationResponse(location));
        return xy;
    }

    @GetMapping("/test")
    public String test (@RequestParam String query) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
        String encode = StandardCharsets.UTF_8.decode(buffer).toString();

        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/keyword.json")
                .queryParam("query", encode)
                .queryParam("x", 126.93068622168563)
                .queryParam("y", 37.40326558195946)
                .queryParam("radius", 1000)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("Authorization", "KakaoAK "+"4f97d3d265a2973723f41e4114c587cb")
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
        return result.getBody();
    }

    @GetMapping("/test2")
    public String test2 (@RequestParam String query) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
        String encode = StandardCharsets.UTF_8.decode(buffer).toString();

        URI uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/address.json")
                .queryParam("query", encode)
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("Authorization", "KakaoAK "+"4f97d3d265a2973723f41e4114c587cb")
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);

        return result.getBody();
    }
}
