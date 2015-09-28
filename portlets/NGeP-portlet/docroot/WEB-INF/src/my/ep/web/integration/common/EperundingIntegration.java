package my.ep.web.integration.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
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

import org.apache.log4j.Logger;

/*
 *  @author Ganga prasad
 */
public class EperundingIntegration extends InitializeSO{
	
	private static Logger log = EpLogger.getLogger(EperundingIntegration.class);

	OsbUtility osbUtility = new OsbUtility();
	FileUtility fileUtility = new FileUtility();
	String path = fileUtility.createDirectory(new Date(),IntegrationConstants.EPERUNDING_UPLOAD_DIR_LOCAL);

	/*public static void main(String args[]) {

		EperundingIntegration eperundingIntegration = new EperundingIntegration();
//		 eperundingIntegration.createFilesforEperunding();
		eperundingIntegration.createFilesAuthMgtEqtBODforEperunding();

		// INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
		// .getOSBServiceObject(INtfSO.class);
		//
		// TestNtfViaEmail testNtfViaEmail = new TestNtfViaEmail();
		// testNtfViaEmail.sendFilestoMail(ntfSO);
	}*/

	public void createFilesforEperunding() {

		 createConsultantProfile(retriveSmSupplierBycreationDate("6", ""));
		 createDeletedCompany(retriveSmSupplierBycreationDate("1",""));
		 createGovernmentProject(retriveSmSupplierBycreationDate("4",""));
		 createRegisteredCategory(retriveSmSupplierBycreationDate("2",""));
		
		 createCompanyOwner(retriveSmSupplierBycreationDate("5","1"));
		 createAssociatedCompany(retriveSmSupplierBycreationDate("5","3"));
		 createBranchCompany(retriveSmSupplierBycreationDate("5","2"));

	}

	public void createFilesAuthMgtEqtBODforEperunding() {

		SupplierSearchResDTO resDTO = retriveSmSupplierBycreationDate("3", "");

		if (resDTO.isSuccessful()) {
			createAuthorizedSignatory(resDTO);
			createEquiytyOwner(resDTO);
			createBoardOfDirector(resDTO);
			createManagementPersonnel(resDTO);
		}

	}

