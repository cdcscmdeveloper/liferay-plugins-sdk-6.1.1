package my.ep.web.integration.common;

import java.util.Date;

import my.ep.web.integration.common.BatchTemplate;
import my.ep.web.integration.common.Encryption;
import my.ep.web.integration.common.FileUtility;
import my.ep.web.integration.common.OsbUtility;
import my.ep.web.integration.common.SFTPUtility;

/*
 * @author P Ganga prasad
 */

public abstract class InitializeUtilityClz extends BatchTemplate {

	protected FileUtility fileUtility = new FileUtility();
	protected SFTPUtility sftpUtility = null;// new SFTPUtility();
	protected OsbUtility osbUtility = new OsbUtility();
	protected Encryption encryption = new Encryption();
	protected Date currentDate = new Date();

	public InitializeUtilityClz() {
		super();
	}

}