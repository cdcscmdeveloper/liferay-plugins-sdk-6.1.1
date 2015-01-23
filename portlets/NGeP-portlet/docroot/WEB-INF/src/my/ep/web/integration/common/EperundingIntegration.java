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
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.lookupservice.EpEjbRemoteBusinessDelegate;
import my.ep.web.integration.eperunding.AuthorizedSignatory;
import my.ep.web.integration.eperunding.BoardOfDirector;
import my.ep.web.integration.eperunding.CapitalProportion;
import my.ep.web.integration.eperunding.ConsultantProfile;
import my.ep.web.integration.eperunding.Director;
import my.ep.web.integration.eperunding.EquityOwner;
import my.ep.web.integration.eperunding.Expertise;
import my.ep.web.integration.eperunding.ManagementPersonnel;
import my.ep.web.integration.eperunding.Owner;
import my.ep.web.integration.eperunding.Personnel;
import my.ep.web.integration.eperunding.ProfBodyDetail;
import my.ep.web.integration.eperunding.Qualification;
import my.ep.web.integration.eperunding.RegistrationHistory;
import my.ep.web.integration.eperunding.Signatory;
import my.ep.web.util.lookupservice.BusinessDelegate;

/*
 *  @author Ganga prasad
 */
public class EperundingIntegration {
	
	String path = createDirectory(new Date(),IntegrationConstants.FILE_PATH);
	FileUtility fileUtility = new FileUtility();
	
	public static void main(String args[]){
		
		EperundingIntegration integration = new EperundingIntegration();
		integration.createFilesforEperunding();
	}
	
	public void createFilesforEperunding(){
		EperundingIntegration integration = new EperundingIntegration();
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate();
		if(resDTO.isSuccessful()){
			integration.createAuthorizedSignatory(resDTO);
			integration.createEquiytyOwner(resDTO);
			integration.createBoardOfDirector(resDTO);
			integration.createManagementPersonnel(resDTO);
		//	integration.createConsultantProfile(resDTO);  // under development
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
		
	//	FileUtility fileUtility = new FileUtility();
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

	//	FileUtility fileUtility = new FileUtility();
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
		
		FileUtility fileUtility = new FileUtility();
		fileUtility.manageProfileFileOutPutStream(managementPersonnelsList, path);
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
		
	//	FileUtility fileUtility = new FileUtility();
		fileUtility.signatoryFileOutPutStream(signatoryList, path);
	}

	/**
	 * @return SupplierSearchResDTO
	 */
	public SupplierSearchResDTO retriveSmSupplierBycreationDate() {
		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
		System.out.println("createAuthorizedSignatory ");
		
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
		
		SupplierSearchResDTO resDTO = supplierSearchSO.getSmSupplierforEperundingByCreatedDate(reqDTO);
		return resDTO;
	}
	
	public void createConsultantProfile(SupplierSearchResDTO resDTO) {
		
		List<ConsultantProfile> consultantProfileList = new ArrayList<ConsultantProfile>();
		ConsultantProfile profile = null;
		ProfBodyDetail profBodyDetail = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(smSupplier.getSmAppl().getSupplierType()=="J"){  // for consultant profiles
						profile = new ConsultantProfile();
						
						profile.setCompanyName("THL CONSULTANTS     ");
						profile.setSSMNo("1086516-V");
						profile.setMOFNo("465-02032414");
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
