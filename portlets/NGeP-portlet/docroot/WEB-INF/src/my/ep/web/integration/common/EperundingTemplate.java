package my.ep.web.integration.common;

import java.util.Date;

import my.ep.dto.sm.search.SupplierSearchResDTO;

/*
 * @author P Ganga prasad
 */
public abstract class EperundingTemplate extends InitializeSO {

	protected OsbUtility osbUtility = new OsbUtility();
	protected FileUtility fileUtility = new FileUtility();
	protected String path = fileUtility.createDirectory(new Date(),
			IntegrationConstants.EPERUNDING_UPLOAD_DIR_LOCAL);
	protected Date currentDate = new Date();

	public abstract void createEquiytyOwner(SupplierSearchResDTO resDTO);

	public abstract void createBoardOfDirector(SupplierSearchResDTO resDTO);

	public abstract void createManagementPersonnel(SupplierSearchResDTO resDTO);

	public abstract void createAuthorizedSignatory(SupplierSearchResDTO resDTO);

	public abstract void createConsultantProfile(SupplierSearchResDTO resDTO);

	public abstract void createDeletedCompany(SupplierSearchResDTO resDTO);

	public abstract void createGovernmentProject(SupplierSearchResDTO resDTO);

	public abstract void createRegisteredCategory(SupplierSearchResDTO resDTO);

	public abstract void createBranchCompany(SupplierSearchResDTO resDTO);

	public abstract void createCompanyOwner(SupplierSearchResDTO resDTO);

	public abstract void createAssociatedCompany(SupplierSearchResDTO resDTO);

}