package com.ydles.user.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @Created by IT李老师
 * 公主号 “IT李哥交朋友”
 * 个人微 itlils
 */
@Table(name="tb_areas")
public class Areas implements Serializable {

	@Id
	private String areaid;//区域ID


	
	private String area;//区域名称
	private String cityid;//城市ID

	
	public String getAreaid() {
		return areaid;
	}
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}

	public String getCityid() {
		return cityid;
	}
	public void setCityid(String cityid) {
		this.cityid = cityid;
	}



}
