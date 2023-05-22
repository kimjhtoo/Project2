package com.tuflex.web.user.payload.response;

import java.util.List;

import com.tuflex.web.user.model.Weather;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherResponse {
    List<Weather> weatherList;
}
