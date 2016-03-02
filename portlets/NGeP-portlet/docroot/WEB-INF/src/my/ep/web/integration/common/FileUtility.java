package my.ep.web.integration.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import my.ep.biz.service.integration.common.JAXBUtility;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.jms.common.JMSReqDTO;
import my.ep.dto.jms.common.JMSResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.dto.sc.requestitem.RequestItemResDTO;
import my.ep.factory.EpLogger;
import my.ep.web.integration.common.BatchReqDTO;
import my.ep.web.integration.common.BatchResDTO;
import my.ep.web.integration.common.Encryption;
import my.ep.web.integration.common.ErrorMessages;
import my.ep.web.integration.common.InitializeSO;
import my.ep.web.integration.common.IntegrationConstants;
import my.ep.web.integration.common.InterfaceRowLength;
import my.ep.web.integration.common.OsbUtility;
import my.ep.web.integration.common.ProcessCode;
import my.ep.web.integration.common.ServiceCode;
import my.ep.web.integration.eperunding.AssociatedCompany;
import my.ep.web.integration.eperunding.BoardOfDirector;
import my.ep.web.integration.eperunding.BranchCompany;
import my.ep.web.integration.eperunding.CompanyOwner;
import my.ep.web.integration.eperunding.ConsultantProfile;
import my.ep.web.integration.eperunding.DeletedCompany;
import my.ep.web.integration.eperunding.EquityOwner;
import my.ep.web.integration.eperunding.GovernmentProject;
import my.ep.web.integration.eperunding.ManagementPersonnel;
import my.ep.web.integration.eperunding.ProfBodyDetail;
import my.ep.web.integration.eperunding.Qualification;
import my.ep.web.integration.eperunding.RegisteredCategory;
import my.ep.web.integration.eperunding.RegistrationHistory;
import my.ep.web.integration.eperunding.Signatory;
import my.ep.web.integration.eterimaan.BillingInformationLine;
import my.ep.web.integration.eterimaan.ReceiptDetails;
import my.ep.web.integration.eterimaan.ReceiptHeader;
import my.ep.web.integration.eterimaan.ReceiptInformation;
import my.ep.web.integration.gfmas.AgCtrlOfficer;
import my.ep.web.integration.gfmas.AgOffice;
import my.ep.web.integration.gfmas.BankAccountInformation;
import my.ep.web.integration.gfmas.CompanyBranch;
import my.ep.web.integration.gfmas.EPErrorResponse;
import my.ep.web.integration.gfmas.ErrorDesc;
import my.ep.web.integration.gfmas.ErrorDetail;
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
import my.ep.web.integration.phis.ItemType;
import my.ep.web.integration.phis.MaterialBrand;
import my.ep.web.integration.phis.MaterialClass;
import my.ep.web.integration.phis.MaterialColor;
import my.ep.web.integration.phis.MaterialMeasurement;
import my.ep.web.integration.phis.PaymentEffNotify;
import my.ep.web.integration.phis.PhisGlAccount;
import my.ep.web.integration.phis.PhisProgramActivity;
import my.ep.web.integration.phis.PhisProject;
import my.ep.web.integration.phis.PhisVotFund;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.common.PmInterfaceLog;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.web.util.DateUtils;

import org.apache.log4j.Logger;

/*
* @ author Ganga prasad
*/
public class FileUtility extends InitializeSO{
	
	private static Logger log = EpLogger.getLogger(FileUtility.class);
	
	private static final String valuestar =".*";
	
//	SFTPUtility sftpUtility = new SFTPUtility();
	OsbUtility osbUtility = new OsbUtility();
	Encryption encryption = new Encryption();
	JAXBUtility jaxbUtility = new JAXBUtility();

	// 1GFMAS

