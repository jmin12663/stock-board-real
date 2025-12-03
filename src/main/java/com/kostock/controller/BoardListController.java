package com.kostock.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.kostock.model.dao.PostDAO;
import com.kostock.model.dao.StockDAO;
import com.kostock.model.dto.IndexSummaryDTO;
import com.kostock.model.dto.PostDTO;
import com.kostock.model.dto.StockPriceDTO;
import com.kostock.service.IndexPriceService;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/board/list.do")
public class BoardListController extends HttpServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	// 지수 데이터 오늘 것 없으면 자동 갱신
        IndexPriceService indexService = new IndexPriceService();
        indexService.refreshTodayIfNeeded();
    	
    	// categoryId
        int categoryId = 1;
        String categoryIdParam = request.getParameter("categoryId");
        if (categoryIdParam != null && !categoryIdParam.isBlank()) {
            categoryId = Integer.parseInt(categoryIdParam);
        }

        // page
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }
        if (page < 1) page = 1;
        
        //searchWord (검색어)
        String searchWord = request.getParameter("searchWord");
        if (searchWord != null) {
            searchWord = searchWord.trim();
            if (searchWord.isEmpty()) searchWord = null;
        }
        //검색 옵션
        String field = request.getParameter("field");
        if (!"title".equals(field) && !"content".equals(field) && !"stock".equals(field)) {
            field = "title";
        }
        
        
        // 보이는 게시글 갯수
        int pageSize = 10;

        PostDAO dao = new PostDAO();

        // 총 글 개수 / 총 페이지 수
        int totalCount;
        int totalPages;
        List<PostDTO> list;

        //  검색어 있으면 검색용 메서드, 없으면 기존 메서드
        if (searchWord != null) {
            totalCount = dao.getPostCountByCategorySearch(categoryId, field, searchWord);
            totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (page > totalPages) page = totalPages;

            list = dao.selectPostListByCategorySearchPaging(categoryId, field, searchWord, page, pageSize);

            request.setAttribute("searchWord", searchWord); // list.jsp에서 유지용
            request.setAttribute("field", field);
        } else {
            totalCount = dao.getPostCountByCategory(categoryId);
            totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (page > totalPages) page = totalPages;

            list = dao.selectPostListByCategoryPaging(categoryId, page, pageSize);
        }
        
        // 지수 요약 리스트
        StockDAO stockDao = new StockDAO();
        List<IndexSummaryDTO> indexList;
        try {
            indexList = stockDao.getIndexSummaries();
        } catch (SQLException e) {
            e.printStackTrace();
            indexList = Collections.emptyList(); // 오류 나도 NPE 안 나게 빈 리스트
        }
        
     // 각 지수별 미니 차트용 일봉 데이터 Map
        Map<String, List<StockPriceDTO>> indexPriceMap = new HashMap<>();

        try {
            for (IndexSummaryDTO idx : indexList) {
                String code = idx.getStockCode();
                // 최근 20일 정도만 가져오기 (원하면 숫자 조정)
                List<StockPriceDTO> prices = stockDao.getDailyPrices(code, 20);
                indexPriceMap.put(code, prices);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // 5) JSP로 전달
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("postList", list);

        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);

        request.setAttribute("indexList", indexList);
        request.setAttribute("indexPriceMap", indexPriceMap);
        
        request.getRequestDispatcher("/board/list.jsp").forward(request, response);
    }
}
