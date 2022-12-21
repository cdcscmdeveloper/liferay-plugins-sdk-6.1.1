/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.format;



/**
 * The class for the attachment format.
 * 
 * @author kenny
 * @since 04.07.2014
 */
public class AttachmentDefaultFormat extends AttachmentFormat {
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
		formatedFileName.append(filename);
		
		return formatedFileName.toString();
	}
	
}
