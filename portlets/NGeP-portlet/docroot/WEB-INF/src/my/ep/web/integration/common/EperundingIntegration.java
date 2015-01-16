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

/*
 *  @author Ganga prasad
 */
public class EperundingIntegration {
	
	String path = createDirectory(new Date(),IntegrationConstants.FILE_PATH)+"/";
	
	public static void main(String args[]){
		
		EperundingIntegration integration = new EperundingIntegration();
		
		//integration.createAuthorizedSignatory();
		integration.createEquiytyOwner();
	}
	
	public void createEquiytyOwner() {
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate();
		
		List<EquityOwner> equityOwnerList = new ArrayList<EquityOwner>();
		EquityOwner equityOwner = null;
		ProfBodyDetail profBodyDetail = null;
		Qualification qualification = null;
		Owner owner = null;
		
		System.out.println("resDTO  :"+resDTO.isSuccessful());
		System.out.println("size  :"+resDTO.getSmSuppliers().size());
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
					for (SmPersonnel personnel : smSupplier.getSmAppl().getSmPersonnels()) {
						if (personnel.isEquityOwner()) {
							
							equityOwner = new EquityOwner();
							
							equityOwner.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
							
							owner = new Owner();
							owner.setIsEquityOwner("Y");
							owner.setName(personnel.getName());
							owner.setNewICNo(personnel.getIdentificationNo()); // need to confirm
							owner.setPassport("N/A");   // not known
							if(personnel.isBumi())
								owner.setIsBumi("Y");
							else
								owner.setIsBumi("N");
							owner.setCitizenOf(131);
							//owner.setCitizenOf(personnel.getPmCountry().getCountryId().intValue());
							owner.setEquityValue(personnel.getShareAmt().toString());
							owner.setEquityPercentage(personnel.getSharePercentage().toString());
							owner.setRace(personnel.getPmRace().getRaceName());
							owner.setDesignation(personnel.getDesignation());
							equityOwner.setOwner(owner);
							System.out.println("getName :"+personnel.getName());
							System.out.println("getSmAssociationBodies :"+smSupplier.getSmAppl().getSmAssociationBodies().size());
							
							for(SmAssociationBody associationBody:smSupplier.getSmAppl().getSmAssociationBodies()){
							
								profBodyDetail = new ProfBodyDetail();
								profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
								profBodyDetail.setProfBodyNo(associationBody.getRegNo()); 
								profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
								equityOwner.setProfBodyDetail(profBodyDetail);
							}
							
							System.out.println("getSmStaffAcademics :"+personnel.getSmStaffAcademics().size());
							for(SmStaffAcademic smStaffAcademic :personnel.getSmStaffAcademics()){
							
								qualification = new Qualification();
								qualification.setMajorIn(smStaffAcademic.getFieldMajor());
								qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());   
								qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
								qualification.setInstitute(smStaffAcademic.getInstituteName());
								qualification.setYear(smStaffAcademic.getYear().toString());
								qualification.setEducationField(smStaffAcademic.getPmEduField().getFieldCode());   
								equityOwner.setQualification(qualification);
								
							}
							
							Expertise expertise = new Expertise();
							expertise.setCategoryClassification("N/A");
							equityOwner.setExpertise(expertise);
									
						}
						equityOwnerList.add(equityOwner);
					}
					}
				}
				
		}
		
		
		
		FileUtility fileUtility = new FileUtility();
		fileUtility.equityOwnerOutPutStream(equityOwnerList, path);
		
	}
	
	public void createBoardOfDirector() {
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate();
//		String path = createDirectory(new Date(),"C:/Integration/Upload/")+"/";
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
								boardOfDirector.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
							
								director = new Director();
								director.setIsBOD("Y");
								director.setIsCEO("N");
								director.setName("MOHD RADZI BIN DAHALAN");
								director.setNewICNo("531016085495");
								director.setPassport("N/A");
								if(personnel.isBumi())
									director.setIsBumi("Y");
								else
									director.setIsBumi("N");
								director.setCitizenOf(131);
								director.setRace("MELAYU");
								director.setDesignation("PENGARAH");
								director.setWorkExperience(34);
								boardOfDirector.setDirector(director);
							
							for(SmAssociationBody associationBody:smSupplier.getSmAppl().getSmAssociationBodies()){
								
								profBodyDetail = new ProfBodyDetail();
								profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
								profBodyDetail.setProfBodyNo(associationBody.getRegNo()); 
								profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
								boardOfDirector.setProfBodyDetail(profBodyDetail);
							}
							for(SmStaffAcademic smStaffAcademic :personnel.getSmStaffAcademics()){
							
								qualification = new Qualification();
								qualification.setMajorIn(smStaffAcademic.getFieldMajor());
								qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());   
								qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
								qualification.setInstitute(smStaffAcademic.getInstituteName());
								qualification.setYear(smStaffAcademic.getYear().toString());
								qualification.setEducationField(smStaffAcademic.getPmEduField().getFieldCode());    
								boardOfDirector.setQualification(qualification);
								
							}
							
							expertise = new Expertise();
							expertise.setCategoryClassification("N/A");
							boardOfDirector.setExpertise(expertise);
						}
						}
					}	
				}
				boardOfDirectorsList.add(boardOfDirector);
		}

		FileUtility fileUtility = new FileUtility();
		fileUtility.boardOfDirectorOutPutStream(boardOfDirectorsList, path);
	}
	
	
	public void createManagementPersonnel() {
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate();
		
//		String path = createDirectory(new Date(),"C:/Integration/Upload/")+"/";
		List<ManagementPersonnel> managementPersonnelsList = new ArrayList<ManagementPersonnel>();
		ManagementPersonnel managementPersonnel = new ManagementPersonnel();
		Personnel personnel = null;
		Qualification qualification = null;
		ProfBodyDetail profBodyDetail = null;
		Expertise expertise = null;
		
		for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				if (null!=smSupplier.getSmAppl()) {
					if(null!=smSupplier.getSmAppl().getSmPersonnels()){
						
						for (SmPersonnel smPersonnel : smSupplier.getSmAppl().getSmPersonnels()) {
							if (smPersonnel.isManagement()) {
								
								managementPersonnel.setMOFNo("465-00002043");

								personnel = new Personnel();
								personnel.setIsManagement("N");
								personnel.setIsProfessional("N");
								personnel.setIsSemiProf("N");
								personnel.setIsClerical("Y");
								personnel.setName("ABDUL HADI BIN RAHIL");
								personnel.setNewICNo("600804015451");
								personnel.setPassport("N/A");
								if(smPersonnel.isBumi())
									personnel.setIsBumi("Y");
								else
									personnel.setIsBumi("N");
								personnel.setCitizenOf(131);
								personnel.setRace("MELAYU");
								personnel.setDesignation("DRIVER");
								personnel.setWorkExperience(25);
								managementPersonnel.setPersonnel(personnel);
								
								for(SmAssociationBody associationBody:smSupplier.getSmAppl().getSmAssociationBodies()){
									
									profBodyDetail = new ProfBodyDetail();
									profBodyDetail.setProfBody(associationBody.getAssociationBodyId().toString());  
									profBodyDetail.setProfBodyNo(associationBody.getRegNo()); 
									profBodyDetail.setProfBodyExpiry(associationBody.getExpDate());
									managementPersonnel.setProfBodyDetail(profBodyDetail);
								}
								
								for(SmStaffAcademic smStaffAcademic :smPersonnel.getSmStaffAcademics()){
									
									qualification = new Qualification();
									qualification.setMajorIn(smStaffAcademic.getFieldMajor());
									qualification.setQualification(smStaffAcademic.getPmEduField().getFieldName());   
									qualification.setQualificationLevel(smStaffAcademic.getQualificationLevelId());
									qualification.setInstitute(smStaffAcademic.getInstituteName());
									qualification.setYear(smStaffAcademic.getYear().toString());
									qualification.setEducationField(smStaffAcademic.getPmEduField().getFieldCode());    
									managementPersonnel.setQualification(qualification);
								}
								
								expertise = new Expertise();
								expertise.setCategoryClassification("N/A");
								managementPersonnel.setExpertise(expertise);
								
							}
						}
					}
				}
				managementPersonnelsList.add(managementPersonnel);
		}
		
		FileUtility fileUtility = new FileUtility();
		fileUtility.manageProfileFileOutPutStream(managementPersonnelsList, path);
	}
	
	public void createAuthorizedSignatory() {
		
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate();
		
//		String path = createDirectory(new Date(),"C:/Integration/Upload/")+"/";
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
								authorizedSignatory.setPassport("N/A");
								if(smPersonnel.isBumi())
									authorizedSignatory.setIsBumi("Y");
								else
									authorizedSignatory.setIsBumi("N");
								authorizedSignatory.setDesignation(smPersonnel.getDesignation());
								signatory.setAuthorizedSignatory(authorizedSignatory);
								signatoryList.add(signatory);
								
							}
						}
					}
				}
		}
		

		FileUtility fileUtility = new FileUtility();
		fileUtility.signatoryFileOutPutStream(signatoryList, path);
	}

	/**
	 * @return SupplierSearchResDTO
	 */
	public SupplierSearchResDTO retriveSmSupplierBycreationDate() {
		ISupplierSearchSO supplierSearchSO = EpEjbRemoteBusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
		
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
	
	public void createConsultantProfile() {
		
		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate();
		
//		String path = createDirectory(new Date(),"C:/Integration/Upload/")+"/";
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
						profile.setDisciplinary("N");
						profile.setIsAssociatedCompany("N");
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
		
		FileUtility fileUtility = new FileUtility();
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
		} else {
			System.out.println("Directory is already exists!");
		}
		
			System.out.println("file.getAbsoluteFile() "+file.getAbsolutePath());
			
			return file.getAbsolutePath();

	}

}
