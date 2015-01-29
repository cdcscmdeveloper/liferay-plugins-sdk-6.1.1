package my.ep.web.integration.common;

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
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAgCtrlOfficer;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmPtjGroup;
import my.ep.persistence.entity.pm.common.PmSetia;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmSupplier;
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
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoPortTypeProxy;
import my.ep.web.integration.gfmas.masterdatamateriainfo.MasterDataMaterialInfoRq_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqHeader_Type;
import my.ep.web.integration.gfmas.masterdatamateriainfo.RqUID_Type;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

public class GfmasIntegration {

	FileUtility fileUtility = new FileUtility();
	SFTPUtility sftpUtility = new SFTPUtility();
	OsbUtility osbUtility = new OsbUtility();
	
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

	public void masterDataMaterialInfoGfmas() {
		List<MasterDataMaterialInfo> materilInfoList = fileUtility
				.setMasterDataMaterialInfo();

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
		System.out.println("inside masterDataVendorGfmasInbound");

		String fileName = null;// sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD,
								// "APOVE");
		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance()
				.getServiceObject("sm", ISupplierSearchSO.class);

		if (osbUtility.osbServiceCallInBound("GFM-370")) {
			fileName = sftpUtility.findFileNameInDirectory(
					IntegrationConstants.GMAS_DOWNLOAD, "APOVE");
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

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLBAC");
		if (osbUtility.osbServiceCallInBound("GFM-190")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<AgOffice> agOfficeList = fileUtility
						.pejabatPerakaunanFileInPutStream(IntegrationConstants.FILE_PATH
								+ fileName);
				AgOfficeReqDTO reqDTO = null;
				PmAgOffice agOffice = null;

				for (AgOffice office : agOfficeList) {
					reqDTO = new AgOfficeReqDTO();
					agOffice = new PmAgOffice();
					agOffice.setOfficeCode(office.getCode());
					agOffice.setOfficeName(office.getDescription());
					agOffice.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					agOffice.setCreatedBy(1L);
					agOffice.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					agOffice.setRecordStatusFlg(1);
					reqDTO.setAgOffice(agOffice);
					agOfficeSO.saveOrUpdateAgOffice(reqDTO);

				}

			}

		}
	}

