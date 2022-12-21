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
public abstract class AttachmentFormat {
	protected static final String FILE_DELIMETER = "_";
	
	public abstract String format(String filename, String preFix);
}
