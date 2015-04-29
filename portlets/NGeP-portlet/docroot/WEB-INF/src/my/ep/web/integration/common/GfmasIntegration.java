package my.ep.web.integration.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.osb.fl.IFLOsbSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.ptjgroup.IPtjGroupSO;
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
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupReqDTO;
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
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
import my.ep.persistence.entity.pm.common.PmAgCtrlOfficer;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmPtjGroup;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.integration.gfmas.AgCtrlOfficer;
import my.ep.web.integration.gfmas.AgOffice;
import my.ep.web.integration.gfmas.BankAccountInformation;
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
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoRq_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqHeader_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqUID_Type;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/*
* author @Ganga prasad
*/

public class GfmasIntegration {

	public static void main(String args[]) {
		GfmasIntegration gfmasIntegration = new GfmasIntegration();
//		gfmasIntegration.masterDataVendorGfmasOutbound();
//		gfmasIntegration.projectGfmasInbound();
//		gfmasIntegration.sstInformationGfmas();
		

	}

	FileUtility fileUtility = new FileUtility();
	SFTPUtility sftpUtility = new SFTPUtility();
	OsbUtility osbUtility = new OsbUtility();
	SMSUtility smsUtility = new SMSUtility();

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
	IPtjGroupSO ptgGroupSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IPtjGroupSO.class);
	IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);
	IFinancialOrgSO financialOrgSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
	IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	IFLOsbSO flOsbSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.ORDER_FULFILLMENT, IFLOsbSO.class);

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
		CodificationResDTO resDTO = codificationSO
				.searchItemByCreateDate(reqDTO);
		List<MasterDataMaterialInfo> materilInfoList = null;

		if (resDTO.isSuccessful()) {

			materilInfoList = fileUtility
					.setGfmasMasterDataMaterialInfo(resDTO);

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

			System.out.println("getMaterialCode "
					+ materialInfo.getMaterialCode());
			System.out.println("getCodeType " + materialInfo.getCodeType());
			System.out.println("getActionCode " + materialInfo.getActionCode());
			System.out.println("getMaterialDesc "
					+ materialInfo.getMaterialDesc());

			System.out.println("getMaterialAddDesc "
					+ materialInfo.getMaterialAddDesc());
			System.out.println("getEndEffectiveDt "
					+ materialInfo.getEndEffectiveDt());
			System.out.println("getStartEffectiveDt "
					+ materialInfo.getStartEffectiveDt());

			System.out.println("getCommodityName "
					+ materialInfo.getCommodityName());
			System.out.println("getBaseUnitOfMeasure "
					+ materialInfo.getBaseUnitOfMeasure());
			System.out.println("getMaterialType "
					+ materialInfo.getMaterialType()); // size 4
			System.out.println("getMaterialGroup "
					+ materialInfo.getMaterialGroup()); // size 8
			System.out.println("getAltUnitOfMeasure "
					+ materialInfo.getAltUnitOfMeasure());

			materialInformationRq_Type.setMaterialCode(materialInfo
					.getMaterialCode());
			materialInformationRq_Type.setCodeType(materialInfo.getCodeType());
			materialInformationRq_Type.setActionCode(materialInfo
					.getActionCode());
			materialInformationRq_Type.setMaterialDesc(materialInfo
					.getMaterialDesc());

			materialInformationRq_Type.setMaterialAddDesc(materialInfo
					.getMaterialAddDesc());
			materialInformationRq_Type.setEndEffectiveDt(materialInfo
					.getEndEffectiveDt());
			materialInformationRq_Type.setStartEffectiveDt(materialInfo
					.getStartEffectiveDt());

			materialInformationRq_Type.setCommodityName(materialInfo
					.getCommodityName());
			materialInformationRq_Type.setBaseUnitOfMeasure(materialInfo
					.getBaseUnitOfMeasure());
			materialInformationRq_Type.setMaterialType(materialInfo
					.getMaterialType()); // size 4
			materialInformationRq_Type.setMaterialGroup(materialInfo
					.getMaterialGroup()); // size 8
			materialInformationRq_Type.setAltUnitOfMeasure(materialInfo
					.getAltUnitOfMeasure());

			epmfRq_Type.setMasterDataMaterialInfoRq(materialInformationRq_Type);
			epmfRq_Type.setRqHeader(rqHeader_Type);

			/*
			 * MasterDataMaterialInfoPortTypeProxy infoPortTypeProxy = new
			 * MasterDataMaterialInfoPortTypeProxy(); try {
			 * my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRs_Type
			 * epmfRs_Type = infoPortTypeProxy.inquire(epmfRq_Type);
			 * System.out.println
			 * ("getStatusCode :"+epmfRs_Type.getRsHeader().getStatus
			 * ().getStatusCode());
			 * System.out.println("getStatusDesc :"+epmfRs_Type
			 * .getRsHeader().getStatus().getStatusDesc()); } catch
			 * (RemoteException e) { e.printStackTrace(); }
			 */
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
		System.out.println("List: " + list.size());
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

	public void masterDataVendorGfmasOutbound() {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "23-04-2015";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SupplierSearchResDTO resDTO = supplierSearchSO.findSupplierforMasterDataVendor(date);
		List<MasterDataVendor> dataVendors = null;//new ArrayList<MasterDataVendor>();
		List<BankAccountInformation> accountInformations = null; //new ArrayList<BankAccountInformation>();
		if (resDTO.isSuccessful()) {
			dataVendors = new ArrayList<MasterDataVendor>();
			MasterDataVendor dataVendor = null;
			BankAccountInformation accountInformation = null;

			for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
				
				if(smSupplier.getSupplierId()!=279330){

				if (null != smSupplier.getSmAppl()) {

					dataVendor = new MasterDataVendor();
					dataVendor.setRecordType(0);
					dataVendor.setAgencyId(smSupplier.getSupplierId().toString());
					dataVendor.setVendorIdCode(smSupplier.getEpNo());
					dataVendor.setEntityCode(getEntityType(smSupplier.getBusinessType())); //default set to SSM   

					dataVendor.setEntityCode(new GfmasIntegration().getEntityType(smSupplier.getBusinessType()));
					
					if (null != smSupplier.getSmSupplierOwners()
							&& smSupplier.getSmSupplierOwners().size() > 0) {
						for (SmSupplierOwner supplierOwner : smSupplier
								.getSmSupplierOwners()) {
							if (supplierOwner.getOwnerName().length() > 35) {
								dataVendor.setName(supplierOwner.getOwnerName().substring(0, 34));
								dataVendor.setName2(supplierOwner.getOwnerName().substring(35));
							} else {
								dataVendor.setName(supplierOwner.getOwnerName());
								dataVendor.setName2(IntegrationConstants.EMPTY_TRING);
							}

						}
					}
//					if (null != smSupplier.getSmSupplierBusinesses()
//							&& smSupplier.getSmSupplierBusinesses().size() > 0) {
//
//					}
					if (null != smSupplier.getSmAppl().getSmPersonnels()) {
						dataVendor.setEmailAddress(smSupplier.getSmAppl().getSmPersonnels().get(0).getEmail());
						dataVendor.setGlReconAccount(IntegrationConstants.GL_RECON_NO);
						dataVendor.setCurrency(IntegrationConstants.CURRENCY_CODE_MY);
						dataVendor.setIncoTerm(IntegrationConstants.EMPTY_TRING);
						dataVendor.setPaymentTerm(IntegrationConstants.EMPTY_TRING);
						dataVendor.setStatus(smSupplier.getRecordStatusFlg());
						dataVendor.setMobileNo(smSupplier.getSmAppl().getSmPersonnels().get(0).getMobileNo());
					}
					if (null != smSupplier.getSmAppl().getSmCompanyBasic()) {
						if (null != smSupplier.getSmAppl().getSmCompanyBasic()
								.getSmCompanyAddresses()
								&& smSupplier.getSmAppl().getSmCompanyBasic()
										.getSmCompanyAddresses().size() > 0) {
							if (null != smSupplier.getSmAppl()
									.getSmCompanyBasic()
									.getSmCompanyAddresses().get(0)
									.getSmAddress()) {

								dataVendor.setAddress(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getAddress1());
								dataVendor.setAddress2(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getAddress2());
								dataVendor.setAddress3(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getAddress3());
								dataVendor.setPostCode(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getPostcode());
								dataVendor.setCity(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getPmCity().getCityName());
								dataVendor.setRegion(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getPmState().getStateCode());
								dataVendor.setCountry(smSupplier.getSmAppl()
										.getSmCompanyBasic()
										.getSmCompanyAddresses().get(0)
										.getSmAddress().getPmCountry().getCountryCode());
								dataVendor.setTelephoneNo(smSupplier
										.getSmAppl().getSmCompanyBasic()
										.getPhoneNo());
								dataVendor.setFaxNo(smSupplier.getSmAppl()
										.getSmCompanyBasic().getFaxNo());
							} else {
								dataVendor.setAddress(IntegrationConstants.EMPTY_TRING);
								dataVendor.setAddress2(IntegrationConstants.EMPTY_TRING);
								dataVendor.setAddress3(IntegrationConstants.EMPTY_TRING);
								dataVendor.setPostCode(IntegrationConstants.EMPTY_TRING);
								dataVendor.setCity(IntegrationConstants.EMPTY_TRING);
								dataVendor.setRegion(IntegrationConstants.EMPTY_TRING);
								dataVendor.setCountry(IntegrationConstants.EMPTY_TRING);
								dataVendor.setTelephoneNo(IntegrationConstants.EMPTY_TRING);
								dataVendor.setFaxNo(IntegrationConstants.EMPTY_TRING);
							}
						}

					}

					if (null != smSupplier.getSmAppl().getSmSupplierBanks()
							&& smSupplier.getSmAppl().getSmSupplierBanks().size() > 0) {
						accountInformations = new ArrayList<BankAccountInformation>();

						dataVendor.setBankAccountInfoCount(smSupplier.getSmAppl().getSmSupplierBanks().size());
						System.out.println("size of accounts :"+smSupplier.getSmAppl().getSmSupplierBanks().size());
						int i = 1;
						for (SmSupplierBank smSupplierBank : smSupplier.getSmAppl().getSmSupplierBanks()) {
							accountInformation = new BankAccountInformation();
							accountInformation.setRecordType(1);
							accountInformation.setBankAccount(smSupplierBank.getAccountNo());
							accountInformation.setAccountHolderName(smSupplier.getCompanyName());
							accountInformation.setBankCountry(IntegrationConstants.COUNTRY_CODE_MY);
//							accountInformation.setBankCountry(smSupplierBank.getPmFinancialOrg().getPmCountry().getCountryCode());
							accountInformation.setCurrency(IntegrationConstants.CURRENCY_CODE_MY);
							
							if (null != smSupplierBank.getPmFinancialOrg()) {
								System.out.println("getPmFinancialOrg :"+smSupplierBank.getPmFinancialOrg().getFinancialOrgId());
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
							if(!dataVendors.contains(dataVendor))
								dataVendors.add(dataVendor);
						}
					}

				} else {
					System.out.println("un successful...");
				}
			}

			}
			System.out.println("dataVendors :"+dataVendors.size());
			fileUtility.masterDataVendorFileOutPutStream(dataVendors,IntegrationConstants.FILE_PATH);
			
		}
		
	}

	public void masterDataVendorGfmasInbound() {
		System.out.println("inside masterDataVendorGfmasInbound");

		String fileName = null;// sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,
								// "APOVE");

		if (osbUtility.osbServiceCallInBound("GFM-370")) {
			fileName = sftpUtility.findFileNameInDirectory(
					IntegrationConstants.GMAS_DOWNLOAD, "APOVE");
			System.out.println("fileName  :" + fileName);
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH
					+ fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,
						IntegrationConstants.GMAS_ARCHIVE + fileName);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH
						+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH
						+ fileName);
				List<MasterDataVendor> dataVendors = fileUtility
						.masterDataVendorFileInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH
								+ fileName);
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

		} else {
			System.out.println("file not available in soa integration server");
		}
	}

	public void pejabatPerakaunanGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLBAC");
