package my.ep.web.integration.common;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.lookupservice.EpEjbRemoteBusinessDelegate;
import my.ep.web.integration.eperunding.AssociatedCompany;
import my.ep.web.integration.eperunding.AuthorizedSignatory;
import my.ep.web.integration.eperunding.BoardOfDirector;
import my.ep.web.integration.eperunding.Branch;
import my.ep.web.integration.eperunding.BranchCompany;
import my.ep.web.integration.eperunding.CapitalProportion;
import my.ep.web.integration.eperunding.Category;
import my.ep.web.integration.eperunding.CompanyOwner;
import my.ep.web.integration.eperunding.ConsultantProfile;
import my.ep.web.integration.eperunding.DeletedCompany;
import my.ep.web.integration.eperunding.Director;
import my.ep.web.integration.eperunding.EquityOwner;
import my.ep.web.integration.eperunding.Expertise;
import my.ep.web.integration.eperunding.GovernmentProject;
import my.ep.web.integration.eperunding.ManagementPersonnel;
import my.ep.web.integration.eperunding.Owner;
import my.ep.web.integration.eperunding.Personnel;
import my.ep.web.integration.eperunding.ProfBodyDetail;
import my.ep.web.integration.eperunding.Project;
import my.ep.web.integration.eperunding.Qualification;
import my.ep.web.integration.eperunding.RegisteredCategory;
import my.ep.web.integration.eperunding.RegistrationHistory;
import my.ep.web.integration.eperunding.Signatory;

/*
 *  @author Ganga prasad
 */
public class EperundingIntegration {
	
	OsbUtility osbUtility = new OsbUtility();
	FileUtility fileUtility = new FileUtility();
	String path = fileUtility.createDirectory(new Date(),IntegrationConstants.FILE_PATH);
	
	IParameterSO parameterSO = EpEjbRemoteBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	
	public static void main(String args[]){
		
		EperundingIntegration eperundingIntegration = new EperundingIntegration();
		eperundingIntegration.createFilesforEperunding();
		
	}
	
	
	public void createFilesforEperunding(){
		EperundingIntegration integration = new EperundingIntegration();
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate("17-07-2013","3","");
//		
		if(resDTO.isSuccessful()){
//			integration.createAuthorizedSignatory(resDTO);
//			integration.createEquiytyOwner(resDTO);
//			integration.createBoardOfDirector(resDTO);
//			integration.createManagementPersonnel(resDTO);
	    }
			
			integration.createConsultantProfile(retriveSmSupplierBycreationDate("22-08-2013","6",""));   
//			integration.createDeletedCompany(retriveSmSupplierBycreationDate("09-05-2012","1",""));
//			integration.createGovernmentProject(retriveSmSupplierBycreationDate("02-10-2010","4",""));
//			integration.createRegisteredCategory(retriveSmSupplierBycreationDate("17-07-2013","2",""));
//			
//			integration.createCompanyOwner(retriveSmSupplierBycreationDate("27-06-2013","5","1"));
//			integration.createAssociatedCompany(retriveSmSupplierBycreationDate("23-05-2012","5","3"));
//			integration.createBranchCompany(retriveSmSupplierBycreationDate("17-07-2013","5","2"));
//		
//		
	}
	
