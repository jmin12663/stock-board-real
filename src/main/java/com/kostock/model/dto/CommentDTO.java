package com.kostock.model.dto;

import java.util.Date;

public class CommentDTO {

    private int commentId;
    private int postId;
    private String userid;
    private String content;
    private Date createdAt;

    public CommentDTO() {}

    public CommentDTO(int postId, String userid, String content) {
        this.postId = postId;
        this.userid = userid;
        this.content = content;
    }

    public int getCommentId() { return commentId; }
    public void setCommentId(int commentId) { this.commentId = commentId; }

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
