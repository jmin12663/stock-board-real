package com.kostock.model.dto;

public class StockPriceDTO {
	private java.sql.Date priceDate;
    private double closePrice;
	
    public java.sql.Date getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(java.sql.Date priceDate) {
		this.priceDate = priceDate;
	}
	public double getClosePrice() {
		return closePrice;
	}
	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}
    
    

}
