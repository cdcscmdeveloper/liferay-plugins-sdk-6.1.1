package my.ep.web.bean.ct;

import java.io.Serializable;

import my.ep.persistence.entity.ct.CtAttachment;

public class CtLetterOfAcceptance implements Serializable {

	private static final long serialVersionUID = 1L;
	private String contentTypeDesc;
	private CtAttachment loaAttachments;
	
	public String getContentTypeDesc() {
		return contentTypeDesc;
	}
	
	public void setContentTypeDesc(String contentTypeDesc) {
		this.contentTypeDesc = contentTypeDesc;
	}
	
	public CtAttachment getLoaAttachments() {
		return loaAttachments;
	}
	
	public void setLoaAttachments(CtAttachment loaAttachments) {
		this.loaAttachments = loaAttachments;
	}
}
