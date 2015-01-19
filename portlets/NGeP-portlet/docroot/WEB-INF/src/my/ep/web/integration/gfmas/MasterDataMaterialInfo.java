package my.ep.web.integration.gfmas;

import java.util.Date;

public class MasterDataMaterialInfo {

	private String MaterialCode;
	private String CodeType;
	private String ActionCode;
	private String MaterialDesc;
	private String MaterialAddDesc;

	private Date StartEffectiveDt;
	private Date EndEffectiveDt;
	private String CommodityName;
	private String BaseUnitOfMeasure;
	private String MaterialType;
	private String MaterialGroup;
	private String AltUnitOfMeasure;

	public String getMaterialCode() {
		return MaterialCode;
	}

	public void setMaterialCode(String materialCode) {
		MaterialCode = materialCode;
	}

	public String getCodeType() {
		return CodeType;
	}

	public void setCodeType(String codeType) {
		CodeType = codeType;
	}

	public String getActionCode() {
		return ActionCode;
	}

	public void setActionCode(String actionCode) {
		ActionCode = actionCode;
	}

	public String getMaterialDesc() {
		return MaterialDesc;
	}

	public void setMaterialDesc(String materialDesc) {
		MaterialDesc = materialDesc;
	}

	public String getMaterialAddDesc() {
		return MaterialAddDesc;
	}

	public void setMaterialAddDesc(String materialAddDesc) {
		MaterialAddDesc = materialAddDesc;
	}

	public Date getStartEffectiveDt() {
		return StartEffectiveDt;
	}

	public void setStartEffectiveDt(Date startEffectiveDt) {
		StartEffectiveDt = startEffectiveDt;
	}

	public Date getEndEffectiveDt() {
		return EndEffectiveDt;
	}

	public void setEndEffectiveDt(Date endEffectiveDt) {
		EndEffectiveDt = endEffectiveDt;
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

	public String getMaterialType() {
		return MaterialType;
	}

	public void setMaterialType(String materialType) {
		MaterialType = materialType;
	}

	public String getMaterialGroup() {
		return MaterialGroup;
	}

	public void setMaterialGroup(String materialGroup) {
		MaterialGroup = materialGroup;
	}

	public String getAltUnitOfMeasure() {
		return AltUnitOfMeasure;
	}

	public void setAltUnitOfMeasure(String altUnitOfMeasure) {
		AltUnitOfMeasure = altUnitOfMeasure;
	}

}
