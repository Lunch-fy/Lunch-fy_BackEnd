package com.lunchfy.lunchfy.naverapi;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface NaverApiRepository {

    Arrays search (List<Place> list);

}
