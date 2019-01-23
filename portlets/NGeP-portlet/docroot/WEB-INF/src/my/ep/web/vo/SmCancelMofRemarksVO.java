package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmAttachment;

public class SmCancelMofRemarksVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5991005051577525876L;

	private String reason;
	private SmAttachment attch;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public SmAttachment getAttch() {
		return attch;
	}

	public void setAttch(SmAttachment attch) {
		this.attch = attch;
	}

}
