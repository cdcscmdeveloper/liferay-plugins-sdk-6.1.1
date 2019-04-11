package my.ep.web.vo;

import java.io.Serializable;

public class CategoryRptVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long catLvl1;
	private Long catLvl2;
	private Long catLvl3;
	
	public Long getCatLvl1() {
		return catLvl1;
	}
	
	public void setCatLvl1(Long catLvl1) {
		this.catLvl1 = catLvl1;
	}
	
	public Long getCatLvl2() {
		return catLvl2;
	}
	
	public void setCatLvl2(Long catLvl2) {
		this.catLvl2 = catLvl2;
	}
	
	public Long getCatLvl3() {
		return catLvl3;
	}
	
	public void setCatLvl3(Long catLvl3) {
		this.catLvl3 = catLvl3;
	}
}
