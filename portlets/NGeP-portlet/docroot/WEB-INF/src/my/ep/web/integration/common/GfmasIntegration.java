package my.ep.web.integration.common;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.osb.fl.IFLOsbSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.osb.fl.FLOsbReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.VotFundType;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.integration.gfmas.AgCtrlOfficer;
import my.ep.web.integration.gfmas.AgOffice;
import my.ep.web.integration.gfmas.BankAccountInformation;
import my.ep.web.integration.gfmas.CompanyBranch;
import my.ep.web.integration.gfmas.GlAccount;
import my.ep.web.integration.gfmas.MasterDataMaterialInfo;
import my.ep.web.integration.gfmas.MasterDataVendor;
import my.ep.web.integration.gfmas.OrgProfile;
import my.ep.web.integration.gfmas.PaymentInfo;
import my.ep.web.integration.gfmas.ProgramActivity;
import my.ep.web.integration.gfmas.Project;
import my.ep.web.integration.gfmas.PtjGroup;
import my.ep.web.integration.gfmas.SSTInformation;
import my.ep.web.integration.gfmas.VotFund;
import my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRq_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeProxy;
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoRq_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqHeader_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqUID_Type;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/*
* author @Ganga prasad
*/

public class GfmasIntegration {
	
	private static Logger log = BatchLogger.getLogger(GfmasIntegration.class);

	public static void main(String args[]) {
		GfmasIntegration gfmasIntegration = new GfmasIntegration();
		gfmasIntegration.masterDataVendorGfmasOutbound(334181L);
//		gfmasIntegration.masterDataVendorGfmasInbound();
////		gfmasIntegration.sstInformationGfmas();
		

	}

	FileUtility fileUtility = new FileUtility();
	SFTPUtility sftpUtility = new SFTPUtility();
	OsbUtility osbUtility = new OsbUtility();
	SMSUtility smsUtility = new SMSUtility();
	
