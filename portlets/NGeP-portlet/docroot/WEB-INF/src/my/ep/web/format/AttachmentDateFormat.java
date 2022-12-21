/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.format;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class for the attachment format.
 * 
 * @author kenny
 * @since 04.07.2014
 */
public class AttachmentDateFormat extends AttachmentFormat {
	private SimpleDateFormat sdf;
	
	public AttachmentDateFormat() {
		this("yyMMddHHmmss");
	}
	
	public AttachmentDateFormat(String pattern) {
		sdf = new SimpleDateFormat(pattern);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.type.sc.ScAttachmentFormat#
	 * format(java.lang.String, java.lang.String)
	 */
	@Override
	public String format(String filename, String preFix) {
		StringBuilder formatedFileName = new StringBuilder();
		
		if (preFix != null) {
			formatedFileName.append(preFix);
			formatedFileName.append(FILE_DELIMETER);	
		}
		formatedFileName.append(sdf.format(new Date()));
		formatedFileName.append(FILE_DELIMETER);
		formatedFileName.append(filename);
		
		return formatedFileName.toString();
	}
	
}
