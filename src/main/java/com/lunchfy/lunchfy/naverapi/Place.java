package com.lunchfy.lunchfy.naverapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public class Place {

    private String placeName;
    private String category;
    private String distance;
    private String phone;
    private String url;
    private String address;
    private String roadAddress;
    private String x;
    private String y;

}
