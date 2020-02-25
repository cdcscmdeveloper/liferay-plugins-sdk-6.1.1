package my.ep.web.backingbean.sc.specification;

import java.math.BigDecimal;

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
	
	//Added By Haidah for CR: #25633
	private String freqPerUom;
	private String uomPerDay;
	private String uomPerMonth;
	private String scoreType;
	private String rangeFromScore;
	private String rangeToScore;
	private String yesScore;
	private String noScore;
	private String score;
	private String tierQuantityFrom;
	private String tierQuantityTo;
	private String specWeightage;

	public SpecInfo(String itemm, String specificationn, String quantityy, String uomm,  String priceTypee, String scoreSetting,
			String scoreTypee, String rangeFromScore, String rangeToScore, String yesScore, String noScore, String score,
			String tierQuantityFrom, String tierQuantityTo, String freqPerUom, String uomPerDay,String uomPerMonth, String specWeightage){
		super();
		this.item = itemm;
		this.specification = specificationn;
		this.uom = uomm;
		this.priceType = priceTypee;
		this.qty=quantityy;
		this.scoreSetting=scoreSetting;
		this.scoreType = scoreTypee;
		this.rangeFromScore = rangeFromScore;
		this.rangeToScore = rangeToScore;
		this.yesScore = yesScore;
		this.noScore = noScore;
		this.score = score;
		this.tierQuantityFrom = tierQuantityFrom;
		this.tierQuantityTo = tierQuantityTo;
		this.freqPerUom = freqPerUom;
		this.uomPerDay = uomPerDay;
		this.uomPerMonth = uomPerMonth;
		this.specWeightage = specWeightage;
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



	public String getScoreType() {
		return scoreType;
	}



	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}



	public String getRangeFromScore() {
		return rangeFromScore;
	}



	public void setRangeFromScore(String rangeFromScore) {
		this.rangeFromScore = rangeFromScore;
	}



	public String getRangeToScore() {
		return rangeToScore;
	}



	public void setRangeToScore(String rangeToScore) {
		this.rangeToScore = rangeToScore;
	}



	public String getYesScore() {
		return yesScore;
	}



	public void setYesScore(String yesScore) {
		this.yesScore = yesScore;
	}



	public String getNoScore() {
		return noScore;
	}



	public void setNoScore(String noScore) {
		this.noScore = noScore;
	}



	public String getScore() {
		return score;
	}



	public void setScore(String score) {
		this.score = score;
	}



	public String getTierQuantityFrom() {
		return tierQuantityFrom;
	}



	public void setTierQuantityFrom(String tierQuantityFrom) {
		this.tierQuantityFrom = tierQuantityFrom;
	}



	public String getTierQuantityTo() {
		return tierQuantityTo;
	}



	public void setTierQuantityTo(String tierQuantityTo) {
		this.tierQuantityTo = tierQuantityTo;
	}



	public String getFreqPerUom() {
		return freqPerUom;
	}

	public void setFreqPerUom(String freqPerUom) {
		this.freqPerUom = freqPerUom;
	}

	public String getUomPerDay() {
		return uomPerDay;
	}

	public void setUomPerDay(String uomPerDay) {
		this.uomPerDay = uomPerDay;
	}

	public String getUomPerMonth() {
		return uomPerMonth;
	}

	public void setUomPerMonth(String uomPerMonth) {
		this.uomPerMonth = uomPerMonth;
	}

	public String getSpecWeightage() {
		return specWeightage;
	}



	public void setSpecWeightage(String specWeightage) {
		this.specWeightage = specWeightage;
	}



	@Override
	public String toString() {
		return "SpecInfo [item=" + item + ", specification=" + specification + ", uom=" + uom + ", priceType="
				+ priceType + ", qty=" + qty + ", scoreSetting=" + scoreSetting + "]";
	}


    
}
