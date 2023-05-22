package com.tuflex.web.user.controller;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.tuflex.web.user.model.ERole;
import com.tuflex.web.user.model.Role;
import com.tuflex.web.user.model.User;
import com.tuflex.web.user.model.Weather;
import com.tuflex.web.user.payload.request.UserRegisterRequest;
import com.tuflex.web.user.payload.response.WeatherResponse;
import com.tuflex.web.user.repository.RoleRepository;
import com.tuflex.web.user.repository.UserRepository;
import com.google.common.base.Predicate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/common")
public class PublicRestController {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SessionRegistry sessionRegistry;

    @Autowired
    PasswordEncoder encoder;

    // @GetMapping("withdrawal")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<?> withdrawal(Authentication authentication,
    // @RequestParam Long pid) {
    // Long myPid = Utils.getPid();
    // if (pid == myPid) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인을 탈퇴시킬 수
    // 없습니다.");
    // }
    // blockUserService.blockUser(adminRepository.findById(pid).get().getPhone());
    // adminRepository.withdrawal(pid);
    // return ResponseEntity.ok().build();
    // }

    @RequestMapping(value = "/weather.do", method = RequestMethod.GET)
    public ResponseEntity<?> weather() throws Exception {
        int[] stnIds = { 108, 109, 159, 143, 156, 146, 133, 131, 105, 184 };
        String[] locations = { "전국", "서울,인천,경기도", "부산,울산,경상남도", "대구,경상북도", "광주,전라남도", "전라북도", "대전,세종,충청남도", "충청북도",
                "강원도", "제주도" };
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        String today = LocalDate.now().format(formatter);
        String startDt = LocalDate.now().minusDays(6).format(formatter);
        List<Weather> weathers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String url = "https://apis.data.go.kr/1360000/WthrWrnInfoService/getWthrWrnMsg?serviceKey=DbXJLlCK1imv6vyqZW1msrjxOajHrt%2BivSun2oRsSmzdyKd0MTd0dRv5YOXbBEqAdDSqh0uSfVhYb2zraBG%2B%2BQ%3D%3D&pageNo=1&numOfRows=10&dataType=JSON&stnId="
                    + stnIds[i] + "&fromTmFc=" + startDt + "&toTmFc=" + today;
            System.out.println(url);
            URI uri = new URI(url);
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            JSONParser jsonParser = new JSONParser();
            if (((String) ((JSONObject) ((JSONObject) ((JSONObject) jsonParser.parse(result))
                    .get("response"))
                    .get("header")).get("resultCode")).equals("00")) {
                JSONObject jsonObject = (JSONObject) ((JSONObject) ((JSONObject) ((JSONObject) jsonParser.parse(result))
                        .get("response"))
                        .get("body")).get("items");
                JSONArray jsonList = (JSONArray) jsonObject.get("item");
                String res = (String) ((JSONObject) jsonList.get(0)).get("t3");
                res = res.split("\r\n")[res.split("\r\n").length - 1];

                String datetime = res.split(":")[1].trim();
                String type = res.split(":")[0].substring(4).trim();
                Weather weather = new Weather(datetime, type, locations[i]);
                weathers.add(weather);
            }
        }
        return ResponseEntity.ok().body(new WeatherResponse(weathers));
    }
}
