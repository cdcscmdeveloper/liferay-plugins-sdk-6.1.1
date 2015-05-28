package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertDoc;

public class SoftcertUploadSuppDocVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8081345015620289053L;
	private SmPersonnel personnel;
	private List<SmSoftcertDoc> suppDocList;

	public SmPersonnel getPersonnel() {
		return personnel;
	}

	public void setPersonnel(SmPersonnel personnel) {
		this.personnel = personnel;
	}

	public List<SmSoftcertDoc> getSuppDocList() {
		return suppDocList;
	}

	public void setSuppDocList(List<SmSoftcertDoc> suppDocList) {
		this.suppDocList = suppDocList;
	}
}
