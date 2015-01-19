package my.ep.web.integration.phis;

import java.util.Date;

public class MasterDataMaterialInfo {
	private String ProductCode;
	private String CodeType;
	private String KodTindakan;

	private String OldProductCode;
	private String ItemDescription;
	private Date StartEffectiveDate;
	private Date EndEffectiveDate;
	private String CommodityName;
	private String BaseUnitOfMeasure;
	private String Segment;
	private String Family;
	private String AltUnitOfMeasurement;

	public String getProductCode() {
		return ProductCode;
	}

	public void setProductCode(String productCode) {
		ProductCode = productCode;
	}

	public String getCodeType() {
		return CodeType;
	}

	public void setCodeType(String codeType) {
		CodeType = codeType;
	}

	public String getKodTindakan() {
		return KodTindakan;
	}

	public void setKodTindakan(String kodTindakan) {
		KodTindakan = kodTindakan;
	}

	public String getOldProductCode() {
		return OldProductCode;
	}

	public void setOldProductCode(String oldProductCode) {
		OldProductCode = oldProductCode;
	}

	public String getItemDescription() {
		return ItemDescription;
	}

	public void setItemDescription(String itemDescription) {
		ItemDescription = itemDescription;
	}

	public Date getStartEffectiveDate() {
		return StartEffectiveDate;
	}

	public void setStartEffectiveDate(Date startEffectiveDate) {
		StartEffectiveDate = startEffectiveDate;
	}

	public Date getEndEffectiveDate() {
		return EndEffectiveDate;
	}

	public void setEndEffectiveDate(Date endEffectiveDate) {
		EndEffectiveDate = endEffectiveDate;
	}

	public String getCommodityName() {
		return CommodityName;
	}

	public void setCommodityName(String commodityName) {
		CommodityName = commodityName;
	}

	public String getBaseUnitOfMeasure() {
		return BaseUnitOfMeasure;
	}

	public void setBaseUnitOfMeasure(String baseUnitOfMeasure) {
		BaseUnitOfMeasure = baseUnitOfMeasure;
	}

	public String getSegment() {
		return Segment;
	}

	public void setSegment(String segment) {
		Segment = segment;
	}

	public String getFamily() {
		return Family;
	}

	public void setFamily(String family) {
		Family = family;
	}

	public String getAltUnitOfMeasurement() {
		return AltUnitOfMeasurement;
	}

	public void setAltUnitOfMeasurement(String altUnitOfMeasurement) {
		AltUnitOfMeasurement = altUnitOfMeasurement;
	}

}
