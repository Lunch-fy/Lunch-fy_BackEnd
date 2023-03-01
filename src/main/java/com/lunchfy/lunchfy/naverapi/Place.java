package com.lunchfy.lunchfy.naverapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public class Place {

    private String placeName;
    private String category;
    private String address;
    private double[] mapXY;
    private String[] imageUrl;

}
