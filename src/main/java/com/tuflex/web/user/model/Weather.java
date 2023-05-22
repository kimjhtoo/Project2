package com.tuflex.web.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Weather {
    String datetime, type, location;
}
