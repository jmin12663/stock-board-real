package com.kostock.model.dto;

public class IndexSummaryDTO {
	private String stockCode;
    private String stockName;
    private double closePrice;    // 오늘 종가
    private Double prevClosePrice; // 전일 종가
    private Double diff;          // 오늘 - 전일
    private Double diffRate;      // (diff / 전일) * 100
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}
	public Double getPrevClosePrice() {
		return prevClosePrice;
	}
	public void setPrevClosePrice(Double prevClosePrice) {
		this.prevClosePrice = prevClosePrice;
	}
	public Double getDiff() {
		return diff;
	}
	public void setDiff(Double diff) {
		this.diff = diff;
	}
	public Double getDiffRate() {
		return diffRate;
	}
	public void setDiffRate(Double diffRate) {
		this.diffRate = diffRate;
	}
    

}