	public void masterDataVendorFileOutPutStream(BatchReqDTO<MasterDataVendor> batchReqDTO) {
		log.info("inside masterDataVendorFileOutPutStream");
		String PROCESS_ID = ProcessCode.APIVE.value();
		
		String fileName = createFileNameGfmas(PROCESS_ID);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(batchReqDTO.getFilePath() + fileName), "utf-8"));

				StringBuffer line;
				StringBuffer branchLine;
				StringBuffer subLine;
				
				log.info("content :"+batchReqDTO.getBatchContent().size());
				for (MasterDataVendor mDataVendor : batchReqDTO.getBatchContent()) {
					
					line = new StringBuffer();
					line.append(formatIntegerField(mDataVendor.getRecordType(),	1, "R"));
					line.append(formatStringField(mDataVendor.getAgencyId(), 6,	"L"));
					line.append(formatStringField(mDataVendor.getVendorIdCode(), 20, "L"));
					line.append(formatStringField(mDataVendor.getEntityCode(),4, "L"));
					line.append(formatStringField(mDataVendor.getEpNo(),20, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
						
					if (mDataVendor.getCompanyBranchs()!= null) {
						for (CompanyBranch companyBranch : mDataVendor.getCompanyBranchs()) {
							
							branchLine = new StringBuffer();
							branchLine.append(formatIntegerField(companyBranch.getRecordType(),	1, "R"));
							branchLine.append(formatStringField(companyBranch.getName(), 100,"L"));
							branchLine.append(formatStringField(companyBranch.getAddress(), 35,"L"));
							branchLine.append(formatStringField(companyBranch.getAddress2(),35, "L"));
							branchLine.append(formatStringField(companyBranch.getAddress3(),35, "L"));
							branchLine.append(formatStringField(companyBranch.getPostCode(), 5,"L"));
							branchLine.append(formatStringField(companyBranch.getCity(), 35,"L"));
							branchLine.append(formatStringField(companyBranch.getRegion(), 3,"L"));
							branchLine.append(formatStringField(companyBranch.getCountry(), 2,"L"));
							branchLine.append(formatStringField(companyBranch.getTelephoneNo(),	15, "L"));
							branchLine.append(formatStringField(companyBranch.getFaxNo(), 15,"L"));
							branchLine.append(formatStringField(companyBranch.getEmailAddress(), 100, "L"));
							branchLine.append(formatStringField(companyBranch.getGlReconAccount(), 10, "L"));
							branchLine.append(formatStringField(companyBranch.getCurrency(), 5,"L"));
							branchLine.append(formatStringField(companyBranch.getIncoTerm(), 3,"L"));
							branchLine.append(formatStringField(companyBranch.getPaymentTerm(),4, "L"));
							branchLine.append(formatIntegerField(companyBranch.getBankAccountInfoCount(), 3, "R"));
							branchLine.append(formatIntegerField(companyBranch.getStatus(), 1,"R"));
							branchLine.append(formatStringField(companyBranch.getMobileNo(), 15,"L"));
							if(companyBranch.getIndicatorCode()==0)
								branchLine.append(formatStringField(IntegrationConstants.HQ_INDICATOR_CODE, 20,"L"));
							else
								branchLine.append(formatLong(companyBranch.getIndicatorCode(), 20));
							branchLine.append(formatStringField(companyBranch.getAccountActionIndicator(), 1,"R"));
							branchLine.append(formatStringField(companyBranch.getReferenceNoGST(), 20,"L"));
							branchLine.append(formatDate(companyBranch.getGSTEffectiveDate()));
							branchLine.append(formatDate(companyBranch.getGSTEffectiveEndDate()));
							branchLine.append(System.getProperty("line.separator"));
							writer.write(branchLine.toString());
							
							if (companyBranch.getBankAccountInfoList()!= null) {
								for (BankAccountInformation bankInfo : companyBranch.getBankAccountInfoList()) {
									subLine = new StringBuffer();
									subLine.append(formatIntegerField(bankInfo.getRecordType(), 1, "R"));
									subLine.append(formatIntegerField(bankInfo.getRecordNumber(), 3, "R"));
									subLine.append(formatStringField(bankInfo.getBankCountry(), 2, "L"));
									subLine.append(formatStringField(bankInfo.getBankKey(), 15, "L"));
									subLine.append(formatStringField(bankInfo.getBankAccount(), 18, "L"));
									subLine.append(formatStringField(bankInfo.getCurrency(), 5, "L"));
									subLine.append(formatStringField(bankInfo.getBankActionIndicator(), 1, "L"));
									subLine.append(System.getProperty("line.separator"));
									writer.write(subLine.toString());
									writer.flush();
						}
						}
					}
				}
				}
				batchReqDTO.setFileName(fileName);
				batchReqDTO.setEncryptFilePath(IntegrationConstants.GMAS_UPLOAD_LOCAL);
				batchReqDTO.setServiceCode(ServiceCode.APIVE.value());
				batchReqDTO.setProcessId(ProcessCode.APIVE.value());
				
				encryption.encryptGmas(batchReqDTO);
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void sstInformationFileOutPutStream(BatchReqDTO<SSTInformation> reqDTO) {
		String PROCESS_ID = ProcessCode.AP516.value();
		String fileName = createFileNameGfmas(PROCESS_ID);
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (SSTInformation sstInfo : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatIntegerField(sstInfo.getRecordType(), 1,"R"));
					line.append(formatStringField(sstInfo.getSSTNumber(), 17,"L"));
					line.append(formatStringField(sstInfo.getPhysicalSSTNumber(), 100, "L"));
					line.append(formatStringField(sstInfo.getSapVendorCode(),10, "L"));
					line.append(formatStringField(sstInfo.getKumpPtj(), 8, "L"));
					line.append(formatStringField(sstInfo.getSstAcknowledgeDate(), 8, "L"));
					line.append(formatBigDecimalField(sstInfo.getSstAmountwithoutGST(), 13));
					line.append(formatBigDecimalField(sstInfo.getSstAmountwithGST(), 13));
					line.append(formatStringField(sstInfo.getPerihalSST(), 500, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}
				writer.close();
				log.info("local fie create....");
				
				reqDTO.setProcessId(PROCESS_ID);
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(ServiceCode.AP516.value());
				reqDTO.setEncryptFilePath(IntegrationConstants.GMAS_UPLOAD_LOCAL);
				
				encryption.encryptGmas(reqDTO);

			}
		} catch (IOException e) {
			
			log.info("Failed in creating file creation");
			

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	

	public List<MasterDataVendor> masterDataVendorFileInPutStream(String filePath) {
			Path path = Paths.get(filePath);
		    String line;
		    List<MasterDataVendor> masterDataVendorList = new ArrayList<MasterDataVendor>();
		    MasterDataVendor masterDataVendor = null;
		    List<BankAccountInformation> bankAccountInformationList = null;
		    BankAccountInformation bankAccountInformation = null;
			
			  try (Scanner scanner =  new Scanner(path, IntegrationConstants.ENCODING.name())){
				  
			      while (scanner.hasNextLine()){
			    	  line = scanner.nextLine();
			    	  if(line.startsWith(IntegrationConstants.VALUE_ZERO)){
			    		  masterDataVendor = new MasterDataVendor();
			    		  bankAccountInformationList = new ArrayList<BankAccountInformation>();
			    		  masterDataVendor.setRecordType(Integer.parseInt(getValue(line,0 ,1 )));
			    		  masterDataVendor.setAgencyId(getValue(line,1 ,6 ));
			    		  masterDataVendor.setVendorIdCode(getValue(line,7 ,20 ));
			    		  masterDataVendor.setEntityCode(getValue(line,27 ,4 ));
			    		  masterDataVendor.setSapVendorCode(getValue(line,31 ,10 ));
			    		  masterDataVendor.getCompanyBranch().setName(getValue(line,41 ,100 ));
			    		  masterDataVendor.getCompanyBranch().setAddress(getValue(line,141 ,35 ));
			    		  masterDataVendor.getCompanyBranch().setAddress2(getValue(line,176 ,35 ));
			    		  masterDataVendor.getCompanyBranch().setAddress3(getValue(line,211 ,35 ));
			    		  masterDataVendor.getCompanyBranch().setPostCode(getValue(line,246 ,5 ));
			    		  masterDataVendor.getCompanyBranch().setCity(getValue(line,251 ,35 ));
			    		  masterDataVendor.getCompanyBranch().setRegion(getValue(line,286 ,3 ));
			    		  masterDataVendor.getCompanyBranch().setCountry(getValue(line,289 ,2 ));
			    		  masterDataVendor.getCompanyBranch().setTelephoneNo(getValue(line,291 ,15 ));
			    		  masterDataVendor.getCompanyBranch().setFaxNo(getValue(line,306 ,15 ));
			    		  masterDataVendor.getCompanyBranch().setEmailAddress(getValue(line,321 ,100 ));
			    		  masterDataVendor.getCompanyBranch().setCurrency(getValue(line,421 ,5 ));
			    		  masterDataVendor.getCompanyBranch().setPaymentTerm(getValue(line,426 ,4 ));
			    		  masterDataVendor.getCompanyBranch().setMobileNo(getValue(line,430 ,10 ));
			    		  masterDataVendor.getCompanyBranch().setIndicatorCode(new Long(getValue(line,440 ,20 )));
			    	  }else if(line.startsWith(IntegrationConstants.VALUE_ONE)){
			    		  bankAccountInformation = new BankAccountInformation();
			    		  bankAccountInformation.setRecordType(Integer.parseInt(getValue(line,0 ,1 )));
			    		  bankAccountInformation.setRecordNumber(Integer.parseInt(getValue(line,1 ,3 )));
			    		  bankAccountInformation.setBankCountry(getValue(line,4 ,2 ));
			    		  bankAccountInformation.setBankKey(getValue(line,6 ,15 ));
			    		  bankAccountInformation.setBankAccount(getValue(line,21 ,12 ));
			    		  if(!bankAccountInformationList.contains(bankAccountInformation))
			    			  bankAccountInformationList.add(bankAccountInformation);
			    		  masterDataVendor.setBankAccountInfoList(bankAccountInformationList);
			    		  masterDataVendorList.add(masterDataVendor);
			    	  }
			      }  
			  } catch (IOException e) {
				e.printStackTrace();
			}
			return masterDataVendorList;
		}
	// GFMAS PM Files Start

		public List<AgOffice> pejabatPerakaunanFileInPutStream(String filePath, String fileName) {

			Path path = Paths.get(filePath);
			String line;
			List<AgOffice> agOfficeList = new ArrayList<AgOffice>();
			AgOffice afOffice = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path,IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLBAC.value()){
						afOffice = new AgOffice();
						recordFlag = false;
						afOffice.setYear(getValue(line, 0, 4));
						afOffice.setCode(getValue(line, 4, 4));
						AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
						reqDTO.setAgOfficeCode(afOffice.getCode());
						AgOfficeResDTO agOfficeDTO = agOfficeSO.retrieveValidAgOfficeByOfficeCode(reqDTO);
						if (agOfficeDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_1));
							recordFlag = true;
						}
						afOffice.setDescription(getValue(line, 8, 255));
						afOffice.setStatus(getValue(line, 263, 1));
						afOffice.setEffDate(IntegrationConstants.DEFAULT_DATE);
						if(!recordFlag)
							agOfficeList.add(afOffice);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
				
				//create error file
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return agOfficeList;
		}

		public List<AgCtrlOfficer> pegawaiPengawalInPutStream(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<AgCtrlOfficer> agCtrOfficeList = new ArrayList<AgCtrlOfficer>();
			AgCtrlOfficer afCtrOffice = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;
			try (Scanner scanner = new Scanner(path,IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLSEG.value()){
						afCtrOffice = new AgCtrlOfficer();
						recordFlag = false;
						afCtrOffice.setYear(getValue(line,0 ,4 ));
						afCtrOffice.setCode(getValue(line, 4, 2));
						
						OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
						orgProfileReqDTO.setOrgTypeCode(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL);
						orgProfileReqDTO.setOrgCode(afCtrOffice.getCode()); 
						OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgProfileReqDTO);
						if (orgProfileResDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_2));
							recordFlag = true;
						} 
						afCtrOffice.setDescription(getValue(line, 6, 255));
						afCtrOffice.setStatus(getValue(line,261, 1));
						if(!recordFlag)
							agCtrOfficeList.add(afCtrOffice);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return agCtrOfficeList;
		}

		public BatchResDTO<VotFund> votInPutStream(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<VotFund> votList = new ArrayList<VotFund>();
			VotFund vot = null;
			EPErrorResponse errorResponse = null;
			BatchResDTO<VotFund> batchResDTO = new BatchResDTO<VotFund>();
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path,IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLVOT.value()){
						recordFlag = false;
						vot = new VotFund();
					    vot.setYear(getValue(line,0 ,4 ));
					    vot.setVotFundType(getValue(line,4 ,1));
						vot.setVotCode(getValue(line, 4, 3));
						
						//Dupplicate checking
						VotFundReqDTO reqDTO = new VotFundReqDTO();
						reqDTO.setVotFundCode(vot.getVotCode());
						VotFundResDTO votFundResDTO = votFundSO.retrieveValidVotFundByVotCode(reqDTO);
						
						if (votFundResDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_3));
							recordFlag = true;
						}
						
						vot.setPtjCode(getValue(line,7 ,2));
						
						//kumpula ptj checking
						OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
						orgProfileReqDTO = new OrgProfileReqDTO();
						orgProfileReqDTO.setOrgTypeCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
						orgProfileReqDTO.setOrgCode(vot.getPtjCode()); 
						
						OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgProfileReqDTO);
						if(!orgProfileResDTO.isSuccessful()){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DEPENDENCY_ERROR_1));
							recordFlag = true;
						}
						vot.setDescription(getValue(line, 9, 255));
						if(formatStringToDate(getValue(line, 264, 8))!=null)
						{
							vot.setEffectiveDate(formatStringToDate(getValue(line, 264, 8)));
						}else{
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						if(formatStringToDate(getValue(line, 272, 8))!=null){
							vot.setExpiryDate(formatStringToDate(getValue(line, 272, 8)));
						} else {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						vot.setStatus(getValue(line, 280,1));
						if(!recordFlag)
							votList.add(vot);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(votList);
			return batchResDTO;
		}

		public EPErrorResponse constructErrorResponseObject(String fileName,
				EPErrorResponse errorResponse) {
			if(errorResponse==null){
				errorResponse = new EPErrorResponse();
				errorResponse.setRecordType(IntegrationConstants.RECORD_TYPE_H);
				errorResponse.setTahunKewangan(2015);
				errorResponse.setNamaFailAsal(fileName);
				List<ErrorDetail> errorDetailList = new ArrayList<ErrorDetail>();
				errorResponse.setErrorDetailList(errorDetailList);
			}
			return errorResponse;
		}

		public BatchResDTO<VotFund> votDanaInPutStream(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<VotFund> votList = new ArrayList<VotFund>();
			VotFund vot = null;
			EPErrorResponse errorResponse = null;
			BatchResDTO<VotFund> batchResDTO = new BatchResDTO<VotFund>();
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path,IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLDNA.value()){
						recordFlag = false;
						vot = new VotFund();
					    vot.setYear(getValue(line,0 ,4 ));								
						vot.setVotCode(getValue(line,4 ,4));
					
						// Duplicate checking
						VotFundReqDTO reqDTO = new VotFundReqDTO();
						reqDTO.setVotFundCode(vot.getVotCode());
						VotFundResDTO votFundDTO = votFundSO.retrieveValidVotFundDanaByVotCode(reqDTO);
						if (votFundDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_4));
							recordFlag = true;
						}
						vot.setDescription(getValue(line, 8, 255));
						try{
							vot.setEffectiveDate(formatStringToDate(getValue(line, 263, 8)));
						}catch(Exception e){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						try {
							vot.setExpiryDate(formatStringToDate(getValue(line, 271, 8)));
						} catch (Exception e) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						vot.setStatus(getValue(line, 279, 1));
						if(!recordFlag)
							votList.add(vot);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(votList);
			return batchResDTO;
		}

		public BatchResDTO<ProgramActivity> programAktiviti(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<ProgramActivity> programActivityList = new ArrayList<ProgramActivity>();
			ProgramActivity programActivity = null;
			EPErrorResponse errorResponse = null;
			BatchResDTO<ProgramActivity> batchResDTO = new BatchResDTO<ProgramActivity>();
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path, IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLPRG.value()){
						recordFlag = false;
						programActivity = new ProgramActivity();
						programActivity.setYear(getValue(line,0 ,4 ));				
						programActivity.setVotCode(getValue(line,4 ,3 ));
						
						//vot code checking
						VotFundReqDTO votreqDTO = new VotFundReqDTO();
						votreqDTO.setVotFundCode(programActivity.getVotCode());
						VotFundResDTO resDTO = votFundSO.retrieveVotFundByVotCode(votreqDTO);
						
						if(resDTO.getPmVotFund()==null){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DEPENDENCY_ERROR_2));
							recordFlag = true;
						}
						
						programActivity.setCode(getValue(line, 7, 6));
						//duplicate checking
						ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
						reqDTO.setCode(programActivity.getCode());
						ProgramActivityResDTO prgResDTO = programActivitySO.retrieveValidPrgActivityByPrgActivityCode(reqDTO);	
						
						if(prgResDTO.isSuccessful()){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_5));
							recordFlag = true;
						}
						programActivity.setDescription(getValue(line, 13, 255));
						
						if(formatStringToDate(getValue(line, 268, 8))!=null)
						{
							programActivity.setEffectiveDate(formatStringToDate(getValue(line, 268, 8)));
						}else{
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						if(formatStringToDate(getValue(line, 276, 8))!=null){
							programActivity.setExpiryDate(formatStringToDate(getValue(line, 276, 8)));
						} else {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						
						programActivity.setStatus(getValue(line, 284, 1));
						if(!recordFlag)
							programActivityList.add(programActivity);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(programActivityList);
			return batchResDTO;
		}

		public BatchResDTO<Project>  project(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<Project> projectList = new ArrayList<Project>();
			BatchResDTO<Project> batchResDTO = new BatchResDTO<Project>();
			Project project = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path, IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLPRJ.value()){
						recordFlag = false;
						project = new Project();
						project.setYear(getValue(line,0 ,4 ));
						project.setVotCode(getValue(line, 4, 3));
						//vot code checking
						
						VotFundReqDTO votreqDTO = new VotFundReqDTO();
						votreqDTO.setVotFundCode(project.getVotCode());
						VotFundResDTO restDTO = votFundSO.retrieveVotFundByVotCode(votreqDTO);
						if(restDTO.getPmVotFund()==null){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DEPENDENCY_ERROR_2));
							recordFlag = true;
						}
						project.setCode(getValue(line, 7, 5));
						//Duplicate checking
						
						ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
						reqDTO.setProjectCode(project.getCode());
						ProjectSetiaResDTO proResDTO = projectSetiaSO.retrieveProjectByProjectCode(reqDTO);							
						if (proResDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_6));
							recordFlag = true;
						}
						project.setSetiaCode(getValue(line, 12, 3));
						project.setSubSetiaCode(getValue(line,15, 4));
						project.setPembiayaan(getValue(line,19, 1));
						project.setDescription(getValue(line,20, 255));

						if(formatStringToDate(getValue(line, 275, 8))!=null)
						{
							project.setEffectiveDate(formatStringToDate(getValue(line, 275, 8)));
						}else{
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						if(formatStringToDate(getValue(line, 283, 8))!=null){
							project.setExpiryDate(formatStringToDate(getValue(line, 283, 8)));
						} else {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						project.setStatus(getValue(line,291, 1));
						if(!recordFlag)
							projectList.add(project);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(projectList);
			return batchResDTO;
		}

		public BatchResDTO<GlAccount> kodAkaun(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<GlAccount> glAccountList = new ArrayList<GlAccount>();
			BatchResDTO<GlAccount> batchResDTO = new BatchResDTO<GlAccount>();
			GlAccount glAccount = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;
			
			try (Scanner scanner = new Scanner(path, IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLGLC.value()){
						recordFlag = false;
						glAccount = new GlAccount();
						glAccount.setYear(getValue(line,0 ,4 ));
						glAccount.setCode(getValue(line, 4, 8));
						
						GlAccountReqDTO reqDTO = new GlAccountReqDTO();
						reqDTO.setGlAccountCode(glAccount.getCode());
						GlAccountResDTO glResDTO = glAccountSO.retrieveValidGlAccountByglAccountCode(reqDTO);
						if (glResDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_7));
							recordFlag = true;
						}
						
						glAccount.setPtjCode(getValue(line,12, 2));
						glAccount.setDescription(getValue(line,14, 255));
//						glAccount.setEffectiveDate(formatStringToDate(getValue(line, 269, 8)));
//						glAccount.setExpiryDate(formatStringToDate(getValue(line, 277, 8)));
						glAccount.setStatus(getValue(line,269, 1));
						if(!recordFlag)
							glAccountList.add(glAccount);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(glAccountList);
			return batchResDTO;
		}

		public BatchResDTO<PtjGroup> kumpulanPTJ(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<PtjGroup> ptjGroupList = new ArrayList<PtjGroup>();
			PtjGroup ptjGroup = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;
			
			try (Scanner scanner = new Scanner(path, IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLPCG.value()){
						recordFlag = false;
						ptjGroup = new PtjGroup();
						ptjGroup.setYear(getValue(line, 0, 4));
						ptjGroup.setCode(getValue(line, 4, 2));
						
						OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
						orgProfileReqDTO.setOrgType(ParameterConst.ORG_TYPE_PTJ_GROUP);
						orgProfileReqDTO.setOrgCode(ptjGroup.getCode()); 
						
						OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgProfileReqDTO);
						//Duplicate checking
						if (orgProfileResDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_8));
							recordFlag = true;
						}
						ptjGroup.setDescription(getValue(line, 6, 255));
						if(formatStringToDate(getValue(line, 261, 8))!=null)
						{
							ptjGroup.setEffectiveDate(formatStringToDate(getValue(line, 264, 8)));
						}else{
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						if(formatStringToDate(getValue(line, 269, 8))!=null){
							ptjGroup.setExpiryDate(formatStringToDate(getValue(line, 272, 8)));
						} else {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						ptjGroup.setStatus(getValue(line, 277, 1));
						if(!recordFlag)
							ptjGroupList.add(ptjGroup);
						
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			BatchResDTO<PtjGroup> batchResDTO = new BatchResDTO<PtjGroup>();
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(ptjGroupList);
			return batchResDTO;
		}

		public BatchResDTO<OrgProfile> masterDataPTJ(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<OrgProfile> orgProfileList = new ArrayList<OrgProfile>();
			OrgProfile orgProfile = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path, IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.GLPTJ.value()){
						recordFlag = false;
						orgProfile = new OrgProfile();
						orgProfile.setYear(getValue(line,0 ,4 ));
						orgProfile.setPtjCode(getValue(line, 4, 2));
						orgProfile.setCode(getValue(line, 6, 6));
						
						OrgProfileReqDTO dupPTJReqDTO = new OrgProfileReqDTO();
						dupPTJReqDTO.setOrgType(ParameterConst.ORG_TYPE_PTJ);
						dupPTJReqDTO.setOrgCode(orgProfile.getPtjCode()+orgProfile.getCode()); 
						
						//Duplicate checking
						OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(dupPTJReqDTO);
						if (orgProfileResDTO.isSuccessful()) {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_9));
							recordFlag = true;
						}
						
						//Kumpulan ptj checking
						OrgProfileReqDTO kumpReqDTO = new OrgProfileReqDTO();
						kumpReqDTO.setOrgTypeCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
						kumpReqDTO.setOrgCode(orgProfile.getPtjCode()); 
						
						OrgProfileResDTO kumResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(kumpReqDTO);
						if(kumResDTO.isSuccessful()){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DEPENDENCY_ERROR_1));
							recordFlag = true;
						}
						orgProfile.setDescription(getValue(line, 12, 255));
						
						
						//pegawai pengawai checking
						OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
						orgReqDTO.setOrgTypeCode(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL);
						orgReqDTO.setOrgCode(orgProfile.getCtrOfficerCode()); 
						
						OrgProfileResDTO pegwaiResDTO = orgProfileSO.retrieveOrgProfileByCodeAndOrgType(orgReqDTO);
						
						if(!pegwaiResDTO.isSuccessful()){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DEPENDENCY_ERROR_4));
							recordFlag = true;
						}
						
						orgProfile.setCtrOfficerCode(getValue(line,267,2));
						
						orgProfile.setAgOfficeCode(getValue(line, 269, 4));
					
						// pejabat perakuan checking
						AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
						reqDTO.setAgOfficeCode(orgProfile.getAgOfficeCode());
						AgOfficeResDTO agOfficeDTO = agOfficeSO.retrieveValidAgOfficeByOfficeCode(reqDTO);
						if(!agOfficeDTO.isSuccessful()){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DEPENDENCY_ERROR_3));
							recordFlag = true;
						}
