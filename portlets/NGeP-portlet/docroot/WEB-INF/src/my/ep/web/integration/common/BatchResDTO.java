package my.ep.web.integration.common;

import java.io.Serializable;
import java.util.List;

import my.ep.web.integration.gfmas.EPErrorResponse;

public class BatchResDTO<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<T> batchResContent;
	private EPErrorResponse errorResponse;

	public List<T> getBatchResContent() {
		return batchResContent;
	}

	public void setBatchResContent(List<T> batchResContent) {
		this.batchResContent = batchResContent;
	}

	public EPErrorResponse getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(EPErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

}
