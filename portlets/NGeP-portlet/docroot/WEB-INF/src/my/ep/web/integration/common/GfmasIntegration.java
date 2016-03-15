package my.ep.web.integration.common;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import my.ep.biz.service.integration.common.JAXBUtility;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.osb.fl.FLOsbReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.requestitem.RequestItemReqDTO;
import my.ep.dto.sc.requestitem.RequestItemResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmInterfaceLog;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSapVendorCode;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.type.pm.VotFundType;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.integration.gfmas.AgCtrlOfficer;
import my.ep.web.integration.gfmas.AgOffice;
import my.ep.web.integration.gfmas.BankAccountInformation;
import my.ep.web.integration.gfmas.CompanyBranch;
import my.ep.web.integration.gfmas.EPErrorResponse;
import my.ep.web.integration.gfmas.ErrorDesc;
import my.ep.web.integration.gfmas.GFMASError;
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
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

/*
* author @P Ganga prasad
*/

@SuppressWarnings({"rawtypes","unused"})
public class GfmasIntegration extends InitializeUtilityClz{
	
	private static Logger log = EpLogger.getLogger(GfmasIntegration.class);

	// need to change the query for material info
	public void permanentMasterDataMaterialInfoGfmas() {

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setCreateDate(currentDate);
		CodificationResDTO resDTO = codificationSO.searchItemByCreateDate(reqDTO);
		List<MasterDataMaterialInfo> materilInfoList = null;

		if (null!=resDTO.getItemList() && resDTO.getItemList().size()>0) {
			
			materilInfoList = fileUtility.setGfmasMasterDataMaterialInfo(resDTO);
			EPMFRq_Type epmfRq_Type = new EPMFRq_Type();
			RqHeader_Type rqHeader_Type = new RqHeader_Type();
			rqHeader_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
			RqUID_Type rqUID_Type = new RqUID_Type(
					UUID.randomUUID().toString(),
					IntegrationConstants.EMPTY_TRING);
			rqHeader_Type.setUID(rqUID_Type);
			
			MasterDataMaterialInfoRq_Type materialInformationRq_Type = null;
			for (MasterDataMaterialInfo materialInfo : materilInfoList) {

				materialInformationRq_Type = new MasterDataMaterialInfoRq_Type();

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
					my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRs_Type epmfRs_Type = infoPortTypeProxy.inquire(epmfRq_Type);
					log.info("getStatusCode :" + epmfRs_Type.getRsHeader().getStatus().getStatusCode());
					log.info("getStatusDesc :"+ epmfRs_Type.getRsHeader().getStatus().getStatusDesc());
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		}else{
			System.out.println("masterDataMaterialInfoGfmas :Records not available");
		}
	}
	
	public void temporarMasterDataMaterialInfoGfmas() {

		RequestItemReqDTO reqDTO = new RequestItemReqDTO();

		reqDTO.setCreatedDate(currentDate);
		
		RequestItemResDTO resDTO  = reqItemSO.getTempRequestItemByCreateDate(reqDTO);
		
		List<MasterDataMaterialInfo> materilInfoList = null;

		if (resDTO.isSuccessful()) {
			
			materilInfoList = fileUtility.setGfmasTempMasterDataMaterialInfo(resDTO);
			
			EPMFRq_Type epmfRq_Type = new EPMFRq_Type();
			RqHeader_Type rqHeader_Type = new RqHeader_Type();
			rqHeader_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
			RqUID_Type rqUID_Type = new RqUID_Type(
					UUID.randomUUID().toString(),
					null);
			rqHeader_Type.setUID(rqUID_Type);
			
			MasterDataMaterialInfoRq_Type materialInformationRq_Type = null;
			for (MasterDataMaterialInfo materialInfo : materilInfoList) {

				materialInformationRq_Type = new MasterDataMaterialInfoRq_Type();


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
//				try {
//					my.ep.web.integration.gfmas.masterdatamateriainfo.EPMFRs_Type epmfRs_Type = infoPortTypeProxy.inquire(epmfRq_Type);
//					log.info("getStatusCode :" + epmfRs_Type.getRsHeader().getStatus().getStatusCode());
//					log.info("getStatusDesc :"+ epmfRs_Type.getRsHeader().getStatus().getStatusDesc());
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
			}
		}else{
			System.out.println("masterDataMaterialInfoGfmas :Records not available");
		}
	}

	@Override
	public void sstInformationGfmas() {

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setStartDate(currentDate);
		
		AwardSupplierResDTO resDTO = qtAwardSO.retrieveLoaListByDateRange(reqDTO);
		
		if(resDTO.isSuccessful()){
			List<SSTInformation> sstInformations = new ArrayList<SSTInformation>();
			SSTInformation sstInformation;
			for (ScLoa scLoa : resDTO.getLoaResultBean().getResultList()) {
				sstInformation = new SSTInformation();
				sstInformation.setRecordType(0);
				sstInformation.setSSTNumber(scLoa.getLoaNo());
				sstInformation.setPhysicalSSTNumber(scLoa.getScLoiLoa().getFileNo());
//				sstInformation.setSapVendorCode(scLoa.getScLoiLoa().getSmSupplier().getSapVendorCode());
				sstInformation.setKumpPtj(scLoa.getScLoiLoa().getPmOrgProfile().getPmOrgValidity().getOrgCode());
				sstInformation.setSstAcknowledgeDate(fileUtility.formatDate(scLoa.getAckDate()));
				sstInformation.setSstAmountwithGST(scLoa.getLoaAmt().add(scLoa.getGstAmt()!=null ? scLoa.getGstAmt() : new BigDecimal(0)));
				sstInformation.setSstAmountwithoutGST(scLoa.getLoaAmt());
//				sstInformation.setPerihalSST(scLoa.getScQt().getQtTitle());
				sstInformation.setPerihalSST("Title");
				if (null != scLoa.getScLoiLoa().getFileNo())
					sstInformations.add(sstInformation);
			}
			String filePath = IntegrationConstants.FILE_PATH_LOCAL;
			
			BatchReqDTO<SSTInformation> batchReqDTO = new BatchReqDTO<SSTInformation>();
			batchReqDTO.setFilePath(filePath);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(sstInformations);
			
//			 calling method to save data in files
			fileUtility.sstInformationFileOutPutStream(batchReqDTO);
			
			}else{
			log.info("Records not available.");
			BatchReqDTO batchReqDTO = new BatchReqDTO();
			batchReqDTO.setServiceCode(ServiceCode.AP516.value());
			batchReqDTO.setProcessId(ProcessCode.AP516.value());
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			insertRecordforEmptyDataOut(batchReqDTO);
		}

	}

	@Override
	public void masterDataVendorGfmasOutbound() {
		log.info("masterDataVendorGfmasOutbound : APIVE");
		Date currentDate = new Date();
		log.info("currentDate :"+currentDate);
		
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setCreateDate(currentDate);
		
		SupplierSearchResDTO resDTO = supplierSearchSO.findSupplierforMasterDataVendor(supplierSearchReqDTO);
		List<MasterDataVendor> dataVendors = null;
		List<BankAccountInformation> accountInformations = null; ;
		List<CompanyBranch> companyBranchs = null;
	
		if (resDTO.isSuccessful()) {
			dataVendors = new ArrayList<MasterDataVendor>();
			MasterDataVendor dataVendor = null;
			BankAccountInformation accountInformation = null;
			CompanyBranch companyBranch = null;
			
			for(SmSupplier smSupplier : resDTO.getSmSuppliers()){
				
				companyBranchs = new ArrayList<CompanyBranch>();
				dataVendor = new MasterDataVendor();
				dataVendor.setRecordType(IntegrationConstants.RECORD_TYPE_ZERO);
				dataVendor.setAgencyId(IntegrationConstants.AGENSI_ID_ePerolehan);  
				dataVendor.setVendorIdCode(smSupplier.getRegNo());
				dataVendor.setEntityCode(getEntityType(smSupplier.getBusinessType())); //default set to SSM   
				dataVendor.setEpNo(smSupplier.getEpNo());
				
				// HQ- Branch Information
				
				if (null != smSupplier.getSmAppl().getSmCompanyBasic()) {
					
					log.info("HQ Branch available...");
					companyBranch = new CompanyBranch();
							
					companyBranch.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);

					companyBranch.setName(smSupplier.getSmAppl().getSmCompanyBasic().getCompanyName());
					
					companyBranch.setEmailAddress(smSupplier.getSmAppl().getSmCompanyBasic().getWebsite());  
					companyBranch.setGlReconAccount(IntegrationConstants.GL_RECON_NO);
					companyBranch.setCurrency(IntegrationConstants.CURRENCY_CODE_RM);
					companyBranch.setIncoTerm(IntegrationConstants.EMPTY_TRING);
					companyBranch.setPaymentTerm(IntegrationConstants.EMPTY_TRING);
					companyBranch.setStatus(smSupplier.getRecordStatusFlg());
					companyBranch.setMobileNo(smSupplier.getSmAppl().getSmCompanyBasic().getPhoneNo());
							
					companyBranch.setIndicatorCode("0");
					companyBranch.setAccountActionIndicator(IntegrationConstants.ACCOUNT_ACTION_UPDATE);
					if(smSupplier.getSmGstSuppliers()!=null){
						 for(SmGstSupplier gstSupplier : smSupplier.getSmGstSuppliers()){
							 companyBranch.setReferenceNoGST(gstSupplier.getGstRegNo());
							 companyBranch.setGSTEffectiveDate(gstSupplier.getGstEffDate());
							 companyBranch.setGSTEffectiveEndDate(gstSupplier.getGstEndDate());
						 }
					}else{
						companyBranch.setReferenceNoGST(IntegrationConstants.EMPTY_TRING);
						companyBranch.setGSTEffectiveDate(IntegrationConstants.DEFAULT_DATE);
						companyBranch.setGSTEffectiveEndDate(IntegrationConstants.DEFAULT_DATE);
					}
							
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
							if(null!=smSupplier.getSmAppl().getSmCompanyBasic().getFaxNo())
								companyBranch.setFaxNo(smSupplier.getSmAppl().getSmCompanyBasic().getFaxNo());
							else
								companyBranch.setFaxNo(IntegrationConstants.EMPTY_TRING);
									
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
					
					// HQ Bank Information
					
					if (null != smSupplier.getSmAppl().getSmSupplierBanks()	&& !smSupplier.getSmAppl().getSmSupplierBanks().isEmpty()) {
						
						accountInformations = new ArrayList<BankAccountInformation>();

						companyBranch.setBankAccountInfoCount(smSupplier.getSmAppl().getSmSupplierBanks().size());
						log.info("size of accounts :"+smSupplier.getSmAppl().getSmSupplierBanks().size());
						int i = 1;
						for (SmSupplierBank smSupplierBank : smSupplier.getSmAppl().getSmSupplierBanks()) {
							accountInformation = new BankAccountInformation();
							accountInformation.setRecordType(IntegrationConstants.RECORD_TYPE_TWO);
							accountInformation.setBankAccount(smSupplierBank.getAccountNo());
							accountInformation.setBankCountry(smSupplierBank.getPmFinancialOrg().getPmCountry().getCountryCode());
							accountInformation.setCurrency(IntegrationConstants.CURRENCY_CODE_RM);
							accountInformation.setBankActionIndicator(IntegrationConstants.ACCOUNT_ACTION_UPDATE);
							
							if (null != smSupplierBank.getPmFinancialOrg()) {
								log.info("getFinancialOrgId :"+smSupplierBank.getPmFinancialOrg().getFinancialOrgId());
								if (null != smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs()){
									for(PmFinancialSvc pmFinancialSvc :smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs()){
										if(pmFinancialSvc.getBankCode()!=null)
											accountInformation.setBankKey(pmFinancialSvc.getBankCode());
									    }
									accountInformation.setRecordNumber(i);
									if(!accountInformations.contains(accountInformation))
										accountInformations.add(accountInformation);
									companyBranch.setBankAccountInfoList(accountInformations);
									i++;
								}
							}
						}
					}
					
					if(!companyBranchs.contains(companyBranch))
						companyBranchs.add(companyBranch);
				}
				
				// Branch Company Information
				
				if (null != smSupplier.getSmAppl().getSmSupplierBranches() && smSupplier.getSmAppl().getSmSupplierBranches().size()>0) {
					
					for(SmSupplierBranch smSupplierBranch:smSupplier.getSmAppl().getSmSupplierBranches()){
						
						companyBranch = new CompanyBranch();
						companyBranch.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
						
						companyBranch.setName(smSupplierBranch.getBranchName());
							
						if(null!=smSupplierBranch.getSmPersonnel())
							companyBranch.setEmailAddress(smSupplierBranch.getSmPersonnel().getEmail()); 
						else
							companyBranch.setEmailAddress(IntegrationConstants.EMPTY_TRING);
						
						companyBranch.setGlReconAccount(IntegrationConstants.GL_RECON_NO);
						companyBranch.setCurrency(IntegrationConstants.CURRENCY_CODE_RM);
						companyBranch.setIncoTerm(IntegrationConstants.EMPTY_TRING);
						companyBranch.setPaymentTerm(IntegrationConstants.EMPTY_TRING);
						companyBranch.setStatus(smSupplierBranch.getRecordStatusFlg());
						companyBranch.setMobileNo(smSupplierBranch.getPhoneNo());   
							
						companyBranch.setIndicatorCode(smSupplierBranch.getSupplierBranchId().toString());
						companyBranch.setAccountActionIndicator(IntegrationConstants.ACCOUNT_ACTION_UPDATE);
						companyBranch.setReferenceNoGST(IntegrationConstants.EMPTY_TRING);
						companyBranch.setGSTEffectiveDate(IntegrationConstants.DEFAULT_DATE);
						companyBranch.setGSTEffectiveEndDate(IntegrationConstants.DEFAULT_DATE);
							
						if(null!=smSupplierBranch.getSmAddress()){
							companyBranch.setAddress(smSupplierBranch.getSmAddress().getAddress1());
							companyBranch.setAddress2(smSupplierBranch.getSmAddress().getAddress2());
							companyBranch.setAddress3(smSupplierBranch.getSmAddress().getAddress3());
							companyBranch.setPostCode(smSupplierBranch.getSmAddress().getPostcode());
							companyBranch.setCity(smSupplierBranch.getSmAddress().getPmCity().getCityName());
							companyBranch.setRegion(smSupplierBranch.getSmAddress().getPmState().getStateCode());
							companyBranch.setCountry(smSupplierBranch.getSmAddress().getPmCountry().getCountryCode());
							companyBranch.setTelephoneNo(smSupplierBranch.getPhoneNo());
							if(null!=smSupplierBranch.getFaxNo())
								companyBranch.setFaxNo(smSupplierBranch.getFaxNo());
							else
								companyBranch.setFaxNo(IntegrationConstants.EMPTY_TRING);
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
							
						// Branch Account Information
						
						if (null != smSupplierBranch.getSmSupplierBanks()	&& !smSupplierBranch.getSmSupplierBanks().isEmpty()) {
							
							accountInformations = new ArrayList<BankAccountInformation>();

							companyBranch.setBankAccountInfoCount(smSupplierBranch.getSmSupplierBanks().size());
							log.info("size of accounts :"+smSupplier.getSmAppl().getSmSupplierBanks().size());
							int i = 1;
							for (SmSupplierBank smSupplierBank : smSupplierBranch.getSmSupplierBanks()) {
								accountInformation = new BankAccountInformation();
								accountInformation.setRecordType(IntegrationConstants.RECORD_TYPE_TWO);
								accountInformation.setBankAccount(smSupplierBank.getAccountNo());
								accountInformation.setBankCountry(smSupplierBank.getPmFinancialOrg().getPmCountry().getCountryCode());
								accountInformation.setCurrency(IntegrationConstants.CURRENCY_CODE_RM);
								accountInformation.setBankActionIndicator(IntegrationConstants.ACCOUNT_ACTION_UPDATE);
								
								if (null != smSupplierBank.getPmFinancialOrg()) {
									log.info("getFinancialOrgId :"+smSupplierBank.getPmFinancialOrg().getFinancialOrgId());
									if (null != smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs()){
										for(PmFinancialSvc pmFinancialSvc :smSupplierBank.getPmFinancialOrg().getPmFinancialSvcs()){
											if(pmFinancialSvc.getBankCode()!=null)
												accountInformation.setBankKey(pmFinancialSvc.getBankCode());
										    }
										accountInformation.setRecordNumber(i);
										if(!accountInformations.contains(accountInformation))
											accountInformations.add(accountInformation);
										companyBranch.setBankAccountInfoList(accountInformations);
										i++;
									}
								}
							}
						}
						if(!companyBranchs.contains(companyBranch))
							companyBranchs.add(companyBranch);
							}
					}
				dataVendor.setCompanyBranchs(companyBranchs);
				if(!dataVendors.contains(dataVendor))
					dataVendors.add(dataVendor);
						
				}
			BatchReqDTO<MasterDataVendor> batchReqDTO = new BatchReqDTO<MasterDataVendor>();
			batchReqDTO.setFilePath(IntegrationConstants.FILE_PATH_LOCAL);
			batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
			batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
			batchReqDTO.setBatchContent(dataVendors);
			
			fileUtility.masterDataVendorFileOutPutStream(batchReqDTO);
			
		}else{
				
				log.info("masterDataVendorGfmasOutbound(APIVE)  : Records not available ");
				BatchReqDTO batchReqDTO = new BatchReqDTO();
				batchReqDTO.setServiceCode(ServiceCode.APIVE.value());
				batchReqDTO.setProcessId(ProcessCode.APIVE.value());
				batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
				batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
				insertRecordforEmptyDataOut(batchReqDTO);
				
			}
		}
	
	@Override
	public void masterDataVendorGfmasInbound(String fileName,String transId) {
		log.info("inside masterDataVendorGfmasInbound : APOVE");
		String serviceCode = ServiceCode.APOVE.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;   
		/*if (osbUtility.osbServiceCallInBound(ServiceCode.APOVE.value(),"")) {
			Thread sleepThread =  sleepThread("APOVE",5000L);
			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, ProcessCode.APOVE.value());*/
				if(null!=fileName){
					if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.APOVE.value(), fileName)){
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					}else{
						if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
							sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
							encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					        
						  /* BatchReqDTO<MasterDataVendor> batchReqDTO = new BatchReqDTO<MasterDataVendor>();
						   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
						   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
						   batchReqDTO.setProcessId(ProcessCode.APOVE.value());
						   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
						   batchReqDTO.setFileName(fileName);
						   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
						   batchReqDTO.setServiceCode(ServiceCode.APOVE.value());
						   fileUtility.setValuesToPmInterface(batchReqDTO);*/
						
							List<MasterDataVendor> dataVendors = fileUtility.masterDataVendorFileInPutStream(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);

							SmSapVendorCode vendorCode = null;
							for (MasterDataVendor vendor : dataVendors) {
								
//								checking Supplier with RegNo
								
//								RegistrationReqDTO reqDTO = new RegistrationReqDTO();
//								reqDTO.setRegNo(vendor.getVendorIdCode());
//								RegistrationResDTO resDTO = registrationSO.retrieveActiveSupplierByRegNo(reqDTO);
								
								vendorCode = new SmSapVendorCode();
								vendorCode.setRegNo(vendor.getVendorIdCode());
								vendorCode.setSapVendorCode(vendor.getSapVendorCode());
								vendorCode.setCreatedBy(IntegrationConstants.BATCH_USER);
								vendorCode.setCreatedDate(new Timestamp(IntegrationConstants.DEFAULT_DATE.getTime()));
								vendorCode.setChangedBy(IntegrationConstants.BATCH_USER);
								vendorCode.setChangedDate(new Timestamp(IntegrationConstants.DEFAULT_DATE.getTime()));
								
								System.out.println("getVendorIdCode :"+vendor.getVendorIdCode() +"getSapVendorCode :"+vendor.getSapVendorCode());
								
								iVendorCodeSO.saveOrUpdateSapVendorCode(vendorCode);
						}
					}
				}
				log.info("masterDataVendorGfmasInbound updated ");
			}

