package com.kostock.batch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kostock.api.KoreaInvestApiClient;
import com.kostock.model.dao.StockDAO;
import com.kostock.model.dto.StockPriceDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// 수동 테스트용
public class IndexPriceBatchMain {

    public static void main(String[] args) {
        KoreaInvestApiClient api = new KoreaInvestApiClient();
        StockDAO stockDao = new StockDAO();

        try {
            // 1) 코스피 (우리 시스템 코드: KOSPI, 지수 코드: 0001)
            saveIndex("KOSPI", "0001", api, stockDao);

            // 2) 코스닥 (우리 시스템 코드: KOSDAQ, 지수 코드: 1001)
            saveIndex("KOSDAQ", "1001", api, stockDao);

            // 3) 원/달러 환율은 나중에 FX용 API 확인 후 추가

            System.out.println("=== 지수 일봉 배치 완료 ===");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveIndex(String stockCode, String indexCode,
                                  KoreaInvestApiClient api, StockDAO stockDao) throws Exception {

        JsonObject json = api.getIndexDailyCandles(indexCode);

        String rtCd = json.get("rt_cd").getAsString();
        String msg1 = json.get("msg1").getAsString();
        System.out.println("[" + stockCode + "] rt_cd=" + rtCd + ", msg1=" + msg1);

        if (!"0".equals(rtCd)) {
            System.out.println("[" + stockCode + "] API 오류, 저장 스킵");
            return;
        }

        JsonArray arr = json.getAsJsonArray("output2");
        List<StockPriceDTO> list = new ArrayList<>();

        DateTimeFormatter fmt = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

        for (int i = 0; i < arr.size(); i++) {
            JsonObject o = arr.get(i).getAsJsonObject();

            String dateStr = o.get("stck_bsop_date").getAsString();   // 20251202
            LocalDate ld = LocalDate.parse(dateStr, fmt);
            Date sqlDate = Date.valueOf(ld);

            double close = Double.parseDouble(o.get("bstp_nmix_prpr").getAsString());

            StockPriceDTO dto = new StockPriceDTO();
            dto.setPriceDate(sqlDate);
            dto.setClosePrice(close);

            list.add(dto);
        }

        // 날짜 오름차순 정렬 (선택 사항)
        list.sort((a, b) -> a.getPriceDate().compareTo(b.getPriceDate()));

        stockDao.upsertDailyClosePrices(stockCode, list);

        System.out.println("[" + stockCode + "] " + list.size() + "건 저장 완료");
    }
}
