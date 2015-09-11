package my.ep.web.integration.common;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
 * @author P Ganga prasad
 */
public class BatchReqDTO<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> batchContent;
	private String filePath;
	private Date dataStartTime;
	private Date dataEndTime;
	private String fileName;
	private String serviceCode;
	private String processId;
	private String encryptFilePath;
	private String targetSystem;
	private String remoteFilePath;
	private String locatFilePath;
	private int fileCreationStatus;
	private int fileFTPStatus;
	private String batchDirection;
	private T batchObject;

	public List<T> getBatchContent() {
		return batchContent;
	}

	public void setBatchContent(List<T> batchContent) {
		this.batchContent = batchContent;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Date getDataStartTime() {
		return dataStartTime;
	}

	public void setDataStartTime(Date dataStartTime) {
		this.dataStartTime = dataStartTime;
	}

	public Date getDataEndTime() {
		return dataEndTime;
	}

	public void setDataEndTime(Date dataEndTime) {
		this.dataEndTime = dataEndTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getEncryptFilePath() {
		return encryptFilePath;
	}

	public void setEncryptFilePath(String encryptFilePath) {
		this.encryptFilePath = encryptFilePath;
	}

	public String getTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}

	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

	public String getLocatFilePath() {
		return locatFilePath;
	}

	public void setLocatFilePath(String locatFilePath) {
		this.locatFilePath = locatFilePath;
	}

	public int getFileCreationStatus() {
		return fileCreationStatus;
	}

	public void setFileCreationStatus(int fileCreationStatus) {
		this.fileCreationStatus = fileCreationStatus;
	}

	public int getFileFTPStatus() {
		return fileFTPStatus;
	}

	public void setFileFTPStatus(int fileFTPStatus) {
		this.fileFTPStatus = fileFTPStatus;
	}

	public String getBatchDirection() {
		return batchDirection;
	}

	public void setBatchDirection(String batchDirection) {
		this.batchDirection = batchDirection;
	}

	public T getBatchObject() {
		return batchObject;
	}

	public void setBatchObject(T batchObject) {
		this.batchObject = batchObject;
	}

}