	public void createEquiytyOwner(SupplierSearchResDTO resDTO) {
		
		log.info("createEquiytyOwner start");

		List<EquityOwner> equityOwnerList = new ArrayList<EquityOwner>();
		EquityOwner equityOwner = null;
		ProfBodyDetail profBodyDetail = null;
		Qualification qualification = null;
		List<ProfBodyDetail> bodyDetails = null;
		List<Qualification> qualifications = null;

		Expertise expertise = null;
		Owner owner = null;
		if (resDTO.isSuccessful()) {
			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

				for (SmPersonnel personnel : smSupplier.getSmAppl()
						.getSmPersonnels()) {
					if (personnel.isEquityOwner()) {

						equityOwner = new EquityOwner();
						owner = new Owner();
						expertise = new Expertise();
						equityOwner.setMOFNo(smSupplier.getSmMofAccounts()
								.get(0).getMofNo());

						qualifications = new ArrayList<Qualification>();
						bodyDetails = new ArrayList<ProfBodyDetail>();
						owner.setIsEquityOwner(IntegrationConstants.VAVLUE_YES);
						owner.setName(personnel.getName());
						// need toconfirm
						owner.setNewICNo(personnel.getIdentificationNo());
						// not known
						owner.setPassport(IntegrationConstants.VAVLUE_NA);
						if (personnel.isBumi())
							owner.setIsBumi(IntegrationConstants.VAVLUE_YES);
						else
							owner.setIsBumi(IntegrationConstants.VAVLUE_NO);
						owner.setCitizenOf(131);
						// owner.setCitizenOf(personnel.getPmCountry().getCountryId().intValue());
						owner.setEquityValue(personnel.getShareAmt().toString());
						owner.setEquityPercentage(personnel
								.getSharePercentage().toString());
						owner.setRace(personnel.getPmRace().getRaceName());
						owner.setDesignation(personnel.getDesignation());

						if (personnel.getSmStaffAcademics() != null) {
							for (SmStaffAcademic smStaffAcademic : personnel
									.getSmStaffAcademics()) {
								qualification = new Qualification();
								qualification.setMajorIn(smStaffAcademic
										.getFieldMajor());
								qualification.setQualification(smStaffAcademic
										.getPmEduField().getFieldName());

								if (null != smStaffAcademic
										.getQualificationLevelId()) {
									ParameterReqDTO reqDTO = new ParameterReqDTO();
									reqDTO.setParameterId(new Integer(
											smStaffAcademic
													.getQualificationLevelId())
											.longValue());
									ParameterResDTO paraResDTO = parameterSO
											.retrieveParameterById(reqDTO);
									PmParameter pmParameter = paraResDTO
											.getPmParameter();

									qualification
											.setQualificationLevel(new Integer(
													pmParameter
															.getParameterCode())
													.intValue());
								} else {
									qualification.setQualificationLevel(0);
								}

								qualification.setInstitute(smStaffAcademic
										.getInstituteName());
								qualification.setYear(smStaffAcademic.getYear()
										.toString());
								qualification.setEducationField(smStaffAcademic
										.getPmEduField().getEduFieldId() + "");
								if (!qualifications.contains(qualifications))
									qualifications.add(qualification);
							}
						} else {
							qualification = new Qualification();
							qualification
									.setMajorIn(IntegrationConstants.EMPTY_TRING);
							qualification
									.setQualification(IntegrationConstants.EMPTY_TRING);
							qualification
									.setQualificationLevel(IntegrationConstants.DEFAULT_INT);
							qualification
									.setInstitute(IntegrationConstants.EMPTY_TRING);
							qualification
									.setYear(IntegrationConstants.EMPTY_TRING);
							qualification
									.setEducationField(IntegrationConstants.EMPTY_TRING);
							qualifications.add(qualification);

						}

						owner.setQualificationList(qualifications);
						equityOwner.setOwner(owner);

						if (smSupplier.getSmAppl().getSmAssociationBodies() != null) {
							for (SmAssociationBody associationBody : smSupplier
									.getSmAppl().getSmAssociationBodies()) {
								profBodyDetail = new ProfBodyDetail();
								profBodyDetail.setProfBody(associationBody
										.getPmCertBody().getCertBodyId()
										.toString());
								profBodyDetail.setProfBodyNo(associationBody
										.getRegNo());
								profBodyDetail
										.setProfBodyExpiry(associationBody
												.getExpDate());
								if (!bodyDetails.contains(bodyDetails))
									bodyDetails.add(profBodyDetail);
							}
						} else {

							profBodyDetail = new ProfBodyDetail();
							profBodyDetail
									.setProfBody(IntegrationConstants.EMPTY_TRING);
							profBodyDetail
									.setProfBodyNo(IntegrationConstants.EMPTY_TRING);
							profBodyDetail
									.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
							bodyDetails.add(profBodyDetail);

						}

						equityOwner.setProfBodyDetailList(bodyDetails);

						expertise
								.setCategoryClassification(IntegrationConstants.VAVLUE_NA);
						equityOwner.setExpertise(expertise);

						equityOwner.setProfBodyDetailList(bodyDetails);

						if (!equityOwnerList.contains(equityOwner))
							equityOwnerList.add(equityOwner);

					}
				}
			}
		}
		BatchReqDTO<EquityOwner> batchReqDTO = new BatchReqDTO<EquityOwner>();
		batchReqDTO.setFilePath(path);
		batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
		batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
		batchReqDTO.setBatchContent(equityOwnerList);
		fileUtility.equityOwnerOutPutStream(batchReqDTO);
	}

	public void createBoardOfDirector(SupplierSearchResDTO resDTO) {
		
		log.info("createBoardOfDirector start");

		List<BoardOfDirector> boardOfDirectorsList = new ArrayList<BoardOfDirector>();
		BoardOfDirector boardOfDirector = null;
		Director director = null;
		ProfBodyDetail profBodyDetail = null;
		Qualification qualification = null;
		Expertise expertise = null;
		List<ProfBodyDetail> bodyDetails = null;
		List<Qualification> qualifications = null;

		if (resDTO.isSuccessful()) {
			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

				for (SmPersonnel personnel : smSupplier.getSmAppl()
						.getSmPersonnels()) {

					if (personnel.isDirector()) {

						boardOfDirector = new BoardOfDirector();
						director = new Director();
						expertise = new Expertise();

						bodyDetails = new ArrayList<ProfBodyDetail>();
						qualifications = new ArrayList<Qualification>();

						boardOfDirector.setMOFNo(smSupplier.getSmMofAccounts()
								.get(0).getMofNo());

						director.setIsBOD(IntegrationConstants.VAVLUE_YES);
						director.setIsCEO(IntegrationConstants.VAVLUE_NO);
						director.setName(personnel.getName());
						director.setNewICNo(personnel.getIdentificationNo());
						director.setPassport(IntegrationConstants.VAVLUE_NA);
						if (personnel.isBumi())
							director.setIsBumi(IntegrationConstants.VAVLUE_YES);
						else
							director.setIsBumi(IntegrationConstants.VAVLUE_NO);
						director.setCitizenOf(131);
						// owner.setCitizenOf(personnel.getPmCountry().getCountryId().intValue());
						director.setRace(personnel.getPmRace().getRaceName());
						director.setDesignation(personnel.getDesignation());
						director.setWorkExperience(personnel
								.getWorkExperienceYear());

						if (personnel.getSmStaffAcademics() != null) {
							for (SmStaffAcademic smStaffAcademic : personnel
									.getSmStaffAcademics()) {
								qualification = new Qualification();
								qualification.setMajorIn(smStaffAcademic
										.getFieldMajor());
								qualification.setQualification(smStaffAcademic
										.getPmEduField().getFieldName());

								if (null != smStaffAcademic
										.getQualificationLevelId()) {
									ParameterReqDTO reqDTO = new ParameterReqDTO();
									reqDTO.setParameterId(new Integer(
											smStaffAcademic
													.getQualificationLevelId())
											.longValue());
									ParameterResDTO paraResDTO = parameterSO
											.retrieveParameterById(reqDTO);
									PmParameter pmParameter = paraResDTO
											.getPmParameter();

									qualification
											.setQualificationLevel(new Integer(
													pmParameter
															.getParameterCode())
													.intValue());
								} else {
									qualification.setQualificationLevel(0);
								}

								qualification.setInstitute(smStaffAcademic
										.getInstituteName());
								qualification.setYear(smStaffAcademic.getYear()
										.toString());
								qualification.setEducationField(smStaffAcademic
										.getPmEduField().getEduFieldId() + "");
								if (!qualifications.contains(qualifications))
									qualifications.add(qualification);
							}
						} else {
							qualification = new Qualification();
							qualification
									.setMajorIn(IntegrationConstants.EMPTY_TRING);
							qualification
									.setQualification(IntegrationConstants.EMPTY_TRING);
							qualification
									.setQualificationLevel(IntegrationConstants.DEFAULT_INT);
							qualification
									.setInstitute(IntegrationConstants.EMPTY_TRING);
							qualification
									.setYear(IntegrationConstants.EMPTY_TRING);
							qualification
									.setEducationField(IntegrationConstants.EMPTY_TRING);
							qualifications.add(qualification);

						}

						director.setQualificationList(qualifications);
						boardOfDirector.setDirector(director);

						if (smSupplier.getSmAppl().getSmAssociationBodies() != null) {
							for (SmAssociationBody associationBody : smSupplier
									.getSmAppl().getSmAssociationBodies()) {
								profBodyDetail = new ProfBodyDetail();
								profBodyDetail.setProfBody(associationBody
										.getPmCertBody().getCertBodyId()
										.toString());
								profBodyDetail.setProfBodyNo(associationBody
										.getRegNo());
								profBodyDetail
										.setProfBodyExpiry(associationBody
												.getExpDate());
								if (!bodyDetails.contains(bodyDetails))
									bodyDetails.add(profBodyDetail);
							}
						} else {

							profBodyDetail = new ProfBodyDetail();
							profBodyDetail
									.setProfBody(IntegrationConstants.EMPTY_TRING);
							profBodyDetail
									.setProfBodyNo(IntegrationConstants.EMPTY_TRING);
							profBodyDetail
									.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
							bodyDetails.add(profBodyDetail);

						}
						boardOfDirector.setProfBodyDetailList(bodyDetails);
						expertise
								.setCategoryClassification(IntegrationConstants.VAVLUE_NA);
						boardOfDirector.setExpertise(expertise);

						if (!boardOfDirectorsList.contains(boardOfDirector))
							boardOfDirectorsList.add(boardOfDirector);
					}
				}
			}
		}
		BatchReqDTO<BoardOfDirector> batchReqDTO = new BatchReqDTO<BoardOfDirector>();
		batchReqDTO.setFilePath(path);
		batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
		batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
		batchReqDTO.setBatchContent(boardOfDirectorsList);
		fileUtility.boardOfDirectorOutPutStream(batchReqDTO);
	}

	public void createManagementPersonnel(SupplierSearchResDTO resDTO) {
		
		log.info("createManagementPersonnel start");

		List<ManagementPersonnel> managementPersonnelsList = new ArrayList<ManagementPersonnel>();
		ManagementPersonnel managementPersonnel = null;
		Personnel personnel = null;
		Qualification qualification = null;
		ProfBodyDetail profBodyDetail = null;
		Expertise expertise = null;
		List<ProfBodyDetail> bodyDetails = null;
		List<Qualification> qualifications = null;
		

		if (resDTO.isSuccessful()) {
			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

				for (SmPersonnel smPersonnel : smSupplier.getSmAppl()
						.getSmPersonnels()) {

					if (smPersonnel.isManagement()) {

						managementPersonnel = new ManagementPersonnel();
						personnel = new Personnel();
						expertise = new Expertise();
						bodyDetails = new ArrayList<ProfBodyDetail>();
						qualifications = new ArrayList<Qualification>();

						managementPersonnel.setMOFNo(smSupplier
								.getSmMofAccounts().get(0).getMofNo());
						if (smPersonnel.isManagement())
							personnel
									.setIsManagement(IntegrationConstants.VAVLUE_YES);
						else
							personnel
									.setIsManagement(IntegrationConstants.VAVLUE_NO);
						if (smPersonnel.isProf())
							personnel
									.setIsProfessional(IntegrationConstants.VAVLUE_YES);
						else
							personnel
									.setIsProfessional(IntegrationConstants.VAVLUE_NO);
						if (smPersonnel.isSemiProf())
							personnel
									.setIsSemiProf(IntegrationConstants.VAVLUE_YES);
						else
							personnel
									.setIsSemiProf(IntegrationConstants.VAVLUE_NO);
						if (smPersonnel.isClerical())
							personnel
									.setIsClerical(IntegrationConstants.VAVLUE_YES);
						else
							personnel
									.setIsClerical(IntegrationConstants.VAVLUE_NO);
						personnel.setName(smPersonnel.getName());
						personnel.setNewICNo(smPersonnel.getIdentificationNo());
						personnel.setPassport(IntegrationConstants.VAVLUE_NA);
						if (smPersonnel.isBumi())
							personnel
									.setIsBumi(IntegrationConstants.VAVLUE_YES);
						else
							personnel.setIsBumi(IntegrationConstants.VAVLUE_NO);
						personnel.setCitizenOf(131);
						personnel
								.setRace(smPersonnel.getPmRace().getRaceName());
						personnel.setDesignation(smPersonnel.getDesignation());
						personnel.setWorkExperience(smPersonnel
								.getWorkExperienceYear());

						if (smPersonnel.getSmStaffAcademics() != null) {
							for (SmStaffAcademic smStaffAcademic : smPersonnel
									.getSmStaffAcademics()) {
								qualification = new Qualification();
								qualification.setMajorIn(smStaffAcademic
										.getFieldMajor());
								qualification.setQualification(smStaffAcademic
										.getPmEduField().getFieldName());

								if (null != smStaffAcademic
										.getQualificationLevelId()) {
									ParameterReqDTO reqDTO = new ParameterReqDTO();
									reqDTO.setParameterId(new Integer(
											smStaffAcademic
													.getQualificationLevelId())
											.longValue());
									ParameterResDTO paraResDTO = parameterSO
											.retrieveParameterById(reqDTO);
									PmParameter pmParameter = paraResDTO
											.getPmParameter();

									qualification
											.setQualificationLevel(new Integer(
													pmParameter
															.getParameterCode())
													.intValue());
								} else {
									qualification.setQualificationLevel(0);
								}

								qualification.setInstitute(smStaffAcademic
										.getInstituteName());
								qualification.setYear(smStaffAcademic.getYear()
										.toString());
								qualification.setEducationField(smStaffAcademic
										.getPmEduField().getEduFieldId() + "");
								if (!qualifications.contains(qualifications))
									qualifications.add(qualification);
							}
						} else {
							qualification = new Qualification();
							qualification
									.setMajorIn(IntegrationConstants.EMPTY_TRING);
							qualification
									.setQualification(IntegrationConstants.EMPTY_TRING);
							qualification
									.setQualificationLevel(IntegrationConstants.DEFAULT_INT);
							qualification
									.setInstitute(IntegrationConstants.EMPTY_TRING);
							qualification
									.setYear(IntegrationConstants.EMPTY_TRING);
							qualification
									.setEducationField(IntegrationConstants.EMPTY_TRING);
							qualifications.add(qualification);

						}
						personnel.setQualificationList(qualifications);
						managementPersonnel.setPersonnel(personnel);

						if (smSupplier.getSmAppl().getSmAssociationBodies() != null) {
							for (SmAssociationBody associationBody : smSupplier
									.getSmAppl().getSmAssociationBodies()) {
								profBodyDetail = new ProfBodyDetail();
								profBodyDetail.setProfBody(associationBody
										.getPmCertBody().getCertBodyId()
										.toString());
								profBodyDetail.setProfBodyNo(associationBody
										.getRegNo());
								profBodyDetail
										.setProfBodyExpiry(associationBody
												.getExpDate());
								if (!bodyDetails.contains(bodyDetails))
									bodyDetails.add(profBodyDetail);
							}

						} else {
							profBodyDetail = new ProfBodyDetail();
							profBodyDetail
									.setProfBody(IntegrationConstants.EMPTY_TRING);
							profBodyDetail
									.setProfBodyNo(IntegrationConstants.EMPTY_TRING);
							profBodyDetail
									.setProfBodyExpiry(IntegrationConstants.DEFAULT_DATE);
							bodyDetails.add(profBodyDetail);

						}
						managementPersonnel.setProfBodyDetailList(bodyDetails);

						expertise
								.setCategoryClassification(IntegrationConstants.VAVLUE_NA);
						managementPersonnel.setExpertise(expertise);
						if (!managementPersonnelsList
								.contains(managementPersonnel))
							managementPersonnelsList.add(managementPersonnel);

					}
				}
			}
		}
		BatchReqDTO<ManagementPersonnel> batchReqDTO = new BatchReqDTO<ManagementPersonnel>();
		batchReqDTO.setFilePath(path);
		batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
		batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
		batchReqDTO.setBatchContent(managementPersonnelsList);
		fileUtility.managementProfileFileOutPutStream(batchReqDTO);
		
	}

	public void createAuthorizedSignatory(SupplierSearchResDTO resDTO) {
		
		log.info("createAuthorizedSignatory start");

		List<Signatory> signatoryList = new ArrayList<Signatory>();
		Signatory signatory = null;
		AuthorizedSignatory authorizedSignatory = null;

		if (resDTO.isSuccessful()) {
			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

				for (SmPersonnel smPersonnel : smSupplier.getSmAppl()
						.getSmPersonnels()) {
					if (smPersonnel.isAuthorized()) {
						signatory = new Signatory();
						authorizedSignatory = new AuthorizedSignatory();
						signatory.setMOFNo(smSupplier.getSmMofAccounts().get(0)
								.getMofNo());

						authorizedSignatory.setName(smPersonnel.getName());
						authorizedSignatory.setNewICNo(smPersonnel
								.getIdentificationNo());
						authorizedSignatory
								.setPassport(IntegrationConstants.VAVLUE_NA);
						if (smPersonnel.isBumi())
							authorizedSignatory
									.setIsBumi(IntegrationConstants.VAVLUE_YES);
						else
							authorizedSignatory
									.setIsBumi(IntegrationConstants.VAVLUE_NO);
						authorizedSignatory.setDesignation(smPersonnel
								.getDesignation());

						signatory.setAuthorizedSignatory(authorizedSignatory);

						if (!signatoryList.contains(signatory))
							signatoryList.add(signatory);
					}
				}
			}
		}
		BatchReqDTO<Signatory> batchReqDTO = new BatchReqDTO<Signatory>();
		batchReqDTO.setFilePath(path);
		batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
		batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
		batchReqDTO.setBatchContent(signatoryList);
	
		fileUtility.signatoryFileOutPutStream(batchReqDTO);
		
	}

	public void createConsultantProfile(SupplierSearchResDTO resDTO) {
		
		log.info("createConsultantProfile start");

		List<ConsultantProfile> consultantProfileList = new ArrayList<ConsultantProfile>();
		ConsultantProfile profile = null;
		ProfBodyDetail profBodyDetail = null;
		
		
		if (resDTO.isSuccessful()) {
			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

				// for consultant profiles
				if (smSupplier.getSmAppl().getSupplierType().equalsIgnoreCase(IntegrationConstants.CONSULTANT_PROFILE_CODE)) {

					profile = new ConsultantProfile();
					profile.setCompanyName(smSupplier.getCompanyName());
					profile.setSSMNo(smSupplier.getRegNo());
					profile.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());
					
					if(smSupplier.getSmMofAccounts().get(0).getSmMofCerts().size()>0){
						profile.setVirtualCertNo(smSupplier.getSmMofAccounts().get(0).getSmMofCerts().get(0).getCertSerialNo());
					}

					// New columns added
					if(null!=smSupplier.getSmGstSuppliers()){
						profile.setGSTNo(smSupplier.getSmGstSuppliers().get(0).getGstRegNo());
						profile.setGSTEffectiveDate(smSupplier.getSmGstSuppliers().get(0).getGstEffDate());
						profile.setFirstTaxablePeriod(smSupplier.getSmGstSuppliers().get(0).getGstDeclaredDate());
					}else{
						profile.setGSTNo(IntegrationConstants.EMPTY_TRING);
						profile.setGSTEffectiveDate(IntegrationConstants.DEFAULT_DATE);
						profile.setFirstTaxablePeriod(IntegrationConstants.DEFAULT_DATE);
					}
					
					profile.setCompanyType(getCompanyType(smSupplier.getBusinessType()));

					String phoneNo = smSupplier.getSmAppl().getSmCompanyBasic().getPhoneNo();
					if (null != phoneNo) {
						if (phoneNo.startsWith("+")) {
							profile.setTelCountryCode(phoneNo.substring(0, 2));
							profile.setTelAreaCode(phoneNo.substring(2, 4));
							profile.setTelNo(phoneNo.substring(4,phoneNo.length()));
						} else {
							profile.setTelCountryCode("+" + phoneNo.substring(0, 1));
							profile.setTelAreaCode(phoneNo.substring(1, 3));
							profile.setTelNo(phoneNo.substring(3,phoneNo.length()));
						}

					}

					String faxNo = smSupplier.getSmAppl().getSmCompanyBasic().getFaxNo();
					if (null != faxNo) {
						if (phoneNo.startsWith("+")) {
							profile.setFaxCountryCode(faxNo.substring(0, 2));
							profile.setFaxAreaCode(faxNo.substring(2, 4));
							profile.setFaxNo(faxNo.substring(4, faxNo.length()));
						} else {
							profile.setFaxCountryCode("+" + faxNo.substring(0, 1));
							profile.setFaxAreaCode(faxNo.substring(1, 3));
							profile.setFaxNo(faxNo.substring(3, faxNo.length()));
						}

					}

					if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().size() > 0) {

						for (SmCompanyAddress companyAddress : smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()) {
							if (null != companyAddress.getSmAddress()) {
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

					profile.setAccumulatedCapital(smSupplier.getSmAppl().getSmCompanyBasic().getAuthorizedCapital()); // accumulated
					// capital not availabele in ng
					profile.setAuthorisedCapital(smSupplier.getSmAppl().getSmCompanyBasic().getAuthorizedCapital());
					profile.setPaidUpCapital(smSupplier.getSmAppl().getSmCompanyBasic().getPaidUpCapital());
					profile.setAccumulatedShare(smSupplier.getSmAppl().getSmCompanyBasic().getAuthorizedCapital()); // accumulated
					// share not available in ng
					profile.setBumiputeraPercentage(smSupplier.getSmAppl().getSmCompanyBasic().getBumiPercentage());
					profile.setNonBumiPercentage(smSupplier.getSmAppl().getSmCompanyBasic().getNonBumiPercentage());

					profile.setRegStatus(smSupplier.getSmAppl().getPmRegStatus().getRegStatusId() + ""); // pm_reg_status
					profile.setEffectDateTo(smSupplier.getSmMofAccounts().get(0).getExpDate());
					profile.setEstablishedDate(smSupplier.getEstablishDate());
					profile.setEffectDateFrom(smSupplier.getSmMofAccounts().get(0).getEffDate());
					profile.setDisciplinary(IntegrationConstants.VAVLUE_NO); // sm-complaint
					profile.setIsAssociatedCompany(IntegrationConstants.VAVLUE_NO);
					profile.setProfileUpdateDate(smSupplier.getSmAppl().getApplSubmitDate()); //

					profBodyDetail = new ProfBodyDetail();
					if (smSupplier.getSmAppl().getSmAssociationBodies().size() != 0) {
						for (SmAssociationBody associationBody : smSupplier.getSmAppl().getSmAssociationBodies()) {
							profBodyDetail.setProfBody(associationBody.getPmCertBody().getCertBodyId().toString()); 
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

					RegistrationHistory registrationHistory = new RegistrationHistory(); 
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
					if(!consultantProfileList.contains(profile))
						consultantProfileList.add(profile);

				}
			}
		}
			BatchReqDTO<ConsultantProfile> batchReqDTO = new BatchReqDTO<ConsultantProfile>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(consultantProfileList);
		
			fileUtility.consultantProfileFileOutPutStream(batchReqDTO);
			
	}

	public void createDeletedCompany(SupplierSearchResDTO resDTO) {
		
		log.info("createDeletedCompany start");

		List<DeletedCompany> deletedCompanyList = new ArrayList<DeletedCompany>();
		DeletedCompany deletedCompany = null;

			if (resDTO.isSuccessful()) {
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					if (smSupplier
							.getSmAppl()
							.getApplType()
							.equalsIgnoreCase(
									IntegrationConstants.RECORD_DELETED_CODE)) {

						deletedCompany = new DeletedCompany();
						deletedCompany.setmOFNo(smSupplier.getSmMofAccounts()
								.get(0).getMofNo());

						SmRemarksReqDTO remarksReqDTO = new SmRemarksReqDTO();
						remarksReqDTO
								.setDocType(IntegrationConstants.RECORD_DELETED_CODE);
						remarksReqDTO.setDocId(smSupplier.getSmAppl()
								.getApplId());

						SmRemaksResDTO remaksResDTO = smRemarksSO
								.retrieveSmRemarksByDocument(remarksReqDTO);

						if (!remaksResDTO.getRemarksList().isEmpty()
								|| remaksResDTO.getRemarksList() != null) {
							for (SmRemark smRemark : remaksResDTO
									.getRemarksList()) {
								// log.info("smRemark :"+smRemark.getRemark());
								deletedCompany.setReason(smRemark.getRemark());
								deletedCompany.setDateDeleted(smRemark
										.getCreatedDate());
							}
						}
					}

					deletedCompanyList.add(deletedCompany);
				}
			}
			BatchReqDTO<DeletedCompany> batchReqDTO = new BatchReqDTO<DeletedCompany>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(deletedCompanyList);
			
			fileUtility.deletedCompanyOutPutStream(batchReqDTO);
			
	}

	public void createGovernmentProject(SupplierSearchResDTO resDTO) {
		
		log.info("createGovernmentProject start");

		List<GovernmentProject> governmentProjectList = new ArrayList<GovernmentProject>();
		GovernmentProject governmentProject = null;
		Project project = null;

			if (resDTO.isSuccessful()) {
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					for (SmCompanyExperience smCompanyExperience : smSupplier
							.getSmAppl().getSmCompanyExperiences()) {

						governmentProject = new GovernmentProject();
						project = new Project();
						governmentProject.setmOFNo(smSupplier
								.getSmMofAccounts().get(0).getMofNo());

						project.setProjectNo(smCompanyExperience.getRefNo());
						project.setProjectName(smCompanyExperience.getTitle());
						project.setProjectLocation(smCompanyExperience
								.getProjectLocation());
						project.setClient(smCompanyExperience.getClientName());
						project.setApppointmentDate(smCompanyExperience
								.getProcurementDate());
						project.setCost(smCompanyExperience.getContractValue()
								.toString());
						project.setServiceDesc(smCompanyExperience
								.getWorkServiceDesc());
						project.setWorkProgress(smCompanyExperience
								.getProgressStatus());
						project.setWorkProgress(getWorkProgress(smCompanyExperience
								.getProgressStatus()));
						// Need to confirm directly cmng value
						project.setWorkProgressOthers(IntegrationConstants.EMPTY_TRING); // this is empty
						project.setConsultantFee(smCompanyExperience
								.getConsultantFee().toString());
						// project.setPaymentMethod(smCompanyExperience.getPaymentMethodId().toString());
						// // this is empty
						project.setPaymentMethod(IntegrationConstants.EMPTY_TRING); // this is empty
						governmentProject.setProject(project);

						if (!governmentProjectList.contains(governmentProject))
							governmentProjectList.add(governmentProject);
					}
				}
			}
			BatchReqDTO<GovernmentProject> batchReqDTO = new BatchReqDTO<GovernmentProject>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(governmentProjectList);
			
			fileUtility.governmentProjectOutPutStream(batchReqDTO);
	}

	public void createRegisteredCategory(SupplierSearchResDTO resDTO) {
		
		log.info("createRegisteredCategory start");

		List<RegisteredCategory> registeredCategoryList = new ArrayList<RegisteredCategory>();
		RegisteredCategory registeredCategory = null;
		Category category = null;

			if (resDTO.isSuccessful()) {
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					for (SmSupplierCategory smSupplierCategory : smSupplier
							.getSmAppl().getSmSupplierCategories()) {

						registeredCategory = new RegisteredCategory();
						category = new Category();
						registeredCategory.setmOFNo(smSupplier
								.getSmMofAccounts().get(0).getMofNo());

						category.setMainCat(smSupplierCategory
								.getPmCategoryL1().getCategoryL1Code());
						category.setSubCat(smSupplierCategory.getPmCategoryL2()
								.getCategoryL2Code());
						category.setSubCatDivision(smSupplierCategory
								.getPmCategoryL3().getCategoryL3Code());
						category.setStatus(getCategoryStatus(smSupplierCategory
								.getRecordStatusFlg()));
						registeredCategory.setCategory(category);
						registeredCategoryList.add(registeredCategory);

					}
				}
			}
			BatchReqDTO<RegisteredCategory> batchReqDTO = new BatchReqDTO<RegisteredCategory>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(registeredCategoryList);
			
			fileUtility.registeredCategoryOutPutStream(batchReqDTO);
	}

	public void createBranchCompany(SupplierSearchResDTO resDTO) {
		
		log.info("createBranchCompany start");

		List<BranchCompany> branchCompanyList = new ArrayList<BranchCompany>();
		BranchCompany branchCompany = null;
		Branch branch = null;

			if (resDTO.isSuccessful()) {
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					for (SmSupplierBranch smSupplierBranch : smSupplier.getSmAppl().getSmSupplierBranches()) {

						branchCompany = new BranchCompany();
						branch = new Branch();
						branchCompany.setMOFNo(smSupplier.getSmMofAccounts().get(0).getMofNo());

						branch.setBranchName(smSupplierBranch.getBranchName());
						branch.setAddress1(smSupplierBranch.getSmAddress().getAddress1());
						branch.setAddress2(smSupplierBranch.getSmAddress().getAddress2());
						branch.setAddress3(smSupplierBranch.getSmAddress().getAddress3());
						branch.setPostcode(smSupplierBranch.getSmAddress().getPostcode());

						if (null != smSupplierBranch.getSmAddress().getPmCity())
							branch.setCity(smSupplierBranch.getSmAddress().getPmCity().getCityName());
						else
							branch.setCity(IntegrationConstants.EMPTY_TRING);

						if (null != smSupplierBranch.getSmAddress()
								.getPmDistrict())
							branch.setDistrict(smSupplierBranch.getSmAddress()
									.getPmDistrict().getDistrictCode());
						else
							branch.setDistrict(IntegrationConstants.EMPTY_TRING);

						if (null != smSupplierBranch.getSmAddress()
								.getPmState())
							branch.setState(smSupplierBranch.getSmAddress()
									.getPmState().getStateCode());
						else
							branch.setState(IntegrationConstants.EMPTY_TRING);

						if (null != smSupplierBranch.getSmAddress()
								.getPmCountry())
							branch.setCountry(smSupplierBranch.getSmAddress()
									.getPmCountry().getCountryCode());
						else
							branch.setCountry(IntegrationConstants.EMPTY_TRING);
						branchCompany.setBranch(branch);

						if (!branchCompanyList.contains(branchCompany))
							branchCompanyList.add(branchCompany);

					}
				}
			}
			BatchReqDTO<BranchCompany> batchReqDTO = new BatchReqDTO<BranchCompany>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(branchCompanyList);
			
			fileUtility.branchCompanyOutPutStream(batchReqDTO);
			
	}

	public void createCompanyOwner(SupplierSearchResDTO resDTO) {
		
		log.info("createCompanyOwner start");

		List<CompanyOwner> companyOwnerList = new ArrayList<CompanyOwner>();
		CompanyOwner companyOwner = null;

			if (resDTO.isSuccessful()) {
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					for (SmEquityCompany equityCompany : smSupplier.getSmAppl()
							.getSmEquityCompanies()) {

						companyOwner = new CompanyOwner();
						companyOwner.setmOFNo(smSupplier.getSmMofAccounts()
								.get(0).getMofNo());
						companyOwner.setCompanyName(equityCompany
								.getCompanyName());
						companyOwner.setsSMNo(equityCompany.getRegNo());
						companyOwner
								.setCompanyOwnerMOFNo(IntegrationConstants.EMPTY_TRING);
						// Need to confirm whether column is there or not
						companyOwner.setShareholdingAmount(equityCompany
								.getShareAmt());
						companyOwner.setShareholdingPercentage(equityCompany
								.getSharePercentage());
						companyOwner.setAddress1(equityCompany.getSmAddress()
								.getAddress1());
						companyOwner.setAddress2(equityCompany.getSmAddress()
								.getAddress2());
						companyOwner.setAddress3(equityCompany.getSmAddress()
								.getAddress3());
						companyOwner.setPostcode(equityCompany.getSmAddress()
								.getPostcode());
						if (null != equityCompany.getSmAddress().getPmCity())
							companyOwner.setCity(equityCompany.getSmAddress()
									.getPmCity().getCityName());
						else
							companyOwner
									.setCity(IntegrationConstants.EMPTY_TRING);
						if (null != equityCompany.getSmAddress()
								.getPmDistrict())
							companyOwner.setDistrict(equityCompany
									.getSmAddress().getPmDistrict()
									.getDistrictCode());
						else
							companyOwner
									.setDistrict(IntegrationConstants.EMPTY_TRING);
						if (null != equityCompany.getSmAddress().getPmState())
							companyOwner.setState(equityCompany.getSmAddress()
									.getPmState().getStateCode());
						else
							companyOwner
									.setState(IntegrationConstants.EMPTY_TRING);
						if (null != equityCompany.getSmAddress().getPmCountry())
							companyOwner.setCountry(equityCompany
									.getSmAddress().getPmCountry()
									.getCountryCode());
						else
							companyOwner
									.setCountry(IntegrationConstants.EMPTY_TRING);

						String phoneNo = equityCompany.getPhoneNo();
						if (null != phoneNo) {
							if (phoneNo.startsWith("+")) {
								companyOwner.setTelCountryCode(phoneNo
										.substring(0, 2));
								companyOwner.setTelAreaCode(phoneNo.substring(
										2, 4));
								companyOwner.setTelNo(phoneNo.substring(4,
										phoneNo.length()));
							} else {
								companyOwner.setTelCountryCode("+"
										+ phoneNo.substring(0, 1));
								companyOwner.setTelAreaCode(phoneNo.substring(
										1, 3));
								companyOwner.setTelNo(phoneNo.substring(3,
										phoneNo.length()));
							}

						}

						String faxNo = equityCompany.getFaxNo();
						if (null != faxNo) {
							if (phoneNo.startsWith("+")) {
								companyOwner.setFaxCountryCode(faxNo.substring(
										0, 2));
								companyOwner.setFaxAreaCode(faxNo.substring(2,
										4));
								companyOwner.setFaxNo(faxNo.substring(4,
										faxNo.length()));
							} else {
								companyOwner.setFaxCountryCode("+"
										+ faxNo.substring(0, 1));
								companyOwner.setFaxAreaCode(faxNo.substring(1,
										3));
								companyOwner.setFaxNo(faxNo.substring(3,
										faxNo.length()));
							}

						}

						if (!companyOwnerList.contains(companyOwner))
							companyOwnerList.add(companyOwner);
					}

				}
			}
			BatchReqDTO<CompanyOwner> batchReqDTO = new BatchReqDTO<CompanyOwner>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(companyOwnerList);
			
			fileUtility.companyOwnerOutPutStream(batchReqDTO);
	}

	public void createAssociatedCompany(SupplierSearchResDTO resDTO) {
		
		log.info("createAssociatedCompany start");

		List<AssociatedCompany> associatedCompanyList = new ArrayList<AssociatedCompany>();
		AssociatedCompany associatedCompany = null;

			if (resDTO.isSuccessful()) {
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					for (SmAssociateCompany associateCompany : smSupplier
							.getSmAppl().getSmAssociateCompanies()) {

						associatedCompany = new AssociatedCompany();
						associatedCompany.setmOFNo(smSupplier
								.getSmMofAccounts().get(0).getMofNo());
						associatedCompany.setCompanyName(associateCompany
								.getCompanyName());
						associatedCompany.setsSMNO(associateCompany.getRegNo());
						associatedCompany
								.setAssociatedCompanyMOFNO(IntegrationConstants.EMPTY_TRING);
						if (associateCompany.getIsShareholding().intValue() == 1)
							associatedCompany
									.setShareholding(IntegrationConstants.VAVLUE_YES);
						else
							associatedCompany
									.setShareholding(IntegrationConstants.VAVLUE_NO);
						associatedCompany.setAddress1(associateCompany
								.getSmAddress().getAddress1());
						associatedCompany.setAddress2(associateCompany
								.getSmAddress().getAddress2());
						associatedCompany.setAddress3(associateCompany
								.getSmAddress().getAddress3());
						associatedCompany.setPostcode(associateCompany
								.getSmAddress().getPostcode());

						if (null != associateCompany.getSmAddress().getPmCity()) {
							associatedCompany.setCity(associateCompany
									.getSmAddress().getPmCity().getCityName());
						} else {
							associatedCompany
									.setCity(IntegrationConstants.EMPTY_TRING);
						}
						if (null != associateCompany.getSmAddress()
								.getPmDistrict()) {
							associatedCompany.setDistrict(associateCompany
									.getSmAddress().getPmDistrict()
									.getDistrictCode());
						} else {
							associatedCompany
									.setDistrict(IntegrationConstants.EMPTY_TRING);
						}
						if (null != associateCompany.getSmAddress()
								.getPmState()) {
							associatedCompany
									.setState(associateCompany.getSmAddress()
											.getPmState().getStateCode());
						} else {
							associatedCompany
									.setState(IntegrationConstants.EMPTY_TRING);
						}
						if (null != associateCompany.getSmAddress()
								.getPmCountry()) {
							associatedCompany.setCountry(associateCompany
									.getSmAddress().getPmCountry()
									.getCountryCode());
						} else {
							associatedCompany
									.setCountry(IntegrationConstants.EMPTY_TRING);
						}

						String phoneNo = associateCompany.getPhoneNo();
						if (null != phoneNo) {
							if (phoneNo.startsWith("+")) {
								associatedCompany.setTelCountryCode(phoneNo
										.substring(0, 2));
								associatedCompany.setTelAreaCode(phoneNo
										.substring(2, 4));
								associatedCompany.setTelNo(phoneNo.substring(4,
										phoneNo.length()));
							} else {
								associatedCompany.setTelCountryCode("+"
										+ phoneNo.substring(0, 1));
								associatedCompany.setTelAreaCode(phoneNo
										.substring(1, 3));
								associatedCompany.setTelNo(phoneNo.substring(3,
										phoneNo.length()));
							}

						}

						String faxNo = associateCompany.getFaxNo();
						if (null != faxNo) {
							if (phoneNo.startsWith("+")) {
								associatedCompany.setFaxCountryCode(faxNo
										.substring(0, 2));
								associatedCompany.setFaxAreaCode(faxNo
										.substring(2, 4));
								associatedCompany.setFaxNo(faxNo.substring(4,
										faxNo.length()));
							} else {
								associatedCompany.setFaxCountryCode("+"
										+ faxNo.substring(0, 1));
								associatedCompany.setFaxAreaCode(faxNo
										.substring(1, 3));
								associatedCompany.setFaxNo(faxNo.substring(3,
										faxNo.length()));
							}

						}

						if (!associatedCompanyList.contains(associatedCompany))
							associatedCompanyList.add(associatedCompany);

					}
				}
			}
			BatchReqDTO<AssociatedCompany> batchReqDTO = new BatchReqDTO<AssociatedCompany>();
			batchReqDTO.setFilePath(path);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(associatedCompanyList);
			
			fileUtility.AssociatedCompanyOutPutStream(batchReqDTO);
	}

	public String createDirectory(Date date, String path) {

		SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

		File file = new File(path + dt1.format(date));
		if (!file.exists()) {
			if (file.mkdir()) {
				log.info("Directory is created!");
			} else {
				log.info("Failed to create directory!");
			}
		} else {
			log.info("Directory is already exists!");
		}

		return file.getAbsolutePath() + "/";

	}

	public String getCategoryStatus(int statusFlg) {

		String categoryStatus = null;

		/* 660 ACTIVE 661 SUSPENDED 662 TERMINATED 663 EXPIRED */

		if (statusFlg == IntegrationConstants.RECORD_STATUS_ACTIVE) {
			categoryStatus = "660";
		} else if (statusFlg == IntegrationConstants.RECORD_STATUS_SUSPENDED) {
			categoryStatus = "661";
		} else if (statusFlg == IntegrationConstants.RECORD_STATUS_CANCELLED) {
			categoryStatus = "662";
		} else if (statusFlg == IntegrationConstants.RECORD_STATUS_EXPIRED) {
			categoryStatus = "663";
		}

		return categoryStatus;

	}

	public String getWorkProgress(String wrkPrg) {
		String wrkProgress = null;
		/* 1 COMPLETED 2 IN PROGRESS 3 FAILURE 4 KIV 5 REPEAL */

		if (wrkPrg.equals(IntegrationConstants.WORK_PROGRESS_CMPLTD)) {
			wrkProgress = "1";
		} else if (wrkPrg.equals(IntegrationConstants.WORK_PROGRESS_INPGRS)) {
			wrkProgress = "2";
		} else if (wrkPrg.equals(IntegrationConstants.WORK_PROGRESS_FAILURE)) {
			wrkProgress = "3";
		} else {
			wrkProgress = "4";
		}

		return wrkProgress;

	}

	public String getCompanyType(String businessType) {

		String compType = null;

		switch (businessType) {
		case "A":
			compType = "1";
			break;
		case "B":
			compType = "4";
			break;
		case "C":
			compType = "2";
			break;
		case "D":
			compType = "2";
			break;
		case "E":
			compType = "2";
			break;
		case "F":
			compType = "1";
			break;
		case "G":
			compType = "1";
			break;
		case "H":
			compType = "1";
			break;
		case "I":
			compType = "3";
			break;
		case "J":
			compType = "4";
			break;
		case "K":
			compType = "6";
			break;
		case "L":
			compType = "5";
			break;
		case "M":
			compType = "7";
			break;
		case "N":
			compType = "?";
			break;
		case "O":
			compType = "6";
			break;
		default:
			compType = "?";
			break;
		}

		return compType;

	}

	/**
	 * @return SupplierSearchResDTO
	 */
	public SupplierSearchResDTO retriveSmSupplierBycreationDate(String methodName, String taskId) {

		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		
		Date currentDate = new Date();
	
		reqDTO.setCreateDate(currentDate);
		reqDTO.setTaskId(taskId);

		SupplierSearchResDTO resDTO = null;
		if (methodName.equals("1"))
			resDTO = supplierSearchSO.getDeletedCompanyEperunding(reqDTO); // deleted company
		else if (methodName.equals("2"))
			resDTO = supplierSearchSO.getRegisteredCategoryEperunding(reqDTO); // registered category
		else if (methodName.equals("3"))
			resDTO = supplierSearchSO.getEquityBodMgtAuthEperunding(reqDTO); // equity,mgt,bod,auth
		else if (methodName.equals("4"))
			resDTO = supplierSearchSO.getGovernProjEperunding(reqDTO); // governament project
		else if (methodName.equals("5"))
			resDTO = supplierSearchSO.getAssociatedBranchComapanyAndCompanyOwnerEperunding(reqDTO); // associate
		else if (methodName.equals("6"))
			resDTO = supplierSearchSO.getConsultantProfileEperunding(reqDTO); // consultant profile

		return resDTO;
	}

}
