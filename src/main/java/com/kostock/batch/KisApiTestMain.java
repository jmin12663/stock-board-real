package com.kostock.batch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kostock.api.KoreaInvestApiClient;

public class KisApiTestMain {
    public static void main(String[] args) {
        KoreaInvestApiClient api = new KoreaInvestApiClient();

        try {
            // 코스피 : 0001, 코스닥 : 1001 (지수 코드)
            JsonObject json = api.getIndexDailyCandles("0001");

            String rtCd = json.get("rt_cd").getAsString();
            System.out.println("rt_cd = " + rtCd + ", msg1 = " + json.get("msg1").getAsString());

            if (!"0".equals(rtCd)) {
                System.out.println("API 오류이므로 종료");
                return;
            }

            JsonArray arr = json.getAsJsonArray("output2");
            System.out.println("output2 캔들 개수 = " + arr.size());

            // 앞에 5개만 테스트 출력
            for (int i = 0; i < Math.min(arr.size(), 5); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();

                String date  = o.get("stck_bsop_date").getAsString();      // 날짜
                String close = o.get("bstp_nmix_prpr").getAsString();      // 종가(지수값)
                String open  = o.get("bstp_nmix_oprc").getAsString();      // 시가
                String high  = o.get("bstp_nmix_hgpr").getAsString();      // 고가
                String low   = o.get("bstp_nmix_lwpr").getAsString();      // 저가

                System.out.println(date + " / 종가=" + close +
                                   " / 시가=" + open +
                                   " / 고가=" + high +
                                   " / 저가=" + low);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