//		if (osbUtility.osbServiceCallInBound("GFM-190")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLBAC40000120150331001.GPG";
				List<AgOffice> agOfficeList = fileUtility.pejabatPerakaunanFileInPutStream(filePath+ fileName);
				AgOfficeReqDTO reqDTO = null;
				PmAgOffice agOffice = null;

				for (AgOffice office : agOfficeList) {
					reqDTO = new AgOfficeReqDTO();
					reqDTO.setAgOfficeCode(office.getCode());
					AgOfficeResDTO agOfficeDTO = agOfficeSO.retrieveValidAgOfficeByOfficeCode(reqDTO);
					if (office.getCode().equals(agOfficeDTO.getAgOffice().getOfficeCode())) {
						System.out.println("Duplicate Code");
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
//			}
//		}
	}

	public void pegawaiPengawalGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLSEG");
//		if (osbUtility.osbServiceCallInBound("GFM-200")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName, IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLSEG40000120150331001.GPG";
				List<AgCtrlOfficer> agOfficeList = fileUtility.pegawaiPengawalInPutStream(filePath + fileName);
				AgOfficeReqDTO reqDTO = null;
				PmAgCtrlOfficer agControlOfficer = null;
				for (AgCtrlOfficer office : agOfficeList) {
					reqDTO = new AgOfficeReqDTO();
					reqDTO.setAgOfficerCode(office.getCode());
					AgOfficeResDTO agOfficeDTO = agOfficeSO.retrieveValidAgOfficerByOfficeCode(reqDTO);
					if (office.getCode().equals(agOfficeDTO.getAgControlOfficer().getCtrlOfficerCode())) {
						System.out.println("Duplicate Code");
					} else {

						agControlOfficer = new PmAgCtrlOfficer();
						agControlOfficer.setCtrlOfficerCode(office.getCode());
						agControlOfficer.setDescription(office.getDescription());
						agControlOfficer.setEffDate(new Timestamp(System.currentTimeMillis()));
						agControlOfficer.setCreatedBy(1L);
						agControlOfficer.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						agControlOfficer.setRecordStatusFlg(1);
						reqDTO.setAgControlOfficer(agControlOfficer);
						agOfficeSO.saveOrUpdatePmAgCtrlOfficer(reqDTO);
					}
				}
//			}
//		}
	}

	public void votGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLVOT");
