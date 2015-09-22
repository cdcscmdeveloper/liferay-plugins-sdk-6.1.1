package my.ep.web.integration.common;

import java.util.Date;

public abstract class InitializeUtilityClz extends BatchTemplate {

	protected FileUtility fileUtility = new FileUtility();
	protected SFTPUtility sftpUtility = new SFTPUtility();
	protected OsbUtility osbUtility = new OsbUtility();
	protected Encryption encryption = new Encryption();
	protected Date currentDate = new Date();

	public InitializeUtilityClz() {
		super();
	}

}