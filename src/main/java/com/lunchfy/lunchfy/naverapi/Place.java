package com.lunchfy.lunchfy.naverapi;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class Place {

    private String placeName;
    private String category;
    private String address;
    private double mapX;
    private double mapY;

}
