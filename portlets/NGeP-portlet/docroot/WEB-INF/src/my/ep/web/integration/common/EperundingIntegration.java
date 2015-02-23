package my.ep.web.integration.common;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
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
	String path = fileUtility.createDirectory(new Date(),IntegrationConstants.FILE_PATH);;
	
	
	public void createFilesforEperunding(){
		EperundingIntegration integration = new EperundingIntegration();
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate("1");
		
		if(resDTO.isSuccessful()){
			integration.createAuthorizedSignatory(resDTO);
			integration.createEquiytyOwner(resDTO);
			integration.createBoardOfDirector(resDTO);
			integration.createManagementPersonnel(resDTO);
		//	integration.createConsultantProfile(resDTO);  // under development
			integration.createDeletedCompany(resDTO);
			integration.createGovernmentProject(resDTO);
			integration.createRegisteredCategory(resDTO);
			
		}
		SupplierSearchResDTO resDTO1 = retriveSmSupplierBycreationDate("2");
		
		if(resDTO1.isSuccessful()){
			integration.createBranchCompany(resDTO1);
		}
		
		SupplierSearchResDTO resDTO2 = retriveSmSupplierBycreationDate("3");
		
		if(resDTO2.isSuccessful()){
			integration.createCompanyOwner(resDTO2);
			integration.createAssociatedCompany(resDTO2);
		}
		
		
	}
	
	public void createEquiytyOwner(SupplierSearchResDTO resDTO) {
		
		List<EquityOwner> equityOwnerList = new ArrayList<EquityOwner>();
		EquityOwner equityOwner = null;
		ProfBodyDetail profBodyDetail = null;
		Qualification qualification = null;
		Owner owner = null;
		Expertise expertise = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
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
							owner.setPassport(IntegrationConstants.VAVLUE_NA);   // not known
							if(personnel.isBumi())
								owner.setIsBumi(IntegrationConstants.VAVLUE_YES);
							else
								owner.setIsBumi(IntegrationConstants.VAVLUE_NO);
							owner.setCitizenOf(131);
//							owner.setCitizenOf(personnel.getPmCountry().getCountryId().intValue());
							owner.setEquityValue(personnel.getShareAmt().toString());
							owner.setEquityPercentage(personnel.getSharePercentage().toString());
							owner.setRace(personnel.getPmRace().getRaceName());
							owner.setDesignation(personnel.getDesignation());
							equityOwner.setOwner(owner);
							
							
							if(smSupplier.getSmAppl().getSmAssociationBodies().size()!=0){
							for(SmAssociationBody associationBody:smSupplier.getSmAppl().getSmAssociationBodies()){
								profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
								profBodyDetail.setProfBodyNo(associationBody.getRegNo()); 
								profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
								equityOwner.setProfBodyDetail(profBodyDetail);
								break;
							}}else{
								profBodyDetail.setProfBody(IntegrationConstants.EMPTY_TRING);  
								profBodyDetail.setProfBodyNo(IntegrationConstants.EMPTY_TRING); 
								profBodyDetail.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
								equityOwner.setProfBodyDetail(profBodyDetail);
								
							}
							
							if(personnel.getSmStaffAcademics().size()!=0){
								for(SmStaffAcademic smStaffAcademic :personnel.getSmStaffAcademics()){
									qualification.setMajorIn(smStaffAcademic.getFieldMajor());
									qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());   
									qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
									qualification.setInstitute(smStaffAcademic.getInstituteName());
									qualification.setYear(smStaffAcademic.getYear().toString());
									qualification.setEducationField(smStaffAcademic.getPmEduField().getFieldCode());   
									equityOwner.setQualification(qualification);
									break;
								}}else{
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
							if(!equityOwnerList.contains(equityOwner))
								equityOwnerList.add(equityOwner);
									
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
										profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
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
											qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
											qualification.setInstitute(smStaffAcademic.getInstituteName());
											qualification.setYear(smStaffAcademic.getYear().toString());
											qualification.setEducationField(smStaffAcademic.getPmEduField().getFieldCode());   
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
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
						
						for (SmPersonnel smPersonnel : smSupplier.getSmAppl().getSmPersonnels()) {
							if (smPersonnel.isManagement()) {
								
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
										profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
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
										qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
										qualification.setInstitute(smStaffAcademic.getInstituteName());
										qualification.setYear(smStaffAcademic.getYear().toString());
										qualification.setEducationField(smStaffAcademic.getPmEduField().getFieldCode());   
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
		
		fileUtility.signatoryFileOutPutStream(signatoryList, path);
	}

	/**
	 * @return SupplierSearchResDTO
	 */
	public SupplierSearchResDTO retriveSmSupplierBycreationDate(String taskId) {
		ISupplierSearchSO supplierSearchSO = EpEjbRemoteBusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "05-12-2013";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("date :"+date);
		
		reqDTO.setCreateDate(date);
		reqDTO.setTaskId(taskId);
		
		SupplierSearchResDTO resDTO = supplierSearchSO.getSmSupplierforEperundingByCreatedDate(reqDTO);
		return resDTO;
	}
	
	public void createConsultantProfile(SupplierSearchResDTO resDTO) {
		
		List<ConsultantProfile> consultantProfileList = new ArrayList<ConsultantProfile>();
		ConsultantProfile profile = null;
		ProfBodyDetail profBodyDetail = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(smSupplier.getSmAppl().getSupplierType().equalsIgnoreCase(IntegrationConstants.CONSULTANT_PROFILE_CODE)){  // for consultant profiles
						profile = new ConsultantProfile();
						
						profile.setCompanyName("THL CONSULTANTS     ");
						profile.setSSMNo("1086516-V");
						profile.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
						profile.setCompanyType("1");
						profile.setAddress1("2-2-2, JALAN SEMARAK API 3    ");
						profile.setAddress2("DIAMOND SQUARE");
						profile.setAddress3("     OFF JALAN GOMBAK");
						profile.setPostcode("53000");
						profile.setCity("KUALA LUMPUR");
						profile.setDistrict("1401");
						profile.setState("14");
						profile.setCountry("MY");
						profile.setTelCountryCode("+6");
						profile.setTelAreaCode("03");
						profile.setTelNo("40238289");
						profile.setFaxCountryCode("+6");
						profile.setFaxAreaCode("04");
						profile.setFaxNo("40211289");
						profile.setAccumulatedCapital(50000);
						profile.setAuthorisedCapital("0");
						profile.setPaidUpCapital("0");
						profile.setAccumulatedShare(0);

						profile.setBumiputeraPercentage(0);
						profile.setNonBumiPercentage(0);
						profile.setRegStatus(3);
						profile.setEffectDateTo(new Date());
						profile.setEstablishedDate(new Date());
						profile.setEffectDateFrom(new Date());
						profile.setDisciplinary(IntegrationConstants.VAVLUE_NO);
						profile.setIsAssociatedCompany(IntegrationConstants.VAVLUE_NO);
						profile.setProfileUpdateDate(new Date());
						
						profBodyDetail = new ProfBodyDetail();
//						profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
//						profBodyDetail.setProfBodyNo(associationBody.getRegNo()); 
//						profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
						profile.setProfBodyDetail(profBodyDetail);

						RegistrationHistory registrationHistory = new RegistrationHistory();
						registrationHistory.setDateStart(new Date());
						registrationHistory.setDateEnd(new Date());
						profile.setRegistrationHistory(registrationHistory);

						CapitalProportion capitalProportion = new CapitalProportion();
						capitalProportion.setBumi("0");
						capitalProportion.setNonBumi("50000");
						capitalProportion.setNonMalaysian("0");
						capitalProportion.setGovAgency("0");
						capitalProportion.setGovEquity("0");
						capitalProportion.setLocalCo("0");
						capitalProportion.setForeignCo("0");
						capitalProportion.setTotalMember(0);
						capitalProportion.setTotalShare(0);
						profile.setCapitalProportion(capitalProportion);
						consultantProfileList.add(profile);
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
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
						for (SmPersonnel smPersonnel : smSupplier.getSmAppl().getSmPersonnels()) {
							if (null!=smPersonnel) {
								deletedCompany = new DeletedCompany();
								deletedCompany.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
								
								deletedCompany.setReason(smPersonnel.getName());
								deletedCompany.setDateDeleted(smPersonnel.getCreatedDate());
								deletedCompanyList.add(deletedCompany);
								
							}
						}
					}
				}
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
						
							project.setProjectNo(smCompanyExperience.getRevNo()+"");
							project.setProjectName(smCompanyExperience.getTitle());
							project.setProjectLocation(smCompanyExperience.getProjectLocation());
							project.setClient(smCompanyExperience.getClientName());
							project.setApppointmentDate(smCompanyExperience.getContractStartDate());
							project.setCost(smCompanyExperience.getContractValue().toString());
							project.setServiceDesc(smCompanyExperience.getWorkServiceDesc());
							project.setWorkProgress(smCompanyExperience.getProgressStatus());
							project.setWorkProgressOthers("");
//							project.setConsultantFee(smCompanyExperience.getConsultantFee().toString());
//							project.setPaymentMethod(smCompanyExperience.getPaymentMethodId().toString());
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
									category.setStatus("66"+smSupplierCategory.getRecordStatusFlg());
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
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmSupplierBranches()&&!(smSupplier.getSmAppl().getSmSupplierBranches().isEmpty())){
						System.out.println("getSmSupplierBranches  :"+smSupplier.getSmAppl().getSmSupplierBranches().size());
						
						for (SmSupplierBranch smSupplierBranch : smSupplier.getSmAppl().getSmSupplierBranches()) {
							if (null!=smSupplierBranch) {
								System.out.println("smSupplierBranch :"+smSupplierBranch.getSupplierBranchId());
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
									branch.setCity(null);
								if(null!=smSupplierBranch.getSmAddress().getPmDistrict())
									branch.setDistrict(smSupplierBranch.getSmAddress().getPmDistrict().getDistrictName());
								else
									branch.setDistrict(null);
								if(null!=smSupplierBranch.getSmAddress().getPmState())	
									branch.setState(smSupplierBranch.getSmAddress().getPmState().getStateName());
								else
									branch.setState(null);
								if(null!=smSupplierBranch.getSmAddress().getPmCountry())
									branch.setCountry(smSupplierBranch.getSmAddress().getPmCountry().getCountryName());
								else
									branch.setCountry(null);
								branchCompany.setBranch(branch);
								
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
							companyOwner.setCompanyOwnerMOFNo(IntegrationConstants.EMPTY_TRING);
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
								companyOwner.setDistrict(equityCompany.getSmAddress().getPmDistrict().getDistrictName());
							else
								companyOwner.setDistrict(null);
							if(null!=equityCompany.getSmAddress().getPmState())	
								companyOwner.setState(equityCompany.getSmAddress().getPmState().getStateName());
							else
								companyOwner.setState(null);
							if(null!=equityCompany.getSmAddress().getPmCountry())
								companyOwner.setCountry(equityCompany.getSmAddress().getPmCountry().getCountryName());
							else
								companyOwner.setCountry(null);
							companyOwner.setTelCountryCode(equityCompany.getPhoneNo());
							companyOwner.setTelAreaCode(equityCompany.getPhoneNo());
							companyOwner.setTelNo(equityCompany.getPhoneNo());
							companyOwner.setFaxCountryCode(equityCompany.getFaxNo());
							companyOwner.setFaxAreaCode(equityCompany.getFaxNo());
							companyOwner.setFaxNo(equityCompany.getFaxNo());
							
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
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
					for (SmAssociateCompany associateCompany : smSupplier.getSmAppl().getSmAssociateCompanies()) {
						
						if(null!=associateCompany){
							
							associatedCompany = new AssociatedCompany();
							associatedCompany.setmOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
							associatedCompany.setCompanyName(associateCompany.getCompanyName());
							associatedCompany.setsSMNO(associateCompany.getRegNo());
							associatedCompany.setAssociatedCompanyMOFNO(IntegrationConstants.EMPTY_TRING);
							associatedCompany.setShareholding(associateCompany.getIsShareholding().toString());
							associatedCompany.setAddress1(associateCompany.getSmAddress().getAddress1());
							associatedCompany.setAddress2(associateCompany.getSmAddress().getAddress2());
							associatedCompany.setAddress3(associateCompany.getSmAddress().getAddress3());
							associatedCompany.setPostcode(associateCompany.getSmAddress().getPostcode());
							if(null!=associateCompany.getSmAddress().getPmCity())
								associatedCompany.setCity(associateCompany.getSmAddress().getPmCity().getCityName());
							else
								associatedCompany.setCity(null);
							if(null!=associateCompany.getSmAddress().getPmDistrict())
								associatedCompany.setDistrict(associateCompany.getSmAddress().getPmDistrict().getDistrictName());
							else
								associatedCompany.setDistrict(null);
							if(null!=associateCompany.getSmAddress().getPmState())	
								associatedCompany.setState(associateCompany.getSmAddress().getPmState().getStateName());
							else
								associatedCompany.setState(null);
							if(null!=associateCompany.getSmAddress().getPmCountry())
								associatedCompany.setCountry(associateCompany.getSmAddress().getPmCountry().getCountryName());
							else
								associatedCompany.setCountry(null);
							
							associatedCompany.setTelCountryCode(associateCompany.getPhoneNo());
							associatedCompany.setTelAreaCode(associateCompany.getPhoneNo());
							associatedCompany.setTelNo(associateCompany.getPhoneNo());
							associatedCompany.setFaxCountryCode(associateCompany.getFaxNo());
							associatedCompany.setFaxAreaCode(associateCompany.getFaxNo());
							associatedCompany.setFaxNo(associateCompany.getFaxNo());
						
							if(!associatedCompanyList.contains(associatedCompany))
								associatedCompanyList.add(associatedCompany);
						}
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
	//	System.out.println("file.getAbsoluteFile() "+file.getAbsolutePath());
			
		return file.getAbsolutePath()+"/";

	}

}