//		if (osbUtility.osbServiceCallInBound("GFM-210")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLVOT40000120150331001.GPG";
				List<VotFund> votList = fileUtility.votInPutStream(filePath+fileName);
				VotFundReqDTO reqDTO = null;
				PmVotFund pmVot = null;

				for (VotFund vot : votList) {
					reqDTO = new VotFundReqDTO();
					reqDTO.setVotFundCode(vot.getCode());
					VotFundResDTO votFundDTO = votFundSO.retrieveValidVotFundByVotCode(reqDTO);							
					
					if (vot.getCode().equals(votFundDTO.getPmVotFund().getVotFundCode())) {
						System.out.println(" Vot Duplicate Code");
					} else {
					pmVot = new PmVotFund();
					
					OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
					
					Long orgProfileId = Long.parseLong(vot.getPtjCode());
					
					System.out.println("orgProfileId :"+orgProfileId);
					orgProfileReqDTO.setOrgProfileId(orgProfileId);
					OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);

//					PtjGroupReqDTO ptjReqDTO = new PtjGroupReqDTO();
//
//					ptjReqDTO.setPtjGroupCode(vot.getPtjCode());
//					PtjGroupResDTO ptjResDTO = ptgGroupSO.retrievePtjGroupByCode(ptjReqDTO);   // with code it shd be there before
//					PmPtjGroup pmPtjGroup = new PmPtjGroup();
//					pmPtjGroup.setPtjGroupId(ptjResDTO.getPtjGroup().get(0).getPtjGroupId());   // here cmng multiple
//					pmVot.setPmPtjGroup(pmPtjGroup);
					pmVot.setPmOrgProfile(orgProfileResDTO.getPmOrgProfile());
					pmVot.setVotFundCode(vot.getCode());
					pmVot.setDescription(vot.getDescription());
					pmVot.setEffDate(new Timestamp(System.currentTimeMillis()));
					pmVot.setCreatedBy(1L);
					pmVot.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					pmVot.setRecordStatusFlg(1);
					reqDTO.setVotFund(pmVot);
					votFundSO.saveOrUpdateVotFund(reqDTO);
					}
				}
