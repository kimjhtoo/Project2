package com.tuflex.web.user.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {
    private String name, email, passwd, snsType, snsId;
}