//		} 
			else {
			log.info("file not available masterDataVendorGfmasInbound : APOVE");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
			BatchReqDTO batchReqDTO = new BatchReqDTO();
			batchReqDTO.setServiceCode(ServiceCode.APOVE.value());
			batchReqDTO.setProcessId(ProcessCode.APOVE.value());
			insertRecordforEmptyDataOut(batchReqDTO);
		}
	}
	
	
	// Every 5 minutes we have to pick file and insert into DB Master Data
	
	@Override
	public void pejabatPerakaunanGfmasInbound(String fileName,String transId) {

		log.info("inside pejabatPerakaunanGfmasInbound :GLBAC");
		try{
			String serviceCode = ServiceCode.GLBAC.value();
			log.info("interfaceSO :"+interfaceSO);
			PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
//			String fileName = null;
//			if (osbUtility.osbServiceCallInBound(ServiceCode.GLBAC.value(),"")) {
//				Thread sleepThread =  sleepThread("GLBAC",5000L);
//				fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, ProcessCode.GLBAC.value());
				if(null!=fileName){
					if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLBAC.value(), fileName)){
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					}else{
						if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
							sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
							encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
						   
							fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
							
						   	List<AgOffice> agOfficeList = fileUtility.pejabatPerakaunanFileInPutStream(IntegrationConstants.GMAS_DOWNLOAD_LOCAL+ fileName, fileName);
							
//						    BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
//						    batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
//							batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
//							batchReqDTO.setProcessId(ProcessCode.GLBAC.value());
//							batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
//							batchReqDTO.setFileName(fileName);
//							batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
//							batchReqDTO.setServiceCode(ServiceCode.GLBAC.value());
//							fileUtility.setValuesToPmInterface(batchReqDTO);
							   
						   	AgOfficeReqDTO reqDTO = null;
							PmAgOffice agOffice = null;

							for (AgOffice office : agOfficeList) {
									reqDTO = new AgOfficeReqDTO();
									agOffice = new PmAgOffice();
									agOffice.setOfficeCode(office.getCode());
									agOffice.setOfficeName(office.getDescription());
									agOffice.setEffDate(new Timestamp(System.currentTimeMillis()));
									agOffice.setCreatedBy(IntegrationConstants.BATCH_USER);
									agOffice.setCreatedDate(new Timestamp(System.currentTimeMillis()));
									agOffice.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
									reqDTO.setAgOffice(agOffice);
									agOfficeSO.saveOrUpdateAgOffice(reqDTO);
							}
						}
						log.info("pejabatPerakaunanGfmasInbound updated ");
					}
						
					}