//						orgProfile.setEffectiveDate(formatStringToDate(getValue(line, 273, 8)));
//						orgProfile.setExpiryDate(formatStringToDate(getValue(line, 281, 8)));
						
						if(formatStringToDate(getValue(line, 273, 8))!=null)
						{
							orgProfile.setEffectiveDate(formatStringToDate(getValue(line, 264, 8)));
						}else{
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						if(formatStringToDate(getValue(line, 281, 8))!=null){
							orgProfile.setExpiryDate(formatStringToDate(getValue(line, 272, 8)));
						} else {
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DATE_CONVERSION_ERROR));
							recordFlag = true;
						}
						
						orgProfile.setStatus(getValue(line, 289, 1));
						if(!recordFlag)
							orgProfileList.add(orgProfile);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			BatchResDTO<OrgProfile> batchResDTO = new BatchResDTO<OrgProfile>();
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(orgProfileList);
			return batchResDTO;
		}

		public BatchResDTO<BankAccountInformation> kodBank(String filePath, String fileName) {
			Path path = Paths.get(filePath);
			String line;
			List<BankAccountInformation> bankList = new ArrayList<BankAccountInformation>();
			BankAccountInformation bank = null;
			EPErrorResponse errorResponse = null;
			boolean recordFlag;

			try (Scanner scanner = new Scanner(path, IntegrationConstants.ENCODING.name())) {
				int lineNo = 1;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.length()==InterfaceRowLength.CMBNK.value()){
						bank = new BankAccountInformation();
						recordFlag = false;

						bank.setSwiftCode(getValue(line, 0, 11));
						bank.setBankName(getValue(line, 11, 40));
						FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
						reqDTO.setFinOrgName(bank.getBankName());
						FinancialOrgResDTO resDTO =financialOrgSO.retrieveFinancialOrgByName(reqDTO);
						if(null!=resDTO.getPmFinancialOrg()){
							errorResponse = constructErrorResponseObject(fileName,	errorResponse);
							errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.DUPLICATE_RECORD_11));
							recordFlag = true;
						}
						bank.setKodBankIndicator(Integer.valueOf(getValue(line, 51, 1)));
						bank.setStatus(getValue(line, 52, 1));
						if(!recordFlag)
							bankList.add(bank);
					}else{
						errorResponse = constructErrorResponseObject(fileName,	errorResponse);
						errorResponse.getErrorDetailList().add(ConstructErrorDetailsObject(lineNo,ErrorMessages.LENGTH_ERROR));
					}
					lineNo++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			BatchResDTO<BankAccountInformation> batchResDTO = new BatchResDTO<BankAccountInformation>();
			batchResDTO.setErrorResponse(errorResponse);
			batchResDTO.setBatchResContent(bankList);
			return batchResDTO;
		}

		public List<PaymentInfo> paymentInfo(String filePath) {
			Path path = Paths.get(filePath);
			String line;
			List<PaymentInfo> paymentList = new ArrayList<PaymentInfo>();
			PaymentInfo paymentInfo = null;

			try (Scanner scanner = new Scanner(path,
					IntegrationConstants.ENCODING.name())) {

				String[] invoiceArray = null;
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					if(line.startsWith(IntegrationConstants.VALUE_ZERO)){
						paymentInfo = new PaymentInfo();
						invoiceArray = new String[1];
						paymentInfo.setPaymentRefNo(getValue(line, 33, 18));
						paymentInfo.setPaymentDate(getValue(line, 11, 8));
						log.info("setPaymentRefNo  :"+getValue(line, 33, 18));
						log.info("setPaymentDate  :"+getValue(line, 11, 8));
					}else if(line.startsWith(IntegrationConstants.VALUE_ONE)){
						invoiceArray[0] =getValue(line, 9, 17);
						log.info("Invoice No  :"+getValue(line, 9, 17));
						paymentInfo.setInvoiceArray(invoiceArray);
						paymentList.add(paymentInfo);
					}
					

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return paymentList;
		}

	// END OF GFMAS PM Files
	
	
	// set values for masterData material Info 1gfmas
	
	public List<MasterDataMaterialInfo> setGfmasMasterDataMaterialInfo(CodificationResDTO resDTO){
		
		List<MasterDataMaterialInfo> masterDataMaterialInfoList = new ArrayList<MasterDataMaterialInfo>();
		MasterDataMaterialInfo masterDataMaterialInfo = null;
		
		if(resDTO.getItemList().size()>0){
			
			for(CmItem cmItem :resDTO.getItemList()){
				masterDataMaterialInfo = new MasterDataMaterialInfo();
				
				
				masterDataMaterialInfo.setMaterialCode(cmItem.getExtensionCode());
				masterDataMaterialInfo.setCodeType("P");
				masterDataMaterialInfo.setActionCode(checkAction(cmItem.getCreatedDate(), cmItem.getChangedDate()));
				if(cmItem.getItemName().length()>40){
					masterDataMaterialInfo.setMaterialDesc(cmItem.getItemName().substring(0, 39));
					masterDataMaterialInfo.setMaterialAddDesc(cmItem.getItemName().substring(40, cmItem.getItemName().length()-1));
				}else{
					masterDataMaterialInfo.setMaterialDesc(cmItem.getItemName());
				}
				
		
				//expiry date will be 3 years from current date
				Calendar c = Calendar.getInstance();    
				c.setTime(new Date());
				c.add(Calendar.YEAR, 3);
				
				masterDataMaterialInfo.setStartEffectiveDt(new Date());
				masterDataMaterialInfo.setEndEffectiveDt(c.getTime());
				masterDataMaterialInfo.setCommodityName(cmItem.getCmUnspsc().getUnspscTitle());
				masterDataMaterialInfo.setBaseUnitOfMeasure("UN");
				masterDataMaterialInfo.setMaterialType(getValue(cmItem.getCmUnspsc().getUnspscCode(), 0, 2)+"00");
				masterDataMaterialInfo.setMaterialGroup(getValue(cmItem.getCmUnspsc().getUnspscCode(), 0, 4)+"0000");
				if(null!=findUomCode(cmItem.getItemId(),resDTO.getCmSupplierItemsList())){
					masterDataMaterialInfo.setAltUnitOfMeasure(findUomCode(cmItem.getItemId(),resDTO.getCmSupplierItemsList()));
				}else{
					masterDataMaterialInfo.setAltUnitOfMeasure("UN");
				}
				masterDataMaterialInfoList.add(masterDataMaterialInfo);
			}
		}
		
		return masterDataMaterialInfoList;
		}
	
	// set values for temporary masterData material Info 1gfmas
		public List<MasterDataMaterialInfo> setGfmasTempMasterDataMaterialInfo(RequestItemResDTO resDTO){
			
			List<MasterDataMaterialInfo> masterDataMaterialInfoList = new ArrayList<MasterDataMaterialInfo>();
			MasterDataMaterialInfo masterDataMaterialInfo = null;
			
			if(resDTO.getRequestItems().size()>0){
				
				for(ScRequestItem scRequestItem :resDTO.getRequestItems()){
					
					masterDataMaterialInfo = new MasterDataMaterialInfo();
					
					masterDataMaterialInfo.setMaterialCode(scRequestItem.getCmItem().getExtensionCode());
					masterDataMaterialInfo.setCodeType("T");
					masterDataMaterialInfo.setActionCode(checkAction(scRequestItem.getCreatedDate(), scRequestItem.getChangedDate()));
					if(scRequestItem.getItemName().length()>40){
						masterDataMaterialInfo.setMaterialDesc(scRequestItem.getItemName().substring(0, 39));
						masterDataMaterialInfo.setMaterialAddDesc(scRequestItem.getItemName().substring(40, scRequestItem.getItemName().length()-1));
					}else{
						masterDataMaterialInfo.setMaterialDesc(scRequestItem.getItemName());
					}
			
					//expiry date will be 3 years from current date
					Calendar c = Calendar.getInstance();    
					c.setTime(new Date());
					c.add(Calendar.YEAR, 3);
					
					masterDataMaterialInfo.setStartEffectiveDt(new Date());
					masterDataMaterialInfo.setEndEffectiveDt(c.getTime());
					masterDataMaterialInfo.setCommodityName(scRequestItem.getCmItem().getCmUnspsc().getUnspscTitle());
					masterDataMaterialInfo.setBaseUnitOfMeasure("UN");
					masterDataMaterialInfo.setMaterialType(getValue(scRequestItem.getCmItem().getCmUnspsc().getUnspscCode(), 0, 2)+"00");
					masterDataMaterialInfo.setMaterialGroup(getValue(scRequestItem.getCmItem().getCmUnspsc().getUnspscCode(), 0, 4)+"0000");
					if(null!=scRequestItem.getUom()){
						masterDataMaterialInfo.setAltUnitOfMeasure(scRequestItem.getUom().getUomCode());
					}else{
						masterDataMaterialInfo.setAltUnitOfMeasure("UN");
					}
					masterDataMaterialInfoList.add(masterDataMaterialInfo);
				}
			}
			return masterDataMaterialInfoList;
			}
	
	// set values for masterData material Info phis
	
    public List<my.ep.web.integration.phis.MasterDataMaterialInfo> setPHISMasterDataMaterialInfo(RequestItemResDTO resDTO){
		
		List<my.ep.web.integration.phis.MasterDataMaterialInfo> masterDataMaterialInfoList = new ArrayList<my.ep.web.integration.phis.MasterDataMaterialInfo>();
		my.ep.web.integration.phis.MasterDataMaterialInfo masterDataMaterialInfo = null;
		
		for (ScRequestItem scRequestItem : resDTO.getRequestItems()) {
			CmItem cmItem = scRequestItem.getCmItem();
			if (cmItem!= null) {
				masterDataMaterialInfo = new my.ep.web.integration.phis.MasterDataMaterialInfo();

				masterDataMaterialInfo.setProductCode(cmItem.getExtensionCode());
				masterDataMaterialInfo.setCodeType("P");
				masterDataMaterialInfo.setOldProductCode("NA");
				masterDataMaterialInfo.setItemDescription(cmItem.getItemName());
				masterDataMaterialInfo.setKodTindakan(checkAction(cmItem.getCreatedDate(), cmItem.getChangedDate()));

				//expiry date will be 3 years from current date
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.YEAR, 3);

				masterDataMaterialInfo.setStartEffectiveDate(new Date());
				masterDataMaterialInfo.setEndEffectiveDate(c.getTime());
				masterDataMaterialInfo.setCommodityName(cmItem.getCmUnspsc().getUnspscTitle());
				masterDataMaterialInfo.setBaseUnitOfMeasure("UN");
				masterDataMaterialInfo.setSegment(getValue(cmItem.getCmUnspsc().getUnspscCode(), 0, 2));
				masterDataMaterialInfo.setFamily(getValue(cmItem.getCmUnspsc().getUnspscCode(), 0, 4));
				String uomCode = scRequestItem.getUom().getUomCode();
//				String uomCode = findUomCode(cmItem.getItemId(),resDTO.getCmSupplierItemsList());
				if (null != uomCode) {
					masterDataMaterialInfo.setAltUnitOfMeasurement(uomCode);
				} else {
					masterDataMaterialInfo.setAltUnitOfMeasurement("UN");
				}
				masterDataMaterialInfoList.add(masterDataMaterialInfo);
			}
		}
		return masterDataMaterialInfoList;
	
		}
	

	public String formatStringField(String input, int length, String align) {
		String formatInput;
		String formatInput1 = null;
		if (!(input==null)) { 
			formatInput = input.trim();
			if (formatInput.length() != length) {
				if (align == "L") {
					formatInput1 = String.format("%-" + length + "s",
							formatInput);
				} else if (align == "R") {
					formatInput1 = String.format("%" + length + "d",
							formatInput);
				}

				return formatInput1;
			} else if (formatInput.length() == length) {
				return formatInput;

			}

		} else {
			formatInput1 = String.format("%-" + length + "s", input);
		}
		return formatInput1;

	}

	public String formatIntegerField(int input, int length, String align) {
		String formatInput = null;
		if (align == "L") {
			formatInput = String.format("%-" + length + "d", input);
		} else if (align == "R") {
			formatInput = String.format("%0" + length + "d", input);
		}
		return formatInput;

	}

	public String createFileNamePhis(String serviceCode) {

		String FILE_NAME;
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");

		String DATE = dt1.format(new Date());

		String fileName = IntegrationConstants.PHIS_BUSINESS_AREA + serviceCode
				+ IntegrationConstants.AGENSI_ID_ePerolehan + DATE;

		String seqNo = generateSeqNo(fileName, serviceCode, "pharma",DATE);
		FILE_NAME = fileName + seqNo + IntegrationConstants.FILE_EXTENSION_PHIS;
		return FILE_NAME;
	}

	public String createFileNameGfmas(String serviceCode) {

		String FILE_NAME;
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");

		String DATE = dt1.format(new Date());

		String fileName = IntegrationConstants.GMAS_BUSINESS_AREA + serviceCode
				+ IntegrationConstants.AGENSI_ID_ePerolehan + DATE;

		String seqNo = generateSeqNo(fileName, serviceCode, "gfmas",DATE);
		FILE_NAME = fileName + seqNo
				+ IntegrationConstants.FILE_EXTENSION_GFMAS;
		return FILE_NAME;
	}

	public String generateSeqNo(String fileName, String serviceCode,
			String region,String date) {

		String sequenceNo = IntegrationConstants.SEQUENCE_NO;
		String fileExtn = null,filePattern = null; 
		if (region.equals("pharma")) {
			fileExtn = IntegrationConstants.FILE_EXTENSION_PHIS;
			filePattern = IntegrationConstants.AGENSI_ID_ePerolehan;
		} else if (region.equals("gfmas")) {
			fileExtn = IntegrationConstants.FILE_EXTENSION_GFMAS;
			filePattern = IntegrationConstants.AGENSI_ID_ePerolehan;
		}
		int seqNo = 0;
		if (!new File(IntegrationConstants.FILE_PATH_LOCAL + fileName
				+ sequenceNo + fileExtn).exists()) {
			return sequenceNo;
		} else {

			File dir = new File(IntegrationConstants.FILE_PATH_LOCAL);
			String[] filesInPath = dir.list();
			List<Integer> seqNos = new ArrayList<Integer>();
			Pattern p = Pattern.compile(valuestar + serviceCode+filePattern+date+valuestar);
			
			for (int i = 0; i < filesInPath.length; i++) {
				String fileName1 = filesInPath[i];
				 Matcher m = p.matcher(fileName1);
				if (m.matches()) {
					int index = fileName1.indexOf('.');
					seqNo = Integer.parseInt(fileName1.substring(index - 3,
							index));
					seqNos.add(seqNo);
				}
			}
			Collections.sort(seqNos);
			if (seqNos != null && !seqNos.isEmpty()) {
				seqNo =  seqNos.get(seqNos.size()-1);
				}else{
					log.info("seqNos :"+seqNos);
				}
			sequenceNo = formatIntegerField(seqNo + 1, 3, "R");

		}
		return sequenceNo;
	}

	public String checkAction(Date createdDate, Date updatedDate) {

		String action;
		if (createdDate.after(updatedDate)) {
			action = IntegrationConstants.DEL;
		} else if (createdDate.before(updatedDate)) {
			action = IntegrationConstants.ADJ;
		} else if (createdDate.equals(updatedDate)) {
			action = IntegrationConstants.CRE;
		} else {
			action = IntegrationConstants.CRE;
		}
		return action;
	}
	
	public String formatDate(Date date) {
		String formatDate = null;
		
		if(null!=date){
			SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

			formatDate = String.format("%s", dt1.format(date));
		}else{
			formatDate = formatStringField(IntegrationConstants.EMPTY_TRING,8,"L");
		}

		return formatDate;
	}
	
	public Date formatStringToDate(String date) {

		SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

		try {
			return dt1.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//===============================Eperunding files creation===========================
	
	
	public void equityOwnerOutPutStream(BatchReqDTO<EquityOwner> reqDTO) {
		String fileName = "EquityOwner.txt";

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));
				StringBuffer line;
				writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
				for (EquityOwner eOwner : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(System.getProperty("line.separator"));
					line.append(formatString(eOwner.getMOFNo(),20));
					if(null!=eOwner.getOwner()){
						line.append(formatString(eOwner.getOwner().getIsEquityOwner(),1));
						line.append(formatString(eOwner.getOwner().getName(),50));
						line.append(formatString(eOwner.getOwner().getNewICNo(),12));
						line.append(formatString(eOwner.getOwner().getPassport(),20));
						line.append(formatString(eOwner.getOwner().getIsBumi(),1));
						line.append(formatInt(eOwner.getOwner().getCitizenOf()));
						line.append(formatString(eOwner.getOwner().getEquityValue(),20));
						line.append(formatString(eOwner.getOwner().getEquityPercentage(),5));
						line.append(formatString(eOwner.getOwner().getRace(),100));
						line.append(formatString(eOwner.getOwner().getDesignation(),50));
						for(Qualification qualification:eOwner.getOwner().getQualificationList()){
							line.append(formatString(qualification.getMajorIn(),300));
							line.append(formatString(qualification.getQualification(),300));
							line.append(formatInt(qualification.getQualificationLevel()));
							line.append(formatString(qualification.getInstitute(),255));
							line.append(formatString(qualification.getYear(),30));
							line.append(formatLong(qualification.getEducationField(),12));
						}
					}
					for(ProfBodyDetail profBodyDetail:eOwner.getProfBodyDetailList()){
						line.append(formatString(profBodyDetail.getProfBody(),12));
						line.append(formatString(profBodyDetail.getProfBodyNo(),30));
						line.append(formatDateEperunding(profBodyDetail.getProfBodyExpiry(),false));
					}
					line.append(String.format("%s", eOwner.getExpertise().getCategoryClassification()));
				writer.write(line.toString());
			}
			
			writer.close();
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE04.value());
			reqDTO.setServiceCode(ServiceCode.EPE04.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			 osbUtility.osbServiceCallOutBound(ServiceCode.EPE04.value(),fileName);
			
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE04.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			 
			 log.info("createEquiytyOwner end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void consultantProfileFileOutPutStream(BatchReqDTO<ConsultantProfile> reqDTO) {

		String fileName = "ConsultantProfiles.txt";
		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
		
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (ConsultantProfile cProfile : reqDTO.getBatchContent()) {

				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));

				line.append(formatString(cProfile.getCompanyName(),100));
				line.append(formatString(cProfile.getSSMNo(),20));
				line.append(formatString(cProfile.getMOFNo(),20));
				
				line.append(formatString(cProfile.getVirtualCertNo(),30));
				line.append(formatString(cProfile.getGSTNo(),12));
				line.append(formatDateEperunding(cProfile.getGSTEffectiveDate(),false));
				line.append(formatDateEperunding(cProfile.getGSTEndDate(),false));
				
				line.append(formatString(cProfile.getCompanyType(),5));
				line.append(formatString(cProfile.getAddress1(),100));
				line.append(formatString(cProfile.getAddress2(),100));
				line.append(formatString(cProfile.getAddress3(),100));
				line.append(formatString(cProfile.getPostcode(),15));
				line.append(formatString(cProfile.getCity(),50));
				line.append(formatString(cProfile.getDistrict(),12));
				line.append(formatString(cProfile.getState(),12));
				line.append(formatString(cProfile.getCountry(),12));
				line.append(formatString(cProfile.getTelCountryCode(),5));
				line.append(formatString(cProfile.getTelAreaCode(),5));
				line.append(formatString(cProfile.getTelNo(),20));
				line.append(formatString(cProfile.getFaxCountryCode(),5));
				line.append(formatString(cProfile.getFaxAreaCode(),5));
				line.append(formatString(cProfile.getFaxNo(),20));
				line.append(formatBigDecimal(cProfile.getAccumulatedCapital()));
				line.append(formatBigDecimal(cProfile.getAuthorisedCapital()));
				line.append(formatBigDecimal(cProfile.getPaidUpCapital()));
				line.append(formatBigDecimal(cProfile.getAccumulatedShare()));
				line.append(formatBigDecimal(cProfile.getBumiputeraPercentage()));
				line.append(formatBigDecimal(cProfile.getNonBumiPercentage()));
				line.append(formatLong(cProfile.getRegStatus()));
				line.append(formatDateEperunding(cProfile.getEffectDateTo(),false));
				line.append(formatDateEperunding(cProfile.getEstablishedDate(),false));
				line.append(formatDateEperunding(cProfile.getEffectDateFrom(),false));
				line.append(formatString(cProfile.getDisciplinary(),1));
				line.append(formatString(cProfile.getIsAssociatedCompany(),1));
				line.append(formatDateEperunding(cProfile.getProfileUpdateDate(),false));

				for(ProfBodyDetail bodyDetail:cProfile.getProfBodyDetailList()){
					line.append(formatString(bodyDetail.getProfBody(),12));
					line.append(formatString(bodyDetail.getProfBodyNo(),30));
					line.append(formatDateEperunding(bodyDetail.getProfBodyExpiry(),false));
				}
				for(RegistrationHistory history:cProfile.getRegistrationHistoryList()){
					line.append(formatDateEperunding(history.getDateStart(),false));
					line.append(formatDateEperunding(history.getDateEnd(),false));
				}
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getBumi()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getNonBumi()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getNonMalaysian()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getGovAgency()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getGovEquity()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getLocalCo()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getForeignCo()));
				line.append(formatBigDecimal(cProfile.getCapitalProportion().getTotalMember()));
				line.append(String.format("%.0f", cProfile.getCapitalProportion().getTotalShare()));

				writer.write(line.toString());
			}
			
			writer.close();
			//upload the txt file to sftp server
			
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE01.value());
			reqDTO.setServiceCode(ServiceCode.EPE01.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE01.value(),fileName);
			
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE01.value(),fileName, transId));
				
			setValuesToPmInterface(reqDTO);
			
			log.info("createConsultantProfile end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	/**
	 * @param fileName
	 * @param transId 
	 * @return
	 */
	public int constructQueueMessageOutbound(String serviceCode, String fileName, String transId) {
		JMSReqDTO jmsReqDTO = new JMSReqDTO();
		jmsReqDTO.setQueueName("queue/Batch_Outbound_Queue");
		jmsReqDTO.setTextMessage(true);
		jmsReqDTO.setTextMessage(jaxbUtility.getMarshalMessageOutbound(serviceCode,fileName, transId));
		
		JMSResDTO jmsResDTO = remoteClientSO.sendMessageToJBossQueue(jmsReqDTO);
		
		if(jmsResDTO.isSuccessful()){
			return 1;
		}else{
			return 0;
		}
	}
	
	/**
	 * @param fileName
	 * @param transId 
	 * @return
	 */
	public int constructQueueMessageInbound(String serviceCode, String fileName, String transId) {
		JMSReqDTO jmsReqDTO = new JMSReqDTO();
		jmsReqDTO.setQueueName("queue/Batch_Outbound_Queue");
		jmsReqDTO.setTextMessage(true);
		jmsReqDTO.setTextMessage(jaxbUtility.getMarshalMessageInbound(serviceCode,fileName, transId));
		
		JMSResDTO jmsResDTO = remoteClientSO.sendMessageToJBossQueue(jmsReqDTO);
		
		if(jmsResDTO.isSuccessful()){
			return 1;
		}else{
			return 0;
		}
	}

	public void signatoryFileOutPutStream(BatchReqDTO<Signatory> reqDTO) {
		
		String fileName = "Signatory.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line = null;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (Signatory signatory : reqDTO.getBatchContent()) {
				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));
				line.append(formatString(signatory.getMOFNo(),20));
				if(null!=signatory.getAuthorizedSignatory()){
					line.append(formatString(signatory.getAuthorizedSignatory().getName(),50));
					line.append(formatString(signatory.getAuthorizedSignatory().getNewICNo(),12));
					line.append(formatString(signatory.getAuthorizedSignatory().getPassport(),20));
					line.append(formatString(signatory.getAuthorizedSignatory().getIsBumi(),1));
					line.append(String.format("%s", signatory.getAuthorizedSignatory().getDesignation()));
				}
				writer.write(line.toString());
			}
			
			writer.close();
			
			//upload the txt file to sftp server
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE07.value());
			reqDTO.setServiceCode(ServiceCode.EPE07.value());
			reqDTO.setFileCreationStatus(IntegrationConstants.FILE_CREATION_SUCCESS);
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE07.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE07.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			log.info("createAuthorizedSignatory end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	
	public void managementProfileFileOutPutStream(BatchReqDTO<ManagementPersonnel> reqDTO) {

		String fileName = "ManagementPersonnel.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (ManagementPersonnel mPersonnel : reqDTO.getBatchContent()) {
				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));

				line.append(formatString(mPersonnel.getMOFNo(),20));
				
				if(null!=mPersonnel.getPersonnel()){
					
					line.append(formatString(mPersonnel.getPersonnel().getIsManagement(),1));
					line.append(formatString(mPersonnel.getPersonnel().getIsProfessional(),1));
					line.append(formatString(mPersonnel.getPersonnel().getIsSemiProf(),1));
					line.append(formatString(mPersonnel.getPersonnel().getIsClerical(),1));
					line.append(formatString(mPersonnel.getPersonnel().getName(),50));
					line.append(formatString(mPersonnel.getPersonnel().getNewICNo(),12));
					line.append(formatString(mPersonnel.getPersonnel().getPassport(),20));
					line.append(formatString(mPersonnel.getPersonnel().getIsBumi(),1));
					line.append(formatInt(mPersonnel.getPersonnel().getCitizenOf()));
					line.append(formatString(mPersonnel.getPersonnel().getRace(),100));
					line.append(formatString(mPersonnel.getPersonnel().getDesignation(),50));
					line.append(formatBigDecimal(mPersonnel.getPersonnel().getWorkExperience()));
					
					for(Qualification qualification:mPersonnel.getPersonnel().getQualificationList()){
						line.append(formatString(qualification.getMajorIn(),300));
						line.append(formatString(qualification.getQualification(),300));
						line.append(formatInt(qualification.getQualificationLevel()));
						line.append(formatString(qualification.getInstitute(),255));
						line.append(formatString(qualification.getYear(),30));
						line.append(formatLong(qualification.getEducationField(),12));
					}
				}
				for(ProfBodyDetail profBodyDetail :mPersonnel.getProfBodyDetailList()){
					line.append(formatString(profBodyDetail.getProfBody(),12));
					line.append(formatString(profBodyDetail.getProfBodyNo(),30));
					line.append(formatDateEperunding(profBodyDetail.getProfBodyExpiry(),false));
				}

				line.append(String.format("%s", mPersonnel.getExpertise().getCategoryClassification()));

				writer.write(line.toString());
			}
			
			writer.close();
			//upload the txt file to sftp server
			
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE06.value());
			reqDTO.setServiceCode(ServiceCode.EPE06.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE06.value(),fileName);
			
			// Thread will sleep for 3 seconds before putting message into JMS Queue
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE06.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createManagementPersonnel end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void boardOfDirectorOutPutStream(BatchReqDTO<BoardOfDirector> reqDTO) {

		String fileName = "BoardOfDirector.txt";

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (BoardOfDirector bDirector : reqDTO.getBatchContent()) {
				line = new StringBuffer();

				line.append(System.getProperty("line.separator"));

				line.append(formatString(bDirector.getMOFNo(),20));
				
				if (null!=bDirector.getDirector()){
					line.append(formatString(bDirector.getDirector().getIsBOD(),1));
					line.append(formatString(bDirector.getDirector().getIsCEO(),1));
					line.append(formatString(bDirector.getDirector().getName(),50));
					line.append(formatString(bDirector.getDirector().getNewICNo(),12));
					line.append(formatString(bDirector.getDirector().getPassport(),20));
					line.append(formatString(bDirector.getDirector().getIsBumi(),1));
					line.append(formatInt(bDirector.getDirector().getCitizenOf()));
					line.append(formatString(bDirector.getDirector().getRace(),100));
					line.append(formatString(bDirector.getDirector().getDesignation(),50));
					line.append(formatBigDecimal(bDirector.getDirector().getWorkExperience()));
					
					for(Qualification qualification:bDirector.getDirector().getQualificationList()){
						line.append(formatString(qualification.getMajorIn(),300));
						line.append(formatString(qualification.getQualification(),300));
						line.append(formatInt(qualification.getQualificationLevel()));
						line.append(formatString(qualification.getInstitute(),255));
						line.append(formatString(qualification.getYear(),30));
						line.append(formatLong(qualification.getEducationField(),12));
					}
				}
				for(ProfBodyDetail profBodyDetail:bDirector.getProfBodyDetailList()){
					line.append(formatString(profBodyDetail.getProfBody(),12));
					line.append(formatString(profBodyDetail.getProfBodyNo(),30));
					line.append(formatDateEperunding(profBodyDetail.getProfBodyExpiry(),false));
				}

				line.append(String.format("%s", bDirector.getExpertise().getCategoryClassification()));
				writer.write(line.toString());
			}
			writer.close();
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE05.value());
			reqDTO.setServiceCode(ServiceCode.EPE05.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE05.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE05.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createBoardOfDirector end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	
	public void branchCompanyOutPutStream(BatchReqDTO<BranchCompany> reqDTO) {

		String fileName = "BranchCompany.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (BranchCompany branchCompany : reqDTO.getBatchContent()) {
				line = new StringBuffer();

				line.append(System.getProperty("line.separator"));

				line.append(formatString(branchCompany.getMOFNo(),20));

				line.append(formatString(branchCompany.getBranch().getBranchName(),100));
				line.append(formatString(branchCompany.getBranch().getGSTNo(),12));
				line.append(formatDateEperunding(branchCompany.getBranch().getGSTEffectiveDate(),false));
				line.append(formatDateEperunding(branchCompany.getBranch().getGSTEndDate(),false));
				line.append(formatString(branchCompany.getBranch().getAddress1(),100));
				line.append(formatString(branchCompany.getBranch().getAddress2(),100));
				line.append(formatString(branchCompany.getBranch().getAddress3(),100));
				line.append(formatString(branchCompany.getBranch().getPostcode(),15));
				line.append(formatString(branchCompany.getBranch().getCity(),50));
				line.append(formatString(branchCompany.getBranch().getDistrict(),12));
				line.append(formatString(branchCompany.getBranch().getState(),12));
				line.append(String.format("%s", branchCompany.getBranch().getCountry()));

				writer.write(line.toString());
			}
			
			writer.close();
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE08.value());
			reqDTO.setServiceCode(ServiceCode.EPE08.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE08.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE08.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createBranchCompany end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void registeredCategoryOutPutStream(BatchReqDTO<RegisteredCategory> reqDTO) {

		String fileName = "RegisteredCategory.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (RegisteredCategory registeredCategory : reqDTO.getBatchContent()) {
				line = new StringBuffer();

				line.append(System.getProperty("line.separator"));

				line.append(formatString(registeredCategory.getmOFNo(),20));

				line.append(formatString(registeredCategory.getCategory().getMainCat(),12));
				line.append(formatString(registeredCategory.getCategory().getSubCat(),12));
				line.append(formatString(registeredCategory.getCategory().getSubCatDivision(),12));
				line.append(String.format("%s", registeredCategory.getCategory().getStatus()));

				writer.write(line.toString());
			}
			
			writer.close();
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE09.value());
			reqDTO.setServiceCode(ServiceCode.EPE09.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE09.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE09.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createRegisteredCategory end");


		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void governmentProjectOutPutStream(BatchReqDTO<GovernmentProject> reqDTO) {

		String fileName = "GovernmentProject.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (GovernmentProject governmentProject : reqDTO.getBatchContent()) {
				line = new StringBuffer();

				line.append(System.getProperty("line.separator"));

				line.append(formatString(governmentProject.getmOFNo(),20));

				line.append(formatString(governmentProject.getProject().getProjectNo(),50));
				line.append(formatString(governmentProject.getProject().getProjectName(),255));
				line.append(formatString(governmentProject.getProject().getProjectLocation(),255));
				line.append(formatString(governmentProject.getProject().getClient(),255));
				line.append(formatDateEperunding(governmentProject.getProject().getApppointmentDate(),false));
				line.append(formatString(governmentProject.getProject().getCost(),20));
				line.append(formatString(governmentProject.getProject().getServiceDesc(),500));
				line.append(formatString(governmentProject.getProject().getWorkProgress(),1));
				line.append(formatString(governmentProject.getProject().getWorkProgressOthers(),255));
				line.append(formatString(governmentProject.getProject().getConsultantFee(),20));
				line.append(String.format("%s", governmentProject.getProject().getPaymentMethod()));

				writer.write(line.toString());
			}
			
			writer.close();
			
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE10.value());
			reqDTO.setServiceCode(ServiceCode.EPE10.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE10.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE10.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createGovernmentProject end");
			

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void deletedCompanyOutPutStream(BatchReqDTO<DeletedCompany> reqDTO) {
		
		String fileName = "DeletedCompany.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (DeletedCompany deletedCompany : reqDTO.getBatchContent()) {
				line = new StringBuffer();

				line.append(System.getProperty("line.separator"));
				line.append(formatString(deletedCompany.getmOFNo(),20));
				line.append(formatString(deletedCompany.getReason(),2000));
				line.append(formatDateEperunding(deletedCompany.getDateDeleted(),true));

				writer.write(line.toString());
			}
			//upload the txt file to sftp server
			
			writer.close();
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE11.value());
			reqDTO.setServiceCode(ServiceCode.EPE11.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE11.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE11.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createDeletedCompany end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void companyOwnerOutPutStream(BatchReqDTO<CompanyOwner> reqDTO) {

		String fileName = "CompanyOwner.txt";
		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
		
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (CompanyOwner companyOwner : reqDTO.getBatchContent()) {

				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));

				line.append(formatString(companyOwner.getmOFNo(),20));
				
				line.append(formatString(companyOwner.getCompanyName(),100));
				line.append(formatString(companyOwner.getCompanyRegistrationNo(),20));
				line.append(formatString(companyOwner.getOtherRegistrationNo(),30));
				line.append(formatString(companyOwner.getRegistrationAuthority(),4));
				line.append(formatString(companyOwner.getCompanyOwnerSSMNO(),20));
				line.append(formatString(companyOwner.getCompanyOwnerMOFNo(),20));
				line.append(formatBigDecimal(companyOwner.getShareholdingAmount()));
				line.append(formatBigDecimal(companyOwner.getShareholdingPercentage()));
				line.append(formatString(companyOwner.getAddress1(),100));
				line.append(formatString(companyOwner.getAddress2(),100));
				line.append(formatString(companyOwner.getAddress3(),100));
				line.append(formatString(companyOwner.getPostcode(),15));
				line.append(formatString(companyOwner.getCity(),50));
				line.append(formatString(companyOwner.getDistrict(),12));
				line.append(formatString(companyOwner.getState(),12));
				line.append(formatString(companyOwner.getCountry(),12));
				line.append(formatString(companyOwner.getTelCountryCode(),5));
				line.append(formatString(companyOwner.getTelAreaCode(),5));
				line.append(formatString(companyOwner.getTelNo(),20));
				line.append(formatString(companyOwner.getFaxCountryCode(),5));
				line.append(formatString(companyOwner.getFaxAreaCode(),5));
				line.append(String.format("%S", companyOwner.getFaxNo()));

				writer.write(line.toString());
			}
			
			writer.close();
			
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE03.value());
			reqDTO.setServiceCode(ServiceCode.EPE03.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE03.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE03.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createCompanyOwner end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void AssociatedCompanyOutPutStream(BatchReqDTO<AssociatedCompany> reqDTO) {

		String fileName = "AssociatedCompany.txt";
		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line;
		
			writer.write("         " + reqDTO.getBatchContent().size() + " Rows Selected");
			for (AssociatedCompany associatedCompany : reqDTO.getBatchContent()) {

				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));

				line.append(formatString(associatedCompany.getmOFNo(),20));
			
				line.append(formatString(associatedCompany.getCompanyName(),100));
				line.append(formatString(associatedCompany.getAssociatedCompanySSMNO(),20));
				line.append(formatString(associatedCompany.getAssociatedCompanyMOFNO(),20));
				line.append(formatString(associatedCompany.getShareholding(),1));
				line.append(formatString(associatedCompany.getAddress1(),100));
				line.append(formatString(associatedCompany.getAddress2(),100));
				line.append(formatString(associatedCompany.getAddress3(),100));
				line.append(formatString(associatedCompany.getPostcode(),15));
				line.append(formatString(associatedCompany.getCity(),50));
				line.append(formatString(associatedCompany.getDistrict(),12));
				line.append(formatString(associatedCompany.getState(),12));
				line.append(formatString(associatedCompany.getCountry(),12));
				line.append(formatString(associatedCompany.getTelCountryCode(),5));
				line.append(formatString(associatedCompany.getTelAreaCode(),5));
				line.append(formatString(associatedCompany.getTelNo(),20));
				line.append(formatString(associatedCompany.getFaxCountryCode(),5));
				line.append(formatString(associatedCompany.getFaxAreaCode(),5));
				line.append(String.format("%S", associatedCompany.getFaxNo()));

				writer.write(line.toString());
			}
			
			writer.close();
			
			//upload the txt file to sftp server
			
			String transId =  UUID.randomUUID().toString();
			reqDTO.setBatchDirection(IntegrationConstants.BATCH_DIRECTION_OUTBOUND);
			reqDTO.setTargetSystem(IntegrationConstants.TARGET_SYSTEM_EPERUNDING);
			reqDTO.setFileName(fileName);
			reqDTO.setProcessId(ProcessCode.EPE02.value());
			reqDTO.setServiceCode(ServiceCode.EPE02.value());
