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

    //kCD 는 Ex)대림대학교 와 같은 키워드로 입력했을시 검색이 안되기에
    //keyword API를 한번 거쳐 정확한 주소를 받은 후 재검색 해야한다.
    //Request Ex) key = "대림대학교" tag = "햄버거"
    public List<Place> keyCombineData(String key, String tag) {
        String location = parsingLocation(placeResponse(key));
        String xy[] = parsingLocationXY(locationResponse(location));
        return placeResponse(tag, xy);
    }

    //lDC는 Ex) 안양시 동안구 임곡로 29 와 같은 정확한 주소로 입력을 해야한다.
    //Request Ex) location = "안양시 동안구 임곡로 29" tag = "햄버거"
    public List<Place> locCombineData(String location, String tag) {
        String xy[] = parsingLocationXY(locationResponse(location));
        return placeResponse(tag, xy);
    }

    //kDC에서 키워드를 정확한 주소로 가져오기 위한 함수
    //Transfer Ex) 대림대학교 -> 안양시 동안구 임곡로 29
    //Request > 내 위치 json
    public String parsingLocation(ResponseEntity<String> result)
    {
        String location = "";

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONArray placeItems = (JSONArray) object.get("documents");
            object = (JSONObject) placeItems.get(0);
            location = (String) object.get("road_address_name");
            if (location.equals("")) {
                location = (String) object.get("address_name");
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    //좌표값을 기준으로 가까운 음식점을 띄어주기위해 xy값을 구하는 함수
    //Request > 내 위치 json
    public String[] parsingLocationXY(ResponseEntity<String> result)
    {
        String[] xy = new String[2];
        try {
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(result.getBody());
                JSONArray placeItems = (JSONArray) object.get("documents");
                object = (JSONObject) placeItems.get(0);
                xy[0] = (String) object.get("x");
                xy[1] = (String) object.get("y");
            }

            catch (Exception e) {
            e.printStackTrace();
        }
        return xy;
    }

    //검색된 음식점들을 Custom 객체에 넣어 원하는 값만 추출하는 함수
    //Request > 음식점 가게들 json
    public List<Place> parsingPlace(ResponseEntity<String> result) {
        List<Place> list = new ArrayList<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONArray placeItems = (JSONArray) object.get("documents");

            for (int i = 0; i < placeItems.size(); i++) {
                object = (JSONObject) placeItems.get(i);
                String placeName = (String) object.get("place_name");
                String category = (String) object.get("category_name");
                String distance = (String) object.get("distance");
                String phone = (String) object.get("phone");
                String url = (String) object.get("place_url");
                String address = (String) object.get("address_name");
                String roadAddress = (String) object.get("road_address_name");
                String x = (String) object.get("x");
                String y = (String) object.get("y");

                list.add(new Place(placeName, category, distance, phone, url, address, roadAddress, x, y));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //kDC 전용 주소값을 변환하기위해 Kakao API를 불러오는 함수
    //Request > key = "키워드"
    public ResponseEntity<String> placeResponse(String key) {
        buffer = StandardCharsets.UTF_8.encode(key);
        encode = StandardCharsets.UTF_8.decode(buffer).toString();

        uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/keyword.json")
                .queryParam("query", encode)
                .queryParam("size",15)
                .encode()
                .build()
                .toUri();

        return getResponseEntity(uri, "Authorization", "KakaoAK " + "4f97d3d265a2973723f41e4114c587cb");
    }

    //kDC, lDC 공용 음식점들을 불러오기위해 Kakao API를 불러오는 함수
    //Request > tag = "음식종류" xy = [X값,Y값]
    public List<Place> placeResponse(String tag, String[] xy) {
        buffer = StandardCharsets.UTF_8.encode(tag);
        encode = StandardCharsets.UTF_8.decode(buffer).toString();

        uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/keyword.json")
                .queryParam("query", encode)
                .queryParam("x", xy[0])
                .queryParam("y", xy[1])
                .queryParam("size",15)
                .encode()
                .build()
                .toUri();

        return parsingPlace(getResponseEntity(uri, "Authorization", "KakaoAK " + "4f97d3d265a2973723f41e4114c587cb"));
    }

    //kDC, lDC 공용 중심 좌표 (X,Y) 를 가져오기위해 Kakao API를 불러오는 함수
    public ResponseEntity<String> locationResponse(String location) {
        buffer = StandardCharsets.UTF_8.encode(location);
        encode = StandardCharsets.UTF_8.decode(buffer).toString();

        uri = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com")
                .path("/v2/local/search/address.json")
                .queryParam("query", encode)
                .encode()
                .build()
                .toUri();

        return getResponseEntity(uri, "Authorization", "KakaoAK " + "4f97d3d265a2973723f41e4114c587cb");
    }

    //Kakao API 에 인증하는 함수
    private ResponseEntity<String> getResponseEntity(URI uri, String idName, String id) {
        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header(idName, id)
                .build();

        ResponseEntity<String> result = restTemplate.exchange(req, String.class);

        return result;
    }

}