//			}else
			{
				log.info("file not available pejabatPerakaunanGfmasInbound :GLBAC");
				if(null!=pmInterfaceLogUpdate){
					pmInterfaceLogUpdate.setFileName("N/A");
					pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
					interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
				}
				
//				BatchReqDTO batchReqDTO = new BatchReqDTO();
//				batchReqDTO.setServiceCode(ServiceCode.GLBAC.value());
//				batchReqDTO.setProcessId(ProcessCode.GLBAC.value());
//				insertRecordforEmptyDataIn(batchReqDTO);
			}
			
		}catch (Exception ex){
			System.out.println(ex);
			
		}
		finally{
			cleanUpResources();
		}
		
	}

	@Override
	public void pegawaiPengawalGfmasInbound(String fileName,String transId) {
		log.info("inside pegawaiPengawalGfmasInbound :GLSEG");
		String serviceCode = ServiceCode.GLSEG.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound(ServiceCode.GLSEG.value(),"")) {
//			Thread sleepThread = sleepThread("GLSEG",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, ProcessCode.GLSEG.value());
			if(null!=fileName){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLSEG.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName, IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
					   List<AgCtrlOfficer> agOfficeList = fileUtility.pegawaiPengawalInPutStream(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName, fileName);
					   
					   /*BatchReqDTO<AgCtrlOfficer> batchReqDTO = new BatchReqDTO<AgCtrlOfficer>();
					   batchReqDTO.setFileFTPStatus(1);
					   batchReqDTO.setFileCreationStatus(1);
					   batchReqDTO.setProcessId(ProcessCode.GLSEG.value());
					   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
					   batchReqDTO.setFileName(fileName);
					   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
					   batchReqDTO.setServiceCode(ServiceCode.GLSEG.value());
					   fileUtility.setValuesToPmInterface(batchReqDTO);*/
						
						OrgProfileReqDTO orgProfileReqDTO = null;

						for (AgCtrlOfficer office : agOfficeList) {
							
								orgProfileReqDTO = new OrgProfileReqDTO();
								
								PmOrgProfile pmOrgProfile = new PmOrgProfile();
								PmOrgValidity orgValidity = new PmOrgValidity();
								orgValidity.setOrgCode(office.getCode());
								orgValidity.setOrgName(office.getDescription());
								orgValidity.setEffDate(new Timestamp(System.currentTimeMillis()));
								orgValidity.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								orgValidity.setCreatedBy(IntegrationConstants.BATCH_USER);
								orgValidity.setPmOrgProfile(pmOrgProfile);
								
								ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
								parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
								parameterReqDTO.setParameterCode(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL);
								ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);

								pmOrgProfile.setGovtSeller(false);  // default
								pmOrgProfile.setGroupOrg(false);    // default
								
								pmOrgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
								pmOrgProfile.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								pmOrgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								pmOrgProfile.setCreatedBy(IntegrationConstants.BATCH_USER);
								pmOrgProfile.setBrandNew(true);
								
								pmOrgProfile.setPmOrgValidity(orgValidity);
								
								orgProfileReqDTO.setPmOrgProfile(pmOrgProfile);
								
								orgProfileSO.saveOrUpdateOrgProfile(orgProfileReqDTO);
							}
					}
					log.info("pegawaiPengawalGfmasInbound updated");
					}
				}