//			}
//		}
	}

	public void votDanaGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLDNA");

//		if (osbUtility.osbServiceCallInBound("GFM-220")) {
//
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLDNA40000120150331001.GPG";
				List<VotFund> votList = fileUtility.votDanaInPutStream(filePath	+ fileName);

				VotFundReqDTO reqDTO = null;
				PmVotFund pmVot = null;

				for (VotFund vot : votList) {
					reqDTO = new VotFundReqDTO();
					reqDTO.setVotFundCode(vot.getCode());
					VotFundResDTO votFundDTO = votFundSO.retrieveValidVotFundDanaByVotCode(reqDTO);							
					if (vot.getCode().equals(votFundDTO.getPmVotFund().getVotFundCode())) {
						System.out.println("Vot Dana Duplicate Code");
					} else {
					pmVot = new PmVotFund();
					pmVot.setVotFundCode(vot.getCode());
					pmVot.setDescription(vot.getDescription());
					pmVot.setVotFundType("A");
					pmVot.setEffDate(new Timestamp(System.currentTimeMillis()));
					pmVot.setCreatedBy(1L);
					pmVot.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					pmVot.setRecordStatusFlg(1);
					reqDTO.setVotFund(pmVot);
					votFundSO.saveOrUpdateVotFund(reqDTO);
					}
				}
