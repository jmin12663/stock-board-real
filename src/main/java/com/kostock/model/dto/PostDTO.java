package com.kostock.model.dto;

import java.util.Date;

public class PostDTO {

    private int postId;
    private String userid;
    private int categoryId;
    private String stockCode;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private int viewCount;
    private int listNo;

    public PostDTO() {}

    // 글 작성용 생성자
    public PostDTO(String userid, int categoryId, String stockCode,
                   String title, String content) {
        this.userid = userid;
        this.categoryId = categoryId;
        this.stockCode = stockCode;
        this.title = title;
        this.content = content;
    }

    // getter / setter
    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    
    public int getListNo() { return listNo; }
    public void setListNo(int listNo) { this.listNo = listNo; }
}
