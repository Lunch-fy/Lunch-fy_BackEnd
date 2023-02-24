package com.lunchfy.lunchfy.naverapi;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class NaverApiService {

    public List<Place> asdf () {
        List<Place> list = new ArrayList<>();

        String query;
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
        String encode = StandardCharsets.UTF_8.decode(buffer).toString();

        URI uri = UriComponentsBuilder
                .fromUriString("https://opneapi.naver.com")
                .path("/v1/search/local.json")
                .queryParam("query", encode)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "random")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", "lrhq41D596jlFArWopIi")
                .header("X-Naver-Client-Secret", "oKfHT8qCnU")
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);

        return list;
    }

}