	public void createEquiytyOwner(SupplierSearchResDTO resDTO) {
		
		List<EquityOwner> equityOwnerList = new ArrayList<EquityOwner>();
		EquityOwner equityOwner = null;
		ProfBodyDetail profBodyDetail = null;
		Qualification qualification = null;
		Owner owner = null;
		Expertise expertise = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (smSupplier.getSmMofAccounts().get(0).getMofNo().equals("465-00002043")) {
					if (null != smSupplier.getSmAppl()) {
						if (null != smSupplier.getSmAppl().getSmPersonnels()) {
							for (SmPersonnel personnel : smSupplier.getSmAppl().getSmPersonnels()) {

								equityOwner = new EquityOwner();
								profBodyDetail = new ProfBodyDetail();
								qualification = new Qualification();
								expertise = new Expertise();

								if (personnel.isEquityOwner()) {

									equityOwner.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());

									owner = new Owner();
									owner.setIsEquityOwner(IntegrationConstants.VAVLUE_YES);
									owner.setName(personnel.getName());
									owner.setNewICNo(personnel.getIdentificationNo()); // need to confirm
									owner.setPassport(IntegrationConstants.VAVLUE_NA); // not known
									if (personnel.isBumi())
										owner.setIsBumi(IntegrationConstants.VAVLUE_YES);
									else
										owner.setIsBumi(IntegrationConstants.VAVLUE_NO);
									owner.setCitizenOf(131);
//									owner.setCitizenOf(personnel.getPmCountry().getCountryId().intValue());
									owner.setEquityValue(personnel.getShareAmt().toString());
									owner.setEquityPercentage(personnel.getSharePercentage().toString());
									owner.setRace(personnel.getPmRace().getRaceName());
									owner.setDesignation(personnel.getDesignation());
									equityOwner.setOwner(owner);

									if (smSupplier.getSmAppl()
											.getSmAssociationBodies().size() != 0) {
										for (SmAssociationBody associationBody : smSupplier.getSmAppl().getSmAssociationBodies()) {
											profBodyDetail.setProfBody(associationBody.getPmCertBody().getCertBodyId().toString());
											profBodyDetail.setProfBodyNo(associationBody.getRegNo());
											profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
											equityOwner.setProfBodyDetail(profBodyDetail);
											break;
										}
									} else {
										profBodyDetail.setProfBody(IntegrationConstants.EMPTY_TRING);
										profBodyDetail.setProfBodyNo(IntegrationConstants.EMPTY_TRING);
										profBodyDetail.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
										equityOwner.setProfBodyDetail(profBodyDetail);

									}

									if (personnel.getSmStaffAcademics().size() != 0) {
										for (SmStaffAcademic smStaffAcademic : personnel.getSmStaffAcademics()) {
											qualification.setMajorIn(smStaffAcademic.getFieldMajor());
											qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());
											

											ParameterReqDTO reqDTO = new ParameterReqDTO();
											reqDTO.setParameterId(new Integer(smStaffAcademic.getQualificationLevelId()).longValue());
											ParameterResDTO paraResDTO = parameterSO.retrieveParameterById(reqDTO);
											PmParameter pmParameter = paraResDTO.getPmParameter();
											
											qualification.setQualificationLevel(new Integer(pmParameter.getParameterCode()).intValue());   //pm_parameter with QL
//											qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());   
											qualification.setInstitute(smStaffAcademic.getInstituteName());
											qualification.setYear(smStaffAcademic.getYear().toString());
											qualification.setEducationField(smStaffAcademic.getPmEduField().getEduFieldId().toString());
											equityOwner.setQualification(qualification);
											break;
										}
									} else {
										qualification.setMajorIn(IntegrationConstants.EMPTY_TRING);
										qualification.setQualification(IntegrationConstants.EMPTY_TRING);
										qualification.setQualificationLevel(IntegrationConstants.DEFAULT_INT);
										qualification.setInstitute(IntegrationConstants.EMPTY_TRING);
										qualification.setYear(IntegrationConstants.EMPTY_TRING);
										qualification.setEducationField(IntegrationConstants.EMPTY_TRING);
										equityOwner.setQualification(qualification);
									}

									expertise.setCategoryClassification(IntegrationConstants.VAVLUE_NA);
									equityOwner.setExpertise(expertise);
									if (!equityOwnerList.contains(equityOwner))
										equityOwnerList.add(equityOwner);

								}

							}
						}
					}
				}
		}
		
		fileUtility.equityOwnerOutPutStream(equityOwnerList, path);
		
	}
	
	public void createBoardOfDirector(SupplierSearchResDTO resDTO) {
		
		List<BoardOfDirector> boardOfDirectorsList = new ArrayList<BoardOfDirector>();
		BoardOfDirector boardOfDirector = null;
		Director director = null;
		ProfBodyDetail profBodyDetail = null;
		Qualification qualification = null;
		Expertise expertise = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			if (smSupplier.getSmMofAccounts().get(0).getMofNo().equals("465-00002043")) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
						
						for (SmPersonnel personnel : smSupplier.getSmAppl().getSmPersonnels()) {
							if (personnel.isDirector()) {
								boardOfDirector = new BoardOfDirector();
								profBodyDetail = new ProfBodyDetail();
								director = new Director();
								qualification = new Qualification();
								expertise = new Expertise();
								
								boardOfDirector.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
								
								//if(personnel.isDirector())
									director.setIsBOD(IntegrationConstants.VAVLUE_YES);
//								else
//									director.setIsBOD(IntegrationConstants.VAVLUE_NO);
								director.setIsCEO(IntegrationConstants.VAVLUE_NO);
								director.setName(personnel.getName());
								director.setNewICNo(personnel.getIdentificationNo());
								director.setPassport(IntegrationConstants.VAVLUE_NA);
								if(personnel.isBumi())
									director.setIsBumi(IntegrationConstants.VAVLUE_YES);
								else
									director.setIsBumi(IntegrationConstants.VAVLUE_NO);
								director.setCitizenOf(131);
//								owner.setCitizenOf(personnel.getPmCountry().getCountryId().intValue());
								director.setRace(personnel.getPmRace().getRaceName());
								director.setDesignation(personnel.getDesignation());
								director.setWorkExperience(personnel.getWorkExperienceYear());
								boardOfDirector.setDirector(director);
							
								if(smSupplier.getSmAppl().getSmAssociationBodies().size()!=0){
									for(SmAssociationBody associationBody:smSupplier.getSmAppl().getSmAssociationBodies()){
										profBodyDetail.setProfBody(associationBody.getPmCertBody().getCertBodyId().toString());  
										profBodyDetail.setProfBodyNo(associationBody.getRegNo()); 
										profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
										boardOfDirector.setProfBodyDetail(profBodyDetail);
										break;
									}}else{
										profBodyDetail.setProfBody(IntegrationConstants.EMPTY_TRING);  
										profBodyDetail.setProfBodyNo(IntegrationConstants.EMPTY_TRING); 
										profBodyDetail.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
										boardOfDirector.setProfBodyDetail(profBodyDetail);
										
									}
									
									if(personnel.getSmStaffAcademics().size()!=0){
										for(SmStaffAcademic smStaffAcademic :personnel.getSmStaffAcademics()){
											qualification.setMajorIn(smStaffAcademic.getFieldMajor());
											qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());  

											ParameterReqDTO reqDTO = new ParameterReqDTO();
											reqDTO.setParameterId(new Integer(smStaffAcademic.getQualificationLevelId()).longValue());
											ParameterResDTO paraResDTO = parameterSO.retrieveParameterById(reqDTO);
											PmParameter pmParameter = paraResDTO.getPmParameter();
											
											qualification.setQualificationLevel(new Integer(pmParameter.getParameterCode()).intValue());   //pm_parameter with QL
//											qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
											qualification.setInstitute(smStaffAcademic.getInstituteName());
											qualification.setYear(smStaffAcademic.getYear().toString());
											qualification.setEducationField(smStaffAcademic.getPmEduField().getEduFieldId().toString());   
											boardOfDirector.setQualification(qualification);
											break;
										}}else{
											qualification.setMajorIn(IntegrationConstants.EMPTY_TRING);
											qualification.setQualification(IntegrationConstants.EMPTY_TRING);   
											qualification.setQualificationLevel(IntegrationConstants.DEFAULT_INT);
											qualification.setInstitute(IntegrationConstants.EMPTY_TRING);
											qualification.setYear(IntegrationConstants.EMPTY_TRING);
											qualification.setEducationField(IntegrationConstants.EMPTY_TRING);   
											boardOfDirector.setQualification(qualification);
										}
							
							expertise.setCategoryClassification(IntegrationConstants.VAVLUE_NA);
							boardOfDirector.setExpertise(expertise);
							
							if(!boardOfDirectorsList.contains(boardOfDirector))
								boardOfDirectorsList.add(boardOfDirector);
						}
						}
					}	
				}
		}
		}

		fileUtility.boardOfDirectorOutPutStream(boardOfDirectorsList, path);
		System.out.println("success");
	}
	
	
	public void createManagementPersonnel(SupplierSearchResDTO resDTO) {
		
		List<ManagementPersonnel> managementPersonnelsList = new ArrayList<ManagementPersonnel>();
		ManagementPersonnel managementPersonnel = null;
		Personnel personnel = null;
		Qualification qualification = null;
		ProfBodyDetail profBodyDetail = null;
		Expertise expertise = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			if (smSupplier.getSmMofAccounts().get(0).getMofNo().equals("465-00002043")) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
						
						for (SmPersonnel smPersonnel : smSupplier.getSmAppl().getSmPersonnels()) {
//							if (smPersonnel.isManagement()) {
								
								managementPersonnel = new ManagementPersonnel();
								personnel = new Personnel();
								profBodyDetail = new ProfBodyDetail();
								qualification = new Qualification();
								expertise = new Expertise();
								
								managementPersonnel.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo()); 

								if(smPersonnel.isManagement())
									personnel.setIsManagement(IntegrationConstants.VAVLUE_YES);
								else
									personnel.setIsManagement(IntegrationConstants.VAVLUE_NO);
								if(smPersonnel.isProf())
									personnel.setIsProfessional(IntegrationConstants.VAVLUE_YES);
								else
									personnel.setIsProfessional(IntegrationConstants.VAVLUE_NO);
								if(smPersonnel.isSemiProf())
									personnel.setIsSemiProf(IntegrationConstants.VAVLUE_YES);
								else
									personnel.setIsSemiProf(IntegrationConstants.VAVLUE_NO);
								if(smPersonnel.isClerical())
									personnel.setIsClerical(IntegrationConstants.VAVLUE_YES);
								else
									personnel.setIsClerical(IntegrationConstants.VAVLUE_NO);
								personnel.setName(smPersonnel.getName());
								personnel.setNewICNo(smPersonnel.getIdentificationNo());
								personnel.setPassport(IntegrationConstants.VAVLUE_NA);
								if(smPersonnel.isBumi())
									personnel.setIsBumi(IntegrationConstants.VAVLUE_YES);
								else
									personnel.setIsBumi(IntegrationConstants.VAVLUE_NO);
								personnel.setCitizenOf(131);
								personnel.setRace(smPersonnel.getPmRace().getRaceName());
								personnel.setDesignation(smPersonnel.getDesignation());
								personnel.setWorkExperience(smPersonnel.getWorkExperienceYear());
								managementPersonnel.setPersonnel(personnel);
								
								if(smSupplier.getSmAppl().getSmAssociationBodies().size()!=0){
									for(SmAssociationBody associationBody:smSupplier.getSmAppl().getSmAssociationBodies()){
										profBodyDetail.setProfBody(associationBody.getPmCertBody().getCertBodyId().toString());  // changed to cert body id
										profBodyDetail.setProfBodyNo(associationBody.getRegNo());  
										profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
										managementPersonnel.setProfBodyDetail(profBodyDetail);
										break;
									}}else{
										profBodyDetail.setProfBody(IntegrationConstants.EMPTY_TRING);  
										profBodyDetail.setProfBodyNo(IntegrationConstants.EMPTY_TRING); 
										profBodyDetail.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
										managementPersonnel.setProfBodyDetail(profBodyDetail);
										
									}
								
								if(smPersonnel.getSmStaffAcademics().size()!=0){
									for(SmStaffAcademic smStaffAcademic :smPersonnel.getSmStaffAcademics()){
										qualification.setMajorIn(smStaffAcademic.getFieldMajor());
										qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());  
									
										ParameterReqDTO reqDTO = new ParameterReqDTO();
										reqDTO.setParameterId(new Integer(smStaffAcademic.getQualificationLevelId()).longValue());
										ParameterResDTO paraResDTO = parameterSO.retrieveParameterById(reqDTO);
										PmParameter pmParameter = paraResDTO.getPmParameter();
										
										qualification.setQualificationLevel(new Integer(pmParameter.getParameterCode()).intValue());   //pm_parameter with QL
										qualification.setInstitute(smStaffAcademic.getInstituteName());
										qualification.setYear(smStaffAcademic.getYear().toString());
										qualification.setEducationField(smStaffAcademic.getPmEduField().getEduFieldId()+"");   
										managementPersonnel.setQualification(qualification);
										break;
									}}else{
										qualification.setMajorIn(IntegrationConstants.EMPTY_TRING);
										qualification.setQualification(IntegrationConstants.EMPTY_TRING);   
										qualification.setQualificationLevel(IntegrationConstants.DEFAULT_INT);
										qualification.setInstitute(IntegrationConstants.EMPTY_TRING);
										qualification.setYear(IntegrationConstants.EMPTY_TRING);
										qualification.setEducationField(IntegrationConstants.EMPTY_TRING);   
										managementPersonnel.setQualification(qualification);
									}
								
								
								expertise.setCategoryClassification(IntegrationConstants.VAVLUE_NA);
								managementPersonnel.setExpertise(expertise);
								if(!managementPersonnelsList.contains(managementPersonnel))
									managementPersonnelsList.add(managementPersonnel);
								
							}
						}