//			}else
			{
			log.info("file not available pegawaiPengawalGfmasInbound :GLSEG");
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLSEG.value());
//			batchReqDTO.setProcessId(ProcessCode.GLSEG.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	/**
	 * @return
	 */
	public Thread sleepThread(String threadName,long millis) {
		Thread sleepThread = Thread.currentThread();
		sleepThread.setName(threadName);
		if(sleepThread.isAlive()){
			try {
				System.out.println("sleepThread :"+sleepThread +" is sleeping ");
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return sleepThread;
	}

	@Override
	public void votGfmasInbound(String fileName, String transId) {
		log.info("inside votGfmasInbound :GLVOT");
		String serviceCode = ServiceCode.GLVOT.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		if (osbUtility.osbServiceCallInBound(ServiceCode.GLVOT.value(),"")) {
//			Thread sleepThread =  sleepThread("GLVOT",5000L);
//			String fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_UPLOAD, ProcessCode.GLVOT.value());
			if(null!=fileName){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLVOT.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_UPLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
					   /*BatchReqDTO<VotFund> batchReqDTO = new BatchReqDTO<VotFund>();
					   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
					   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
					   batchReqDTO.setProcessId(ProcessCode.GLVOT.value());
					   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
					   batchReqDTO.setFileName(fileName);
					   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
					   batchReqDTO.setServiceCode(ServiceCode.GLVOT.value());
					   fileUtility.setValuesToPmInterface(batchReqDTO);*/
					   
					   BatchResDTO<VotFund> batchResDTO = fileUtility.votInPutStream(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName, fileName);
					   EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
					   VotFundReqDTO reqDTO = null;
					   PmVotFund pmVot = null;
					   List<PmVotFund> votFundList = new ArrayList<PmVotFund>();

						for (VotFund vot : batchResDTO.getBatchResContent()) {
							reqDTO = new VotFundReqDTO();
							pmVot = new PmVotFund();
							OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
							orgProfileReqDTO = new OrgProfileReqDTO();
							orgProfileReqDTO.setOrgTypeCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
							orgProfileReqDTO.setOrgCode(vot.getPtjCode()); 
							
							OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgProfileReqDTO);
							if(orgProfileResDTO.isSuccessful()){
								pmVot.setPmOrgProfile(orgProfileResDTO.getPmOrgProfile());
								pmVot.setVotFundCode(vot.getVotCode());
								pmVot.setVotFundType(vot.getVotFundType());
								pmVot.setDescription(vot.getDescription());
								pmVot.setEffDate(vot.getEffectiveDate());
								pmVot.setExpDate(vot.getExpiryDate());
								pmVot.setCreatedBy(IntegrationConstants.BATCH_USER);
								pmVot.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								pmVot.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								votFundList.add(pmVot);
								}
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
						//send list of objects for updating or creating 
						reqDTO = new VotFundReqDTO();
						reqDTO.setVotFundList(votFundList);
						votFundSO.saveOrUpdateVotFundList(reqDTO);
						log.info("votGfmasInbound updated");
					}
				}
					
				}
//			}else
			{
			log.info("file not available votGfmasInbound :GLVOT");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLVOT.value());
//			batchReqDTO.setProcessId(ProcessCode.GLVOT.value());
//			insertRecordforEmptyDataOut(batchReqDTO);
	}
	}

	@Override
	public void votDanaGfmasInbound(String fileName,String transId) {
		log.info("inside votDanaGfmasInbound :GLDNA");
		String serviceCode = ServiceCode.GLDNA.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound(ServiceCode.GLDNA.value(),"")) {
//			Thread sleepThread =  sleepThread("GLDNA",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, ProcessCode.GLDNA.value());
			if(null!=fileName){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLDNA.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
						
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
					
					   /*BatchReqDTO<VotFund> batchReqDTO = new BatchReqDTO<VotFund>();
					   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
					   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
					   batchReqDTO.setProcessId(ProcessCode.GLDNA.value());
					   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
					   batchReqDTO.setFileName(fileName);
					   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
					   batchReqDTO.setServiceCode(ServiceCode.GLDNA.value());
					   fileUtility.setValuesToPmInterface(batchReqDTO);*/
					   
						
						 BatchResDTO<VotFund> batchResDTO = fileUtility.votDanaInPutStream(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName, fileName);
						 EPErrorResponse errorResponse = batchResDTO.getErrorResponse();

						List<PmVotFund> votFundList = new ArrayList<PmVotFund>();
						PmVotFund pmVot = null;

						for (VotFund vot : batchResDTO.getBatchResContent()) {
								pmVot = new PmVotFund();
								pmVot.setVotFundCode(vot.getVotCode());
								pmVot.setDescription(vot.getDescription());
								pmVot.setVotFundType(VotFundType.AMANAH.value());
								pmVot.setEffDate(vot.getEffectiveDate());
								pmVot.setExpDate(vot.getExpiryDate());
								pmVot.setPmOrgProfile(null);
								pmVot.setCreatedBy(IntegrationConstants.BATCH_USER);
								pmVot.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								pmVot.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								votFundList.add(pmVot);
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
						//send list of objects for updating or creating 
						VotFundReqDTO reqDTO = new VotFundReqDTO();
						reqDTO.setVotFundList(votFundList);
						votFundSO.saveOrUpdateVotFundList(reqDTO);
					}
					log.info("votDanaGfmasInbound updated");
				}
				}
//			}else
			{
			log.info("file not available votDanaGfmasInbound :GLDNA");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLDNA.value());
//			batchReqDTO.setProcessId(ProcessCode.GLDNA.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	@Override
	public void programAktivitiGfmasInbound(String fileName,String transId) {
		log.info("inside programAktivitiGfmasInbound :GLPRG");
		String serviceCode = ServiceCode.GLPRG.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound(ServiceCode.GLPRG.value(),"")) {
//			Thread sleepThread =  sleepThread("GLPRG",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, ProcessCode.GLPRG.value());
			if(null!=fileName){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLPRG.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
					  /* BatchReqDTO<ProgramActivity> batchReqDTO = new BatchReqDTO<ProgramActivity>();
					   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
					   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
					   batchReqDTO.setProcessId(ProcessCode.GLPRG.value());
					   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
					   batchReqDTO.setFileName(fileName);
					   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
					   batchReqDTO.setServiceCode(ServiceCode.GLPRG.value());
					   fileUtility.setValuesToPmInterface(batchReqDTO);*/
					
					   BatchResDTO<ProgramActivity> batchResDTO = fileUtility.programAktiviti(IntegrationConstants.GMAS_DOWNLOAD_LOCAL  + fileName, fileName);
					   EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
					   ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();;
					   PmPrgActivity prgActivity = null;
					   List<PmPrgActivity> prgActivitiesList = new ArrayList<PmPrgActivity>();

						for (ProgramActivity programActivity : batchResDTO.getBatchResContent()) {
							prgActivity = new PmPrgActivity();
							
							VotFundReqDTO votreqDTO = new VotFundReqDTO();
							votreqDTO.setVotFundCode(programActivity.getVotCode());
							VotFundResDTO restDTO = votFundSO.retrieveVotFundByVotCode(votreqDTO);
						
							prgActivity.setPmVotFund(restDTO.getPmVotFund());
							prgActivity.setPrgActivityCode(programActivity.getCode());
							prgActivity.setDescription(programActivity.getDescription());
							prgActivity.setEffDate(programActivity.getEffectiveDate());
							prgActivity.setExpDate(programActivity.getExpiryDate());
							prgActivity.setCreatedBy(IntegrationConstants.BATCH_USER);
							prgActivity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							prgActivity.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
							prgActivitiesList.add(prgActivity);
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
						reqDTO.setPrgActivityList(prgActivitiesList);
						programActivitySO.saveOrUpdatePrgActivityList(reqDTO);
					}
					log.info("programAktivitiGfmasInbound updated");
					}
				}
//			}else
			{
			log.info("file not available programAktivitiGfmasInbound :GLPRG");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLPRG.value());
//			batchReqDTO.setProcessId(ProcessCode.GLPRG.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	@Override
	public void projectGfmasInbound(String fileName,String transId) {
		log.info("inside projectGfmasInbound :GLPRJ");
		String serviceCode = ServiceCode.GLPRJ.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;;
//		if (osbUtility.osbServiceCallInBound("GFM-240","")) {
//			Thread sleepThread =  sleepThread("GLPRJ",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPRJ");
			if(null!=fileName){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLPRJ.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					 
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
					  /* BatchReqDTO<Project> batchReqDTO = new BatchReqDTO<Project>();
					   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
					   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
					   batchReqDTO.setProcessId(ProcessCode.GLPRJ.value());
					   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
					   batchReqDTO.setFileName(fileName);
					   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
					   batchReqDTO.setServiceCode(ServiceCode.GLPRJ.value());
					   fileUtility.setValuesToPmInterface(batchReqDTO);*/
					
					   BatchResDTO<Project> batchResDTO = fileUtility.project(IntegrationConstants.GMAS_DOWNLOAD_LOCAL +fileName, fileName);
					   EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
					   ProjectSetiaReqDTO reqDTO = null;
					   PmSubSetia subSitia = null;
					   List<PmSubSetia> subSetiaList = new ArrayList<PmSubSetia>();

						for (Project project : batchResDTO.getBatchResContent()) {
								subSitia = new PmSubSetia();
							
								subSitia.setSubSetiaCode(project.getSubSetiaCode());
								subSitia.setDescription(project.getDescription());
								subSitia.setProjectCode(project.getCode());
								subSitia.setSetiaCode(project.getSetiaCode());
								
								VotFundReqDTO votreqDTO = new VotFundReqDTO();
								votreqDTO.setVotFundCode(project.getVotCode());
								VotFundResDTO restDTO = votFundSO.retrieveVotFundByVotCode(votreqDTO);
								subSitia.setPmVotFund(restDTO.getPmVotFund());
								if (project.getPembiayaan() != null) {
									ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
									parameterReqDTO.setParameterCode(project.getPembiayaan());
									parameterReqDTO.setParameterType(ParameterTypeConst.FINANCING_TYPE);
									ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
									subSitia.setFinancingType(parameterResDTO.getPmParameter());
								}
								subSitia.setEffDate(project.getEffectiveDate());
								subSitia.setExpDate(project.getExpiryDate());
								subSitia.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								subSitia.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								subSitia.setCreatedBy(IntegrationConstants.BATCH_USER);

								subSetiaList.add(subSitia);
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
						reqDTO = new ProjectSetiaReqDTO();
						reqDTO.setSubSetiaList(subSetiaList);
						projectSetiaSO.saveOrUpdateSubSetiaList(reqDTO);
					}
					log.info("projectGfmasInbound updated");
					}
				}
