package com.tuflex.web.user.controller;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import com.tuflex.web.user.model.ERole;
import com.tuflex.web.user.model.Hotel;
import com.tuflex.web.tool.Utils;;

@Controller
public class HomeController {

    @GetMapping(value = "index")
    public String index(Model model) {
        System.out.println("index");
        ERole role = Utils.getRole();
        System.out.println(role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
                return "index";
            default:
                return "index";
        }
    }

    @GetMapping(value = "login")
    public String loginView() {
        System.out.println("login");
        ERole role = Utils.getRole();
        switch (role) {
            case ROLE_USER:
                return "redirect:/index";
            default:
                return "login";
        }
    }

    @GetMapping(value = "signup")
    public String register() {
        ERole role = Utils.getRole();
        switch (role) {
            case ROLE_USER:
                return "redirect:/index";
            default:
                return "signup";
        }
    }

    @GetMapping(value = "help")
    public String help(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "help";
        }
    }

    @GetMapping(value = "map")
    public String map(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "map";
        }
    }

    @GetMapping(value = "search")
    public String search(Model model, @RequestParam(value = "k", defaultValue = "") String place,
            @RequestParam(value = "s", defaultValue = "") String s,
            @RequestParam(value = "e", defaultValue = "") String e,
            @RequestParam(value = "r", defaultValue = "1") Integer room,
            @RequestParam(value = "a", defaultValue = "0") Integer adult,
            @RequestParam(value = "c", defaultValue = "0") Integer child) throws URISyntaxException, ParseException {
        ERole role = Utils.getRole();
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(s);
        } catch (Exception err) {
            startDate = LocalDate.now();
        }
        try {
            endDate = LocalDate.parse(s);
            endDate = endDate.plusDays(1);
        } catch (Exception err) {
            endDate = LocalDate.now();
        }

        String url = "http://sandbox-affiliateapi.agoda.com/api/v4/property/availability";

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> map = new HashMap<>();
        map.put("waitTime", 60);
        Map<String, Object> criteria = new HashMap<>();
        List<Integer> propertyIds = new ArrayList<>();
        map.put("waitTime", 60);
        propertyIds.add(12157);
        criteria.put("propertyIds", propertyIds);
        criteria.put("checkIn", startDate.toString());
        criteria.put("checkOut", endDate.toString());
        criteria.put("rooms", room);
        criteria.put("adults", adult);
        criteria.put("children", child);
        criteria.put("language", "ko-kr");
        criteria.put("currency", "KRW");
        criteria.put("userCountry", "KR");
        map.put("criteria", criteria);
        Map<String, Object> features = new HashMap<>();
        features.put("ratesPerProperty", 25);
        List<String> extra = new ArrayList<>();
        extra.add("content");
        extra.add("surchargeDetail");
        extra.add("CancellationDetail");
        extra.add("BenefitDetail");
        extra.add("dailyRate");
        extra.add("taxDetail");
        extra.add("rateDetail");
        extra.add("promotionDetail");
        features.put("extra", extra);
        map.put("features", features);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "1818775:D3BECA16-7CB7-490F-94E5-11C0A076DCF5");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        System.out.println(response);

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        List<Hotel> hotels = new ArrayList<>();
        try {
            for (int i = 0; i < ((JSONArray) jsonObject.get("properties")).size(); i++) {
                JSONObject property = (JSONObject) ((JSONArray) jsonObject.get("properties")).get(i);
                long propertyId = (Long) property.get("propertyId");
                String name = (String) property.get("propertyName");
                int price = (int) ((Double) ((JSONObject) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                        .get("totalPayment")).get("exclusive")).doubleValue();

                int aminityCount = ((JSONArray) ((JSONObject) ((JSONArray) property.get("rooms")).get(0))
                        .get("benefits"))
                        .size();

                hotels.add(new Hotel(name, "메무 아톨, 몰디브 아일랜드 - 도심까지 142.1km",
                        "https://pix8.agoda.net/hotelImages/121/12157/12157_15060814420028880006.jpg?ca=4&ce=1&s=312x",
                        "우수", propertyId, price, 4, aminityCount, 529, 8.3));

                hotels.add(new Hotel(name, "메무 아톨, 몰디브 아일랜드 - 도심까지 142.1km",
                        "https://pix8.agoda.net/hotelImages/121/12157/12157_15060814420028880006.jpg?ca=4&ce=1&s=312x",
                        "우수", propertyId, price, 4, aminityCount, 529, 8.3));
            }
        } catch (Exception ex) {

        }
        model.addAttribute("hotels", hotels);
        model.addAttribute("place", place);
        model.addAttribute("startDate", startDate.toString());
        model.addAttribute("endDate", endDate.toString());
        model.addAttribute("room", room);
        model.addAttribute("adult", adult);
        model.addAttribute("child", child);
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "search";
        }
    }

    @GetMapping(value = "/hotel-detail/{pid}")
    public String detail(Model model, @PathVariable("pid") Long pid,
            @RequestParam(value = "s", defaultValue = "") String s,
            @RequestParam(value = "e", defaultValue = "") String e,
            @RequestParam(value = "r", defaultValue = "1") Integer room,
            @RequestParam(value = "a", defaultValue = "0") Integer adult,
            @RequestParam(value = "c", defaultValue = "0") Integer child) {
        ERole role = Utils.getRole();
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(s);
        } catch (Exception err) {
            startDate = LocalDate.now();
        }
        try {
            endDate = LocalDate.parse(s);
            endDate = endDate.plusDays(1);
        } catch (Exception err) {
            endDate = LocalDate.now();
        }
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        model.addAttribute("startDate", startDate.toString());
        model.addAttribute("endDate", endDate.toString());
        model.addAttribute("room", room);
        model.addAttribute("adult", adult);
        model.addAttribute("child", child);
        switch (role) {
            case ROLE_USER:
            default:
                return "detail";
        }
    }

    @GetMapping(value = "/cart")
    public String cart(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "cart";
        }
    }

    @GetMapping(value = "/reserve")
    public String reserve(Model model) {
        ERole role = Utils.getRole();
        model.addAttribute("name", role.equals(ERole.ROLE_USER) ? Utils.getName() : "");
        switch (role) {
            case ROLE_USER:
            default:
                return "reserve";
        }
    }

    @GetMapping(value = "/success")
    public String success(@RequestParam("paymentKey") String paymentKey, @RequestParam("amount") Long amount,
            @RequestParam("orderId") String orderId) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String uri = "https://api.tosspayments.com/v1/payments/confirm";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic dGVzdF9za19PNkJZcTdHV1BWdjJxMXdleE1tVk5FNXZibzFkOg==");
            headers.add("Content-Type", "application/json");

            Map<String, Object> map = new HashMap<>();
            map.put("amount", amount);
            map.put("orderId", orderId);
            map.put("paymentKey", paymentKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

            String result = restTemplate.postForObject(uri, entity, String.class);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
        } catch (Exception e) {
            return "redirect:/index";
        }
        return "success";
    }

    @GetMapping(value = "/failed")
    public String failed() {
        return "failed";
    }
}