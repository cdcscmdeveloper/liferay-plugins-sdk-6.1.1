package my.ep.web.vo;

import java.io.Serializable;
import java.util.List;

import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertDoc;
import my.ep.persistence.entity.sm.SmSoftcertDocFile;

public class SoftcertUploadSuppDocVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8081345015620289053L;
	private SmPersonnel personnel;
	private List<SmSoftcertDoc> suppDocList;
	private SmSoftcertDoc suppDoc;
	private SmSoftcertDocFile suppDocFile;

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

	public SmSoftcertDoc getSuppDoc() {
		return suppDoc;
	}

	public void setSuppDoc(SmSoftcertDoc suppDoc) {
		this.suppDoc = suppDoc;
	}

	public SmSoftcertDocFile getSuppDocFile() {
		return suppDocFile;
	}

	public void setSuppDocFile(SmSoftcertDocFile suppDocFile) {
		this.suppDocFile = suppDocFile;
	}
}