//			}else
			{
			log.info("file not available projectGfmasInbound :GLPRJ");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLPRJ.value());
//			batchReqDTO.setProcessId(ProcessCode.GLPRJ.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	@Override
	public void kodAkaunGfmasInbound(String fileName,String transId) {

		log.info("inside kodAkaunGfmasInbound :GLGLC");
		String serviceCode = ServiceCode.GLGLC.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound("GFM-260","")) {
//			Thread sleepThread =  sleepThread("GLGLC",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLGLC");
			if(null!=fileName){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLGLC.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
					   /*BatchReqDTO<Project> batchReqDTO = new BatchReqDTO<Project>();
					   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
					   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
					   batchReqDTO.setProcessId(ProcessCode.GLGLC.value());
					   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
					   batchReqDTO.setFileName(fileName);
					   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
					   batchReqDTO.setServiceCode(ServiceCode.GLGLC.value());
					   fileUtility.setValuesToPmInterface(batchReqDTO);*/
					   
					   BatchResDTO<GlAccount> batchResDTO = fileUtility.kodAkaun(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName, fileName);
						EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
						GlAccountReqDTO reqDTO = new GlAccountReqDTO();
						PmGlAccount glAccount = null;
						List<PmGlAccount> glAccountList = new ArrayList<PmGlAccount>();
						for (GlAccount account : batchResDTO.getBatchResContent()) {
							glAccount = new PmGlAccount();
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
									glAccount.setPmOrgProfile(null);
								}
								glAccount.setEffDate(new Timestamp(System.currentTimeMillis()));
								glAccount.setExpDate(null);
								glAccount.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								glAccount.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								glAccount.setCreatedBy(IntegrationConstants.BATCH_USER);
								glAccountList.add(glAccount);
					}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
						reqDTO.setGlAccountList(glAccountList);
						glAccountSO.saveOrUpdateGlAccountList(reqDTO);
				}
				log.info("kodAkaunGfmasInbound updated");
				}
				}
//		}else
		{
			log.info("file not available kodAkaunGfmasInbound :GLGLC");
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLGLC.value());
//			batchReqDTO.setProcessId(ProcessCode.GLGLC.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	@Override
	public void kumpulanPtjGfmasInbound(String fileName,String transId) {
		log.info("inside kumpulanPtjGfmasInbound :GLPCG");
		String serviceCode = ServiceCode.GLPCG.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound(ServiceCode.GLPCG.value(),"")) {
//			Thread sleepThread =  sleepThread("GLPCG",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPCG");
			if(fileName!=null){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLPCG.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
						
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
						BatchResDTO<PtjGroup> batchResDTO = fileUtility.kumpulanPTJ(IntegrationConstants.GMAS_DOWNLOAD_LOCAL	+fileName, fileName);
						EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
						
						/*BatchReqDTO<PtjGroup> batchReqDTO = new BatchReqDTO<PtjGroup>();
						batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
						batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
						batchReqDTO.setProcessId(ProcessCode.GLPCG.value());
						batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
						batchReqDTO.setFileName(fileName);
						batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
						batchReqDTO.setServiceCode(ServiceCode.GLPCG.value());
						fileUtility.setValuesToPmInterface(batchReqDTO);*/
					
						List<PmOrgProfile> pmOrgProfileList = new ArrayList<PmOrgProfile>();

						for (PtjGroup group : batchResDTO.getBatchResContent()) {
							
								PmOrgProfile pmOrgProfile = new PmOrgProfile();
								
								PmOrgValidity orgValidity = new PmOrgValidity();
								orgValidity.setOrgCode(group.getCode());
								orgValidity.setOrgName(group.getDescription());
								orgValidity.setEffDate(group.getEffectiveDate());
								orgValidity.setExpDate(group.getExpiryDate());
								orgValidity.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								orgValidity.setCreatedBy(IntegrationConstants.BATCH_USER);
								orgValidity.setBrandNew(true);
								orgValidity.setPmOrgProfile(pmOrgProfile);
														
								ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
								parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
								parameterReqDTO.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
								ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);

								pmOrgProfile.setGovtSeller(false);  // default
								pmOrgProfile.setGroupOrg(false);    // default
								
								pmOrgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
								pmOrgProfile.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								pmOrgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								pmOrgProfile.setCreatedBy(IntegrationConstants.BATCH_USER);
								pmOrgProfile.setBrandNew(true);
								
								pmOrgProfile.setPmOrgValidity(orgValidity);
								
								if(!pmOrgProfileList.contains(pmOrgProfile))
									pmOrgProfileList.add(pmOrgProfile);
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
						
//						send batch update statements to update or insert
						orgProfileSO.saveOrUpdateOrgProfileList(pmOrgProfileList);
						log.info("kumpulanPtjGfmasInbound updated");
					}
				}
			}
//		}else
		{
		log.info("file not available kumpulanPtjGfmasInbound :GLPCG");
		
		if(null!=pmInterfaceLogUpdate){
			pmInterfaceLogUpdate.setFileName("N/A");
			pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
			interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
		}
//		BatchReqDTO batchReqDTO = new BatchReqDTO();
//		batchReqDTO.setServiceCode(ServiceCode.GLPCG.value());
//		batchReqDTO.setProcessId(ProcessCode.GLPCG.value());
//		insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	@Override
	public void masterDataPtjGfmasInbound(String fileName,String transId) {
		log.info("inside masterDataPtjGfmasInbound :GLPTJ");
		String serviceCode = ServiceCode.GLPTJ.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName =  null;
//		if (osbUtility.osbServiceCallInBound("GFM-280","")) {
//			Thread sleepThread =  sleepThread("GLPTJ",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "GLPTJ");
			if(fileName!=null){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.GLPTJ.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
						
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
						/* BatchReqDTO<PtjGroup> batchReqDTO = new BatchReqDTO<PtjGroup>();
						 batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
						 batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
						 batchReqDTO.setProcessId(ProcessCode.GLPTJ.value());
						 batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
						 batchReqDTO.setFileName(fileName);
						 batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
						 batchReqDTO.setServiceCode(ServiceCode.GLPTJ.value());
						 fileUtility.setValuesToPmInterface(batchReqDTO);*/
						
						 BatchResDTO<OrgProfile> batchResDTO = fileUtility.masterDataPTJ(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName, fileName);
						 EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
						 PmOrgProfile pmOrgProfile = null;
						 List<PmOrgProfile> pmOrgProfileList = new ArrayList<PmOrgProfile>();

						for (OrgProfile orgProfile : batchResDTO.getBatchResContent()) {
							
								pmOrgProfile = new PmOrgProfile();

								PmOrgValidity orgValidity = new PmOrgValidity();
								orgValidity.setOrgCode(orgProfile.getPtjCode()+orgProfile.getCode());
								orgValidity.setOrgName(orgProfile.getDescription());
								orgValidity.setEffDate(orgProfile.getEffectiveDate());
								orgValidity.setExpDate(orgProfile.getExpiryDate());
								orgValidity.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								orgValidity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								orgValidity.setCreatedBy(IntegrationConstants.BATCH_USER);
								orgValidity.setBrandNew(true);
								orgValidity.setPmOrgProfile(pmOrgProfile);

								pmOrgProfile.setPmOrgValidity(orgValidity);
								
								// set Kumpulan ptj
								OrgProfileReqDTO kumpReqDTO = new OrgProfileReqDTO();
								kumpReqDTO.setOrgTypeCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
								kumpReqDTO.setOrgCode(orgProfile.getPtjCode()); 
								
								OrgProfileResDTO kumResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(kumpReqDTO);
								pmOrgProfile.setParentOrgProfile(kumResDTO.getPmOrgProfile());

								// Set AgOffice
								
								AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
								reqDTO.setAgOfficeCode(orgProfile.getAgOfficeCode());
								AgOfficeResDTO agOfficeDTO = agOfficeSO.retrieveValidAgOfficeByOfficeCode(reqDTO);
//								pmOrgProfile.setPmAgOffice(agOfficeDTO.getAgOffice());
								orgValidity.setPmAgOffice(agOfficeDTO.getAgOffice());

								ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
								parameterReqDTO.setParameterType(ParameterTypeConst.ORGANISATION_TYPES);
								parameterReqDTO.setParameterCode(ParameterConst.ORG_TYPE_PTJ);
								ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);

								pmOrgProfile.setOrgTypeCode(parameterResDTO.getPmParameter());
								pmOrgProfile.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
								pmOrgProfile.setCreatedDate(new Timestamp(System.currentTimeMillis()));
								pmOrgProfile.setCreatedBy(IntegrationConstants.BATCH_USER);
								pmOrgProfile.setBrandNew(true);
								if(!pmOrgProfileList.contains(pmOrgProfile))
									pmOrgProfileList.add(pmOrgProfile);
//							}
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
//						send batch update statements to update or insert
						orgProfileSO.saveOrUpdateOrgProfileList(pmOrgProfileList);
					}
						log.info("masterDataPtjGfmasInbound updated");
					}
				}
