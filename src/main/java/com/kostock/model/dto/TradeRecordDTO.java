package com.kostock.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public class TradeRecordDTO {

    private int recordId;
    private String userid;
    private String stockCode;
    private String stockName;
    private Date tradeDate;
    private String tradeType; // BUY / SELL
    private int quantity;
    private BigDecimal price;
    private String memo;
    private Date createdAt;

    public TradeRecordDTO() {}
    
    //  삽입용 (recordId/createdAt은 DB)
    public TradeRecordDTO(String userid, String stockCode, String stockName, Date tradeDate,
                          String tradeType, int quantity, BigDecimal price, String memo) {
        this.userid = userid;
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.tradeDate = tradeDate;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.memo = memo;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public Date getTradeDate() { return tradeDate; }
    public void setTradeDate(Date tradeDate) { this.tradeDate = tradeDate; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }


    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
