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

    private ByteBuffer buffer;
    private String encode;
    private URI uri;
    
    //Google Custom API를 사용하여 가게 이름을 기준으로 검색된 결과 json을 반환하는 함수
    public String[] imgUrlResponse(String location) {

        buffer = StandardCharsets.UTF_8.encode(location);
        encode = StandardCharsets.UTF_8.decode(buffer).toString();

        uri = UriComponentsBuilder
                .fromUriString("https://customsearch.googleapis.com")
                .path("/customsearch/v1")
                .queryParam("cx", "d499d085af3454d83")
                .queryParam("fileType", "jpg")
                .queryParam("num", "5")
                .queryParam("q", encode)
                .queryParam("searchType", "image")
                .queryParam("key", "AIzaSyDwdiKDGXT5z7ZPa0wE0ILWpxKwdEjqWJU")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);
        return parsingImage(result);
    }

    //Geocoding API를 사용하여 가게이름을 넣으면 좌표를 포함한 정보들을 json으로 반환하는 함수
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
                String configPN = placeName;
                configPN = configPN.replaceAll("<b>","");
                configPN = configPN.replaceAll("</b>"," ");
                String[] imgUrls = imgUrlResponse(configPN);
                list.add(new Place(configPN, category, address, mapXY, imgUrls));
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

            mapxy[1] = Double.parseDouble((String) object.get("x"));
            mapxy[0] = Double.parseDouble((String) object.get("y"));
            //원래 순서상 x(0) y(1)이 들어가는것이 맞지만 네이버 Map API 특성상 반대로 넣어야함

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapxy;
    }

    //Google Custom API에서 받은 json을 가공하기 위한 함수
    public String[] parsingImage(ResponseEntity<String> result)
    {
        String[] imageUrls = new String[5];

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONArray placeItems = (JSONArray) object.get("items");

            for (int i = 0; i < placeItems.size(); i++) {
                object = (JSONObject) placeItems.get(i);
                imageUrls[i] = (String) object.get("link");
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        return imageUrls;
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