//					}
				}
			}
		}
		
		fileUtility.managementProfileFileOutPutStream(managementPersonnelsList, path);
	}
	
	public void createAuthorizedSignatory(SupplierSearchResDTO resDTO) {
		
		List<Signatory> signatoryList = new ArrayList<Signatory>();
		Signatory signatory = null;
		AuthorizedSignatory authorizedSignatory = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			if (smSupplier.getSmMofAccounts().get(0).getMofNo().equals("465-00002043")) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
						for (SmPersonnel smPersonnel : smSupplier.getSmAppl().getSmPersonnels()) {
							if (smPersonnel.isAuthorized()) {
								signatory = new Signatory();
								signatory.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
								
								authorizedSignatory = new AuthorizedSignatory();
								
								authorizedSignatory.setName(smPersonnel.getName());
								authorizedSignatory.setNewICNo(smPersonnel.getIdentificationNo());
								authorizedSignatory.setPassport(IntegrationConstants.VAVLUE_NA);
								if(smPersonnel.isBumi())
									authorizedSignatory.setIsBumi(IntegrationConstants.VAVLUE_YES);
								else
									authorizedSignatory.setIsBumi(IntegrationConstants.VAVLUE_NO);
								authorizedSignatory.setDesignation(smPersonnel.getDesignation());
								signatory.setAuthorizedSignatory(authorizedSignatory);
								signatoryList.add(signatory);
								
							}
						}
					}
				}
		}
		}
		
		fileUtility.signatoryFileOutPutStream(signatoryList, path);
	}

	
	
	public void createConsultantProfile(SupplierSearchResDTO resDTO) { 
		
		List<ConsultantProfile> consultantProfileList = new ArrayList<ConsultantProfile>();
		ConsultantProfile profile = null;
		ProfBodyDetail profBodyDetail = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			if (smSupplier.getSmMofAccounts().get(0).getMofNo().equals("465-02004072")) {
			if (null!=smSupplier.getSmAppl()) {
				
					if(smSupplier.getSmAppl().getSupplierType().equalsIgnoreCase(IntegrationConstants.CONSULTANT_PROFILE_CODE)){  // for consultant profiles
						
						profile = new ConsultantProfile();
						
						profile.setCompanyName(smSupplier.getCompanyName());
						profile.setSSMNo(smSupplier.getRegNo());
						profile.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
						profile.setCompanyType(getCompanyType(smSupplier.getBusinessType()));      
						
						String phoneNo = smSupplier.getSmAppl().getSmCompanyBasic().getPhoneNo();
						if(null!=phoneNo){
							if(phoneNo.startsWith("+")){
								profile.setTelCountryCode(phoneNo.substring(0, 2));
								profile.setTelAreaCode(phoneNo.substring(2, 4));
								profile.setTelNo(phoneNo.substring(4, phoneNo.length()));
							}else{
								profile.setTelCountryCode("+"+phoneNo.substring(0, 1));
								profile.setTelAreaCode(phoneNo.substring(1, 3));
								profile.setTelNo(phoneNo.substring(3, phoneNo.length()));
							}
							
						}
						
						String faxNo = smSupplier.getSmAppl().getSmCompanyBasic().getFaxNo();
						if(null!=faxNo){
							if(phoneNo.startsWith("+")){
								profile.setFaxCountryCode(faxNo.substring(0, 2));
								profile.setFaxAreaCode(faxNo.substring(2, 4));
								profile.setFaxNo(faxNo.substring(4, faxNo.length()));
							}else{
								profile.setFaxCountryCode("+"+faxNo.substring(0, 1));
								profile.setFaxAreaCode(faxNo.substring(1, 3));
								profile.setFaxNo(faxNo.substring(3, faxNo.length()));
							}
							
						}
						
						if(smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().size()>0){
							
							for(SmCompanyAddress companyAddress :smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()){
								if(null!=companyAddress.getSmAddress()){
									profile.setAddress1(companyAddress.getSmAddress().getAddress1());
									profile.setAddress2(companyAddress.getSmAddress().getAddress2());
									profile.setAddress3(companyAddress.getSmAddress().getAddress3());
									profile.setPostcode(companyAddress.getSmAddress().getPostcode());
									profile.setCity(companyAddress.getSmAddress().getPmCity().getCityName());
									profile.setDistrict(companyAddress.getSmAddress().getPmDistrict().getDistrictCode());
									profile.setState(companyAddress.getSmAddress().getPmState().getStateCode());
									profile.setCountry(IntegrationConstants.COUNTRY_CODE_MY);
									
								}
							}
						}
					
						profile.setAccumulatedCapital(smSupplier.getSmAppl().getSmCompanyBasic().getAuthorizedCapital());  //accumulated capital not availabele in ng
						profile.setAuthorisedCapital(smSupplier.getSmAppl().getSmCompanyBasic().getAuthorizedCapital());
						profile.setPaidUpCapital(smSupplier.getSmAppl().getSmCompanyBasic().getPaidUpCapital());
						profile.setAccumulatedShare(smSupplier.getSmAppl().getSmCompanyBasic().getAuthorizedCapital());    //accumulated share not available in ng
						profile.setBumiputeraPercentage(smSupplier.getSmAppl().getSmCompanyBasic().getBumiPercentage());
						profile.setNonBumiPercentage(smSupplier.getSmAppl().getSmCompanyBasic().getNonBumiPercentage());
					
						profile.setRegStatus(smSupplier.getSmAppl().getPmRegStatus().getRegStatusId()+"");   // pm_reg_status
						profile.setEffectDateTo(smSupplier.getSmMofAccounts().get(0).getExpDate());
						profile.setEstablishedDate(smSupplier.getEstablishDate());         
						profile.setEffectDateFrom(smSupplier.getSmMofAccounts().get(0).getEffDate());
						profile.setDisciplinary(IntegrationConstants.VAVLUE_NO);             //sm-complaint
						profile.setIsAssociatedCompany(IntegrationConstants.VAVLUE_NO);
						profile.setProfileUpdateDate(smSupplier.getSmAppl().getApplSubmitDate());
						
						
						profBodyDetail = new ProfBodyDetail();
						if (smSupplier.getSmAppl().getSmAssociationBodies().size() != 0) {
							for (SmAssociationBody associationBody : smSupplier.getSmAppl().getSmAssociationBodies()) {
								profBodyDetail.setProfBody(associationBody.getPmCertBody().getCertBodyId().toString());   // changed to cert body id
								profBodyDetail.setProfBodyNo(associationBody.getRegNo());
								profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
								profile.setProfBodyDetail(profBodyDetail);
								break;
							}
						   } else {
							profBodyDetail.setProfBody(IntegrationConstants.EMPTY_TRING);
							profBodyDetail.setProfBodyNo(IntegrationConstants.EMPTY_TRING);
							profBodyDetail.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
							profile.setProfBodyDetail(profBodyDetail);

						}
						

						RegistrationHistory registrationHistory = new RegistrationHistory();                   // Changed to mof start date and exp date
						registrationHistory.setDateStart(smSupplier.getSmMofAccounts().get(0).getEffDate());   
						registrationHistory.setDateEnd(smSupplier.getSmMofAccounts().get(0).getExpDate());    
						profile.setRegistrationHistory(registrationHistory);

						CapitalProportion capitalProportion = new CapitalProportion();
						capitalProportion.setBumi(smSupplier.getSmAppl().getSmCompanyBasic().getBumiEquity());   
						capitalProportion.setNonBumi(smSupplier.getSmAppl().getSmCompanyBasic().getNonBumiEquity());   
						capitalProportion.setNonMalaysian(smSupplier.getSmAppl().getSmCompanyBasic().getForeignerEquity());
						capitalProportion.setGovAgency(smSupplier.getSmAppl().getSmCompanyBasic().getGovtUnitrustEquity());
						capitalProportion.setGovEquity(smSupplier.getSmAppl().getSmCompanyBasic().getGovtEquity());
						capitalProportion.setLocalCo(smSupplier.getSmAppl().getSmCompanyBasic().getLocalCompanyEquity());
						capitalProportion.setForeignCo(smSupplier.getSmAppl().getSmCompanyBasic().getForeignCompanyEquity());
						capitalProportion.setTotalMember(smSupplier.getSmAppl().getSmCompanyBasic().getTotalMember());
						capitalProportion.setTotalShare(smSupplier.getSmAppl().getSmCompanyBasic().getTotalShare());
						profile.setCapitalProportion(capitalProportion);
						consultantProfileList.add(profile);
					}
				}
		}
		}
		fileUtility.consultantProfileFileOutPutStream(consultantProfileList, path);
	}
	
	public void createDeletedCompany(SupplierSearchResDTO resDTO) {
		
		List<DeletedCompany> deletedCompanyList = new ArrayList<DeletedCompany>();
		DeletedCompany deletedCompany = null;
		
		System.out.println("resDTO.getSmSuppliers() :"+resDTO.getSmSuppliers().size());
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			
			deletedCompany = new DeletedCompany();
			deletedCompany.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
			if(null!=smSupplier.getSmAppl()&&null!=smSupplier.getSmAppl().getSmApplRejectReasons()){
				deletedCompany.setReason(smSupplier.getSmAppl().getSmApplRejectReasons().get(0).
						 getPmReason().getPmReasonDescs().get(0).getReasonDesc());        // this is currently appl_id is null need to link 
				}else{
				deletedCompany.setReason(IntegrationConstants.EMPTY_TRING);	
					}
			deletedCompany.setDateDeleted(smSupplier.getCreatedDate());
			deletedCompanyList.add(deletedCompany);
								
		}
		
		fileUtility.deletedCompanyOutPutStream(deletedCompanyList, path);
	}


	public void createGovernmentProject(SupplierSearchResDTO resDTO) {
		
		List<GovernmentProject> governmentProjectList = new ArrayList<GovernmentProject>();
		GovernmentProject governmentProject = null;
		Project project = null;
	
		System.out.println("resDTO.getSmSuppliers() :"+resDTO.getSmSuppliers().size());
	
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			if (null!=smSupplier.getSmAppl()) {
				if(null!=smSupplier.getSmAppl().getSmCompanyExperiences()){
					for (SmCompanyExperience smCompanyExperience : smSupplier.getSmAppl().getSmCompanyExperiences()) {
						if (null!=smCompanyExperience) {
							governmentProject = new GovernmentProject();
							project = new Project();
							governmentProject.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
						
							project.setProjectNo(smCompanyExperience.getRefNo());
							project.setProjectName(smCompanyExperience.getTitle());
							project.setProjectLocation(smCompanyExperience.getProjectLocation());
							project.setClient(smCompanyExperience.getClientName());
							project.setApppointmentDate(smCompanyExperience.getProcurementDate());
							project.setCost(smCompanyExperience.getContractValue().toString());
							project.setServiceDesc(smCompanyExperience.getWorkServiceDesc());
							project.setWorkProgress(smCompanyExperience.getProgressStatus());
							//project.setWorkProgress(getWorkProgress(smCompanyExperience.getProgressStatus()));    // Need to confirm directly cmng value
							project.setWorkProgressOthers("");              // this is empty
							project.setConsultantFee(smCompanyExperience.getConsultantFee().toString());
//							project.setPaymentMethod(smCompanyExperience.getPaymentMethodId().toString());   // this is empty
							project.setPaymentMethod(IntegrationConstants.EMPTY_TRING);   // this is empty
							governmentProject.setProject(project);
							
							governmentProjectList.add(governmentProject);
							
							}
						}
					}
				}
			}
	
		fileUtility.governmentProjectOutPutStream(governmentProjectList, path);
	}
	
	public void createRegisteredCategory(SupplierSearchResDTO resDTO) {
		
		List<RegisteredCategory> registeredCategoryList = new ArrayList<RegisteredCategory>();
		RegisteredCategory registeredCategory = null;
		Category category = null;
		
		System.out.println("resDTO.getSmSuppliers() :"+resDTO.getSmSuppliers().size());
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmSupplierCategories()){
						for (SmSupplierCategory smSupplierCategory : smSupplier.getSmAppl().getSmSupplierCategories()) {
							if (null!=smSupplierCategory) {
								registeredCategory = new RegisteredCategory();
								category = new Category();
								registeredCategory.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
								
								if(smSupplierCategory.getPmCategoryL1().getCategoryL1Id()==26||smSupplierCategory.getPmCategoryL1().getCategoryL1Id()==25){
									category.setMainCat(smSupplierCategory.getPmCategoryL1().getCategoryL1Code());
									category.setSubCat(smSupplierCategory.getPmCategoryL2().getCategoryL2Code());
									category.setSubCatDivision(smSupplierCategory.getPmCategoryL3().getCategoryL3Code());
									category.setStatus(getCategoryStatus(smSupplierCategory.getRecordStatusFlg())); 
									registeredCategory.setCategory(category);
									registeredCategoryList.add(registeredCategory);
								}
								
								
								}
							}
						}
					}
				}
		
			fileUtility.registeredCategoryOutPutStream(registeredCategoryList, path);
		}
	
	public void createBranchCompany(SupplierSearchResDTO resDTO) {
		
		List<BranchCompany> branchCompanyList = new ArrayList<BranchCompany>();
		BranchCompany branchCompany = null;
		Branch branch = null;
		
		System.out.println("resDTO.getSmSuppliers() :"+resDTO.getSmSuppliers().size());
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			if (smSupplier.getSmMofAccounts().get(0).getMofNo().equals("465-00002043")) {	
					if(null!=smSupplier.getSmAppl().getSmSupplierBranches()&&!(smSupplier.getSmAppl().getSmSupplierBranches().isEmpty())){
						
						for (SmSupplierBranch smSupplierBranch : smSupplier.getSmAppl().getSmSupplierBranches()) {
							if (null!=smSupplierBranch) {

								branchCompany = new BranchCompany();
								branch = new Branch();
								branchCompany.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
							
								branch.setBranchName(smSupplierBranch.getBranchName());
								branch.setAddress1(smSupplierBranch.getSmAddress().getAddress1());
								branch.setAddress2(smSupplierBranch.getSmAddress().getAddress2());
								branch.setAddress3(smSupplierBranch.getSmAddress().getAddress3());
								branch.setPostcode(smSupplierBranch.getSmAddress().getPostcode());
								
								if(null!=smSupplierBranch.getSmAddress().getPmCity())
									branch.setCity(smSupplierBranch.getSmAddress().getPmCity().getCityName());
								else
									branch.setCity(IntegrationConstants.EMPTY_TRING);
							
								if(null!=smSupplierBranch.getSmAddress().getPmDistrict())
									branch.setDistrict(smSupplierBranch.getSmAddress().getPmDistrict().getDistrictCode());
								else
									branch.setDistrict(IntegrationConstants.EMPTY_TRING);
							
								if(null!=smSupplierBranch.getSmAddress().getPmState())	
									branch.setState(smSupplierBranch.getSmAddress().getPmState().getStateCode());
								else
									branch.setState(IntegrationConstants.EMPTY_TRING);
								
								if(null!=smSupplierBranch.getSmAddress().getPmCountry())
									branch.setCountry(smSupplierBranch.getSmAddress().getPmCountry().getCountryCode());
								else
									branch.setCountry(IntegrationConstants.EMPTY_TRING);
								branchCompany.setBranch(branch);
								
								if(!branchCompanyList.contains(branchCompany))
									branchCompanyList.add(branchCompany);
								
								}
							}

							
						}
			}
		}
		
			fileUtility.branchCompanyOutPutStream(branchCompanyList, path);
		}
	
	public void createCompanyOwner(SupplierSearchResDTO resDTO) {
		
		List<CompanyOwner> companyOwnerList = new ArrayList<CompanyOwner>();
		CompanyOwner companyOwner = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
					for (SmEquityCompany equityCompany : smSupplier.getSmAppl().getSmEquityCompanies()) {
						
						companyOwner = new CompanyOwner();
					
						if (null!=equityCompany) {
							
							companyOwner.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
							companyOwner.setCompanyName(equityCompany.getCompanyName());
							companyOwner.setsSMNo(equityCompany.getRegNo());
							companyOwner.setCompanyOwnerMOFNo(IntegrationConstants.EMPTY_TRING);   // Need to confirm whether column is there or not
							companyOwner.setShareholdingAmount(equityCompany.getShareAmt());
							companyOwner.setShareholdingPercentage(equityCompany.getSharePercentage());
							companyOwner.setAddress1(equityCompany.getSmAddress().getAddress1());
							companyOwner.setAddress2(equityCompany.getSmAddress().getAddress2());
							companyOwner.setAddress3(equityCompany.getSmAddress().getAddress3());
							companyOwner.setPostcode(equityCompany.getSmAddress().getPostcode());
							if(null!=equityCompany.getSmAddress().getPmCity())
								companyOwner.setCity(equityCompany.getSmAddress().getPmCity().getCityName());
							else
								companyOwner.setCity(null);
							if(null!=equityCompany.getSmAddress().getPmDistrict())
								companyOwner.setDistrict(equityCompany.getSmAddress().getPmDistrict().getDistrictCode());
							else
								companyOwner.setDistrict(null);
							if(null!=equityCompany.getSmAddress().getPmState())	
								companyOwner.setState(equityCompany.getSmAddress().getPmState().getStateCode());
							else
								companyOwner.setState(null);
							if(null!=equityCompany.getSmAddress().getPmCountry())
								companyOwner.setCountry(equityCompany.getSmAddress().getPmCountry().getCountryCode());
							else
								companyOwner.setCountry(null);
							
							

							String phoneNo = equityCompany.getPhoneNo();
							if(null!=phoneNo){
								if(phoneNo.startsWith("+")){
									companyOwner.setTelCountryCode(phoneNo.substring(0, 2));
									companyOwner.setTelAreaCode(phoneNo.substring(2, 4));
									companyOwner.setTelNo(phoneNo.substring(4, phoneNo.length()));
								}else{
									companyOwner.setTelCountryCode("+"+phoneNo.substring(0, 1));
									//companyOwner.setTelAreaCode(phoneNo.substring(1, 3));
//									companyOwner.setTelNo(phoneNo.substring(3, phoneNo.length()));
								}
								
							}
							
							String faxNo = equityCompany.getFaxNo();
							if(null!=faxNo){
								if(phoneNo.startsWith("+")){
									companyOwner.setFaxCountryCode(faxNo.substring(0, 2));
									companyOwner.setFaxAreaCode(faxNo.substring(2, 4));
									companyOwner.setFaxNo(faxNo.substring(4, faxNo.length()));
								}else{
									companyOwner.setFaxCountryCode("+"+faxNo.substring(0, 1));
//									companyOwner.setFaxAreaCode(faxNo.substring(1, 3));
//									companyOwner.setFaxNo(faxNo.substring(3, faxNo.length()));
								}
								
							}
							
							if(!companyOwnerList.contains(companyOwner))
								companyOwnerList.add(companyOwner);
									
						}
						
					}
					}
				}
		}
		
		fileUtility.companyOwnerOutPutStream(companyOwnerList, path);
		
	}
	
	public void createAssociatedCompany(SupplierSearchResDTO resDTO) {
		
		List<AssociatedCompany> associatedCompanyList = new ArrayList<AssociatedCompany>();
		AssociatedCompany associatedCompany = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				
			if (null!=smSupplier.getSmAppl()) {
				
				for (SmAssociateCompany associateCompany : smSupplier.getSmAppl().getSmAssociateCompanies()) {
						
						if(null!=associateCompany){
							
							associatedCompany = new AssociatedCompany();
							associatedCompany.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
							associatedCompany.setCompanyName(associateCompany.getCompanyName());
							associatedCompany.setsSMNO(associateCompany.getRegNo());
							associatedCompany.setAssociatedCompanyMOFNO(IntegrationConstants.EMPTY_TRING);
							if(associateCompany.getIsShareholding().intValue()==1)
								associatedCompany.setShareholding(IntegrationConstants.VAVLUE_YES);
							else
								associatedCompany.setShareholding(IntegrationConstants.VAVLUE_NO);
							associatedCompany.setAddress1(associateCompany.getSmAddress().getAddress1());
							associatedCompany.setAddress2(associateCompany.getSmAddress().getAddress2());
							associatedCompany.setAddress3(associateCompany.getSmAddress().getAddress3());
							associatedCompany.setPostcode(associateCompany.getSmAddress().getPostcode());
							
							if(null!=associateCompany.getSmAddress().getPmCity())
								associatedCompany.setCity(associateCompany.getSmAddress().getPmCity().getCityName());
							else
								associatedCompany.setCity(null);
							if(null!=associateCompany.getSmAddress().getPmDistrict())
								associatedCompany.setDistrict(associateCompany.getSmAddress().getPmDistrict().getDistrictCode());
							else
								associatedCompany.setDistrict(null);
							if(null!=associateCompany.getSmAddress().getPmState())	
								associatedCompany.setState(associateCompany.getSmAddress().getPmState().getStateCode());
							else
								associatedCompany.setState(null);
							if(null!=associateCompany.getSmAddress().getPmCountry())
								associatedCompany.setCountry(associateCompany.getSmAddress().getPmCountry().getCountryCode());
							else
								associatedCompany.setCountry(null);
							
							
							String phoneNo = associateCompany.getPhoneNo();
							if(null!=phoneNo){
								if(phoneNo.startsWith("+")){
									associatedCompany.setTelCountryCode(phoneNo.substring(0, 2));
									associatedCompany.setTelAreaCode(phoneNo.substring(2, 4));
									associatedCompany.setTelNo(phoneNo.substring(4, phoneNo.length()));
								}else{
									associatedCompany.setTelCountryCode("+"+phoneNo.substring(0, 1));
									associatedCompany.setTelAreaCode(phoneNo.substring(1, 3));
									associatedCompany.setTelNo(phoneNo.substring(3, phoneNo.length()));
								}
								
							}
							
							String faxNo = associateCompany.getFaxNo();
							if(null!=faxNo){
								if(phoneNo.startsWith("+")){
									associatedCompany.setFaxCountryCode(faxNo.substring(0, 2));
									associatedCompany.setFaxAreaCode(faxNo.substring(2, 4));
									associatedCompany.setFaxNo(faxNo.substring(4, faxNo.length()));
								}else{
									associatedCompany.setFaxCountryCode("+"+faxNo.substring(0, 1));
									associatedCompany.setFaxAreaCode(faxNo.substring(1, 3));
									associatedCompany.setFaxNo(faxNo.substring(3, faxNo.length()));
								}
								
							}
							
							if(!associatedCompanyList.contains(associatedCompany))
								associatedCompanyList.add(associatedCompany);
						}
					}
			}
		}
		fileUtility.AssociatedCompanyOutPutStream(associatedCompanyList, path);
		
	}
	
	
	public String createDirectory(Date date,String path) {
		
		SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

		File file = new File(path+dt1.format(date));
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}else{
			System.out.println("Directory is already exists!");
		}
			
		return file.getAbsolutePath()+"/";

	}
	
	public String getCategoryStatus(int statusFlg){
		
		String categoryStatus = null;
		
		/*660	ACTIVE 		661	SUSPENDED		662	TERMINATED		663	EXPIRED*/
		
		if(statusFlg == IntegrationConstants.RECORD_STATUS_ACTIVE){
			categoryStatus ="660";
		}else if(statusFlg == IntegrationConstants.RECORD_STATUS_SUSPENDED){
			categoryStatus ="661";
		}else if(statusFlg == IntegrationConstants.RECORD_STATUS_CANCELLED){
			categoryStatus ="662";
		}else if(statusFlg == IntegrationConstants.RECORD_STATUS_EXPIRED){
			categoryStatus ="663";
		}

		return categoryStatus;
		
	}
	
	public String getWorkProgress(String wrkPrg){
		String wrkProgress = null;
		/*1	COMPLETED 		2	IN PROGRESS		3	FAILURE		4	KIV		5	REPEAL*/
		
		if(wrkPrg.equals(IntegrationConstants.WORK_PROGRESS_CMPLTD)){
			wrkProgress ="1";
		}else if(wrkPrg.equals(IntegrationConstants.WORK_PROGRESS_INPGRS)){
			wrkProgress ="2";
		}else if(wrkPrg.equals(IntegrationConstants.WORK_PROGRESS_FAILURE)){
			wrkProgress ="3";
		}else {
			wrkProgress ="4";
		}

		return wrkProgress;
		
	}
	
	public String getCompanyType(String businessType){
		
		String compType = null; 
		
		switch(businessType){
			case "A":  compType = "1";
				break;
			case "B":  compType = "4";
				break;
			case "C":  compType = "2";
				break;
			case "D":  compType = "2";
				break;
			case "E":  compType = "2";
				break;
			case "F":  compType = "1";
				break;
			case "G":  compType = "1";
				break;
			case "H":  compType = "1";
				break;
			case "I":  compType = "3";
				break;
			case "J": compType = "4";
				break;
			case "K": compType = "6";
				break;
			case "L": compType = "5";
				break;
			case "M": compType = "7";
				break;
			case "N": compType = "?";
				break;
			case "O": compType = "6";
				break;
			default: compType = "?";
				break;
		}
		
		return compType;
		
	}
	/**
	 * @return SupplierSearchResDTO
	 */
	public SupplierSearchResDTO retriveSmSupplierBycreationDate(String DateString,String methodName,String taskId) {
		
	   ISupplierSearchSO supplierSearchSO = EpEjbRemoteBusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date date = null;
		try {
			date = sdf.parse(DateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("date :"+date);
		
		reqDTO.setCreateDate(date);
		reqDTO.setTaskId(taskId);
		
		SupplierSearchResDTO resDTO = null;
		if(methodName.equals("1"))
		  resDTO = supplierSearchSO.getDeletedCompanyEperunding(reqDTO);     // deleted company
		else if(methodName.equals("2"))
		  resDTO = supplierSearchSO.getRegisteredCategoryEperunding(reqDTO);  // registered category
		else if(methodName.equals("3"))
		  resDTO = supplierSearchSO.getEquityBodMgtAuthEperunding(reqDTO);   // equity,mgt,bod,auth
		else if(methodName.equals("4"))
		 resDTO = supplierSearchSO.getGovernProjEperunding(reqDTO);        // governament project
		else if(methodName.equals("5"))
		 resDTO = supplierSearchSO.getAssociatedBranchComapanyAndCompanyOwnerEperunding(reqDTO);      // associate ,branch company owner 
		else if(methodName.equals("6"))
		 resDTO = supplierSearchSO.getConsultantProfileEperunding(reqDTO);   //consultant profile
		
		return resDTO;
	}

}