	Encryption encryption = new Encryption();

	
	IAgOfficeSO agOfficeSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IAgOfficeSO.class);
	IVotFundSO votFundSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IVotFundSO.class);
	IProgramActivitySO programActivitySO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProgramActivitySO.class);
	IProjectSetiaSO projectSetiaSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProjectSetiaSO.class);
	IGlAccountSO glAccountSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IGlAccountSO.class);
	IFinancialOrgSO financialOrgSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
	IFLOsbSO flOsbSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.ORDER_FULFILLMENT, IFLOsbSO.class);
	
	IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	
	IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);

	ISupplierSearchSO supplierSearchSO = BusinessDelegate
			.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

	ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
			.getInstance().getServiceObject("cm", ICodificationSO.class);
	
	IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	

	public void masterDataMaterialInfoGfmas() {

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "12-03-2015";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		reqDTO.setCreateDate(date);
		// reqDTO.setCompanyName("PHARMANIAGA LOGISTICS SDN BHD");
		CodificationResDTO resDTO = codificationSO.searchItemByCreateDate(reqDTO);
		List<MasterDataMaterialInfo> materilInfoList = null;

		if (resDTO.isSuccessful()) {

			materilInfoList = fileUtility.setGfmasMasterDataMaterialInfo(resDTO);

		}

		EPMFRq_Type epmfRq_Type = new EPMFRq_Type();
		RqHeader_Type rqHeader_Type = new RqHeader_Type();
		rqHeader_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
		rqHeader_Type.setIpAddress(IntegrationConstants.SERVICE_HOST);
		rqHeader_Type.setOrgUnit("MMINF");
		rqHeader_Type.setSegment(IntegrationConstants.SEQUENCE_NO);
		RqUID_Type rqUID_Type = new RqUID_Type();
		rqUID_Type.setAsyncRqUID("MMINF");
		rqUID_Type.setRqUID(IntegrationConstants.SEQUENCE_NO);

		rqHeader_Type.setUID(rqUID_Type);

		MasterDataMaterialInfoRq_Type materialInformationRq_Type = null;
		for (MasterDataMaterialInfo materialInfo : materilInfoList) {

			materialInformationRq_Type = new MasterDataMaterialInfoRq_Type();

			log.info("getMaterialCode " + materialInfo.getMaterialCode());
			log.info("getCodeType " + materialInfo.getCodeType());
			log.info("getActionCode " + materialInfo.getActionCode());
			log.info("getMaterialDesc " + materialInfo.getMaterialDesc());

			log.info("getMaterialAddDesc " + materialInfo.getMaterialAddDesc());
			log.info("getEndEffectiveDt " + materialInfo.getEndEffectiveDt());
			log.info("getStartEffectiveDt " + materialInfo.getStartEffectiveDt());

			log.info("getCommodityName " + materialInfo.getCommodityName());
			log.info("getBaseUnitOfMeasure " + materialInfo.getBaseUnitOfMeasure());
			log.info("getMaterialType " + materialInfo.getMaterialType()); // size 4
			log.info("getMaterialGroup " + materialInfo.getMaterialGroup()); // size 8
			log.info("getAltUnitOfMeasure " + materialInfo.getAltUnitOfMeasure());

			materialInformationRq_Type.setMaterialCode(materialInfo.getMaterialCode());
			materialInformationRq_Type.setCodeType(materialInfo.getCodeType());
			materialInformationRq_Type.setActionCode(materialInfo.getActionCode());
			materialInformationRq_Type.setMaterialDesc(materialInfo.getMaterialDesc());

			materialInformationRq_Type.setMaterialAddDesc(materialInfo.getMaterialAddDesc());
			materialInformationRq_Type.setEndEffectiveDt(materialInfo.getEndEffectiveDt());
			materialInformationRq_Type.setStartEffectiveDt(materialInfo.getStartEffectiveDt());

			materialInformationRq_Type.setCommodityName(materialInfo.getCommodityName());
			materialInformationRq_Type.setBaseUnitOfMeasure(materialInfo.getBaseUnitOfMeasure());
			materialInformationRq_Type.setMaterialType(materialInfo.getMaterialType()); // size 4
			materialInformationRq_Type.setMaterialGroup(materialInfo.getMaterialGroup()); // size 8
			materialInformationRq_Type.setAltUnitOfMeasure(materialInfo.getAltUnitOfMeasure());

			epmfRq_Type.setMasterDataMaterialInfoRq(materialInformationRq_Type);
			epmfRq_Type.setRqHeader(rqHeader_Type);

			
			  MasterDataMaterialInfoPortTypeProxy infoPortTypeProxy = new MasterDataMaterialInfoPortTypeProxy(); 
			  try {
				  my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRs_Type
				  epmfRs_Type = infoPortTypeProxy.inquire(epmfRq_Type);
				  log.info("getStatusCode :"+epmfRs_Type.getRsHeader().getStatus().getStatusCode());
				  log.info("getStatusDesc :"+epmfRs_Type.getRsHeader().getStatus().getStatusDesc());
			  } catch(RemoteException e) { e.printStackTrace(); }
			 
		}

	}

	public void sstInformationGfmas() {

		SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = ft.parse("04/03/2013");
			endDate = ft.parse("20/05/2015");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setStartDate(startDate);
		reqDTO.setEndDate(endDate);

		AwardSupplierResDTO resDTO = qtAwardSO.retrieveLoaListByDateRange(reqDTO);

		List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
		log.info("List: " + list.size());
		List<ScLoa> scLoaList = resDTO.getLoaResultBean().getResultList();
		List<SSTInformation> sstInformations = new ArrayList<SSTInformation>();
		SSTInformation sstInformation;
		for (ScLoa scLoa : scLoaList) {
			sstInformation = new SSTInformation();
			sstInformation.setRecordType(0);
			sstInformation.setSSTNumber(scLoa.getLoaNo());
			sstInformation.setPhysicalSSTNumber(scLoa.getScLoiLoa().getFileNo());
			sstInformation.setSapVendorCode(scLoa.getScLoiLoa().getSmSupplier().getSapVendorCode());
			sstInformation.setKumpPtj(scLoa.getScLoiLoa().getPmOrgProfile().getPmOrgValidity().getOrgCode());
			sstInformation.setSstAcknowledgeDate(fileUtility.formatDate(scLoa.getAckDate()));
			if (null != scLoa.getScLoiLoa().getFileNo())
				sstInformations.add(sstInformation);
		}

		String filePath = IntegrationConstants.FILE_PATH;

//		 calling method to save data in files
		fileUtility.sstInformationFileOutPutStream(sstInformations, filePath);

	}

	public void masterDataVendorGfmasOutbound(Long supplierId) {
		

		/*SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "15-06-2015";
		Date currentDate = null;//new Date();
		try {
			currentDate = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSupplierId(supplierId);
		
		SupplierSearchResDTO resDTO = supplierSearchSO.findSupplierforMasterDataVendor(supplierSearchReqDTO);
		List<MasterDataVendor> dataVendors = null;
		List<BankAccountInformation> accountInformations = null; ;
		List<CompanyBranch> companyBranchs = null;
		SmSupplier smSupplier = resDTO.getSmSupplier();
		if (resDTO.isSuccessful() && smSupplier!=null) {
			dataVendors = new ArrayList<MasterDataVendor>();
			MasterDataVendor dataVendor = null;
			BankAccountInformation accountInformation = null;
			CompanyBranch companyBranch = null;

//			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
			
				companyBranchs = new ArrayList<CompanyBranch>();
				
				if (null != smSupplier.getSmAppl()) {

					dataVendor = new MasterDataVendor();
					dataVendor.setRecordType(IntegrationConstants.RECORD_TYPE_ZERO);
					dataVendor.setAgencyId(IntegrationConstants.AGENSI_ID_ePerolehan);  
					dataVendor.setVendorIdCode(smSupplier.getEpNo());
					dataVendor.setEntityCode(getEntityType(smSupplier.getBusinessType())); //default set to SSM   

					// HQ- Branch Information
					
					if (null != smSupplier.getSmAppl().getSmCompanyBasic()) {
						
						log.info("HQ Branch available...");
							companyBranch = new CompanyBranch();
							
							companyBranch.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
						
							if (smSupplier.getSmAppl().getSmCompanyBasic().getCompanyName().length() > 35) {
								companyBranch.setName(smSupplier.getSmAppl().getSmCompanyBasic().getCompanyName().substring(0, 34));  
								companyBranch.setName2(smSupplier.getSmAppl().getSmCompanyBasic().getCompanyName().substring(35));
							} else {
								companyBranch.setName(smSupplier.getSmAppl().getSmCompanyBasic().getCompanyName());
								companyBranch.setName2(IntegrationConstants.EMPTY_TRING);
							}
							
							companyBranch.setEmailAddress(smSupplier.getSmAppl().getSmPersonnels().get(0).getEmail());  // Need to confirm
							companyBranch.setGlReconAccount(IntegrationConstants.GL_RECON_NO);
							companyBranch.setCurrency(IntegrationConstants.CURRENCY_CODE_MY);
							companyBranch.setIncoTerm(IntegrationConstants.EMPTY_TRING);
							companyBranch.setPaymentTerm(IntegrationConstants.EMPTY_TRING);
							companyBranch.setStatus(smSupplier.getRecordStatusFlg());
							companyBranch.setMobileNo(smSupplier.getSmAppl().getSmCompanyBasic().getPhoneNo());
							
							companyBranch.setIndicatorCode(IntegrationConstants.HQ_INDICATOR_CODE);
							companyBranch.setAccountActionIndicator(IntegrationConstants.ACCOUNT_ACTION_CREATE);
							companyBranch.setReferenceNoGST(smSupplier.getSmAppl().getSmCompanyBasic().getGstRegistrationNo());
							
							for(SmCompanyAddress companyAddress:smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()){
							
								if(companyAddress.getAddressType().equalsIgnoreCase(IntegrationConstants.COMPANY_ADDRESS)){
									companyBranch.setAddress(companyAddress.getSmAddress().getAddress1());
									companyBranch.setAddress2(companyAddress.getSmAddress().getAddress2());
									companyBranch.setAddress3(companyAddress.getSmAddress().getAddress3());
									companyBranch.setPostCode(companyAddress.getSmAddress().getPostcode());
									companyBranch.setCity(companyAddress.getSmAddress().getPmCity().getCityName());
									companyBranch.setRegion(companyAddress.getSmAddress().getPmState().getStateCode());
									companyBranch.setCountry(companyAddress.getSmAddress().getPmCountry().getCountryCode());
									companyBranch.setTelephoneNo(smSupplier.getSmAppl().getSmCompanyBasic().getPhoneNo());
									companyBranch.setFaxNo(smSupplier.getSmAppl().getSmCompanyBasic().getFaxNo());
								}else{
									companyBranch.setAddress(IntegrationConstants.EMPTY_TRING);
									companyBranch.setAddress2(IntegrationConstants.EMPTY_TRING);
									companyBranch.setAddress3(IntegrationConstants.EMPTY_TRING);
									companyBranch.setPostCode(IntegrationConstants.EMPTY_TRING);
									companyBranch.setCity(IntegrationConstants.EMPTY_TRING);
									companyBranch.setRegion(IntegrationConstants.EMPTY_TRING);
									companyBranch.setCountry(IntegrationConstants.EMPTY_TRING);
									companyBranch.setTelephoneNo(IntegrationConstants.EMPTY_TRING);
									companyBranch.setFaxNo(IntegrationConstants.EMPTY_TRING);
								}
								
							}
							
							if(!companyBranchs.contains(companyBranch))
								companyBranchs.add(companyBranch);
							
					}
				
					// Branch Company Information
					
					if (null != smSupplier.getSmAppl().getSmSupplierBranches() && !smSupplier.getSmAppl().getSmSupplierBranches().isEmpty()) {
						
						log.info("Branch company available");
						
						for(SmSupplierBranch smSupplierBranch:smSupplier.getSmAppl().getSmSupplierBranches()){
							
							companyBranch = new CompanyBranch();
							companyBranch.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
						
							if (smSupplierBranch.getBranchName().length() > 35) {
								companyBranch.setName(smSupplierBranch.getBranchName().substring(0, 34));  
								companyBranch.setName2(smSupplierBranch.getBranchName().substring(35));
							} else {
								companyBranch.setName(smSupplierBranch.getBranchName());
								companyBranch.setName2(IntegrationConstants.EMPTY_TRING);
							}
							
							companyBranch.setEmailAddress(smSupplier.getSmAppl().getSmPersonnels().get(0).getEmail());  // Need to confirm
							companyBranch.setGlReconAccount(IntegrationConstants.GL_RECON_NO);
							companyBranch.setCurrency(IntegrationConstants.CURRENCY_CODE_MY);
							companyBranch.setIncoTerm(IntegrationConstants.EMPTY_TRING);
							companyBranch.setPaymentTerm(IntegrationConstants.EMPTY_TRING);
							companyBranch.setStatus(smSupplier.getRecordStatusFlg());
							companyBranch.setMobileNo(smSupplierBranch.getPhoneNo());    // need to confirm
							
							companyBranch.setIndicatorCode(IntegrationConstants.BRANCH_INDICATOR_CODE);
							companyBranch.setAccountActionIndicator(IntegrationConstants.ACCOUNT_ACTION_CREATE);
							companyBranch.setReferenceNoGST(smSupplierBranch.getGstRegistrationNo());
							
							for(SmCompanyAddress companyAddress:smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()){
							
								if(companyAddress.getAddressType().equalsIgnoreCase(IntegrationConstants.COMPANY_ADDRESS)){
									companyBranch.setAddress(smSupplierBranch.getSmAddress().getAddress1());
									companyBranch.setAddress2(smSupplierBranch.getSmAddress().getAddress2());
									companyBranch.setAddress3(smSupplierBranch.getSmAddress().getAddress3());
									companyBranch.setPostCode(smSupplierBranch.getSmAddress().getPostcode());
									companyBranch.setCity(smSupplierBranch.getSmAddress().getPmCity().getCityName());
									companyBranch.setRegion(smSupplierBranch.getSmAddress().getPmState().getStateCode());
									companyBranch.setCountry(smSupplierBranch.getSmAddress().getPmCountry().getCountryCode());
									companyBranch.setTelephoneNo(smSupplierBranch.getPhoneNo());
									companyBranch.setFaxNo(smSupplierBranch.getFaxNo());
								}else{
									companyBranch.setAddress(IntegrationConstants.EMPTY_TRING);
									companyBranch.setAddress2(IntegrationConstants.EMPTY_TRING);
									companyBranch.setAddress3(IntegrationConstants.EMPTY_TRING);
									companyBranch.setPostCode(IntegrationConstants.EMPTY_TRING);
									companyBranch.setCity(IntegrationConstants.EMPTY_TRING);
									companyBranch.setRegion(IntegrationConstants.EMPTY_TRING);
									companyBranch.setCountry(IntegrationConstants.EMPTY_TRING);
									companyBranch.setTelephoneNo(IntegrationConstants.EMPTY_TRING);
									companyBranch.setFaxNo(IntegrationConstants.EMPTY_TRING);
								}
							}
							if(!companyBranchs.contains(companyBranch))
								companyBranchs.add(companyBranch);
						}
						
					}
					
					dataVendor.setCompanyBranchs(companyBranchs);
					

					if (null != smSupplier.getSmAppl().getSmSupplierBanks()	&& !smSupplier.getSmAppl().getSmSupplierBanks().isEmpty()) {
						accountInformations = new ArrayList<BankAccountInformation>();

						companyBranch.setBankAccountInfoCount(smSupplier.getSmAppl().getSmSupplierBanks().size());
						log.info("size of accounts :"+smSupplier.getSmAppl().getSmSupplierBanks().size());
						int i = 1;
						for (SmSupplierBank smSupplierBank : smSupplier.getSmAppl().getSmSupplierBanks()) {
							accountInformation = new BankAccountInformation();
							accountInformation.setRecordType(IntegrationConstants.RECORD_TYPE_TWO);
							accountInformation.setBankAccount(smSupplierBank.getAccountNo());
							accountInformation.setAccountHolderName(smSupplier.getCompanyName());
							accountInformation.setBankCountry(IntegrationConstants.COUNTRY_CODE_MY);
//							accountInformation.setBankCountry(smSupplierBank.getPmFinancialOrg().getPmCountry().getCountryCode());
							accountInformation.setCurrency(IntegrationConstants.CURRENCY_CODE_MY);
							accountInformation.setBankActionIndicator(IntegrationConstants.ACCOUNT_ACTION_CREATE);
							
							if (null != smSupplierBank.getPmFinancialOrg()) {
								log.info("getPmFinancialOrg :"+smSupplierBank.getPmFinancialOrg().getFinancialOrgId());
								if (null != smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs()){
//										&& smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs().size() > 0) {
									for(PmFinancialSvc pmFinancialSvc :smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs()){
										if(pmFinancialSvc.getRecordStatusFlg()==1)
											accountInformation.setBankKey(pmFinancialSvc.getBankCode());
									    }
									accountInformation.setRecordNumber(i);
									if(!accountInformations.contains(accountInformation))
										accountInformations.add(accountInformation);
									dataVendor.setBankAccountInfoList(accountInformations);
									i++;
								}
							}
							
						}
					}
					if(!dataVendors.contains(dataVendor))
						dataVendors.add(dataVendor);
				} 
//			}
			fileUtility.masterDataVendorFileOutPutStream(dataVendors,IntegrationConstants.FILE_PATH);
			
		}else{
			log.info("masterDataVendorGfmasOutbound(APIVE)  : Records not available ");
		}
		
	}

	public void masterDataVendorGfmasInbound() {
		log.info("inside masterDataVendorGfmasInbound : APOVE");

		String fileName = null;   //sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"APOVE");

		if (osbUtility.osbServiceCallInBound("GFM-370","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "APOVE");
				if(null!=fileName){
				
					if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				
						List<MasterDataVendor> dataVendors = fileUtility.masterDataVendorFileInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
						SupplierSearchReqDTO reqDTO = null;
						SmSupplier smSupplier = null;

						for (MasterDataVendor vendor : dataVendors) {
							reqDTO = new SupplierSearchReqDTO();
							smSupplier = new SmSupplier();
							smSupplier.setSapVendorCode(vendor.getSapVendorCode());
							smSupplier.setChangedBy(IntegrationConstants.CHANGED_BY);
							reqDTO.setSmSupplier(smSupplier);
							supplierSearchSO.saveOrUpdateSmSupplier(reqDTO);
					}
				}
				log.info("masterDataVendorGfmasInbound updated ");
			}

		} else {
			log.info("file not available masterDataVendorGfmasInbound : APOVE");
		}
	}
	
	
	// Every 5 minutes we have to pick file and insert into DB
	
	public void pejabatPerakaunanGfmasInbound() {

		log.info("inside pejabatPerakaunanGfmasInbound :GLBAC");
		String fileName = null;
	
		if (osbUtility.osbServiceCallInBound("GFM-190","")) {
			
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLBAC");
			
			if(null!=fileName){
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
					//String filePath = "C:/Integration/Download/";
				//	fileName = "1000GLBAC40000120150331001.GPG";
					List<AgOffice> agOfficeList = fileUtility.pejabatPerakaunanFileInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH+ fileName);
					AgOfficeReqDTO reqDTO = null;
					PmAgOffice agOffice = null;

					for (AgOffice office : agOfficeList) {
						reqDTO = new AgOfficeReqDTO();
						reqDTO.setAgOfficeCode(office.getCode());
						AgOfficeResDTO agOfficeDTO = agOfficeSO.retrieveValidAgOfficeByOfficeCode(reqDTO);
						if (office.getCode().equals(agOfficeDTO.getAgOffice().getOfficeCode())) {
							log.info("Duplicate Code");
						} else {
							reqDTO = new AgOfficeReqDTO();
							agOffice = new PmAgOffice();
							agOffice.setOfficeCode(office.getCode());
							agOffice.setOfficeName(office.getDescription());
							agOffice.setEffDate(new Timestamp(System.currentTimeMillis()));
							agOffice.setCreatedBy(1L);
							agOffice.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							agOffice.setRecordStatusFlg(1);
							reqDTO.setAgOffice(agOffice);
							agOfficeSO.saveOrUpdateAgOffice(reqDTO);
						}
					}
				}
				log.info("pejabatPerakaunanGfmasInbound updated ");
				
			}
		}else{
			log.info("file not available pejabatPerakaunanGfmasInbound :GLBAC");
		}
	}

	public void pegawaiPengawalGfmasInbound() {
		log.info("inside pegawaiPengawalGfmasInbound :GLSEG");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-200","")) {
		
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLSEG");
			
			if(null!=fileName){
				
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName, IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//					String filePath = "C:/Integration/Download/";
//					fileName = "1000GLSEG40000120150331001.GPG";
					List<AgCtrlOfficer> agOfficeList = fileUtility.pegawaiPengawalInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
					
					OrgProfileReqDTO orgProfileReqDTO = null;

					for (AgCtrlOfficer office : agOfficeList) {
						
						orgProfileReqDTO = new OrgProfileReqDTO();
						orgProfileReqDTO.setOrgType(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL);
						orgProfileReqDTO.setOrgCode(office.getCode()); 
						
						OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgProfileReqDTO);
					
						if (orgProfileResDTO.isSuccessful()) {
							log.info("Duplicate Code");
						} else {
							
							orgProfileReqDTO = new OrgProfileReqDTO();
							
							PmOrgProfile pmOrgProfile = new PmOrgProfile();
							
							PmOrgValidity orgValidity = new PmOrgValidity();
							orgValidity.setOrgCode(office.getCode());
							orgValidity.setOrgName(office.getDescription());
							orgValidity.setEffDate(new Timestamp(System.currentTimeMillis()));
							orgValidity.setRecordStatusFlg(1);
							orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							orgValidity.setChangedBy(1L);
							orgValidity.setCreatedBy(1L);
							orgValidity.setBrandNew(true);
							
							
							ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
							parameterReqDTO.setParameterCode(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL);
							ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);

							pmOrgProfile.setGovtSeller(false);  // default
							pmOrgProfile.setGroupOrg(false);    // default
							
							pmOrgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
							pmOrgProfile.setRecordStatusFlg(1);
							pmOrgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							pmOrgProfile.setChangedBy(1L);
							pmOrgProfile.setCreatedBy(1L);
							pmOrgProfile.setBrandNew(true);
							
							pmOrgProfile.setPmOrgValidity(orgValidity);
							
							orgProfileReqDTO.setPmOrgProfile(pmOrgProfile);
							
							orgProfileSO.saveOrUpdateOrgProfile(orgProfileReqDTO);
						}
					}
				}
				log.info("pegawaiPengawalGfmasInbound updated");
				}
			}else{
			log.info("file not available pegawaiPengawalGfmasInbound :GLSEG");
		}
	}

	public void votGfmasInbound() {
		log.info("inside votGfmasInbound :GLVOT");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-210","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLVOT");
			if(null!=fileName){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//					String filePath = "C:/Integration/Download/";
//					fileName = "1000GLVOT40000120150331001.GPG";
					List<VotFund> votList = fileUtility.votInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
					VotFundReqDTO reqDTO = null;
					PmVotFund pmVot = null;

					for (VotFund vot : votList) {
						reqDTO = new VotFundReqDTO();
						reqDTO.setVotFundCode(vot.getCode());
						VotFundResDTO votFundDTO = votFundSO.retrieveValidVotFundByVotCode(reqDTO);							
						
						if (vot.getCode().equals(votFundDTO.getPmVotFund().getVotFundCode())) {
							log.info(" Vot Duplicate Code");
						} else {
							pmVot = new PmVotFund();
							OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
							Long orgProfileId = Long.parseLong(vot.getPtjCode());
							log.info("orgProfileId :"+orgProfileId);
							
							orgProfileReqDTO.setOrgProfileId(orgProfileId);
							OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);

							pmVot.setPmOrgProfile(orgProfileResDTO.getPmOrgProfile());
							pmVot.setVotFundCode(vot.getCode());
							pmVot.setVotFundType(vot.getVotFundType());
							pmVot.setDescription(vot.getDescription());
							pmVot.setEffDate(vot.getEffectiveDate());
							pmVot.setExpDate(vot.getExpiryDate());
							pmVot.setCreatedBy(1L);
							pmVot.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							pmVot.setRecordStatusFlg(1);
							reqDTO.setVotFund(pmVot);
							votFundSO.saveOrUpdateVotFund(reqDTO);
						}
					}
				}
				log.info("votGfmasInbound updated");
				}
//			}else{
//			log.info("file not available votGfmasInbound :GLVOT");
//		}
	}

	public void votDanaGfmasInbound() {
		
		log.info("inside votDanaGfmasInbound :GLDNA");
		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-220","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLDNA");
			if(null!=fileName){
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
					
//					String filePath = "C:/Integration/Download/";
//					fileName = "1000GLDNA40000120150331001.GPG";
					List<VotFund> votList = fileUtility.votDanaInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);

					VotFundReqDTO reqDTO = null;
					PmVotFund pmVot = null;

					for (VotFund vot : votList) {
						reqDTO = new VotFundReqDTO();
						reqDTO.setVotFundCode(vot.getCode());
						VotFundResDTO votFundDTO = votFundSO.retrieveValidVotFundDanaByVotCode(reqDTO);							
						if (vot.getCode().equals(votFundDTO.getPmVotFund().getVotFundCode())) {
							log.info("Vot Dana Duplicate Code");
						} else {
							pmVot = new PmVotFund();
							pmVot.setVotFundCode(vot.getCode());
							pmVot.setDescription(vot.getDescription());
							pmVot.setVotFundType(VotFundType.AMANAH.value());
							pmVot.setEffDate(vot.getEffectiveDate());
							pmVot.setExpDate(vot.getExpiryDate());
							pmVot.setPmOrgProfile(null);
							pmVot.setCreatedBy(1L);
							pmVot.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							pmVot.setRecordStatusFlg(1);
							reqDTO.setVotFund(pmVot);
							votFundSO.saveOrUpdateVotFund(reqDTO);
						}
					}
				}
				log.info("votDanaGfmasInbound updated");
			}
			}else{
			log.info("file not available votDanaGfmasInbound :GLDNA");
		}
	}

	public void programAktivitiGfmasInbound() {
		log.info("inside programAktivitiGfmasInbound :GLPRG");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-230","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPRG");
			
			if(null!=fileName){
				
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//					String filePath = "C:/Integration/Download/";
//					fileName = "1000GLPRG40000120150331001.GPG";
					List<ProgramActivity> votList = fileUtility.programAktiviti(IntegrationConstants.DECRYPTED_FILE_PATH  + fileName);
					ProgramActivityReqDTO reqDTO = null;
					PmPrgActivity prgActivity = null;

					for (ProgramActivity programActivity : votList) {
						reqDTO = new ProgramActivityReqDTO();
						prgActivity = new PmPrgActivity();
						reqDTO.setCode(programActivity.getCode());
						ProgramActivityResDTO prgResDTO = programActivitySO.retrieveValidPrgActivityByPrgActivityCode(reqDTO);							
						if (programActivity.getCode().equals(
								prgResDTO.getPrgActivity().getPrgActivityCode())) {
								log.info(" Prg Duplicate Code");
						} else {
						
						VotFundReqDTO votreqDTO = new VotFundReqDTO();
						votreqDTO.setVotFundCode(programActivity.getVotCode());
						VotFundResDTO restDTO = votFundSO.retrieveVotFundByVotCode(votreqDTO);
					
						prgActivity.setPmVotFund(restDTO.getPmVotFund());
						prgActivity.setPrgActivityCode(programActivity.getCode());
						prgActivity.setDescription(programActivity.getDescription());
						prgActivity.setEffDate(programActivity.getEffectiveDate());
						prgActivity.setExpDate(programActivity.getExpiryDate());
						prgActivity.setCreatedBy(1L);
						prgActivity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						prgActivity.setRecordStatusFlg(1);
						reqDTO.setPrgActivity(prgActivity);
						programActivitySO.saveOrUpdatePrgActivity(reqDTO);
						}
					}
				}
				log.info("programAktivitiGfmasInbound updated");
				
				}
			}else{
			log.info("file not available programAktivitiGfmasInbound :GLPRG");
		}
	}

	public void projectGfmasInbound() {
		log.info("inside projectGfmasInbound :GLPRJ");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-240","")) {
			
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPRJ");
			if(null!=fileName){
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//					String filePath = "C:/Integration/Download/";
//					fileName = "1000GLPRJ40000120150331001.GPG";
					List<Project> projectList = fileUtility.project(IntegrationConstants.DECRYPTED_FILE_PATH +fileName);
					ProjectSetiaReqDTO reqDTO = null;

					for (Project project : projectList) {
						reqDTO = new ProjectSetiaReqDTO();
						reqDTO.setProjectCode(project.getCode());
						ProjectSetiaResDTO proResDTO = projectSetiaSO.retrieveProjectByProjectCode(reqDTO);							
						if (proResDTO.isSuccessful()) {
							log.info(" Project Duplicate Code");
						} else {
						
							PmSubSetia subSitia = new PmSubSetia();
						
							subSitia.setSubSetiaCode(project.getSubSetiaCode());
							subSitia.setDescription(project.getDescription());
							subSitia.setProjectCode(project.getCode());
							subSitia.setSetiaCode(project.getSetiaCode());
							if (project.getPembiayaan() != null	&& project.getPembiayaan().isEmpty()) {
								ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
								parameterReqDTO.setParameterCode(project.getPembiayaan());
								parameterReqDTO.setParameterType(ParameterTypeConst.FINANCING_TYPE);
								ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
								subSitia.setFinancingType(parameterResDTO.getPmParameter());
							}
							subSitia.setEffDate(project.getEffectiveDate());
							subSitia.setExpDate(project.getExpiryDate());
							subSitia.setRecordStatusFlg(1);
							subSitia.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							subSitia.setChangedBy(1L);
							subSitia.setCreatedBy(1L);

							reqDTO.setSubSetia(subSitia);
							projectSetiaSO.saveOrUpdateSubSetia(reqDTO);
					}
					}
				}
				log.info("projectGfmasInbound updated");
				}
			}else{
			log.info("file not available projectGfmasInbound :GLPRJ");
		}
	}

	public void kodAkaunGfmasInbound() {

		log.info("inside kodAkaunGfmasInbound :GLGLC");
		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-260","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLGLC");
			if(null!=fileName){
				
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//					String filePath = "C:/Integration/Download/";
//					fileName = "1000GLGLC40000120150331001.GPG";
					List<GlAccount> glAccountList = fileUtility.kodAkaun(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
					GlAccountReqDTO reqDTO = null;
					PmGlAccount glAccount = null;
					for (GlAccount account : glAccountList) {
						reqDTO = new GlAccountReqDTO();
						glAccount = new PmGlAccount();
					
						reqDTO.setGlAccountCode(account.getCode());
						GlAccountResDTO glAccountDTO = glAccountSO.retrieveValidGlAccountByglAccountCode(reqDTO);
							
						if (account.getCode().equals(glAccountDTO.getGlAccount().getGlAccCode())) {
							log.info("Duplicate Code");
						} else {
							glAccount.setGlAccCode(account.getCode());
							glAccount.setDescription(account.getDescription());
					
							if (!account.getPtjCode().equalsIgnoreCase(IntegrationConstants.KUMP_PTJ_CMN_CODE)) {
						
								OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
								Long orgProfileId = Long.parseLong(account.getPtjCode());
						
								log.info("orgProfileId :"+orgProfileId);
								orgProfileReqDTO.setOrgProfileId(orgProfileId);
								OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);
								glAccount.setPmOrgProfile(orgProfileResDTO.getPmOrgProfile());
							}else{
								/*OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
								Long orgProfileId = Long.parseLong(account.getPtjCode());
						
								log.info("orgProfileId :"+orgProfileId);
								orgProfileReqDTO.setOrgProfileId(orgProfileId);
								OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);*/
								glAccount.setPmOrgProfile(null);
								
							}
				
							glAccount.setEffDate(account.getEffectiveDate());
							glAccount.setExpDate(account.getExpiryDate());
							glAccount.setRecordStatusFlg(1);
							glAccount.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							glAccount.setChangedBy(1L);
							glAccount.setCreatedBy(1L);
							reqDTO.setGlAccount(glAccount);
							glAccountSO.saveOrUpdateGlAccount(reqDTO);
					}
				}
			}
			log.info("kodAkaunGfmasInbound updated");
			}
		}else{
			log.info("file not available kodAkaunGfmasInbound :GLGLC");
		}
	}

	public void kumpulanPtjGfmasInbound() {
		log.info("inside kumpulanPtjGfmasInbound :GLPCG");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-270","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPCG");
			if(fileName!=null){
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//				String filePath = "C:/Integration/Download/";
//				fileName = "1000GLPCG40000120150331001.GPG";
				List<PtjGroup> glAccountList = fileUtility.kumpulanPTJ(IntegrationConstants.DECRYPTED_FILE_PATH	+fileName);
			
				OrgProfileReqDTO orgProfileReqDTO = null;

				for (PtjGroup group : glAccountList) {
					
					orgProfileReqDTO = new OrgProfileReqDTO();
//					orgProfileReqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
					orgProfileReqDTO.setOrgCode(group.getCode()); 
					
					OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgProfileReqDTO);
					
					if (orgProfileResDTO.isSuccessful()) {
						log.info("Duplicate Code");
					} else {
						
						orgProfileReqDTO = new OrgProfileReqDTO();
						
						PmOrgProfile pmOrgProfile = new PmOrgProfile();
						
						PmOrgValidity orgValidity = new PmOrgValidity();
						orgValidity.setOrgCode(group.getCode());
						orgValidity.setOrgName(group.getDescription());
						orgValidity.setEffDate(group.getEffectiveDate());
						orgValidity.setExpDate(group.getExpiryDate());
						orgValidity.setRecordStatusFlg(1);
						orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						orgValidity.setChangedBy(1L);
						orgValidity.setCreatedBy(1L);
						orgValidity.setBrandNew(true);
						
						
						ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
						parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
						parameterReqDTO.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
						ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);

						pmOrgProfile.setGovtSeller(false);  // default
						pmOrgProfile.setGroupOrg(false);    // default
						
						pmOrgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
						pmOrgProfile.setRecordStatusFlg(1);
						pmOrgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						pmOrgProfile.setChangedBy(1L);
						pmOrgProfile.setCreatedBy(1L);
						pmOrgProfile.setBrandNew(true);
						
						pmOrgProfile.setPmOrgValidity(orgValidity);
						
						orgProfileReqDTO.setPmOrgProfile(pmOrgProfile);
						
						orgProfileSO.saveOrUpdateOrgProfile(orgProfileReqDTO);
					}
				}
			}
			log.info("kumpulanPtjGfmasInbound updated");
			}
		}else{
		log.info("file not available kumpulanPtjGfmasInbound :GLPCG");
		}
	}

	public void masterDataPtjGfmasInbound() {
		log.info("inside masterDataPtjGfmasInbound :GLPTJ");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-280","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPTJ");
			if(fileName!=null){
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//				String filePath = "C:/Integration/Download/";
//				fileName = "1000GLPTJ40000120150331001.GPG";
				List<OrgProfile> orgProfileList = fileUtility.masterDatePTJ(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				
				PmOrgProfile pmOrgProfile = null;
				OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();

				for (OrgProfile orgProfile : orgProfileList) {
					
					pmOrgProfile = new PmOrgProfile();

					PmOrgValidity orgValidity = new PmOrgValidity();
					orgValidity.setOrgCode(orgProfile.getCode());
					orgValidity.setOrgName(orgProfile.getDescription());
					orgValidity.setEffDate(orgProfile.getEffectiveDate());
					orgValidity.setExpDate(orgProfile.getExpiryDate());
					orgValidity.setRecordStatusFlg(1);
					orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					orgValidity.setChangedBy(1L);
					orgValidity.setCreatedBy(1L);
					orgValidity.setBrandNew(true);

					pmOrgProfile.setPmOrgValidity(orgValidity);
					
					Long orgProfileId = Long.parseLong(orgProfile.getPtjCode());
					
					log.info("orgProfileId :"+orgProfileId);
					orgProfileReqDTO.setOrgProfileId(orgProfileId);
					OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);

					pmOrgProfile.setParentOrgProfile(orgProfileResDTO.getPmOrgProfile());
					
					
					AgOfficeReqDTO agOfficeReqDTO = new AgOfficeReqDTO();
					AgOfficeReqDTO agCtrOfficeReqDTO = new AgOfficeReqDTO();
					agCtrOfficeReqDTO.setAgOfficerCode(orgProfile.getCtrOfficerCode());
					agOfficeReqDTO.setAgOfficerCode(orgProfile.getAgOfficeCode());
//					AgOfficeResDTO agOfficers = agOfficeSO.retrieveAgOfficerByOfficerCode(agCtrOfficeReqDTO);
					AgOfficeResDTO agOfficee = agOfficeSO.retrieveAgOfficeByOfficeCode(agOfficeReqDTO);

//					List<PmAgCtrlOfficer> agOfficer = agOfficers.getAgControlOfficerList();
					List<PmAgOffice> agOffice = agOfficee.getAgOfficeList();

//					 orgProfile.setPmAgCtrlOfficer(agOfficer);
//					 orgProfile.setPmAgOffice(agOffice);

					ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
					parameterReqDTO.setParameterCode(ParameterConst.ORG_TYPE_PTJ);
					ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);

					pmOrgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
					pmOrgProfile.setRecordStatusFlg(1);
					pmOrgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					pmOrgProfile.setChangedBy(1L);
					pmOrgProfile.setCreatedBy(1L);
					pmOrgProfile.setBrandNew(true);
					orgProfileReqDTO.setPmOrgProfile(pmOrgProfile);
					orgProfileSO.saveOrUpdateOrgProfile(orgProfileReqDTO);
				}
			}
				log.info("masterDataPtjGfmasInbound updated");
			}
		}else{
			log.info("file not available masterDataPtjGfmasInbound :GLPTJ");
		}
	}

	public void kodBankGfmasInbound() {
		
		log.info("inside kodBankGfmasInbound :CMBNK");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-360","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "CMBNK");
			if(fileName!=null){
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
//				String filePath = "C:/Integration/Download/";
//				fileName = "1000CMBNK40000120150331001.GPG";
				List<BankAccountInformation> bankList = fileUtility.kodBank(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);

				for (BankAccountInformation bank : bankList) {
					FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
					PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);

					PmFinancialOrg financialOrg = new PmFinancialOrg();
					financialOrg.setFinOrgName(bank.getBankName());
					financialOrg.setPmCountry(defaultCountry);
					
					List<PmFinancialSvc> financialServiceList = new ArrayList<PmFinancialSvc>();
					
					PmFinancialSvc financialService = new PmFinancialSvc();
					financialService.setBankCode(bank.getSwiftCode());
					IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,IParameterSO.class);
					ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
					parameterReqDTO.setParameterCode(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
					ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
					financialService.setServiceTypeCode(parameterResDTO.getPmParameter());
					financialService.setRecordStatusFlg(1);
					financialService.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					financialService.setChangedBy(1L);
					financialService.setCreatedBy(1L);
					financialService.setBrandNew(true);
					financialServiceList.add(financialService);
					
					financialOrg.setRecordStatusFlg(1);
					financialOrg.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					financialOrg.setChangedBy(1L);
					financialOrg.setCreatedBy(1L);
					financialOrg.setBrandNew(true);
					financialOrg.setPmFinancialSvcs(financialServiceList);
					financialService.setPmFinancialOrg(financialOrg);
					reqDTO.setPmFinancialOrg(financialOrg);
					financialOrgSO.saveOrUpdateFinancialOrg(reqDTO);
				}
			}
			log.info("kodBankGfmasInbound updated");
		}
		}else{
			log.info("file not available kodBankGfmasInbound :CMBNK");
		}
	}
	public void paymentGfmasInbound() {
		log.info("inside paymentGfmasInbound :AP511");

		String fileName = null;
		if (osbUtility.osbServiceCallInBound("GFM-140","")) {
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "AP511");
			if(fileName!=null){
				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.DECRYPTED_FILE_PATH);
					List<PaymentInfo> paymentInfoList = fileUtility.paymentInfo(IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				
					FLOsbReqDTO reqDTO = null;

					for (PaymentInfo bank : paymentInfoList) {
						reqDTO = new FLOsbReqDTO();
						reqDTO.setInvoiceNoList(bank.getInvoiceArray());
						reqDTO.setPaymentDate(bank.getPaymentDate());
						reqDTO.setPaymentRefNo(bank.getPaymentRefNo());
						flOsbSO.updatePaymentDetails(reqDTO);
				}
			}
			log.info("paymentGfmasInbound updated");
			}
			}else{
			log.info("file not available paymentGfmasInbound :AP511");
			}
	}
