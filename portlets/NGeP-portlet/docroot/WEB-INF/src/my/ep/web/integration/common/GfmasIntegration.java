package my.ep.web.integration.common;

import java.io.File;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.integration.gfmas.MasterDataMaterialInfo;
import my.ep.web.integration.gfmas.MasterDataVendor;
import my.ep.web.integration.gfmas.SSTInformation;
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

		String filePath = "C:/Integration/Encryption/decrypt/";// IntegrationConstants.FILE_PATH;
		String fileName = "1000APOVE40000120141113001.GPG";
		ISupplierSearchSO supplierSearchSO = BusinessDelegate
				.getInstance().getServiceObject("sm", ISupplierSearchSO.class);

		if (osbUtility.osbServiceCallInBound("GFM-370")) {
			if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH,IntegrationConstants.GMAS_DOWNLOAD)) {
			//	sftpUtility.move(remoteSrcFilePath, remoteDestFilePath);
				Encryption encryption = new Encryption();
				encryption.decryptGmas(new File(IntegrationConstants.ENCRYPTED_FILE_PATH), new File(IntegrationConstants.DECRYPTED_FILE_PATH));
				List<MasterDataVendor> dataVendors = fileUtility
						.masterDataVendorFileInPutStream(filePath + fileName);
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

//		String filePath = "C:/Integration/Encryption/decrypt/";// IntegrationConstants.FILE_PATH;
//		String fileName = "1000APOVE40000120141113001.GPG";
		

	}
	public void masterDataVendorGfmasOutbound() {
		String processId ="APIVE"; 
		
	}
}
