/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.util;

import java.util.Calendar;

import my.ep.constant.EpPropertiesConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.util.EpPropertiesUtils;

/**
 * @author Kieyun
 * @since 13.07.2014
 *
 */
public class DocumentRepositoryUtils {
	
	/**
	 * Get File Upload Path
	 * @param moduleCode
	 * @param docType
	 * @param docNo. For FL, either Contract No or Request Note No
	 * @return String
	 */
	public static String getUploadDirectoryPath(String moduleCode, String docType, String docNo) {
		StringBuilder uploadPath = new StringBuilder();
		uploadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR));
		uploadPath.append("/");
		
		if(moduleCode.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)) {
			uploadPath.append(Calendar.getInstance().get(Calendar.YEAR));
			uploadPath.append("/");
			uploadPath.append(ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase());
			uploadPath.append("/");
			uploadPath.append(docNo);
			uploadPath.append("/");
			uploadPath.append(docType);
			uploadPath.append("/");
		} else if(moduleCode.equalsIgnoreCase(ModuleCodeConst.CONTRACT_MANAGEMENT)) {
			uploadPath.append(ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase());
			uploadPath.append("/");
			if(docType.equalsIgnoreCase(ParameterConst.DOCTYPE_PANEL_CIRCULAR) || docType.equalsIgnoreCase(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR)) {
				uploadPath.append("Circulars");
				uploadPath.append("/");
				uploadPath.append(docType);
				uploadPath.append("/");				
			} else {
				uploadPath.append(docNo);
				uploadPath.append("/");
				uploadPath.append(docType);
				uploadPath.append("/");
			}
		}
		
		return uploadPath.toString();
	}
	
	public static String getDBFilePath(String filePath) {
		String baseDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
		return filePath.replaceFirst(baseDir+"/", "");
	}

	public static String getBaseDir() {
		return EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/";
	}
	
}