//	public void softcertNotifications() {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//		String dateInString = "12-03-2015";
//		Date date = null;
//		try {
//			date = sdf.parse(dateInString);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		SupplierSearchResDTO resDTO = supplierSearchSO.findSupplierforSoftCertNotify(date);
//		if(resDTO.isSuccessful()){
//			for(SmSupplier smSupplier : resDTO.getSmSuppliers()){
//				for(SmPersonnel personnel:smSupplier.getSmAppl().getSmPersonnels()){
//					log.info(" personnel.getMobileNo() :"+personnel.getMobileNo());
//					smsUtility.sendSMS(personnel.getMobileNo());
//				}
//			}
//		}else{
//			log.info("un successful...");
//		}
//	
//		
//	}
	
	// to find entity code
	
	public String getEntityType(String code) {

		String entityType = null;

		if (code.equalsIgnoreCase(SupplierBusinessCode.A.value())) {
			entityType = SupplierBusinessTypeCodes.A.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.B.value())) {
			entityType = SupplierBusinessTypeCodes.B.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.C.value())) {
			entityType = SupplierBusinessTypeCodes.C.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.D.value())) {
			entityType = SupplierBusinessTypeCodes.D.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.E.value())) {
			entityType = SupplierBusinessTypeCodes.E.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.F.value())) {
			entityType = SupplierBusinessTypeCodes.F.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.G.value())) {
			entityType = SupplierBusinessTypeCodes.G.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.H.value())) {
			entityType = SupplierBusinessTypeCodes.H.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.I.value())) {
			entityType = SupplierBusinessTypeCodes.I.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.J.value())) {
			entityType = SupplierBusinessTypeCodes.J.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.K.value())) {
			entityType = SupplierBusinessTypeCodes.K.value();
		} else if (code.equalsIgnoreCase(SupplierBusinessCode.L.value())) {
			entityType = SupplierBusinessTypeCodes.L.value();
		}else if (code.equalsIgnoreCase(SupplierBusinessCode.M.value())) {
			entityType = SupplierBusinessTypeCodes.M.value();
		}else if (code.equalsIgnoreCase(SupplierBusinessCode.N.value())) {
			entityType = SupplierBusinessTypeCodes.N.value();
		}else if (code.equalsIgnoreCase(SupplierBusinessCode.O.value())) {
			entityType = SupplierBusinessTypeCodes.O.value();
		}else if (code.equalsIgnoreCase(SupplierBusinessCode.P.value())) {
			entityType = SupplierBusinessTypeCodes.P.value();
		}else{
			entityType = SupplierBusinessTypeCodes.B.value();
		}
			

		return entityType;
	}
	
	
	// E - Teriman Integration  
	
	
	public void billingInformationGfmasOutbound() {

		
	}
	
	public void receiptInformationGfmasOutbound() {

		
	}
}