//			}
//		}
	}

	public void programAktivitiGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPRG");
//		if (osbUtility.osbServiceCallInBound("GFM-230")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLPRG40000120150331001.GPG";
				List<ProgramActivity> votList = fileUtility.programAktiviti(filePath + fileName);
				ProgramActivityReqDTO reqDTO = null;
				PmPrgActivity prgActivity = null;

				for (ProgramActivity programActivity : votList) {
					reqDTO = new ProgramActivityReqDTO();
					prgActivity = new PmPrgActivity();
					reqDTO.setCode(programActivity.getCode());
					ProgramActivityResDTO prgResDTO = programActivitySO.retrieveValidPrgActivityByPrgActivityCode(reqDTO);							
					if (programActivity.getCode().equals(
							prgResDTO.getPrgActivity().getPrgActivityCode())) {
						System.out.println(" Prg Duplicate Code");
					} else {
					
					VotFundReqDTO votreqDTO = new VotFundReqDTO();
					votreqDTO.setVotFundCode(programActivity.getVotCode());
					VotFundResDTO restDTO = votFundSO.retrieveVotFundByVotCode(votreqDTO);
					prgActivity.setPmVotFund(restDTO.getPmVotFund());
					prgActivity.setPrgActivityCode(programActivity.getCode());
					prgActivity.setDescription(programActivity.getDescription());
					prgActivity.setEffDate(new Timestamp(System.currentTimeMillis()));
					prgActivity.setCreatedBy(1L);
					prgActivity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					prgActivity.setRecordStatusFlg(1);
					reqDTO.setPrgActivity(prgActivity);
					programActivitySO.saveOrUpdatePrgActivity(reqDTO);
					}
				}
//			}
//		}
	}

	public void projectGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPRJ");
//		if (osbUtility.osbServiceCallInBound("GFM-240")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLPRJ40000120150331001.GPG";
				List<Project> projectList = fileUtility.project(filePath+fileName);
				ProjectSetiaReqDTO reqDTO = null;

				for (Project project : projectList) {
					reqDTO = new ProjectSetiaReqDTO();
					reqDTO.setProjectCode(project.getCode());
					ProjectSetiaResDTO proResDTO = projectSetiaSO.retrieveValidProjectByProjectCode(reqDTO);							
					if (project.getCode().equals(proResDTO.getPmProject().getProjectCode())) {
						System.out.println(" Project Duplicate Code");
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
					subSitia.setEffDate(new Timestamp(System.currentTimeMillis()));
					subSitia.setRecordStatusFlg(1);
					subSitia.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					subSitia.setChangedBy(1L);
					subSitia.setCreatedBy(1L);

					reqDTO.setSubSetia(subSitia);
					ProjectSetiaResDTO subresDTO = projectSetiaSO.saveOrUpdateSubSetia(reqDTO);
					subresDTO.setSuccessful(true);
				}
				}
//			}
//		}
	}

	public void kodAkaunGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLGLC");
