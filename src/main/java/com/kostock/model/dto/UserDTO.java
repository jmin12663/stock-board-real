package com.kostock.model.dto;

import java.util.Date;

public class UserDTO {

    private String userid;
    private String password;
    private String name;
    private String role;
    private Date joinDate;
   
    public UserDTO() {}
    
	public UserDTO(String userid, String password, String name, String role, Date joinDate) {
		super();
		this.userid = userid;
		this.password = password;
		this.name = name;
		this.role = role;
		this.joinDate = joinDate;
	}
	
	public UserDTO(String userid, String password, String name, String role) {
        this.userid = userid;
        this.password = password;
        this.name = name;
        this.role = role;
	}
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
}