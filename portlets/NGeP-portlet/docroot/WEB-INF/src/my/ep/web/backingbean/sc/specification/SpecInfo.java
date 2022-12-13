package my.ep.web.backingbean.sc.specification;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="dtSpecView")
@ViewScoped
public class SpecInfo {

	public SpecInfo() {
		super();
	}

	private String item;
	private String specification;
	private String uom;
	private String priceType;
	private String qty;
	private String scoreSetting;
	

	public SpecInfo(String itemm, String specificationn, String quantityy, String uomm,  String priceTypee, String scoree) {
		super();
		this.item = itemm;
		this.specification = specificationn;
		this.uom = uomm;
		this.priceType = priceTypee;
		this.qty=quantityy;
		this.scoreSetting=scoree;
	}



	public String getItem() {
		return item;
	}



	public void setItem(String item) {
		this.item = item;
	}



	public String getSpecification() {
		return specification;
	}



	public void setSpecification(String specification) {
		this.specification = specification;
	}



	public String getUom() {
		return uom;
	}



	public void setUom(String uom) {
		this.uom = uom;
	}



	public String getPriceType() {
		return priceType;
	}



	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}



	public String getQty() {
		return qty;
	}



	public void setQty(String qty) {
		this.qty = qty;
	}



	public String getScoreSetting() {
		return scoreSetting;
	}



	public void setScoreSetting(String scoreSetting) {
		this.scoreSetting = scoreSetting;
	}



	@Override
	public String toString() {
		return "SpecInfo [item=" + item + ", specification=" + specification + ", uom=" + uom + ", priceType="
				+ priceType + ", qty=" + qty + ", scoreSetting=" + scoreSetting + "]";
	}


    
}