//		if (osbUtility.osbServiceCallInBound("GFM-260")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLGLC40000120150331001.GPG";
				List<GlAccount> glAccountList = fileUtility.kodAkaun(filePath + fileName);
				GlAccountReqDTO reqDTO = null;
				PmGlAccount glAccount = null;
				for (GlAccount account : glAccountList) {
					reqDTO = new GlAccountReqDTO();
					glAccount = new PmGlAccount();
					
					reqDTO.setGlAccountCode(account.getCode());
					GlAccountResDTO glAccountDTO = glAccountSO.retrieveValidGlAccountByglAccountCode(reqDTO);
							
					if (account.getCode().equals(glAccountDTO.getGlAccount().getGlAccCode())) {
						System.out.println("Duplicate Code");
					} else {
					glAccount.setGlAccCode(account.getCode());
					glAccount.setDescription(account.getDescription());
					
					if (!account.getPtjCode().equalsIgnoreCase("00")) {
						PtjGroupReqDTO ptjReqDTO = new PtjGroupReqDTO();   
						ptjReqDTO.setPtjGroupCode(account.getPtjCode());
						PtjGroupResDTO ptjResDTO = ptgGroupSO.retrievePtjGroupByCode(ptjReqDTO);
						PmPtjGroup pmPtjGroup = new PmPtjGroup();
						pmPtjGroup.setPtjGroupId(ptjResDTO.getPtjGroup().get(0).getPtjGroupId());
						glAccount.setPmPtjGroup(pmPtjGroup);
						
						OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
						
						Long orgProfileId = Long.parseLong(account.getPtjCode());
						
						System.out.println("orgProfileId :"+orgProfileId);
						orgProfileReqDTO.setOrgProfileId(orgProfileId);
						OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);
						glAccount.setPmOrgProfile(orgProfileResDTO.getPmOrgProfile());
					}
				
					glAccount.setEffDate(new Timestamp(System.currentTimeMillis()));
					glAccount.setRecordStatusFlg(1);
					glAccount.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					glAccount.setChangedBy(1L);
					glAccount.setCreatedBy(1L);
					reqDTO.setGlAccount(glAccount);
					GlAccountResDTO resDTO = glAccountSO.saveOrUpdateGlAccount(reqDTO);
					resDTO.setSuccessful(true);
					System.out.println("Saved Data into GLAccont");
					}
				}
//			}
//		}
	}

	public void kumpulanPtjGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPCG");
//		if (osbUtility.osbServiceCallInBound("GFM-270")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLPCG40000120150331001.GPG";
				List<PtjGroup> glAccountList = fileUtility.kumpulanPTJ(filePath	+fileName);
				PtjGroupReqDTO reqDTO = null;
				PmPtjGroup ptjGroup = null;

				for (PtjGroup group : glAccountList) {
					reqDTO = new PtjGroupReqDTO();
					reqDTO.setPtjGroupCode(group.getCode());
					PtjGroupResDTO ptjGroupt = ptgGroupSO.retrieveValidPtjGroupByCode(reqDTO);
					if (group.getCode().equals(	ptjGroupt.getPmPtjGroup().getGroupCode())) {
						System.out.println("Duplicate Code");
					} else {
						ptjGroup = new PmPtjGroup();
						ptjGroup.setGroupCode(group.getCode());
						ptjGroup.setGroupName(group.getDescription());
						ptjGroup.setEffDate(new Timestamp(System.currentTimeMillis()));
						ptjGroup.setRecordStatusFlg(1);
						ptjGroup.setCreatedDate(new Timestamp(System.currentTimeMillis()));
						ptjGroup.setChangedBy(1L);
						ptjGroup.setCreatedBy(1L);
						reqDTO.setPmPtjGroup(ptjGroup);
						ptgGroupSO.saveOrUpdatePtjGroup(reqDTO);
					}

				}

//			}

//		}
	}

	public void masterDataPtjGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPTJ");
