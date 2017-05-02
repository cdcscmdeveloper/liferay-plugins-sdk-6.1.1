package my.ep.web.vo;

import java.util.List;

import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;

public class CategoryRptVO {
	private String catLvl1;
	private String catLvl2;
	private String catLvl3;

	
	public String getCatLvl1() {
		return catLvl1;
	}

	public void setCatLvl1(String catLvl1) {
		this.catLvl1 = catLvl1;
	}

	public String getCatLvl2() {
		return catLvl2;
	}

	public void setCatLvl2(String catLvl2) {
		this.catLvl2 = catLvl2;
	}

	public String getCatLvl3() {
		return catLvl3;
	}

	public void setCatLvl3(String catLvl3) {
		this.catLvl3 = catLvl3;
	}
}
