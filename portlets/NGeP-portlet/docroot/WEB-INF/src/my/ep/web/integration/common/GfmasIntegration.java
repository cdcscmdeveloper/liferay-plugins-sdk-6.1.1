package my.ep.web.integration.common;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import my.ep.dto.osb.fl.FLOsbReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupReqDTO;
import my.ep.dto.pm.authorization.ptjgroup.PtjGroupResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmAgCtrlOfficer;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmPtjGroup;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmSupplier;
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
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeProxy;
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoRq_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqHeader_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqUID_Type;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class GfmasIntegration {
	
	FileUtility fileUtility = new FileUtility();
	SFTPUtility sftpUtility = new SFTPUtility();
	OsbUtility osbUtility = new OsbUtility();
	IAgOfficeSO agOfficeSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAgOfficeSO.class);
	IVotFundSO votFundSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IVotFundSO.class);
	IProgramActivitySO programActivitySO = BusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProgramActivitySO.class);
	IProjectSetiaSO projectSetiaSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProjectSetiaSO.class);
	IGlAccountSO glAccountSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IGlAccountSO.class);
	IPtjGroupSO ptgGroupSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IPtjGroupSO.class);
	IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);
	IFinancialOrgSO financialOrgSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
	IParameterSO parameterSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);
	IFLOsbSO flOsbSO = BusinessDelegate
			.getInstance()
			.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFLOsbSO.class);

	public void masterDataMaterialInfoGfmas() {
		List<MasterDataMaterialInfo> materilInfoList = fileUtility.setMasterDataMaterialInfo();

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

			MasterDataMaterialInfoPortTypeProxy infoPortTypeProxy = new MasterDataMaterialInfoPortTypeProxy();
			try {
				infoPortTypeProxy.inquire(epmfRq_Type);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	public void sstInformationGfmas() {
		IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

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

		AwardSupplierResDTO resDTO = qtAwardSO
				.retrieveLoaListByDateRange(reqDTO);

		List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
		System.out.println("List: " + list.size());
		FileUtility utility = new FileUtility();
		List<ScLoa> scLoaList = resDTO.getLoaResultBean().getResultList();
		List<SSTInformation> sstInformations = new ArrayList<SSTInformation>();
		SSTInformation sstInformation;
		for (ScLoa scLoa : scLoaList) {
			sstInformation = new SSTInformation();
			sstInformation.setRecordType(0);
			sstInformation.setSSTNumber(scLoa.getLoaId().toString());
			sstInformation.setPhysicalSSTNumber(scLoa.getLoaNo());
			sstInformation.setSapVendorCode(scLoa.getScLoiLoa().getSmSupplier()
					.getSapVendorCode());
			sstInformation.setKumpPtj(scLoa.getScLoiLoa().getPmOrgProfile()
					.getPmOrgValidity().getOrgCode());
			sstInformation.setSstAcknowledgeDate(utility.formatDate(scLoa
					.getAckDate()));
			sstInformations.add(sstInformation);
		}

		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.sstInformationFileOutPutStream(sstInformations, filePath);

	}

	public void masterDataVendorGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"");
		ISupplierSearchSO supplierSearchSO = BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

		if (osbUtility.osbServiceCallInBound("GFM-370")) {
			
		//	find out the file name here
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<MasterDataVendor> dataVendors = fileUtility
						.masterDataVendorFileInPutStream(IntegrationConstants.FILE_PATH + fileName);
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

		}

	}
	public void masterDataVendorGfmasOutbound() {
		String processId ="APIVE"; 
		
	}
	public void pejabatPerakaunanGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLBAC");
		if (osbUtility.osbServiceCallInBound("GFM-190")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<AgOffice> agOfficeList = fileUtility
						.pejabatPerakaunanFileInPutStream(IntegrationConstants.FILE_PATH + fileName);
				AgOfficeReqDTO reqDTO = null;
				PmAgOffice agOffice = null;

				for (AgOffice office : agOfficeList) {
					reqDTO = new AgOfficeReqDTO();
					agOffice = new PmAgOffice();
					agOffice.setOfficeCode(office.getCode());
					agOffice.setOfficeName(office.getDescription());
					agOffice.setEffDate(new Timestamp(System.currentTimeMillis()));
					//agOffice.setExpDate((Timestamp) new Date());
					agOffice.setCreatedBy(1L);
					agOffice.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					agOffice.setRecordStatusFlg(1);					
					reqDTO.setAgOffice(agOffice);
					agOfficeSO.saveOrUpdateAgOffice(reqDTO);

				}
				
			}

		}
	}
	public void pegawaiPengawalGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLSEG");
		if (osbUtility.osbServiceCallInBound("GFM-200")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<AgCtrlOfficer> agOfficeList = fileUtility
						.pegawaiPengawalInPutStream(IntegrationConstants.FILE_PATH + fileName);
				AgOfficeReqDTO reqDTO = null;
				PmAgCtrlOfficer agControlOfficer = null;

				for (AgCtrlOfficer office : agOfficeList) {
					reqDTO = new AgOfficeReqDTO();
					agControlOfficer = new PmAgCtrlOfficer();
					agControlOfficer.setCtrlOfficerCode(office.getCode());
					agControlOfficer.setDescription(office.getDescription());
					agControlOfficer.setExpDate((Timestamp) new Date());
					agControlOfficer.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setAgControlOfficer(agControlOfficer);
					agOfficeSO.saveOrUpdatePmAgCtrlOfficer(reqDTO);

				}
				
			}

		}
	}
	public void votGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLVOT");
		if (osbUtility.osbServiceCallInBound("GFM-210")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<VotFund> votList = fileUtility.votInPutStream(IntegrationConstants.FILE_PATH + fileName);
				VotFundReqDTO reqDTO = null;
				PmVotFund pmVot = null;

				for (VotFund vot : votList) {
					reqDTO = new VotFundReqDTO();
					pmVot = new PmVotFund();

					PtjGroupReqDTO ptjReqDTO = new PtjGroupReqDTO();

					ptjReqDTO.setPtjGroupCode(vot.getCode());
					PtjGroupResDTO ptjResDTO = ptgGroupSO
							.retrievePtjGroupByCode(ptjReqDTO);
					PmPtjGroup pmPtjGroup = new PmPtjGroup();
					pmPtjGroup.setPtjGroupId(ptjResDTO.getPtjGroup().get(0)
							.getPtjGroupId());
					pmVot.setVotFundCode(vot.getCode());
					pmVot.setDescription(vot.getDescription());
					pmVot.setPmPtjGroup(pmPtjGroup);
					pmVot.setExpDate((Timestamp) new Date());
					pmVot.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setVotFund(pmVot);
					votFundSO.saveOrUpdateVotFund(reqDTO);

				}
				
			}

		}
	}
	public void votDanaGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLDNA");
		if (osbUtility.osbServiceCallInBound("GFM-220")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<VotFund> votList = fileUtility.votDanaInPutStream(IntegrationConstants.FILE_PATH + fileName);
				VotFundReqDTO reqDTO = null;
				PmVotFund pmVot = null;

				for (VotFund vot : votList) {
					reqDTO = new VotFundReqDTO();
					pmVot = new PmVotFund();
					pmVot.setVotFundCode(vot.getCode());
					pmVot.setDescription(vot.getDescription());
					pmVot.setExpDate((Timestamp) new Date());
					pmVot.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setVotFund(pmVot);
					votFundSO.saveOrUpdateVotFund(reqDTO);

				}
				
			}

		}
	}
	public void programAktivitiGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLPRG");
		if (osbUtility.osbServiceCallInBound("GFM-230")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<ProgramActivity> votList = fileUtility.programAktiviti(IntegrationConstants.FILE_PATH + fileName);
				ProgramActivityReqDTO reqDTO = null;
				PmPrgActivity prgActivity = null;

				for (ProgramActivity programActivity : votList) {
					reqDTO = new ProgramActivityReqDTO();
					prgActivity = new PmPrgActivity();
					VotFundReqDTO votreqDTO = new VotFundReqDTO();					
					votreqDTO.setVotFundCode(programActivity.getVotCode());
					VotFundResDTO restDTO = votFundSO
							.retrieveVotFundByVotCode(votreqDTO);
					prgActivity.setPmVotFund(restDTO.getPmVotFund());
					prgActivity.setPrgActivityCode(programActivity.getCode());
					prgActivity.setDescription(programActivity.getDescription());
					prgActivity.setExpDate((Timestamp) new Date());
					prgActivity.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setPrgActivity(prgActivity);
					programActivitySO.saveOrUpdatePrgActivity(reqDTO);

				}
				
			}

		}
	}
	public void projectGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLPRJ");
		if (osbUtility.osbServiceCallInBound("GFM-240")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<Project> projectList = fileUtility.project(IntegrationConstants.FILE_PATH + fileName);
				ProjectSetiaReqDTO reqDTO = null;
				PmProject pmProject = null;

				for (Project project : projectList) {
					reqDTO = new ProjectSetiaReqDTO();
					pmProject = new PmProject();
					pmProject.setProjectCode(project.getCode());
					pmProject.setDescription(project.getDescription());
					pmProject.setExpDate((Timestamp) new Date());
					pmProject.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setProject(pmProject);
					projectSetiaSO.saveOrUpdateProject(reqDTO);

				}
				
			}

		}
	}
	public void kodAkaunGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLGLC");
		if (osbUtility.osbServiceCallInBound("GFM-260")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<GlAccount> glAccountList = fileUtility.kodAkaun(IntegrationConstants.FILE_PATH + fileName);
				GlAccountReqDTO reqDTO = null;
				PmGlAccount glAccount = null;

				for (GlAccount account : glAccountList) {
					reqDTO = new GlAccountReqDTO();
					glAccount = new PmGlAccount();
					glAccount.setGlAccCode(account.getCode());
					glAccount.setDescription(account.getDescription());
					glAccount.setExpDate((Timestamp) new Date());
					glAccount.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setGlAccount(glAccount);
					glAccountSO.saveOrUpdateGlAccount(reqDTO);

				}
				
			}

		}
	}
	public void kumpulanPtjGfmasInbound() {
		
		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLPCG");
		if (osbUtility.osbServiceCallInBound("GFM-270")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<PtjGroup> glAccountList = fileUtility.kumpulanPTJ(IntegrationConstants.FILE_PATH + fileName);
				PtjGroupReqDTO reqDTO = null;
				PmPtjGroup ptjGroup = null;

				for (PtjGroup group : glAccountList) {
					reqDTO = new PtjGroupReqDTO();
					ptjGroup = new PmPtjGroup();
					ptjGroup.setGroupCode(group.getCode());
					ptjGroup.setGroupName(group.getDescription());
					ptjGroup.setExpDate((Timestamp) new Date());
					ptjGroup.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setPmPtjGroup(ptjGroup);
					ptgGroupSO.saveOrUpdatePtjGroup(reqDTO);

				}
				
			}

		}
	}
	public void masterDataPtjGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"GLPTJ");
		if (osbUtility.osbServiceCallInBound("GFM-280")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<OrgProfile> orgProfileList = fileUtility.masterDatePTJ(IntegrationConstants.FILE_PATH + fileName);
				OrgProfileReqDTO reqDTO = null;
				PmOrgProfile pmOrgProfile = null;

				for (OrgProfile orgProfile : orgProfileList) {
					reqDTO = new OrgProfileReqDTO();
					pmOrgProfile = new PmOrgProfile();
					pmOrgProfile.getPmOrgValidity().setOrgCode(orgProfile.getCode());
					//pmOrgProfile.setGroupName(group.getDescription());
					//pmOrgProfile.setExpDate((Timestamp) new Date());
					pmOrgProfile.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setPmOrgProfile(pmOrgProfile);
					orgProfileSO.saveOrUpdateOrgProfile(reqDTO);

				}
				
			}

		}
	}
	public void kodBankGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"CMBNK");
		if (osbUtility.osbServiceCallInBound("GFM-360")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<BankAccountInformation> bankList = fileUtility.kodBank(IntegrationConstants.FILE_PATH + fileName);
				FinancialOrgReqDTO reqDTO = null;
				PmFinancialOrg pmFinancialOrg = null;

				for (BankAccountInformation bank : bankList) {
					reqDTO = new FinancialOrgReqDTO();
					pmFinancialOrg = new PmFinancialOrg();
					pmFinancialOrg.setFinOrgName(bank.getBankName());
					//pmOrgProfile.setGroupName(group.getDescription());
					//pmOrgProfile.setExpDate((Timestamp) new Date());
					pmFinancialOrg.setChangedBy(IntegrationConstants.CHANGED_BY);
					reqDTO.setPmFinancialOrg(pmFinancialOrg);
					financialOrgSO.saveOrUpdateFinancialOrg(reqDTO);

				}
				
			}

		}
	}
	public void paymentGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,"AP511");
		if (osbUtility.osbServiceCallInBound("GFM-140")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<PaymentInfo> paymentInfoList = fileUtility.paymentInfo(IntegrationConstants.FILE_PATH + fileName);
				FLOsbReqDTO reqDTO = null;
				

				for (PaymentInfo bank : paymentInfoList) {
					reqDTO = new FLOsbReqDTO();
					
					
					
					reqDTO.setInvoiceNoList(new String[]{"10000004371400210"});
					//reqDTO.setInvoiceNoList(new String[] { bank.getInoviceNumber() });
					reqDTO.setPaymentDate(bank.getPaymentDate());
					//reqDTO.setPaymentRefNo(row.getPaymentRefNum());
					flOsbSO.updatePaymentDetails(reqDTO);

				}
				
			}

		}
	}
}