//		}else
		{
			log.info("file not available masterDataPtjGfmasInbound :GLPTJ");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.GLPTJ.value());
//			batchReqDTO.setProcessId(ProcessCode.GLPTJ.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}

	@Override
	public void kodBankGfmasInbound(String fileName,String transId) {
		log.info("inside kodBankGfmasInbound :CMBNK");
		String serviceCode = ServiceCode.CMBNK.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound("GFM-360","")) {
//			Thread sleepThread =  sleepThread("CMBNK",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "CMBNK");
			if(fileName!=null){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.CMBNK.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL	+ fileName, IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
						
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
						   /*BatchReqDTO<BankAccountInformation> batchReqDTO = new BatchReqDTO<BankAccountInformation>();
						   batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
						   batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
						   batchReqDTO.setProcessId(ProcessCode.CMBNK.value());
						   batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
						   batchReqDTO.setFileName(fileName);
						   batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
						   batchReqDTO.setServiceCode(ServiceCode.CMBNK.value());
						   fileUtility.setValuesToPmInterface(batchReqDTO);*/
				
						   BatchResDTO<BankAccountInformation> batchResDTO = fileUtility.kodBank(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName, fileName);
						   EPErrorResponse errorResponse = batchResDTO.getErrorResponse();
						   List<PmFinancialOrg> pmFinancialOrgList = new ArrayList<PmFinancialOrg>();
						   
						for (BankAccountInformation bank : batchResDTO.getBatchResContent()) {
							PmCountry defaultCountry = PmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);

							PmFinancialOrg financialOrg = new PmFinancialOrg();
							financialOrg.setFinOrgName(bank.getBankName());
							financialOrg.setPmCountry(defaultCountry);
							
							List<PmFinancialSvc> financialServiceList = new ArrayList<PmFinancialSvc>();
							
							PmFinancialSvc financialService = new PmFinancialSvc();
							financialService.setBankCode(bank.getSwiftCode());
							ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
							parameterReqDTO.setParameterType(ParameterTypeConst.BANK_SERVICES);
							parameterReqDTO.setParameterCode(ParameterConst.BANK_SERVICES_PAYMENT_BANK);
							ParameterResDTO parameterResDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
						
							financialService.setServiceTypeCode(parameterResDTO.getPmParameter());
							financialService.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
							financialService.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							financialService.setCreatedBy(IntegrationConstants.BATCH_USER);
							financialService.setBrandNew(true);
							financialServiceList.add(financialService);
							
							financialOrg.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
							financialOrg.setCreatedDate(new Timestamp(System.currentTimeMillis()));
							financialOrg.setCreatedBy(IntegrationConstants.BATCH_USER);
							financialOrg.setBrandNew(true);
							financialOrg.setPmFinancialSvcs(financialServiceList);
							financialService.setPmFinancialOrg(financialOrg);
							if(!pmFinancialOrgList.contains(financialOrg))
								pmFinancialOrgList.add(financialOrg);
						}
						if(errorResponse!=null){
							fileUtility.errorFileOutBound(errorResponse);
						}
//						send batch update statements to update or insert
						financialOrgSO.saveOrUpdateFinancialOrgList(pmFinancialOrgList);
					}
					log.info("kodBankGfmasInbound updated");
				}
				}
//		}else
		{
			log.info("file not available kodBankGfmasInbound :CMBNK");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.CMBNK.value());
