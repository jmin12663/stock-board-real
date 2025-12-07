package com.kostock.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kostock.config.AppConfig;

public class KoreaInvestApiClient {

    private final Gson gson = new Gson();

    private String accessToken;
    private long tokenExpiresAt = 0L;  // 만료 시간

    // 공통 유틸: HTTP 응답 읽기
    private String readAll(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    // 액세스 토큰 발급 (공통)
    public String getAccessToken() throws IOException {
        long now = System.currentTimeMillis();
        if (accessToken != null && now < tokenExpiresAt) {
            // 아직 유효하면 재사용
            return accessToken;
        }

        String baseUrl   = AppConfig.get("kis.baseUrl");
        String appKey    = AppConfig.get("kis.appKey");
        String appSecret = AppConfig.get("kis.appSecret");

        // 개인용: /oauth2/tokenP
        URL url = new URL(baseUrl + "/oauth2/tokenP");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("content-type", "application/json; charset=UTF-8");

        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("grant_type", "client_credentials");
        bodyJson.addProperty("appkey", appKey);
        bodyJson.addProperty("appsecret", appSecret);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(bodyJson.toString().getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        String resBody = readAll(code >= 200 && code < 300 ?
                conn.getInputStream() : conn.getErrorStream());

        if (code < 200 || code >= 300) {
            throw new IOException("토큰 요청 실패: HTTP " + code + " / " + resBody);
        }

        JsonObject json = gson.fromJson(resBody, JsonObject.class);
        String token    = json.get("access_token").getAsString();
        int expiresIn   = json.get("expires_in").getAsInt(); // 초 단위

        this.accessToken   = token;
        this.tokenExpiresAt = now + (long) (expiresIn - 60) * 1000L; // 1분 여유

        System.out.println("[DEBUG] access_token = " + token);
        return token;
    }


    // 지수 일봉 조회 (코스피/코스닥 등)
    //  indexCode : "0001"(코스피), "1001"(코스닥) 
    public JsonObject getIndexDailyCandles(String indexCode) throws IOException {
        // api 값 지정
    	String baseUrl = AppConfig.get("kis.baseUrl");
        String path    = AppConfig.get("kis.indexDailyPath");  // /uapi/.../inquire-daily-indexchartprice
        String trId    = AppConfig.get("kis.indexDailyTrId");  // FHKUP03500100

        // 최근 30일 범위 (원하면 숫자 조정 가능)
        LocalDate end   = LocalDate.now();
        LocalDate start = end.minusDays(30);
        //api yyyyMMdd 바꿔야함
        String startDate = start.format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        String endDate   = end.format(DateTimeFormatter.BASIC_ISO_DATE);

        // 지수용 데이터 조회: FID_COND_MRKT_DIV_CODE = U
        String query =
                "FID_COND_MRKT_DIV_CODE=" + URLEncoder.encode("U", "UTF-8") +
                "&FID_INPUT_ISCD="        + URLEncoder.encode(indexCode, "UTF-8") +
                "&FID_INPUT_DATE_1="      + URLEncoder.encode(startDate, "UTF-8") +
                "&FID_INPUT_DATE_2="      + URLEncoder.encode(endDate, "UTF-8") +
                "&FID_PERIOD_DIV_CODE="   + URLEncoder.encode("D", "UTF-8") +
                "&FID_ORG_ADJ_PRC="       + URLEncoder.encode("0", "UTF-8");
        //======api 요철 URL 생성을 서버로 연결
        URL url = new URL(baseUrl + path + "?" + query);
        System.out.println("[DEBUG] index daily URL = " + url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        //헤더생성
        conn.setRequestProperty("content-type", "application/json; charset=UTF-8");
        conn.setRequestProperty("authorization", "Bearer " + getAccessToken());
        conn.setRequestProperty("appkey", AppConfig.get("kis.appKey"));
        conn.setRequestProperty("appsecret", AppConfig.get("kis.appSecret"));
        conn.setRequestProperty("tr_id", trId);
        conn.setRequestProperty("custtype", "P"); // 개인
        // 응답 수신및 오류 처리
        int codeHttp = conn.getResponseCode();
        String resBody = readAll(codeHttp >= 200 && codeHttp < 300 ?
                conn.getInputStream() : conn.getErrorStream());

        System.out.println("=== 지수 일봉 원본 JSON ===");
        System.out.println(resBody);

        if (codeHttp < 200 || codeHttp >= 300) {
            throw new IOException("지수 일봉 조회 실패: HTTP " + codeHttp + " / " + resBody);
        }
        // 성공하면 json 문자열로 변환하고 반환함
        return gson.fromJson(resBody, JsonObject.class);
    }
    

}