//		if (osbUtility.osbServiceCallInBound("GFM-280")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000GLPTJ40000120150331001.GPG";
				List<OrgProfile> orgProfileList = fileUtility.masterDatePTJ(filePath + fileName);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				PmOrgProfile pmOrgProfile = new PmOrgProfile();

				for (OrgProfile orgProfile : orgProfileList) {

					PmOrgValidity orgValidity = new PmOrgValidity();
					orgValidity.setOrgCode(orgProfile.getCode());
					orgValidity.setOrgName(orgProfile.getDescription());
					orgValidity.setEffDate(new Timestamp(System.currentTimeMillis()));
					orgValidity.setRecordStatusFlg(1);
					orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					orgValidity.setChangedBy(1L);
					orgValidity.setCreatedBy(1L);
					orgValidity.setBrandNew(true);

					orgValidity.setPmOrgProfile(pmOrgProfile);
				//	pmOrgProfile.setPmOrgValidity(orgValidity);
					
//					PtjGroupReqDTO prtReqDTO = new PtjGroupReqDTO();
//					prtReqDTO.setPtjGroupCode(orgProfile.getPtjCode());
//					PtjGroupResDTO dto = ptgGroupSO.retrievePtjGroupByCode(prtReqDTO);
//					PmPtjGroup ptjGroup = new PmPtjGroup();
//					ptjGroup = dto.getPmPtjGroup();
//					
//					pmOrgProfile.setPmPtjGroup(ptjGroup);  
					
					OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
					
					Long orgProfileId = Long.parseLong(orgProfile.getPtjCode());
					
					System.out.println("orgProfileId :"+orgProfileId);
					orgProfileReqDTO.setOrgProfileId(orgProfileId);
					OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgWithSupplierByProfileId(orgProfileReqDTO);

					pmOrgProfile.setParentOrgProfile(orgProfileResDTO.getPmOrgProfile());
					
					
					AgOfficeReqDTO agOfficeReqDTO = new AgOfficeReqDTO();
					AgOfficeReqDTO agCtrOfficeReqDTO = new AgOfficeReqDTO();
					agCtrOfficeReqDTO.setAgOfficerCode(orgProfile.getCtrOfficerCode());
					agOfficeReqDTO.setAgOfficerCode(orgProfile.getAgOfficeCode());
					AgOfficeResDTO agOfficers = agOfficeSO.retrieveAgOfficerByOfficerCode(agCtrOfficeReqDTO);
					AgOfficeResDTO agOfficee = agOfficeSO.retrieveAgOfficeByOfficeCode(agOfficeReqDTO);

					List<PmAgCtrlOfficer> agOfficer = agOfficers.getAgControlOfficerList();
					List<PmAgOffice> agOffice = agOfficee.getAgOfficeList();

					// System.out.println("AgOfficerID:"+
					// agOfficer.get(0).getAgCtrlOfficerId());
					// System.out.println("AgOfficeid"+
					// agOffice.get(0).getOfficeName());
					// agOfficer = agOfficers.getAgControlOfficer();
					// System.out.println("AgOfficerID :::"+agOfficers.getAgControlOfficer().getAgCtrlOfficerId());
					// orgProfile.setPmAgCtrlOfficer(agOfficer);
					// orgProfile.setPmAgOffice(agOffice);

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
					reqDTO.setPmOrgProfile(pmOrgProfile);
					OrgProfileResDTO resDTO = orgProfileSO.saveOrUpdateOrgProfile(reqDTO);
					resDTO.setSuccessful(true);
				}
//			}
//		}
	}

	public void kodBankGfmasInbound() {

//		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "CMBNK");
//		if (osbUtility.osbServiceCallInBound("GFM-360")) {
//			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
//				Encryption encryption = new Encryption();
//				encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
				String filePath = "C:/Integration/Download/";
				String fileName = "1000CMBNK40000120150331001.GPG";
				List<BankAccountInformation> bankList = fileUtility.kodBank(filePath + fileName);

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
					FinancialOrgResDTO resDTO = financialOrgSO.saveOrUpdateFinancialOrg(reqDTO);
					resDTO.setSuccessful(true);
				}
//			}
//		}
	}
	public void paymentGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "AP511");
		if (osbUtility.osbServiceCallInBound("GFM-140")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
				sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,IntegrationConstants.GMAS_ARCHIVE + fileName);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<PaymentInfo> paymentInfoList = fileUtility.paymentInfo(IntegrationConstants.FILE_PATH + fileName);
//				List<PaymentInfo> paymentInfoList = fileUtility.paymentInfo("C:/Integration/Encryption/decrypt/1004AP51140000120150311003.GPG");
				FLOsbReqDTO reqDTO = null;

				for (PaymentInfo bank : paymentInfoList) {
					reqDTO = new FLOsbReqDTO();
					reqDTO.setInvoiceNoList(bank.getInvoiceArray());
					reqDTO.setPaymentDate(bank.getPaymentDate());
					reqDTO.setPaymentRefNo(bank.getPaymentRefNo());
					flOsbSO.updatePaymentDetails(reqDTO);
				}

			}

		}
	}
	public void softcertNotifications() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "12-03-2015";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		SupplierSearchResDTO resDTO = supplierSearchSO.findSupplierforSoftCertNotify(date);
		if(resDTO.isSuccessful()){
			for(SmSupplier smSupplier : resDTO.getSmSuppliers()){
				for(SmPersonnel personnel:smSupplier.getSmAppl().getSmPersonnels()){
					System.out.println(" personnel.getMobileNo() :"+personnel.getMobileNo());
					smsUtility.sendSMS(personnel.getMobileNo());
				}
			}
		}else{
			System.out.println("un successful...");
		}
	
		
	}
	
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
