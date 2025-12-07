package com.kostock.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kostock.api.KoreaInvestApiClient;
import com.kostock.model.dao.StockDAO;
import com.kostock.model.dto.StockPriceDTO;

public class IndexPriceService {

    private final KoreaInvestApiClient api = new KoreaInvestApiClient();
    private final StockDAO stockDao = new StockDAO();
    private final DateTimeFormatter fmt = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    /** 오늘 데이터가 없으면 한국투자 API 호출해서 KOSPI,
     *  KOSDAQ 일봉을 DB에 저장. 있으면 아무것도 안 함.*/
    public void refreshTodayIfNeeded() {
        try {
            LocalDate today = LocalDate.now();

            refreshOneIfNeeded("KOSPI", "0001", today);
            refreshOneIfNeeded("KOSDAQ", "1001", today);
        } catch (Exception e) {
            e.printStackTrace();
            // 여기서 예외 터져도 게시판은 뜨게 그냥 로그만 남김
        }
    }

    private void refreshOneIfNeeded(String stockCode, String indexCode, LocalDate today) throws Exception {
        // DB에서 해당 지수의 가장 최근 날짜 조회
        Date latest = stockDao.getLatestPriceDate(stockCode);

        if (latest != null && !latest.toLocalDate().isBefore(today)) {
            // latest >= today --> 오늘자 데이터 이미 있음 → API 호출 안 함
            System.out.println("[" + stockCode + "] 오늘 데이터 이미 있어서 스킵");
            return;
        }
        //  한국투자 API에서 지수 일봉 조회
        JsonObject json = api.getIndexDailyCandles(indexCode);
        // api 응답코드 저장용. rtCd = 0 성공
        String rtCd = json.get("rt_cd").getAsString();
        String msg1 = json.get("msg1").getAsString();
        System.out.println("[" + stockCode + "] rt_cd=" + rtCd + ", msg1=" + msg1);
        // 응답코드가 0이 아닐대 오류 처리
        if (!"0".equals(rtCd)) {
            System.out.println("[" + stockCode + "] API 오류, 저장 스킵");
            return;
        }
        // output2 키의 값을 배열로 반환함
        JsonArray arr = json.getAsJsonArray("output2");
        // 데이터가 list로 담겨짐
        List<StockPriceDTO> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
        	//arr의 모든 요소를 "o"로 가져옴
            JsonObject o = arr.get(i).getAsJsonObject();
            // "o"에서 날짜를 문자열로 받고 LocalDate로 바꿔서 sqlDate에 저장함
            String dateStr = o.get("stck_bsop_date").getAsString(); // 20251202
            LocalDate ld = LocalDate.parse(dateStr, fmt);
            Date sqlDate = Date.valueOf(ld);
            
            // bstp_nmix_prpr= 종가 키 를 문자열로 추출해서 실수로 변환
            double close = Double.parseDouble(o.get("bstp_nmix_prpr").getAsString());
            
            //StockPriceDTO dto에 앞의 값을 저장해서 LIST에 추가함
            StockPriceDTO dto = new StockPriceDTO();
            dto.setPriceDate(sqlDate);
            dto.setClosePrice(close);
            list.add(dto);
        }
        // 앞에서 받은 list의 날짜 오름차순 정렬 (선택)
        list.sort((a, b) -> a.getPriceDate().compareTo(b.getPriceDate()));
        stockDao.upsertDailyClosePrices(stockCode, list);
        System.out.println("[" + stockCode + "] " + list.size() + "건 저장/갱신 완료");
    }
}