//			reqDTO.setRemoteFilePath(IntegrationConstants.EPERUNDING_UPLOAD_DIR+fileName);
//			reqDTO.setLocatFilePath(reqDTO.getFilePath()+fileName);
			reqDTO.setTransId(transId);
			
//			sftpUtility.upload(reqDTO);
			
			//once upload is done cal web service osb integration to transfer file.
//			osbUtility.osbServiceCallOutBound(ServiceCode.EPE02.value(),fileName);
			
			// Call JMS send method to send to the Queue.
			reqDTO.setQueueMsgStatus(constructQueueMessageOutbound(ServiceCode.EPE02.value(),fileName, transId));
			
			setValuesToPmInterface(reqDTO);
			
			log.info("createAssociatedCompany end");

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public String formatString(String input,int length) {
		
		String checkInput=null;
		if (input == null)
			return "NULL|";
		if (input.isEmpty() && input.length() == 0)
			return "|";
		if(input.length()<=length)
			checkInput = input;
		else
			checkInput = input.substring(0, length-1);
		return String.format("%s|", checkInput.trim());
	}

	// for ePerunding
	public String formatDateEperunding(Date date,boolean isEOF) {

		if (date == null)
			return "|";

		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");
		if(isEOF)
			return String.format("%s", dt1.format(date));
		else
			return String.format("%s|", dt1.format(date));
	}

	public String formatFloat(float value) {

		return String.format("%.0f|", value);
	}

	public String formatInt(int value) {

		return String.format("%d|", value);

	}
	
	public String formatBigDecimal(BigDecimal value) {

		if(null!=value)
			return value.toString()+"|";
		else
			return "|";
	}
	
	public String formatLong(Long value) {

		if(null!=value)
			return value.toString()+"|";
		else
			return "|";
	}
	public String formatLong(Long value,int length) {

		String checkInput = null;
		if(value!=null){
			checkInput=value.toString();
			if(checkInput.length()>length){
				checkInput = checkInput.substring(0, length);
			}
		}
			return String.format("%s", checkInput);
	}

	public String createDirectory(Date date,String path) {
		
		SimpleDateFormat dt1 = new SimpleDateFormat("ddMMyyyy");

		File file = new File(path+dt1.format(DateUtils.minusDays(date,1)));
		if (!file.exists()) {
			if (file.mkdir()) {
				log.info("Directory is created!");
			} else {
				log.info("Failed to create directory!");
			}
		} else {
			log.info("Directory is already exists!");
		}
		return file.getAbsolutePath()+"/";
	}
	
	public String getValue(String line,int begin,int size){
		  String result = line.substring(begin, begin+size).trim();
		  if(result.isEmpty()){
			  result = IntegrationConstants.EMPTY_TRING;
		  }
		  return result;
	  }
	
	
	public String formatBigDecimalField(BigDecimal value,int size) {

		if(null!=value)
			return String.format("%" + size + "s", value.toString());
		else
			return String.format("%" + size + "s", IntegrationConstants.EMPTY_TRING);

	}
	
	public String findUomCode(Long itemId,List<CmSupplierItem> supplierItems){
		String uomCode = null;
		
		for(CmSupplierItem cmSupplierItem :supplierItems){
			if(cmSupplierItem.getCmItem().getItemId().equals(itemId)){
				uomCode = cmSupplierItem.getPmUom().getUomCode();
			}
		}
		log.info("uomCode :"+uomCode);
		return uomCode;
	}
	
	// E- teriman Integration
	
	/*public void billingInformationFileOutPutStream(List<BillingInformation> content, String filePath) {
		String PROCESS_ID = "AR501";
		
		String fileName = createFileNameGfmas(PROCESS_ID);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				StringBuffer billHeaderLine;
				StringBuffer customerLine;
				StringBuffer chargeLine;
				for (BillingInformation billingInformation : content) {
					line = new StringBuffer();
				
					line.append(formatIntegerField(billingInformation.getRecordType(),	1, "R"));
					line.append(formatStringField(billingInformation.getAgencyId(), 6,	"L"));
					line.append(formatStringField(billingInformation.getTotalBillCount()+"", 3,	"L"));
					
					if(null!=billingInformation.getBillHeaderList() && billingInformation.getBillHeaderList().size()>0){
						
						for(BillHeader billHeader : billingInformation.getBillHeaderList()){
							billHeaderLine = new StringBuffer();
							billHeaderLine.append(formatIntegerField(billHeader.getRecordType(),1,"R"));
							billHeaderLine.append(formatStringField(billHeader.getBillType(), 1, "L"));
							billHeaderLine.append(formatStringField(billHeader.getBillRCreditRDebitNumber(),15, "L"));
							billHeaderLine.append(formatStringField(billHeader.getBillReference(), 15,"L"));
							billHeaderLine.append(formatDate(billHeader.getBillReferenceDate()));
							billHeaderLine.append(formatDate(billHeader.getBillRCreditRDebitDate()));
							billHeaderLine.append(formatDate(billHeader.getPostingDate()));
							billHeaderLine.append(formatStringField(billHeader.getABTIndicator(),1, "L"));
							billHeaderLine.append(formatStringField(billHeader.getYear(), 4,"L"));
							billHeaderLine.append(formatStringField(billHeader.getAccountingOffice(), 4,"L"));
							billHeaderLine.append(formatStringField(billHeader.getPegawaiPengawalPembayar(), 2,"L"));
							billHeaderLine.append(formatStringField(billHeader.getKumpulanPtj(), 8,"L"));
							billHeaderLine.append(formatStringField(billHeader.getCurrency(),	5, "L"));
							billHeaderLine.append(formatIntegerField(billHeader.getCustTotal(), 3,"R"));
							billHeaderLine.append(formatIntegerField(billHeader.getChargeLineTotal(), 3, "R"));
							billHeaderLine.append(System.getProperty("line.separator"));
							writer.write(billHeaderLine.toString());
						}
					}
				if (null!=billingInformation.getCustomerInformationList() && billingInformation.getCustomerInformationList().size() != 0) {
					
					for (CustomerInformation customerInformation  : billingInformation.getCustomerInformationList()) {
						
						customerLine = new StringBuffer();
						customerLine.append(formatIntegerField(customerInformation.getRecordType(),1,"R"));
						customerLine.append(formatStringField(customerInformation.getCustomerIdCode(),20,"L"));
						customerLine.append(formatStringField(customerInformation.getEntityCode(),4,"L"));
						customerLine.append(formatStringField(customerInformation.getName1(),35,"L"));
						customerLine.append(formatStringField(customerInformation.getName2(),35,"L"));
						customerLine.append(formatStringField(customerInformation.getAddress1(),35,"L"));
						customerLine.append(formatStringField(customerInformation.getAddress2(),35,"L"));
						customerLine.append(formatStringField(customerInformation.getAddress3(),35,"L"));
						customerLine.append(formatStringField(customerInformation.getPostCode(),5,"L"));
						customerLine.append(formatStringField(customerInformation.getCity(),35,"L"));
						customerLine.append(formatStringField(customerInformation.getRegion(),3,"L"));
						customerLine.append(formatStringField(customerInformation.getCountry(),2,"L"));
						customerLine.append(formatStringField(customerInformation.getTelNo(),15,"L"));
						customerLine.append(formatStringField(customerInformation.getFaxNo(),15,"L"));
						customerLine.append(formatStringField(customerInformation.getEmailAddress(),100,"L"));
						customerLine.append(formatBigDecimal(customerInformation.getAmount()));
						customerLine.append(formatStringField(customerInformation.getDescription(),50,"L"));
						customerLine.append(formatStringField(customerInformation.getDescription2(),50,"L"));
						customerLine.append(formatStringField(customerInformation.getAltGlReconAcc(),10,"L"));
						customerLine.append(System.getProperty("line.separator"));
						writer.write(customerLine.toString());
						}
					}
				if (null!=billingInformation.getCustomerInformationList() && billingInformation.getCustomerInformationList().size() != 0) {
					
					for (ChargeLine chargeLineObj  : billingInformation.getChargeLineList()) {
						chargeLine = new StringBuffer();
						chargeLine.append(formatIntegerField(chargeLineObj.getRecordType(),1,"R"));
						chargeLine.append(formatIntegerField(chargeLineObj.getRecordNumber(),3,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getDK(),1,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getKodPenjenisanTeriman(),15,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getVotDana(),5,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getPegawaiPengawalDipertanggug(),2,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getKumpulanPtjNptjDipertanggug(),8,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getProgramAktiviti(),9,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getProjek(),16,"L"));
						chargeLine.append(formatStringField(chargeLineObj.getKodAkaun(),8,"L"));
						chargeLine.append(formatBigDecimal(chargeLineObj.getAmount()));
						chargeLine.append(System.getProperty("line.separator"));
						writer.write(chargeLine.toString());
						writer.flush();
					}
				}
				}
				// Encryption 
				encryption.encryptGmas(filePath,IntegrationConstants.ENCRYPTED_FILE_PATH, fileName,PROCESS_ID);
				}else{
				log.info("content is null");
			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}*/
	
	
	public void receiptInformationFileOutPutStream(BatchReqDTO<ReceiptInformation> batchReqDTO) {
		
	String PROCESS_ID = "AR502";
	
	String fileName = createFileNameGfmas(PROCESS_ID);

	Writer writer = null;

	try {
		writer = new BufferedWriter(new OutputStreamWriter(	new FileOutputStream(batchReqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer line = null;
			StringBuffer receiptHeaderLine = null;
			StringBuffer receiptDetailsLine = null;
			StringBuffer billInfoLine = null;
			for (ReceiptInformation receiptInformation : batchReqDTO.getBatchContent()) {
				line = new StringBuffer();
			
				line.append(formatIntegerField(receiptInformation.getRecordType(),	1, "L"));
				line.append(formatStringField(receiptInformation.getAgencyId(), 6,	"L"));
				line.append(formatIntegerField(receiptInformation.getTotalBillCount(), 4,	"R"));
				line.append(System.getProperty("line.separator"));
//				writer.write(line.toString());
				
				if(null!=receiptInformation.getReceiptHeaderList() && receiptInformation.getReceiptHeaderList().size()>0){
					
					for(ReceiptHeader receiptHeader : receiptInformation.getReceiptHeaderList()){
						receiptHeaderLine = new StringBuffer();
						receiptHeaderLine.append(formatIntegerField(receiptHeader.getRecordType(),1,"R"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getReceiptType(), 1, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getReceiptNumberNew(),20, "L"));
						receiptHeaderLine.append(formatDate(receiptHeader.getReceiptDateNew()));
						receiptHeaderLine.append(formatDate(receiptHeader.getReceiptPostingDateNew()));
						receiptHeaderLine.append(formatStringField(receiptHeader.getReceiptNumberOld(),20,"L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getChequeNumberOld(),50,"L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getSpickCodeOld(),10, "L"));
						receiptHeaderLine.append(formatIntegerField(receiptHeader.getYear(), 4,"R"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getAccountingOffice(), 4,"L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getPegawaiPengawalPembeyar(), 2,"L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getKumpulanPtjNPembeyar(), 8,"L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getCurrency(),5, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getDescription1(),50, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getDescription2(),50, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getCustomerIdCode(),20, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getEntityCode(),4, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getName1(),35, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getName2(),35, "L"));
						receiptHeaderLine.append(formatStringField(receiptHeader.getAlternateGlReconAcc(),10, "L"));
						receiptHeaderLine.append(formatBigDecimalField(receiptHeader.getReceiptAmount(),13));
						receiptHeaderLine.append(formatStringField(receiptHeader.getBankInStatus(),1,"L"));
						receiptHeaderLine.append(formatIntegerField(receiptHeader.getReceiptDetailsCount(),3,"R"));
						receiptHeaderLine.append(formatIntegerField(receiptHeader.getBillingCount(),3,"R"));
						receiptHeaderLine.append(System.getProperty("line.separator"));
					//	writer.write(receiptHeaderLine.toString());
						line.append(receiptHeaderLine.toString());
					}
				}
			if (null!=receiptInformation.getReceiptDetailsList() && receiptInformation.getReceiptDetailsList().size() > 0) {
				
				log.info("receiptInformation.getReceiptDetailsList().size() :"+receiptInformation.getReceiptDetailsList().size());
				for (ReceiptDetails receiptDetails  : receiptInformation.getReceiptDetailsList()) {
					receiptDetailsLine = new StringBuffer();
					receiptDetailsLine.append(formatIntegerField(receiptDetails.getRecordType(),1,"R"));
					receiptDetailsLine.append(formatIntegerField(receiptDetails.getRecordNumber(),3,"R"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getReceiptNumber(),20,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getKodPenjenisanTerimaan(),15,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getVotDana(),5,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getPegawaiPegawalDipertanggung(),2,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getKumpulanPtjDipertanggung(),8,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getProgramAktiviti(),9,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getProjek(),16,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getKodAkaun(),8,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getPaymentMethod(),1,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getReferenceNo(),50,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getSpickCode(),10,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getSwiftCode(),11,"L"));
					receiptDetailsLine.append(formatStringField(receiptDetails.getBranchName(),50,"L"));
					receiptDetailsLine.append(formatDate(receiptDetails.getPaymentDate()));
					receiptDetailsLine.append(formatBigDecimalField(receiptDetails.getAmount(),13));
					receiptDetailsLine.append(System.getProperty("line.separator"));
					line.append(receiptDetailsLine.toString());
//					writer.write(receiptDetailsLine.toString());
//					log.info("receiptDetailsLine.toString() :"+receiptDetailsLine.toString());
//					writer.flush();
					}
				}
			if (null!=receiptInformation.getBillingInformationLineList() && receiptInformation.getBillingInformationLineList().size() > 0) {
				
				log.info("receiptInformation.getBillingInformationLineList().size() :"+receiptInformation.getBillingInformationLineList().size());
				for (BillingInformationLine billingInformationLine  : receiptInformation.getBillingInformationLineList()) {
					billInfoLine = new StringBuffer();
					billInfoLine.append(formatIntegerField(billingInformationLine.getRecordType(),1,"R"));
					billInfoLine.append(formatIntegerField(billingInformationLine.getRecordNumber(),3,"R"));
					billInfoLine.append(formatStringField(billingInformationLine.getReceiptNumber(),20,"L"));
					billInfoLine.append(formatStringField(billingInformationLine.getBillDebitCreditNoteNum(),15,"L"));
					billInfoLine.append(formatDate(billingInformationLine.getBillDebitCreditNoteDate()));
					billInfoLine.append(formatStringField(billingInformationLine.getDescription1(),50,"L"));
					billInfoLine.append(formatStringField(billingInformationLine.getDescription2(),50,"L"));
					billInfoLine.append(formatStringField(billingInformationLine.getCustomerIdCode(),20,"L"));
					billInfoLine.append(formatStringField(billingInformationLine.getEntityCode(),4,"L"));
					billInfoLine.append(formatBigDecimalField(billingInformationLine.getAmountOfbillDebitCreditNote(),13));
					billInfoLine.append(formatBigDecimalField(billingInformationLine.getAmountReceivedbillDebitCreditNote(),13));
					billInfoLine.append(System.getProperty("line.separator"));
					line.append(billInfoLine.toString());
//					writer.write(billInfoLine.toString());
//					log.info("billInfoLine.toString() :"+billInfoLine.toString());
//					writer.flush();
				}
			}
			writer.write(line.toString());
			}
			batchReqDTO.setFileName(fileName);
			batchReqDTO.setEncryptFilePath(IntegrationConstants.GMAS_UPLOAD_LOCAL);
			batchReqDTO.setServiceCode(ServiceCode.AR502.value());
			batchReqDTO.setProcessId(ProcessCode.AR502.value());
			// Encryption 
			encryption.encryptGmas(batchReqDTO);
	} catch (IOException e) {

	} finally {

		if (writer != null) {
			try {
				writer.close();
			} catch (Exception e) {

			}
		}
	}
}
	
	// Error file
	public void errorFileOutPutStream(BatchReqDTO<EPErrorResponse> reqDTO) {
		System.out.println("inside errorFileOutPutStream : GLMDE");
		String fileName = createFileNameGfmas(ProcessCode.GLMDE.value());
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			StringBuffer errResLine = null;
			StringBuffer errDtlLine = null;
			if (reqDTO.getBatchObject() != null) {
				errResLine = new StringBuffer();
				errResLine.append(formatStringField(reqDTO.getBatchObject().getRecordType(), 1, "L"));
				errResLine.append(formatIntegerField(reqDTO.getBatchObject().getTahunKewangan(), 4, "R"));
				errResLine.append(formatStringField(reqDTO.getBatchObject().getNamaFailAsal(), 30,"L"));
				errResLine.append(System.getProperty("line.separator"));
				writer.write(errResLine.toString());
				if(null!=reqDTO.getBatchObject().getErrorDetailList()){
					for(ErrorDetail errorDetail:reqDTO.getBatchObject().getErrorDetailList()){
						errDtlLine = new StringBuffer();
						errDtlLine.append(formatStringField(errorDetail.getRecordType(),1, "L"));
						errDtlLine.append(formatIntegerField(errorDetail.getLineNo(), 10, "R"));
						errDtlLine.append(formatStringField(errorDetail.getErrorDescription(), 255, "L"));
						errDtlLine.append(System.getProperty("line.separator"));
						writer.write(errDtlLine.toString());
						writer.flush();
					}
				}
				writer.close();
				reqDTO.setEncryptFilePath(IntegrationConstants.GMAS_UPLOAD_LOCAL);
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(ServiceCode.GLMDE.value());
				reqDTO.setProcessId(ProcessCode.GLMDE.value());
				encryption.encryptGmas(reqDTO);

			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	public List<GFMASError> gfmasErrorInPutStream(String filePath) {
		Path path = Paths.get(filePath);
		String line;
		List<GFMASError> gfmasErrorList = new ArrayList<GFMASError>();
		List<ErrorDesc> errorDescList = null;
		GFMASError gfmasError = null;
		ErrorDesc errorDesc = null;

		try (Scanner scanner = new Scanner(path,
				IntegrationConstants.ENCODING.name())) {

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				 if(line.startsWith(IntegrationConstants.VALUE_ZERO)){
					 gfmasError = new GFMASError();
					 errorDescList = new ArrayList<ErrorDesc>();
					 gfmasError.setRecordType(IntegrationConstants.RECORD_TYPE_ZERO);
					 gfmasError.setAgencyId(getValue(line, 1, 6));
					 gfmasError.setTotalRecordCount(Integer.valueOf(getValue(line, 7, 3)));
					 
				 }else if(line.startsWith(IntegrationConstants.VALUE_ONE)){
					 errorDesc = new ErrorDesc();
					 errorDesc.setRecordType(IntegrationConstants.RECORD_TYPE_ONE);
					 errorDesc.setReferenceNo(getValue(line, 1, 20));
					 errorDesc.setErrorCode(getValue(line, 21, 6));
					 errorDesc.setErrorDescription(getValue(line, 27, 255));
					 errorDescList.add(errorDesc);
					 
					 if(gfmasError.getTotalRecordCount()==errorDescList.size()){
						 gfmasError.setErrorDescs(errorDescList);
						 gfmasErrorList.add(gfmasError);
					 }
				 }

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gfmasErrorList;
	}
	
	@SuppressWarnings("rawtypes")
	public void setValuesToPmInterface(BatchReqDTO reqDTO){
	
		PmInterfaceLog pmInterfaceLog = new PmInterfaceLog();
		
		pmInterfaceLog.setDirection(reqDTO.getBatchDirection());
		pmInterfaceLog.setDataStartTime(reqDTO.getDataStartTime());
		pmInterfaceLog.setDataEndTime(reqDTO.getDataEndTime());
		pmInterfaceLog.setFileCreationStatus(reqDTO.getFileCreationStatus());
		pmInterfaceLog.setFtpStatus(reqDTO.getFileFTPStatus());
		pmInterfaceLog.setFrequency(1);
		pmInterfaceLog.setIsCurrent(IntegrationConstants.ROW_STATUS_CURRENT);
		pmInterfaceLog.setTargetSystem(reqDTO.getTargetSystem());
		pmInterfaceLog.setServiceCode(reqDTO.getServiceCode());
		pmInterfaceLog.setProcessId(reqDTO.getProcessId());
		pmInterfaceLog.setFileName(reqDTO.getFileName());
		pmInterfaceLog.setTransId(reqDTO.getTransId());
		pmInterfaceLog.setQueueMsgStatus(reqDTO.getQueueMsgStatus());
		
		pmInterfaceLog.setRecordStatusFlg(IntegrationConstants.RECORD_STATUS_ACTIVE);
		pmInterfaceLog.setCreatedBy(IntegrationConstants.BATCH_USER);
		pmInterfaceLog.setChangedBy(IntegrationConstants.BATCH_USER);
		pmInterfaceLog.setCreatedDate(new Timestamp(new Date().getTime()));
		pmInterfaceLog.setCreatedDate(new Timestamp(new Date().getTime()));
		
		interfaceSO.saveInterfaceLOg(pmInterfaceLog);
	}
	
	
	
	// Changed method for BatchReqDTO changed method signatures
	public void votFundFileOutPutStream(BatchReqDTO<PhisVotFund> reqDTO) {
		log.info("inside votFundFileOutPutStream : PHS-090");
		String SERVICE_CODE = ServiceCode.PHS006.value();

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (PhisVotFund votFund : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatIntegerField(votFund.getYear(), 4, "R"));
					line.append(formatStringField(votFund.getCode()+votFund.getPtjCode() , 5, "L"));
					line.append(formatStringField(votFund.getDescription(),255, "L"));
					line.append(formatDate(votFund.getEffDate()));
					line.append(formatDate(votFund.getExpDate()));
					line.append(formatStringField(votFund.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
				writer.close();
				reqDTO.setProcessId(ProcessCode.PHS006.value());
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(SERVICE_CODE);
				reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
				reqDTO.setRemoteFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL+fileName);

				setValuesToPmInterface(encryption.encryptPhis(reqDTO));
			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	// Changed method for BatchReqDTO
	public void votFundDanaFileOutPutStream(BatchReqDTO<PhisVotFund> reqDTO) {
		log.info("inside votFundDanaFileOutPutStream : PHS-100");
		String SERVICE_CODE = ServiceCode.PHS007.value();

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (PhisVotFund votFund : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatIntegerField(votFund.getYear(), 4, "R"));
					line.append(formatStringField(votFund.getCode(), 4, "L"));
					line.append(formatStringField(votFund.getDescription(),255, "L"));
					line.append(formatDate(votFund.getEffDate()));
					line.append(formatDate(votFund.getExpDate()));
					line.append(formatStringField(votFund.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
				writer.close();
				reqDTO.setProcessId(ProcessCode.PHS007.value());
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(SERVICE_CODE);
				reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
			
//				encryption.encryptPhis(reqDTO);
				
				setValuesToPmInterface(encryption.encryptPhis(reqDTO));

			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	// Changed method for BatchReqDTO
	public void programActivityFileOutPutStream(BatchReqDTO<PhisProgramActivity> reqDTO) {
		log.info("inside programActivityFileOutPutStream : PHS-110");
		String SERVICE_CODE = ServiceCode.PHS008.value();

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (PhisProgramActivity prgActivity : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatIntegerField(prgActivity.getYear(), 4,"R"));
					line.append(formatStringField(prgActivity.getVotCode()+ prgActivity.getCode(), 9, "L"));
					line.append(formatStringField(prgActivity.getDescription(),255, "L"));
					line.append(formatDate(prgActivity.getEffDate()));
					line.append(formatDate(prgActivity.getExpDate()));
					line.append(formatStringField(prgActivity.getStatus(), 1,"L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
				writer.close();
				reqDTO.setProcessId(ProcessCode.PHS008.value());
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(SERVICE_CODE);
				reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
				
//				encryption.encryptPhis(reqDTO);
				setValuesToPmInterface(encryption.encryptPhis(reqDTO));

			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	// Changed method for BatchReqDTO
	public void projectFileOutPutStream(BatchReqDTO<PhisProject> reqDTO) {
		log.info("inside projectFileOutPutStream :PHS-120");
		String SERVICE_CODE = ServiceCode.PHS009.value();

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (PhisProject project : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatIntegerField(project.getYear(), 4, "R"));
					line.append(formatStringField(project.getVotCode()+ project.getCode() + project.getSetiaCode()
							+ project.getSubSetiaCode()+project.getCaraPembiayaan(), 16, "L"));
					line.append(formatStringField(project.getDescription(),255, "L"));
					line.append(formatDate(project.getEffDate()));
					line.append(formatDate(project.getExpDate()));
					line.append(formatStringField(project.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
				writer.close();
				reqDTO.setProcessId(ProcessCode.PHS009.value());
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(SERVICE_CODE);
				reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//				encryption.encryptPhis(reqDTO);
				setValuesToPmInterface(encryption.encryptPhis(reqDTO));

			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	// Changed method for BatchReqDTO
	public void glAccountFileOutPutStream(BatchReqDTO<PhisGlAccount> reqDTO) {
		log.info("inside glAccountFileOutPutStream : PHS-140");
		String SERVICE_CODE = ServiceCode.PHS010.value();

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (PhisGlAccount glAccount : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatIntegerField(glAccount.getYear(), 4, "R"));
					line.append(formatStringField(glAccount.getCode(), 8, "L"));
					line.append(formatStringField(glAccount.getPtjCode(), 2,"L"));
					line.append(formatStringField(glAccount.getDescription(),255, "L"));
					line.append(formatDate(glAccount.getEffDate()));
					line.append(formatDate(glAccount.getExpDate()));
					line.append(formatStringField(glAccount.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
				writer.close();
				reqDTO.setProcessId(ProcessCode.PHS010.value());
				reqDTO.setFileName(fileName);
				reqDTO.setServiceCode(SERVICE_CODE);
				reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//				encryption.encryptPhis(reqDTO);
				setValuesToPmInterface(encryption.encryptPhis(reqDTO));
			}
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void materialClassFileOutPutStream(BatchReqDTO<MaterialClass> reqDTO) {
		log.info("inside materialClassFileOutPutStream : PHS-030");
		String SERVICE_CODE = ServiceCode.PHS001.value();

		Writer writer = null;
		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				log.info("content no equal to zero....");
				StringBuffer line;
				for (MaterialClass mclass : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatStringField(mclass.getMaterialClassCode(), 8, "L"));
					line.append(formatStringField(mclass.getMaterialClassDescription(), 200, "L"));
					line.append(formatStringField(mclass.getMaterialClassAction(), 3, "L"));
					line.append(formatStringField(mclass.getMaterialOldClassCode(), 8, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
			}
			writer.close();
			reqDTO.setProcessId(ProcessCode.PHS001.value());
			reqDTO.setFileName(fileName);
			reqDTO.setServiceCode(SERVICE_CODE);
			reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//			encryption.encryptPhis(reqDTO);
			setValuesToPmInterface(encryption.encryptPhis(reqDTO));
			
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void materialBrandFileOutPutStream(BatchReqDTO<MaterialBrand> reqDTO) {
		log.info("inside materialBrandFileOutPutStream :PHS-040");
		String SERVICE_CODE = ServiceCode.PHS002.value();
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				log.info("content no equal to zero....");
				StringBuffer line;
				for (MaterialBrand mbrand : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatStringField(mbrand.getUnspscCode(), 8,"L"));
					line.append(formatStringField(mbrand.getMaterialBrandCode(), 2, "L"));  
					line.append(formatStringField(mbrand.getMaterialBrandDescription(), 150, "L"));
					line.append(formatStringField(mbrand.getMaterialBrandAction(), 3, "L"));
					line.append(formatStringField(mbrand.getMaterialOldBrandCode(), 3, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
			}
			writer.close();
			reqDTO.setProcessId(ProcessCode.PHS002.value());
			reqDTO.setFileName(fileName);
			reqDTO.setServiceCode(SERVICE_CODE);
			reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//			encryption.encryptPhis(reqDTO);
			setValuesToPmInterface(encryption.encryptPhis(reqDTO));
			
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void materialColorFileOutPutStream(BatchReqDTO<MaterialColor> reqDTO) {
		log.info("inside materialColorFileOutPutStream : PHS-050");
		String SERVICE_CODE = ServiceCode.PHS003.value();
		Writer writer = null;
		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				
				StringBuffer line;
				for (MaterialColor mcolor : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatStringField(mcolor.getMaterialColorCode(), 2, "L"));
					line.append(formatStringField(mcolor.getMaterialColorDescription(), 150, "L"));
					line.append(formatStringField(mcolor.getMaterialColorAction(), 3, "L"));
					line.append(formatStringField(mcolor.getMaterialOldColorCode(), 2, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
			}
			writer.close();
			reqDTO.setProcessId(ProcessCode.PHS003.value());
			reqDTO.setFileName(fileName);
			reqDTO.setServiceCode(SERVICE_CODE);
			reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//			encryption.encryptPhis(reqDTO);
			setValuesToPmInterface(encryption.encryptPhis(reqDTO));
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void materialMeasurementFileOutPutStream(BatchReqDTO<MaterialMeasurement> reqDTO) {
		log.info("inside materialMeasurementFileOutPutStream : PHS-060");
		String SERVICE_CODE = ServiceCode.PHS004.value();
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (MaterialMeasurement mMeasurement : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatStringField(mMeasurement.getUnspscCode(), 8,"L"));
					line.append(formatStringField(mMeasurement.getMeasurementCode(), 3, "L"));
					line.append(formatStringField(mMeasurement.getMeasurementDescription(), 150, "L"));
					line.append(formatStringField(mMeasurement.getMeasurementAction(), 3, "L"));
					line.append(formatStringField(mMeasurement.getMeasurementOldCode(), 3, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}
			}
			writer.close();
			reqDTO.setProcessId(ProcessCode.PHS004.value());
			reqDTO.setFileName(fileName);
			reqDTO.setServiceCode(SERVICE_CODE);
			reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//			encryption.encryptPhis(reqDTO);
			setValuesToPmInterface(encryption.encryptPhis(reqDTO));
		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void itemTypeFileOutPutStream(BatchReqDTO<ItemType> reqDTO) {
		log.info("inside itemTypeFileOutPutStream : PHS-070");
		String SERVICE_CODE = ServiceCode.PHS005.value();
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (ItemType itemType : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatStringField(itemType.getUnspscCode(), 8,"L"));
					line.append(formatStringField(itemType.getItemCode(), 3,"L"));
					line.append(formatStringField(itemType.getItemDescription(), 150, "L"));
					line.append(formatStringField(itemType.getItemAction(), 3,"L"));
					line.append(formatStringField(itemType.getItemOldcode(), 3,	"L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}

			}
			writer.close();
			reqDTO.setProcessId(ProcessCode.PHS005.value());
			reqDTO.setFileName(fileName);
			reqDTO.setServiceCode(SERVICE_CODE);
			reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//			encryption.encryptPhis(reqDTO);
			setValuesToPmInterface(encryption.encryptPhis(reqDTO));

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void paymentEffNootifyOutPutStream(BatchReqDTO<PaymentEffNotify> reqDTO) {
		log.info("inside itemTypeFileOutPutStream : PHS-200");
		String SERVICE_CODE = ServiceCode.PHS011.value();
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(reqDTO.getFilePath() + fileName), "utf-8"));

			if (reqDTO.getBatchContent().size() != 0) {
				StringBuffer line;
				for (PaymentEffNotify effNotify : reqDTO.getBatchContent()) {
					line = new StringBuffer();
					line.append(formatStringField(effNotify.getContractOrderNo(), 17,"L"));
					line.append(formatStringField(effNotify.getPaymenyReferenceNo(), 18,"L"));
					line.append(formatStringField(effNotify.getInvoiceNo(), 30, "L"));
					line.append(formatStringField(effNotify.getInvoiceRefNo(), 17,"L"));
					line.append(formatBigDecimalField(effNotify.getAmountPaid(), 13));
					line.append(formatDate(effNotify.getPaymentDate()));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					writer.flush();
				}

			}
			writer.close();
			reqDTO.setProcessId(ProcessCode.PHS011.value());
			reqDTO.setFileName(fileName);
			reqDTO.setServiceCode(SERVICE_CODE);
			reqDTO.setEncryptFilePath(IntegrationConstants.PHIS_UPLOAD_DIR_LOCAL);
//			encryption.encryptPhis(reqDTO);
			setValuesToPmInterface(encryption.encryptPhis(reqDTO));

		} catch (IOException e) {

		} finally {

			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	public void deleteFilefromAppServer(String fileName){
		try {

			File file = new File(fileName);

			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	//Error processing 
	
	public ErrorDetail ConstructErrorDetailsObject(int lineNo,String errInfo){
		ErrorDetail errorDetail = new ErrorDetail();
		errorDetail.setRecordType(IntegrationConstants.RECORD_TYPE_L);
		errorDetail.setLineNo(lineNo);
		errorDetail.setErrorDescription(errInfo);
		return errorDetail;
	}
	
	public void errorFileOutBound(EPErrorResponse errorResponse) {
		if(errorResponse!=null){
			BatchReqDTO<EPErrorResponse> reqDTO = new BatchReqDTO<EPErrorResponse>();
			reqDTO.setFilePath(IntegrationConstants.FILE_PATH_LOCAL);
			reqDTO.setBatchObject(errorResponse);
			errorFileOutPutStream(reqDTO);
		}
	}
	
	
}
