package my.ep.web.vo;

import java.io.Serializable;

public class SpkiVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6100649175683372366L;
	private String errMsg;
	private String statusCode;
	private String statusMsg;
	private byte[] signedData;

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public byte[] getSignedData() {
		return signedData;
	}

	public void setSignedData(byte[] signedData) {
		this.signedData = signedData;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}
}
