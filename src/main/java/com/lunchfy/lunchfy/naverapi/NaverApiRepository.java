package com.lunchfy.lunchfy.naverapi;

import org.springframework.http.RequestEntity;

import java.util.Arrays;

public interface NaverApiRepository {

    Arrays search (RequestEntity<String> result);

}
