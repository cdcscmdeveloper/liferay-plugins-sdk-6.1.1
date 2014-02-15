package my.ep.web.bean.ct;

import java.io.Serializable;


public class AgreementDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
     private String filePath;
     private String fileName;
     private String fileType;
     
	 public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
     	
}
