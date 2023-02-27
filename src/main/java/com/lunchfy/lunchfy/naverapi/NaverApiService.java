package com.lunchfy.lunchfy.naverapi;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
@RequiredArgsConstructor
public class NaverApiService {

    //private final NaverApiRepository naverApiRepository;

    private ByteBuffer buffer;
    private String encode;
    private URI uri;


    //Geocoding api사용을 위한 함수
    public double[] geoResponse(String location) {
        buffer = StandardCharsets.UTF_8.encode(location);
        encode = StandardCharsets.UTF_8.decode(buffer).toString();

        uri = UriComponentsBuilder
                .fromUriString("https://naveropenapi.apigw.ntruss.com")
                .path("/map-geocode/v2/geocode")
                .queryParam("query", encode)
                .encode()
                .build()
                .toUri();

        return parsingGeo(getResponseEntity(uri, "X-NCP-APIGW-API-KEY-ID", "xsv7gw5z6r",
                "X-NCP-APIGW-API-KEY", "HWtsyaPDJq9rnIen0MFZnvtgqpJ6cnwrE5nWWHLr"));
    }

    //검색한 내용의 음식점을 찾기위한 네이버지도api 사용 함수
    public List<Place> placeResponse(String searchThings) {
        buffer = StandardCharsets.UTF_8.encode(searchThings);
        encode = StandardCharsets.UTF_8.decode(buffer).toString();

        uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/local.json")
                .queryParam("query", encode)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "random")
                .encode()
                .build()
                .toUri();

        return parsingPlace(getResponseEntity(uri, "X-Naver-Client-Id", "lrhq41D596jlFArWopIi",
                "X-Naver-Client-Secret", "oKfHT8qCnU"));
    }

    //네이버지도api에서 받은 json을 가공하기 위한 함수
    public List<Place> parsingPlace(ResponseEntity<String> result)
    {
        List<Place> list = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONArray placeItems = (JSONArray) object.get("items");
            for (int i = 0; i < placeItems.size(); i++) {
                object = (JSONObject) placeItems.get(i);
                String placeName = (String) object.get("title");
                String category = (String) object.get("category");
                String address = (String) object.get("roadAddress");
                if (address.equals("")) {
                    address = (String) object.get("address");
                }

                double[] mapXY = geoResponse(address);
                list.add(new Place(placeName, category, address, mapXY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //Geocoding 에서 받은 json을 가공하기 위한 함수
    public double[] parsingGeo(ResponseEntity<String> result)
    {
        double[] mapxy = new double[2];

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONArray placeItems = (JSONArray) object.get("addresses");
            object = (JSONObject) placeItems.get(0);
            mapxy[0] = Double.parseDouble((String) object.get("x"));
            mapxy[1] = Double.parseDouble((String) object.get("y"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapxy;
    }

    //json을 받기 위해서 api에 로그인하는 함수
    private ResponseEntity<String> getResponseEntity(URI uri, String idName, String id, String pwdName, String pwd) {
        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header(idName, id)
                .header(pwdName, pwd)
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);

        return result;
    }

}