//			batchReqDTO.setProcessId(ProcessCode.CMBNK.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
		}
	}
	
	@Override
	public void paymentGfmasInbound(String fileName,String transId) {
		log.info("inside paymentGfmasInbound :AP511");
		String serviceCode = ServiceCode.AP511.value();
		PmInterfaceLog pmInterfaceLogUpdate = interfaceSO.retrieveInterfaceLogByServiceCodeAndTransId(serviceCode, transId);
		
//		String fileName = null;
//		if (osbUtility.osbServiceCallInBound("GFM-140","")) {
//			Thread sleepThread =  sleepThread("AP511",5000L);
//			fileName = sftpUtility.findFileNameInDirectory(IntegrationConstants.GMAS_DOWNLOAD, "AP511");
			if(fileName!=null){
				if(interfaceSO.retrieveInterfaceLogByServiceCodeAndFileName(ServiceCode.AP511.value(), fileName)){
					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);					
				}else{
					if (sftpUtility.download(IntegrationConstants.FILE_PATH_LOCAL,IntegrationConstants.GMAS_DOWNLOAD)) {
						sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,IntegrationConstants.GMAS_ARCHIVE + fileName);
						encryption.decryptGmas(IntegrationConstants.FILE_PATH_LOCAL,IntegrationConstants.GMAS_DOWNLOAD_LOCAL);
						
						fileUtility.deleteFilefromAppServer(IntegrationConstants.FILE_PATH_LOCAL+ fileName);
						
						/*BatchReqDTO<PaymentInfo> batchReqDTO = new BatchReqDTO<PaymentInfo>();
						batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
						batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
						batchReqDTO.setProcessId(ProcessCode.AP511.value());
						batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
						batchReqDTO.setFileName(fileName);
						batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
						batchReqDTO.setServiceCode(ServiceCode.AP511.value());
						fileUtility.setValuesToPmInterface(batchReqDTO);*/
						   
						List<PaymentInfo> paymentInfoList = fileUtility.paymentInfo(IntegrationConstants.GMAS_DOWNLOAD_LOCAL + fileName);
					
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
				}
//			}else
			{
			log.info("file not available paymentGfmasInbound :AP511");
			
			if(null!=pmInterfaceLogUpdate){
				pmInterfaceLogUpdate.setFileName("N/A");
				pmInterfaceLogUpdate.setCreatedDate(new Timestamp(new Date().getTime()));
				interfaceSO.updateInterfaceLOg(pmInterfaceLogUpdate);
			}
//			BatchReqDTO batchReqDTO = new BatchReqDTO();
//			batchReqDTO.setServiceCode(ServiceCode.AP511.value());
//			batchReqDTO.setProcessId(ProcessCode.AP511.value());
//			insertRecordforEmptyDataIn(batchReqDTO);
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
	

	
	/*public void billingInformationGfmasOutbound() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "15-06-2015";
		Date currentDate = null;//new Date();
		try {
			currentDate = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		
//		SupplierSearchResDTO resDTO = supplierSearchSO.findSupplierforMasterDataVendor(supplierSearchReqDTO);
		
		List<SmBill> smBillList =iPaymentSO.retrieveLatestBillByCreateDate();
		
		List<BillingInformation> billingInformations = null;
		List<BillHeader> billHeaders = null; ;
		List<CustomerInformation> customerInformations = null;
		List<ChargeLine> chargeLines = null;
//		SmSupplier smSupplier = resDTO.getSmSupplier();
	
		if (smBillList!=null && smBillList.size()>0) {
			
			billingInformations = new ArrayList<BillingInformation>();
			
			BillingInformation billingInformation = null;
			BillHeader billHeader = null;
			CustomerInformation customerInformation = null;
			ChargeLine chargeLine = null;

			for(SmBill smBill :smBillList){
				billingInformation = new BillingInformation();
				billingInformation.setRecordType(IntegrationConstants.RECORD_TYPE_ZERO);
				billingInformation.setAgencyId(IntegrationConstants.AGENSI_ID_ePerolehan);  
				billingInformation.setTotalBillCount(smBill.getSmPayments().size());
				
				// Bill Header Information
				
				for(SmPayment smPayment:smBill.getSmPayments()){
					
					log.info("Bill Header...");
					billHeader = new BillHeader();
					billHeader.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
					billHeader.setBillType("B");
					billHeader.setBillReference("");
					billHeader.setBillReferenceDate(new Date());
					billHeader.setBillRCreditRDebitDate(new Date());
					billHeader.setABTIndicator("Y");
					billHeader.setYear("2006");
					billHeader.setAccountingOffice("");
					billHeader.setPegawaiPengawalPembayar("");
					billHeader.setKumpulanPtj("");
					billHeader.setCurrency(IntegrationConstants.CURRENCY_CODE_MY);
					billHeader.setBillRCreditRDebitNumber(smPayment.getReceiptNo());
					billHeader.setPostingDate(smBill.getPaymentDueDate());
					
					billHeader.setCustTotal();
					billHeader.setChargeLineTotal();
						
					if(!billHeaders.contains(billHeader))
						billHeaders.add(billHeader);
				}
				// Customer Information
				if(null!=smBill.getSmSupplier().getSmAppl().getSmCompanyBasic()){
					
					log.info("Customer available");
					
					customerInformation = new CustomerInformation();
					customerInformation.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
					customerInformation.setEntityCode(getEntityType(smBill.getSmSupplier().getBusinessType()));
					customerInformation.setEmailAddress("");
					customerInformation.setAmount(0);
					customerInformation.setDescription("");
					customerInformation.setDescription2("");
					customerInformation.setAltGlReconAcc("");
					
					if (smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getCompanyName().length() > 50) {
						customerInformation.setName1(smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getCompanyName().substring(0, 49));  
						customerInformation.setName2(smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getCompanyName().substring(50));
						} else {
						customerInformation.setName1(smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getCompanyName());
						customerInformation.setName2(IntegrationConstants.EMPTY_TRING);
						}
							
					for(SmCompanyAddress companyAddress : smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()){
						if(companyAddress.getAddressType().equalsIgnoreCase(IntegrationConstants.COMPANY_ADDRESS)){
								
							customerInformation.setAddress1(companyAddress.getSmAddress().getAddress1());
							customerInformation.setAddress2(companyAddress.getSmAddress().getAddress2());
							customerInformation.setAddress3(companyAddress.getSmAddress().getAddress3());
							customerInformation.setPostCode(companyAddress.getSmAddress().getPostcode());
							customerInformation.setCity(companyAddress.getSmAddress().getPmCity().getCityName());
							customerInformation.setRegion(companyAddress.getSmAddress().getPmState().getStateCode());
							customerInformation.setCountry(companyAddress.getSmAddress().getPmCountry().getCountryCode());
							customerInformation.setTelNo(smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getPhoneNo());
							customerInformation.setFaxNo(smBill.getSmSupplier().getSmAppl().getSmCompanyBasic().getFaxNo());
							}else{
							customerInformation.setAddress1(IntegrationConstants.EMPTY_TRING);
							customerInformation.setAddress2(IntegrationConstants.EMPTY_TRING);
							customerInformation.setAddress3(IntegrationConstants.EMPTY_TRING);
							customerInformation.setPostCode(IntegrationConstants.EMPTY_TRING);
							customerInformation.setCity(IntegrationConstants.EMPTY_TRING);
							customerInformation.setRegion(IntegrationConstants.EMPTY_TRING);
							customerInformation.setCountry(IntegrationConstants.EMPTY_TRING);
							customerInformation.setTelNo(IntegrationConstants.EMPTY_TRING);
							customerInformation.setFaxNo(IntegrationConstants.EMPTY_TRING);
							}
							}
						if(!customerInformations.contains(customerInformation))
							customerInformations.add(customerInformation);
				}
				// ChargeLine Information
				
				smBill.getsm
			}
			if (null != smSupplier.getSmAppl()) {
				
					log.info("ChargeLine Information...");
					chargeLine = new ChargeLine();
					chargeLine.setRecordType(IntegrationConstants.RECORD_TYPE_TWO);
					chargeLine.setRecordNumber(1);
					chargeLine.setDK("K");
					chargeLine.setKodPenjenisanTeriman("");
					chargeLine.setVotDana("");
					chargeLine.setPegawaiPengawalDipertanggug("");
					chargeLine.setKumpulanPtjNptjDipertanggug("");
					chargeLine.setProgramAktiviti("");
					chargeLine.setProjek("");
					chargeLine.setKodAkaun("");
					chargeLine.setAmount(0);
						
					if(!chargeLines.contains(chargeLine))
						chargeLines.add(chargeLine);
							
					}
				
					// Customer Information
					
			if (null != smSupplier.getSmAppl().getSmCompanyBasic()) {
						
				
					}
					
				} 
//			fileUtility.billingInformationFileOutPutStream(billingInformations,IntegrationConstants.FILE_PATH);
			
		}else{
			log.info("masterDataVendorGfmasOutbound(APIVE)  : Records not available ");
		}
	}*/
	
	public void errorGfmasInbound() {

		log.info("inside errorGfmasInbound : APERR ");
		String fileName = "1000APERR40000120150424005.GPG";
	
//		if (osbUtility.osbServiceCallInBound("GFM-090","")) {
//			
//			fileName = sftpUtility.gfmasErrorInPutStream(IntegrationConstants.GMAS_DOWNLOAD, "APERR");
			
			if(null!=fileName){
//				if (sftpUtility.download(IntegrationConstants.ENCRYPTED_FILE_PATH + fileName, IntegrationConstants.GMAS_DOWNLOAD + fileName)) {
//					sftpUtility.move(IntegrationConstants.GMAS_DOWNLOAD + fileName,	IntegrationConstants.GMAS_ARCHIVE + fileName);
					encryption.decryptGmas(IntegrationConstants.ENCRYPTED_FILE_PATH	+ fileName, IntegrationConstants.DECRYPTED_FILE_PATH + fileName);
					List<GFMASError> gfmasErrors = fileUtility.gfmasErrorInPutStream(IntegrationConstants.DECRYPTED_FILE_PATH+ fileName);
					for(GFMASError gfmasError:gfmasErrors){
						System.out.println("getAgencyId :"+gfmasError.getAgencyId());
						System.out.println("getTotalRecordCount :"+gfmasError.getTotalRecordCount());
						for(ErrorDesc errorDesc:gfmasError.getErrorDescs()){
							System.out.println("getReferenceNo :"+errorDesc.getReferenceNo());
							System.out.println("getErrorCode :"+errorDesc.getErrorCode());
							System.out.println("getErrorDescription :"+errorDesc.getErrorDescription());
						}
					}

//				}
				
			}
//		}else{
//			log.info("file not available pejabatPerakaunanGfmasInbound :GLBAC");
//		}
	}
	
	// Receipt Information
	@Override
	public void receiptInformationGfmasOutbound() {/*
		
	List<SmBill> smBillList =iPaymentSO.retrieveLatestBillByCreateDate();
		
		List<ReceiptInformation> receiptInformationList = null;
		
		List<ReceiptHeader> receiptHeaderList = null; ;
		List<ReceiptDetails> receiptDetailList = null;
		List<BillingInformationLine> billInfoLineList = null;
	
		if (smBillList!=null && smBillList.size()>0) {
			
			receiptInformationList = new ArrayList<ReceiptInformation>();
			receiptHeaderList = new ArrayList<ReceiptHeader>();
			
			ReceiptInformation receiptInformation = null;
			ReceiptHeader receiptHeader = null;

			for(PyBill smBill :smBillList){
			
				receiptInformation = new ReceiptInformation();
				
				receiptInformation.setRecordType(IntegrationConstants.RECORD_TYPE_ZERO);
				receiptInformation.setAgencyId(IntegrationConstants.AGENSI_ID_ePerolehan);  
				receiptInformation.setTotalBillCount(smBill.getPyPayments().size());
				
				// Receipt Header Information
				
				for(PyPayment smPayment:smBill.getPyPayments()){
					
					log.info("Receipt  Header....");
					if (null!=smPayment.getReceiptNo()) {
						receiptHeader = new ReceiptHeader();
						receiptHeader.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
						receiptHeader.setReceiptType("N"); // value will be N or C or D
						receiptHeader.setReceiptNumberNew(smPayment.getReceiptNo());
						receiptHeader.setReceiptDateNew(smBill.getCreatedDate()); // receipt create date
						receiptHeader.setReceiptPostingDateNew(smBill.getCreatedDate());
						receiptHeader.setReceiptNumberOld(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setChequeNumberOld(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setSpickCodeOld(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setYear(2014);
						receiptHeader.setAccountingOffice(IntegrationConstants.ACCONT_OFFICE);
						receiptHeader.setPegawaiPengawalPembeyar(IntegrationConstants.PEGAWAI_PENGAWAL);
						receiptHeader.setKumpulanPtjNPembeyar(IntegrationConstants.KUMP_PTJ);
						receiptHeader.setCurrency(IntegrationConstants.CURRENCY_CODE_RM);
						receiptHeader.setDescription1("DESC"); //needs confirmation
						receiptHeader.setDescription2(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setCustomerIdCode(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setEntityCode(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setName1(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setName2(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setAlternateGlReconAcc(IntegrationConstants.EMPTY_TRING);
						receiptHeader.setReceiptAmount(smPayment.getPaymentAmt()); // Amount  to be paid
						receiptHeader.setBankInStatus("A"); // values will be A. bank in or B.to be bank in
						receiptHeader.setReceiptDetailsCount(smBill.getPyBillDtls().size());
						receiptHeader.setBillingCount(smPayment.getPyPaymentOrder().getPyPaymentResponses().size());
						if (!receiptHeaderList.contains(receiptHeader))
							receiptHeaderList.add(receiptHeader);
					}
				}
				receiptInformation.setReceiptHeaderList(receiptHeaderList);
//					Receipt Details Information
				for(PyPayment smPayment:smBill.getPyPayments()){
					if(null!=smPayment.getPyPaymentDtls() && smPayment.getPyPaymentDtls().size()>0){
						receiptDetailList = new ArrayList<ReceiptDetails>();
						ReceiptDetails receiptDetails = null;
					    int recordCount =0;
					    log.info("Receipt Details available ");
						
					    for(PyPaymentDtl smPaymentDtl:smPayment.getPyPaymentDtls()){
							receiptDetails = new ReceiptDetails();
							receiptDetails.setRecordType(IntegrationConstants.RECORD_TYPE_TWO);
							receiptDetails.setRecordNumber(recordCount);
							receiptDetails.setReceiptNumber(smPayment.getReceiptNo());
							receiptDetails.setKodPenjenisanTerimaan(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setVotDana(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setPegawaiPegawalDipertanggung(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setKumpulanPtjDipertanggung(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setProgramAktiviti(IntegrationConstants.EMPTY_TRING);  
							receiptDetails.setProjek(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setKodAkaun(IntegrationConstants.EMPTY_TRING);
//							receiptDetails.setPaymentMethod(smPayment.getPaymentMethod().getParameterCode());  // Need to match
							receiptDetails.setReferenceNo(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setSpickCode(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setSwiftCode(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setBranchName(IntegrationConstants.EMPTY_TRING);
							receiptDetails.setPaymentDate(smBill.getCreatedDate());
							receiptDetails.setAmount(smPayment.getPaymentAmt());
									
							if(!receiptDetailList.contains(receiptDetails))
								receiptDetailList.add(receiptDetails);
							recordCount++;
						}
						receiptInformation.setReceiptDetailsList(receiptDetailList);
						
					}
					
					// Billing Information Line
					if(null!=smPayment.getSmPaymentOrder().getSmPaymentResponses() && smPayment.getSmPaymentOrder().getSmPaymentResponses().size()>0){
						 log.info(" Billing Information available ");
					
						billInfoLineList = new ArrayList<BillingInformationLine>();
						BillingInformationLine billInfoLine = null;
						int recordCount =0;
						for(SmPaymentResponse smPaymentResponse:smPayment.getSmPaymentOrder().getSmPaymentResponses()){
							
							billInfoLine = new BillingInformationLine();
							billInfoLine.setRecordType(IntegrationConstants.RECORD_TYPE_THREE);
							billInfoLine.setRecordNumber(recordCount);
							billInfoLine.setReceiptNumber(smPayment.getReceiptNo());
							billInfoLine.setBillDebitCreditNoteNum(smBill.getBillId().toString());
							billInfoLine.setBillDebitCreditNoteDate(smBill.getCreatedDate());
							billInfoLine.setDescription1("Desc");
							billInfoLine.setDescription2(IntegrationConstants.EMPTY_TRING);
							billInfoLine.setCustomerIdCode(smBill.getSmSupplier().getEpNo()); 
							billInfoLine.setEntityCode(getEntityType(smBill.getSmSupplier().getBusinessType()));
							billInfoLine.setAmountOfbillDebitCreditNote(smPaymentResponse.getPaidAmt());
							billInfoLine.setAmountReceivedbillDebitCreditNote(smPaymentResponse.getPaidAmt());
							
							if(!billInfoLineList.contains(billInfoLine))
								billInfoLineList.add(billInfoLine);
							recordCount++;
						}
						receiptInformation.setBillingInformationLineList(billInfoLineList);
					}
					
				}
				if(!receiptInformationList.contains(receiptInformation))
					receiptInformationList.add(receiptInformation);
			}
		}
		
		BatchReqDTO<ReceiptInformation> batchReqDTO = new BatchReqDTO<ReceiptInformation>();
		batchReqDTO.setFilePath(IntegrationConstants.FILE_PATH_LOCAL);
//		batchReqDTO.setDataStartTime(resDTO.getDataStartTime());
//		batchReqDTO.setDataEndTime(resDTO.getDataEndTime());
		batchReqDTO.setBatchContent(receiptInformationList);
		System.out.println("receiptInformationList :"+receiptInformationList.size());
		fileUtility.receiptInformationFileOutPutStream(batchReqDTO);
	*/}
	
	public void errorFileOutBound(EPErrorResponse errorResponse) {
		
		if(errorResponse!=null){
			BatchReqDTO<EPErrorResponse> reqDTO = new BatchReqDTO<EPErrorResponse>();
			reqDTO.setFilePath(IntegrationConstants.FILE_PATH_LOCAL);
			reqDTO.setBatchObject(errorResponse);
			fileUtility.errorFileOutPutStream(reqDTO);
		}
	}
	
	private void insertRecordforEmptyDataOut(BatchReqDTO reqDTO){
		
		reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
		reqDTO.setDataStartTime(reqDTO.getDataStartTime());
		reqDTO.setDataEndTime(reqDTO.getDataEndTime());
		reqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_FAIL);
		reqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_FAIL);
		reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_1GFMAS);
		reqDTO.setServiceCode(reqDTO.getServiceCode());
		reqDTO.setProcessId(reqDTO.getProcessId());
		reqDTO.setFileName(IntegrationConstants.VAVLUE_NA);
		
		fileUtility.setValuesToPmInterface(reqDTO);
		
	}

	private void insertRecordforEmptyDataIn(BatchReqDTO reqDTO){
	
	reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
	reqDTO.setDataStartTime(null);
	reqDTO.setDataEndTime(null);
	reqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_FAIL);
	reqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_FAIL);
	reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
	reqDTO.setServiceCode(reqDTO.getServiceCode());
	reqDTO.setProcessId(reqDTO.getProcessId());
	reqDTO.setFileName(IntegrationConstants.EMPTY_TRING);
	
	fileUtility.setValuesToPmInterface(reqDTO);
	
	}
	
	public void masterDataVendorInboundService() {
		String transId = UUID.randomUUID().toString();
		JAXBUtility jaxbUtility = new JAXBUtility();
		jaxbUtility.getMarshalMessageInbound(ServiceCode.APOVE.value(), null, transId);
		
		BatchReqDTO<MasterDataVendor> batchReqDTO = new BatchReqDTO<MasterDataVendor>();
//		batchReqDTO.setFileFTPStatus(IntegrationConstants.FTP_STATUS_SUCCESS);
//	    batchReqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
		batchReqDTO.setProcessId(ProcessCode.APOVE.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setFileName(null);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(ServiceCode.APOVE.value());
		
		batchReqDTO.setQueueMsgStatus(fileUtility.constructQueueMessageOutbound(ServiceCode.APOVE.value(), null, transId));
		   
		fileUtility.setValuesToPmInterface(batchReqDTO);
		
	}

	//New version
	
	@Override
	public void pejabatPerakaunanGfmasInbound() {
		
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLBAC.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLBAC.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void pegawaiPengawalGfmasInbound() {
		
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLSEG.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLSEG.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void kumpulanPtjGfmasInbound() {
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLPCG.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLPCG.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void votDanaGfmasInbound() {
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLDNA .value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLDNA .value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void programAktivitiGfmasInbound() {
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLPRG.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLPRG.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void projectGfmasInbound() {
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLPRJ.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLPRJ.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void kodAkaunGfmasInbound() {
		
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLGLC.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLGLC.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void masterDataPtjGfmasInbound() {
		
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLPTJ.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLPTJ.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void kodBankGfmasInbound() {
		
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.CMBNK.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.CMBNK.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void votGfmasInbound() {
		
		String transId = UUID.randomUUID().toString();
		String serviceCode = ServiceCode.GLVOT.value();
		String fileName = null;
		
		BatchReqDTO<AgOffice> batchReqDTO = new BatchReqDTO<AgOffice>();
		batchReqDTO.setProcessId(ProcessCode.GLVOT.value());
		batchReqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_INBOUND);
		batchReqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPEROLEHAN);
		batchReqDTO.setServiceCode(serviceCode);
		batchReqDTO.setTransId(transId);
		int status = fileUtility.constructQueueMessageInbound(serviceCode,fileName,transId);
		batchReqDTO.setQueueMsgStatus(status);
		
		insertRecordforEmptyDataIn(batchReqDTO);
		
	}

	@Override
	public void masterDataVendorGfmasInbound() {
		
	}

	@Override
	public void paymentGfmasInbound() {
		
	}

}