	public void pegawaiPengawalGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLSEG");
		if (osbUtility.osbServiceCallInBound("GFM-200")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<AgCtrlOfficer> agOfficeList = fileUtility
						.pegawaiPengawalInPutStream(IntegrationConstants.FILE_PATH
								+ fileName);
				AgOfficeReqDTO reqDTO = null;
				PmAgCtrlOfficer agControlOfficer = null;

				for (AgCtrlOfficer office : agOfficeList) {
					reqDTO = new AgOfficeReqDTO();
					agControlOfficer = new PmAgCtrlOfficer();
					agControlOfficer.setCtrlOfficerCode(office.getCode());
					agControlOfficer.setDescription(office.getDescription());
					agControlOfficer.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					agControlOfficer.setCreatedBy(1L);
					agControlOfficer.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					agControlOfficer.setRecordStatusFlg(1);
					reqDTO.setAgControlOfficer(agControlOfficer);
					agOfficeSO.saveOrUpdatePmAgCtrlOfficer(reqDTO);

				}

			}

		}
	}

	public void votGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLVOT");
		if (osbUtility.osbServiceCallInBound("GFM-210")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<VotFund> votList = fileUtility
						.votInPutStream(IntegrationConstants.FILE_PATH
								+ fileName);
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
					pmVot.setEffDate(new Timestamp(System.currentTimeMillis()));
					pmVot.setPmPtjGroup(pmPtjGroup);
					pmVot.setCreatedBy(1L);
					pmVot.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					pmVot.setRecordStatusFlg(1);
					reqDTO.setVotFund(pmVot);
					votFundSO.saveOrUpdateVotFund(reqDTO);

				}

			}

		}
	}

	public void votDanaGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLDNA");
		if (osbUtility.osbServiceCallInBound("GFM-220")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<VotFund> votList = fileUtility
						.votDanaInPutStream(IntegrationConstants.FILE_PATH
								+ fileName);
				VotFundReqDTO reqDTO = null;
				PmVotFund pmVot = null;

				for (VotFund vot : votList) {
					reqDTO = new VotFundReqDTO();
					pmVot = new PmVotFund();
					pmVot.setVotFundCode(vot.getCode());
					pmVot.setDescription(vot.getDescription());
					pmVot.setVotFundType("A");
					pmVot.setEffDate(new Timestamp(System.currentTimeMillis()));
					pmVot.setCreatedBy(1L);
					pmVot.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					pmVot.setRecordStatusFlg(1);
					reqDTO.setVotFund(pmVot);
					votFundSO.saveOrUpdateVotFund(reqDTO);

				}

			}

		}
	}

	public void programAktivitiGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLPRG");
		if (osbUtility.osbServiceCallInBound("GFM-230")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<ProgramActivity> votList = fileUtility
						.programAktiviti(IntegrationConstants.FILE_PATH
								+ fileName);
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
					prgActivity
							.setDescription(programActivity.getDescription());
					prgActivity.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					prgActivity.setCreatedBy(1L);
					prgActivity.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					prgActivity.setRecordStatusFlg(1);
					reqDTO.setPrgActivity(prgActivity);
					programActivitySO.saveOrUpdatePrgActivity(reqDTO);

				}

			}

		}
	}

	public void projectGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLPRJ");
		if (osbUtility.osbServiceCallInBound("GFM-240")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<Project> projectList = fileUtility
						.project(IntegrationConstants.FILE_PATH + fileName);
				ProjectSetiaReqDTO reqDTO = null;
				PmProject pmProject = null;

				for (Project project : projectList) {
					reqDTO = new ProjectSetiaReqDTO();
					pmProject = new PmProject();
					pmProject.setProjectCode(project.getCode());
					pmProject.setDescription(project.getDescription());
					VotFundReqDTO votreqDTO = new VotFundReqDTO();
					votreqDTO.setVotFundCode(project.getVotCode());
					VotFundResDTO votresDTO = votFundSO
							.retrieveVotFundByVotCode(votreqDTO);
					pmProject.setPmVotFund(votresDTO.getPmVotFund());
					pmProject.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					pmProject.setCreatedBy(1L);
					pmProject.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					pmProject.setRecordStatusFlg(1);
					reqDTO.setProject(pmProject);
					ProjectSetiaResDTO resDTO = projectSetiaSO
							.saveOrUpdateProject(reqDTO);
					PmSetia setia = new PmSetia();
					setia.setPmProject(resDTO.getPmProject());
					setia.setSetiaCode(project.getSetiaCode());
					setia.setDescription(project.getDescription());
					setia.setEffDate(new Timestamp(System.currentTimeMillis()));
					setia.setRecordStatusFlg(1);
					setia.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					setia.setChangedBy(1L);
					setia.setCreatedBy(1L);
					reqDTO.setSetia(setia);
					ProjectSetiaResDTO setiaresDTO = projectSetiaSO
							.saveOrUpdateSetia(reqDTO);
					setiaresDTO.setSuccessful(true);
					reqDTO.setSetia(setia);
					PmSubSetia subSitia = new PmSubSetia();
					subSitia.setPmSetia(setiaresDTO.getPmSetia());
					subSitia.setSubSetiaCode(project.getSubSetiaCode());
					subSitia.setDescription(project.getDescription());
					PmParameter paymentType = new PmParameter();
					paymentType.setParameterCode("");
					subSitia.setFinancingType(paymentType);
					subSitia.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					subSitia.setRecordStatusFlg(1);
					subSitia.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					subSitia.setChangedBy(1L);
					subSitia.setCreatedBy(1L);

					reqDTO.setSubSetia(subSitia);
					ProjectSetiaResDTO subresDTO = projectSetiaSO
							.saveOrUpdateSubSetia(reqDTO);
					subresDTO.setSuccessful(true);

				}

			}

		}
	}

	public void kodAkaunGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLGLC");
		if (osbUtility.osbServiceCallInBound("GFM-260")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<GlAccount> glAccountList = fileUtility
						.kodAkaun(IntegrationConstants.FILE_PATH + fileName);
				GlAccountReqDTO reqDTO = null;
				PmGlAccount glAccount = null;

				for (GlAccount account : glAccountList) {
					reqDTO = new GlAccountReqDTO();
					glAccount = new PmGlAccount();
					glAccount.setGlAccCode(account.getCode());
					glAccount.setDescription(account.getDescription());
					if (!account.getPtjCode().equalsIgnoreCase("00")) {
						PtjGroupReqDTO ptjReqDTO = new PtjGroupReqDTO();

						ptjReqDTO.setPtjGroupCode(account.getPtjCode());
						PtjGroupResDTO ptjResDTO = ptgGroupSO
								.retrievePtjGroupByCode(ptjReqDTO);
						PmPtjGroup pmPtjGroup = new PmPtjGroup();
						pmPtjGroup.setPtjGroupId(ptjResDTO.getPtjGroup().get(0)
								.getPtjGroupId());
						glAccount.setPmPtjGroup(pmPtjGroup);
					}
					glAccount.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					glAccount.setRecordStatusFlg(1);
					glAccount.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					glAccount.setChangedBy(1L);
					glAccount.setCreatedBy(1L);
					reqDTO.setGlAccount(glAccount);
					glAccountSO.saveOrUpdateGlAccount(reqDTO);

				}

			}

		}
	}

	public void kumpulanPtjGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLPCG");
		if (osbUtility.osbServiceCallInBound("GFM-270")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<PtjGroup> glAccountList = fileUtility
						.kumpulanPTJ(IntegrationConstants.FILE_PATH + fileName);
				PtjGroupReqDTO reqDTO = null;
				PmPtjGroup ptjGroup = null;

				for (PtjGroup group : glAccountList) {
					reqDTO = new PtjGroupReqDTO();
					ptjGroup = new PmPtjGroup();
					ptjGroup.setGroupCode(group.getCode());
					ptjGroup.setGroupName(group.getDescription());
					ptjGroup.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					ptjGroup.setRecordStatusFlg(1);
					ptjGroup.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					ptjGroup.setChangedBy(1L);
					ptjGroup.setCreatedBy(1L);
					reqDTO.setPmPtjGroup(ptjGroup);
					ptgGroupSO.saveOrUpdatePtjGroup(reqDTO);

				}

			}

		}
	}

	public void masterDataPtjGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "GLPTJ");
		if (osbUtility.osbServiceCallInBound("GFM-280")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<OrgProfile> orgProfileList = fileUtility
						.masterDatePTJ(IntegrationConstants.FILE_PATH
								+ fileName);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				PmOrgProfile pmOrgProfile = new PmOrgProfile();

				for (OrgProfile orgProfile : orgProfileList) {

					PmOrgValidity orgValidity = new PmOrgValidity();
					orgValidity.setOrgCode(orgProfile.getCode());
					orgValidity.setOrgName(orgProfile.getDescription());
					orgValidity.setEffDate(new Timestamp(System
							.currentTimeMillis()));
					orgValidity.setRecordStatusFlg(1);
					orgValidity.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					orgValidity.setChangedBy(1L);
					orgValidity.setCreatedBy(1L);
					orgValidity.setBrandNew(true);

					orgValidity.setPmOrgProfile(pmOrgProfile);
					pmOrgProfile.setPmOrgValidity(orgValidity);
					PtjGroupReqDTO prtReqDTO = new PtjGroupReqDTO();
					prtReqDTO.setPtjGroupCode(orgProfile.getPtjCode());
					PtjGroupResDTO dto = ptgGroupSO
							.retrievePtjGroupByCode(prtReqDTO);
					PmPtjGroup ptjGroup = new PmPtjGroup();
					ptjGroup = dto.getPmPtjGroup();
					pmOrgProfile.setPmPtjGroup(ptjGroup);
					AgOfficeReqDTO agOfficeReqDTO = new AgOfficeReqDTO();
					AgOfficeReqDTO agCtrOfficeReqDTO = new AgOfficeReqDTO();
					agCtrOfficeReqDTO.setAgOfficerCode(orgProfile
							.getCtrOfficerCode());
					agOfficeReqDTO.setAgOfficerCode(orgProfile
							.getAgOfficeCode());
					AgOfficeResDTO agOfficers = agOfficeSO
							.retrieveAgOfficerByOfficerCode(agCtrOfficeReqDTO);
					AgOfficeResDTO agOfficee = agOfficeSO
							.retrieveAgOfficeByOfficeCode(agOfficeReqDTO);

					List<PmAgCtrlOfficer> agOfficer = agOfficers
							.getAgControlOfficerList();
					List<PmAgOffice> agOffice = agOfficee.getAgOfficeList();

					System.out.println("AgOfficerID:"
							+ agOfficer.get(0).getAgCtrlOfficerId());
					System.out.println("AgOfficeid"
							+ agOffice.get(0).getOfficeName());
					// agOfficer = agOfficers.getAgControlOfficer();
					// System.out.println("AgOfficerID :::"+agOfficers.getAgControlOfficer().getAgCtrlOfficerId());
					// orgProfile.setPmAgCtrlOfficer(agOfficer);
					// orgProfile.setPmAgOffice(agOffice);

					ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
					parameterReqDTO
							.setParameterCode(ParameterConst.ORG_TYPE_PTJ);
					ParameterResDTO parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);

					pmOrgProfile.setOrgTypeCode(parameterResDTO
							.getPmParameter());
					pmOrgProfile.setRecordStatusFlg(1);
					pmOrgProfile.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					pmOrgProfile.setChangedBy(1L);
					pmOrgProfile.setCreatedBy(1L);
					pmOrgProfile.setBrandNew(true);
					reqDTO.setPmOrgProfile(pmOrgProfile);
					OrgProfileResDTO resDTO = orgProfileSO
							.saveOrUpdateOrgProfile(reqDTO);
					resDTO.setSuccessful(true);

				}

			}

		}
	}

	public void kodBankGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "CMBNK");
		if (osbUtility.osbServiceCallInBound("GFM-360")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<BankAccountInformation> bankList = fileUtility
						.kodBank(IntegrationConstants.FILE_PATH + fileName);

				for (BankAccountInformation bank : bankList) {
					FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
					PmCountry defaultCountry = PmCommonUtil
							.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);

					PmFinancialOrg financialOrg = new PmFinancialOrg();
					financialOrg.setFinOrgName(bank.getBankName());
					financialOrg.setPmCountry(defaultCountry);
					List<PmFinancialSvc> financialServiceList = new ArrayList<PmFinancialSvc>();
					PmFinancialSvc financialService = new PmFinancialSvc();
					financialService.setBankCode(bank.getSwiftCode());
					IParameterSO parameterSO = (IParameterSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
					// parameterReqDTO.setParameterType(ParameterTypeConst.FINANCIAL_ORG_REG_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
					ParameterResDTO parameterResDTO = parameterSO
							.retrieveParameterByTypeAndCode(parameterReqDTO);
					financialService.setServiceTypeCode(parameterResDTO
							.getPmParameter());
					financialService.setRecordStatusFlg(1);
					financialService.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					financialService.setChangedBy(1L);
					financialService.setCreatedBy(1L);
					financialService.setBrandNew(true);
					financialServiceList.add(financialService);
					financialOrg.setRecordStatusFlg(1);
					financialOrg.setCreatedDate(new Timestamp(System
							.currentTimeMillis()));
					financialOrg.setChangedBy(1L);
					financialOrg.setCreatedBy(1L);
					financialOrg.setBrandNew(true);
					financialOrg.setPmFinancialSvcs(financialServiceList);
					financialService.setPmFinancialOrg(financialOrg);
					reqDTO.setPmFinancialOrg(financialOrg);
					FinancialOrgResDTO resDTO = financialOrgSO
							.saveOrUpdateFinancialOrg(reqDTO);
					resDTO.setSuccessful(true);

				}

			}

		}
	}

	public void paymentGfmasInbound() {

		String fileName = sftpUtility.findFileNameInDirectory(
				IntegrationConstants.GMAS_DOWNLOAD, "AP511");
		if (osbUtility.osbServiceCallInBound("GFM-140")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,
					IntegrationConstants.GMAS_DOWNLOAD)) {
				// sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(
						IntegrationConstants.ENCRYPTED_FILE_PATH,
						IntegrationConstants.DECRYPTED_FILE_PATH);
				List<PaymentInfo> paymentInfoList = fileUtility
						.paymentInfo(IntegrationConstants.FILE_PATH + fileName);
				FLOsbReqDTO reqDTO = null;

				for (PaymentInfo bank : paymentInfoList) {
					reqDTO = new FLOsbReqDTO();				
					reqDTO.setInvoiceNoList(bank.getInvoiceNoList());
					reqDTO.setPaymentDate(bank.getPaymentDate());
					reqDTO.setPaymentRefNo(bank.getPaymentRefNo());
					flOsbSO.updatePaymentDetails(reqDTO);
				}

			}

		}
	}
}
