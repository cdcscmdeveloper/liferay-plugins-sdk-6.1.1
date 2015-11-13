package my.ep.web.integration.common;

import java.util.Date;

/*
 * @author P Ganga prasad
 */
public abstract class EperundingTemplate extends InitializeSO {

	protected OsbUtility osbUtility = new OsbUtility();
	protected FileUtility fileUtility = new FileUtility();
	protected String path = fileUtility.createDirectory(new Date(),
			IntegrationConstants.EPERUNDING_UPLOAD_DIR_LOCAL);
	protected Date currentDate = new Date();

	public abstract void createConsultantProfile();
	
	public abstract void createDeletedCompany();

	public abstract void createGovernmentProject();

	public abstract void createRegisteredCategory();

	public abstract void createBranchCompany();

	public abstract void createCompanyOwner();

	public abstract void createAssociatedCompany();
	
	public abstract void createFilesMgtEqtBODforEperunding();

	public abstract void createAuthorizedSignatory(); 

}