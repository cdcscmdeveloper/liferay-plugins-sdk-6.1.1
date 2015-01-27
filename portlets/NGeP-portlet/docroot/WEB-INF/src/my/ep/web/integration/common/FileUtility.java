package my.ep.web.integration.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import my.ep.web.integration.eperunding.BoardOfDirector;
import my.ep.web.integration.eperunding.ConsultantProfile;
import my.ep.web.integration.eperunding.EquityOwner;
import my.ep.web.integration.eperunding.ManagementPersonnel;
import my.ep.web.integration.eperunding.Signatory;
import my.ep.web.integration.gfmas.BankAccountInformation;
import my.ep.web.integration.gfmas.GlAccount;
import my.ep.web.integration.gfmas.MasterDataMaterialInfo;
import my.ep.web.integration.gfmas.MasterDataVendor;
import my.ep.web.integration.gfmas.ProgramActivity;
import my.ep.web.integration.gfmas.Project;
import my.ep.web.integration.gfmas.SSTInformation;
import my.ep.web.integration.gfmas.VotFund;
import my.ep.web.integration.phis.ItemType;
import my.ep.web.integration.phis.MaterialBrand;
import my.ep.web.integration.phis.MaterialClass;
import my.ep.web.integration.phis.MaterialColor;
import my.ep.web.integration.phis.MaterialMeasurement;

public class FileUtility {

	/**
	 * Write character data to a big file - use BufferedWriter
	 */
	public void materialClassFileOutPutStream(List<MaterialClass> content,
			String filePath) {
		System.out.println("inside materialClassFileOutPutStream");
		String SERVICE_CODE = "PHS-030";

		Writer writer = null;
		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (MaterialClass mclass : content) {
					line = new StringBuffer();
					line.append(formatStringField(
							mclass.getMaterialClassCode(), 8, "L"));
					line.append(formatStringField(
							mclass.getMaterialClassDescription(), 2000, "L"));
					line.append(formatStringField(
							mclass.getMaterialClassAction(), 3, "L"));
					line.append(formatStringField(
							mclass.getMaterialOldClassCode(), 8, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

			}
			Encryption encryption = new Encryption();
			encryption.encryptPhis(filePath,
					IntegrationConstants.ENCRYPTED_FILE_PATH, fileName, SERVICE_CODE);
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

	public void materialBrandFileOutPutStream(List<MaterialBrand> content,
			String filePath) {
		System.out.println("inside materialBrandFileOutPutStream");
		String SERVICE_CODE = "PHS-040";
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (MaterialBrand mbrand : content) {
					line = new StringBuffer();
					line.append(formatStringField(
							mbrand.getMaterialBrandCode(), 3, "L"));
					line.append(formatStringField(
							mbrand.getMaterialBrandDescription(), 150, "L"));
					line.append(formatStringField(
							mbrand.getMaterialBrandAction(), 3, "L"));
					line.append(formatStringField(
							mbrand.getMaterialOldBrandCode(), 3, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

			}
			Encryption encryption = new Encryption();
			encryption.encryptPhis(filePath,
					IntegrationConstants.ENCRYPTED_FILE_PATH, fileName, SERVICE_CODE);
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

	public void materialColorFileOutPutStream(List<MaterialColor> content,
			String filePath) {
		System.out.println("inside materialColorFileOutPutStream..");
		String SERVICE_CODE = "PHS-050";
		Writer writer = null;
		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (MaterialColor mcolor : content) {
					line = new StringBuffer();
					line.append(formatStringField(
							mcolor.getMaterialColorCode(), 2, "L"));
					line.append(formatStringField(
							mcolor.getMaterialColorDescription(), 150, "L"));
					line.append(formatStringField(
							mcolor.getMaterialColorAction(), 3, "L"));
					line.append(formatStringField(
							mcolor.getMaterialOldColorCode(), 2, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

			}
			Encryption encryption = new Encryption();
			encryption.encryptPhis(filePath,
					IntegrationConstants.ENCRYPTED_FILE_PATH, fileName, SERVICE_CODE);
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

	public void materialMeasurementFileOutPutStream(
			List<MaterialMeasurement> content, String filePath) {
		System.out.println("inside materialMeasurementFileOutPutStream");
		String SERVICE_CODE = "PHS-060";
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (MaterialMeasurement mMeasurement : content) {
					line = new StringBuffer();
					line.append(formatStringField(
							mMeasurement.getMeasurementCode(), 3, "L"));
					line.append(formatStringField(
							mMeasurement.getMeasurementDescription(), 150, "L"));
					line.append(formatStringField(
							mMeasurement.getMeasurementAction(), 3, "L"));
					line.append(formatStringField(
							mMeasurement.getMeasurementOldCode(), 3, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

			}
			Encryption encryption = new Encryption();
			encryption.encryptPhis(filePath,
					IntegrationConstants.ENCRYPTED_FILE_PATH, fileName, SERVICE_CODE);
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

	public void itemTypeFileOutPutStream(List<ItemType> content, String filePath) {
		System.out.println("inside itemTypeFileOutPutStream ");
		String SERVICE_CODE = "PHS-070";
		Writer writer = null;

		String fileName = createFileNamePhis(SERVICE_CODE);

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (ItemType itemType : content) {
					line = new StringBuffer();
					line.append(formatStringField(itemType.getItemCode(), 2,
							"L"));
					line.append(formatStringField(
							itemType.getItemDescription(), 150, "L"));
					line.append(formatStringField(itemType.getItemAction(), 3,
							"L"));
					line.append(formatStringField(itemType.getItemOldcode(), 3,
							"L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

			}
			// encript the content and put in a location

			Encryption encryption = new Encryption();
			encryption.encryptPhis(filePath,IntegrationConstants.ENCRYPTED_FILE_PATH, fileName, SERVICE_CODE);

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

	// 1GFMAS

	public void masterDataVendorFileOutPutStream(
			List<MasterDataVendor> content, String filePath) {
		String PROCESS_ID = "APIVE";

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath
							+ createFileNameGfmas(PROCESS_ID)), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				StringBuffer subLine;
				for (MasterDataVendor mDataVendor : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(mDataVendor.getRecordType(),
							1, "R"));
					line.append(formatStringField(mDataVendor.getAgencyId(), 6,
							"L"));
					line.append(formatStringField(mDataVendor.getVendorIdCode()
							.replaceAll("-", ""), 20, "L"));
					line.append(formatStringField(mDataVendor.getEntityCode(),
							4, "L"));
					line.append(formatStringField(mDataVendor.getName(), 35,
							"L"));
					line.append(formatStringField(mDataVendor.getName2(), 35,
							"L"));
					line.append(formatStringField(mDataVendor.getAddress(), 35,
							"L"));
					line.append(formatStringField(mDataVendor.getAddress2(),
							35, "L"));
					line.append(formatStringField(mDataVendor.getAddress3(),
							35, "L"));
					line.append(formatStringField(mDataVendor.getPostCode(), 5,
							"L"));
					line.append(formatStringField(mDataVendor.getCity(), 35,
							"L"));
					line.append(formatStringField(mDataVendor.getRegion(), 3,
							"L"));
					line.append(formatStringField(mDataVendor.getCountry(), 2,
							"L"));
					line.append(formatStringField(mDataVendor.getTelephoneNo(),
							15, "L"));
					line.append(formatStringField(mDataVendor.getFaxNo(), 15,
							"L"));
					line.append(formatStringField(
							mDataVendor.getEmailAddress(), 100, "L"));
					line.append(formatStringField(
							mDataVendor.getGlReconAccount(), 10, "L"));
					line.append(formatStringField(mDataVendor.getCurrency(), 5,
							"L"));
					line.append(formatStringField(mDataVendor.getIncoTerm(), 3,
							"L"));
					line.append(formatStringField(mDataVendor.getPaymentTerm(),
							4, "L"));
					line.append(formatIntegerField(
							mDataVendor.getBankAccountInfoCount(), 3, "R"));
					line.append(formatIntegerField(mDataVendor.getStatus(), 1,
							"R"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
					if (mDataVendor.getBankAccountInfoList().size() != 0) {
						for (BankAccountInformation bankInfo : mDataVendor
								.getBankAccountInfoList()) {
							subLine = new StringBuffer();
							subLine.append(formatIntegerField(
									bankInfo.getRecordType(), 1, "R"));
							subLine.append(formatIntegerField(
									bankInfo.getRecordNumber(), 3, "R"));
							subLine.append(formatStringField(
									bankInfo.getBankCountry(), 2, "L"));
							subLine.append(formatStringField(
									bankInfo.getBankKey(), 15, "L"));
							subLine.append(formatStringField(
									bankInfo.getBankAccount(), 18, "L"));
							subLine.append(formatStringField(
									bankInfo.getCurrency(), 5, "L"));
							subLine.append(formatStringField(
									bankInfo.getAccountHolderName(), 60, "L"));
							subLine.append(System.getProperty("line.separator"));
							writer.write(subLine.toString());
						}

					}

				}
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

	public void sstInformationFileOutPutStream(List<SSTInformation> content,
			String filePath) {
		String PROCESS_ID = "AP516";
		
		String fileName = createFileNameGfmas(PROCESS_ID);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath
							+ fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (SSTInformation sstInfo : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(sstInfo.getRecordType(), 1,"R"));
					line.append(formatStringField(sstInfo.getSSTNumber(), 17,"L"));
					line.append(formatStringField(sstInfo.getPhysicalSSTNumber(), 100, "L"));
					line.append(formatStringField(sstInfo.getSapVendorCode(),10, "L"));
					line.append(formatStringField(sstInfo.getKumpPtj(), 8, "L"));
					line.append(formatStringField(sstInfo.getSstAcknowledgeDate(), 8, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}
				
				Encryption encryption = new Encryption();
				encryption.encryptGmas(filePath,IntegrationConstants.ENCRYPTED_FILE_PATH, fileName,"GFM-350");

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
	

	public List<MasterDataVendor> masterDataVendorFileInPutStream(String filePath) {
			String PROCESS_ID = "APOVE";
			Path path = Paths.get(filePath);
		    String line;
		    List<MasterDataVendor> masterDataVendorList = new ArrayList<MasterDataVendor>();
		    MasterDataVendor masterDataVendor = null;
		    BankAccountInformation bankAccountInformation = null;
			
			  try (Scanner scanner =  new Scanner(path, IntegrationConstants.ENCODING.name())){
				  
				  FileUtility fileUtility = new FileUtility();
			      while (scanner.hasNextLine()){
			    	  line = scanner.nextLine();
			    	  if(line.startsWith(IntegrationConstants.VALUE_ZERO)){
			    		  masterDataVendor = new MasterDataVendor();
			    		 // masterDataVendor.setRecordType(Integer.parseInt(fileUtility.getValue(line,0 ,1 )));
			    		  masterDataVendor.setRecordType(0);
			    		  masterDataVendor.setAgencyId(fileUtility.getValue(line,1 ,6 ));
			    		  masterDataVendor.setVendorIdCode(fileUtility.getValue(line,7 ,20 ));
			    		  masterDataVendor.setEntityCode(fileUtility.getValue(line,27 ,4 ));
			    		  masterDataVendor.setSapVendorCode(fileUtility.getValue(line,31 ,10 ));
			    		  masterDataVendor.setName(fileUtility.getValue(line,41 ,35 ));
			    		  masterDataVendor.setName2(fileUtility.getValue(line,76 ,35 ));
			    		  masterDataVendor.setAddress(fileUtility.getValue(line,111 ,35 ));
			    		  masterDataVendor.setAddress2(fileUtility.getValue(line,146 ,35 ));
			    		  masterDataVendor.setAddress3(fileUtility.getValue(line,181 ,35 ));
			    		  masterDataVendor.setPostCode(fileUtility.getValue(line,216 ,5 ));
			    		  masterDataVendor.setCity(fileUtility.getValue(line,221 ,35 ));
			    		  masterDataVendor.setRegion(fileUtility.getValue(line,256 ,3 ));
			    		  masterDataVendor.setCountry(fileUtility.getValue(line,259 ,2 ));
			    		  masterDataVendor.setTelephoneNo(fileUtility.getValue(line,261 ,15 ));
			    		  masterDataVendor.setFaxNo(fileUtility.getValue(line,276 ,15 ));
			    		  masterDataVendor.setEmailAddress(fileUtility.getValue(line,291 ,100 ));
			    		//  masterDataVendor.setCurrency(fileUtility.getValue(line,391 ,5 ));
//			    		  masterDataVendor.setPaymentTerm(fileUtility.getValue(line,396 ,4 ));
//			    		  masterDataVendor.setMobileNo(fileUtility.getValue(line,400 ,15 ));
			    	  }else if(line.startsWith(IntegrationConstants.VALUE_ONE)){
			    		  bankAccountInformation = new BankAccountInformation();
			    		  bankAccountInformation.setRecordType(1);
			    		  bankAccountInformation.setRecordNumber(Integer.parseInt(fileUtility.getValue(line,1 ,3 )));
			    		  bankAccountInformation.setBankCountry(fileUtility.getValue(line,4 ,2 ));
			    		  bankAccountInformation.setBankKey(fileUtility.getValue(line,6 ,15 ));
			    		  bankAccountInformation.setBankAccount(fileUtility.getValue(line,21 ,18 ));
//			    		  bankAccountInformation.setCurrency(fileUtility.getValue(line,39 ,5 ));
//			    		  bankAccountInformation.setAccountHolderName(fileUtility.getValue(line,44 ,60 ));
//			    		  masterDataVendor.setBankAccountInformation(bankAccountInformation);
			    		  masterDataVendorList.add(masterDataVendor);
			    	  }
			      }  
			  } catch (IOException e) {
				e.printStackTrace();
			}
			return masterDataVendorList;
		}
	
	
	// set values for masterData material Info
	
	public List<MasterDataMaterialInfo> setMasterDataMaterialInfo(){
		
		
		return null;
		
	}
	
	

	public String formatStringField(String input, int length, String align) {
		String formatInput;
		String formatInput1 = null;
		//System.out.println("input.length() :"+input);
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
	//	System.out.println("formatInput :" + formatInput);
		return formatInput;

	}

	public String createFileNamePhis(String serviceCode) {

		String FILE_NAME;
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");
		//System.out.println(dt1.format(new Date()));

		String DATE = dt1.format(new Date());

		String fileName = IntegrationConstants.PHIS_BUSINESS_AREA + serviceCode
				+ IntegrationConstants.AGENSI_ID_ePerolehan + DATE;

		String seqNo = generateSeqNo(fileName, serviceCode, "pharma");
		FILE_NAME = fileName + seqNo + IntegrationConstants.FILE_EXTENSION_PHIS;
		return FILE_NAME;
	}

	public String createFileNameGfmas(String serviceCode) {

		String FILE_NAME;
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");
		System.out.println(dt1.format(new Date()));

		String DATE = dt1.format(new Date());

		String fileName = IntegrationConstants.GMAS_BUSINESS_AREA + serviceCode
				+ IntegrationConstants.AGENSI_ID_ePerolehan + DATE;

		String seqNo = generateSeqNo(fileName, serviceCode, "gfmas");
		FILE_NAME = fileName + seqNo
				+ IntegrationConstants.FILE_EXTENSION_GFMAS;
		return FILE_NAME;
	}

	public String generateSeqNo(String fileName, String serviceCode,
			String region) {

		String sequenceNo = IntegrationConstants.SEQUENCE_NO;

		String fileExtn = null;
		if (region.equals("pharma")) {
			fileExtn = IntegrationConstants.FILE_EXTENSION_PHIS;
		} else if (region.equals("gfmas")) {
			fileExtn = IntegrationConstants.FILE_EXTENSION_GFMAS;
		}
		int seqNo;
		if (!new File(IntegrationConstants.FILE_PATH + fileName
				+ IntegrationConstants.SEQUENCE_NO + fileExtn).exists()) {
			return sequenceNo;
		} else {

			File dir = new File(IntegrationConstants.FILE_PATH);
			String[] filesInPath = dir.list();
			List<Integer> seqNos = new ArrayList<Integer>();
			for (int i = 0; i < filesInPath.length; i++) {

				String fileName1 = filesInPath[i];
				if (fileName1.contains(serviceCode)) {
					int index = fileName1.indexOf('.');
					seqNo = Integer.parseInt(fileName1.substring(index - 3,
							index));
					seqNos.add(seqNo);
				}
			}
			Collections.sort(seqNos);
			seqNo = seqNos.get(seqNos.size() - 1);
			sequenceNo = formatIntegerField(seqNo + 1, 3, "R");

		}
		return sequenceNo;
	}

	public boolean createPhisFileInLocal(List<ItemType> itemTypes,
			List<MaterialColor> mColors, List<MaterialMeasurement> mMeasures,
			List<MaterialBrand> mBrands,List<MaterialClass> mClasses, String localFilePath) {

		 System.out.println("createPhisFileInLocal .."+localFilePath);

		 itemTypeFileOutPutStream(itemTypes, localFilePath);
		 materialBrandFileOutPutStream(mBrands, localFilePath);
		 materialMeasurementFileOutPutStream(mMeasures, localFilePath);
		 materialColorFileOutPutStream(mColors, localFilePath);
		 materialClassFileOutPutStream(mClasses, localFilePath);

		return true;
	}
	
	public String checkAction(Date createdDate, Date updatedDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null, date2 = null;
		try {
			date1 = sdf.parse(createdDate.getYear() + "-"
					+ createdDate.getMonth() + "-" + createdDate.getDate());
			date2 = sdf.parse(updatedDate.getYear() + "-"
					+ updatedDate.getMonth() + "-" + updatedDate.getDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String action;
		if (date1.compareTo(date2) > 0) {
			action = IntegrationConstants.DEL;
		} else if (date1.compareTo(date2) < 0) {
			action = IntegrationConstants.ADJ;
		} else if (date1.compareTo(date2) == 0) {
			action = IntegrationConstants.CRE;
		} else {
			action = IntegrationConstants.CRE;
		}
		return action;
	}
	
	public String formatDate(Date date) {

		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");

		return String.format("%s", dt1.format(date));
	}
	
	//===============================Eperunding files creation===========================
	
	
	public void equityOwnerOutPutStream(List<EquityOwner> content,
			String filePath) {
		String fileName = "EquityOwner.txt";

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + fileName), "utf-8"));

			
			if (content.size() != 0) {
				StringBuffer line;
				writer.write("         " + content.size() + " Rows Selected");
				for (EquityOwner eOwner : content) {
					line = new StringBuffer();

					line.append(System.getProperty("line.separator"));

					line.append(formatString(eOwner.getMOFNo()));

					line.append(formatString(eOwner.getOwner().getIsEquityOwner()));
					line.append(formatString(eOwner.getOwner().getName()));
					line.append(formatString(eOwner.getOwner().getNewICNo()));
					line.append(formatString(eOwner.getOwner().getPassport()));
					line.append(formatString(eOwner.getOwner().getIsBumi()));
					line.append(formatInt(eOwner.getOwner().getCitizenOf()));
					line.append(formatString(eOwner.getOwner().getEquityValue()));
					line.append(formatString(eOwner.getOwner()
						.getEquityPercentage()));
					line.append(formatString(eOwner.getOwner().getRace()));
					line.append(formatString(eOwner.getOwner().getDesignation()));

					line.append(formatString(eOwner.getQualification().getMajorIn()));
					line.append(formatString(eOwner.getQualification().getQualification()));
					line.append(formatInt(eOwner.getQualification()
						.getQualificationLevel()));
					line.append(formatString(eOwner.getQualification()
						.getInstitute()));
					line.append(formatString(eOwner.getQualification().getYear()));
					line.append(formatString(eOwner.getQualification().getEducationField()));

					line.append(formatString(eOwner.getProfBodyDetail()
						.getProfBody()));
					line.append(formatString(eOwner.getProfBodyDetail()
						.getProfBodyNo()));
					line.append(formatDateEperunding(eOwner.getProfBodyDetail()
						.getProfBodyExpiry()));

					line.append(String.format("%s", eOwner.getExpertise().getCategoryClassification()));

				writer.write(line.toString());
			}
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
	
	public void consultantProfileFileOutPutStream(
			List<ConsultantProfile> content, String filePath) {

		String fileName = "ConsultantProfiles.txt";
		Writer writer = null;

		try {

			// Using OutputStreamWriter you don't have to convert the String to
			// byte[]
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line;
		
			writer.write("         " + content.size() + " Rows Selected");
			for (ConsultantProfile cProfile : content) {

				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));

				line.append(formatString(cProfile.getCompanyName()));
				line.append(formatString(cProfile.getSSMNo()));
				line.append(formatString(cProfile.getMOFNo()));
				line.append(formatString(cProfile.getCompanyType()));
				line.append(formatString(cProfile.getAddress1()));
				line.append(formatString(cProfile.getAddress2()));
				line.append(formatString(cProfile.getAddress3()));
				line.append(formatString(cProfile.getPostcode()));
				line.append(formatString(cProfile.getCity()));
				line.append(formatString(cProfile.getDistrict()));
				line.append(formatString(cProfile.getState()));
				line.append(formatString(cProfile.getCountry()));
				line.append(formatString(cProfile.getTelCountryCode()));
				line.append(formatString(cProfile.getTelAreaCode()));
				line.append(formatString(cProfile.getTelNo()));
				line.append(formatString(cProfile.getFaxCountryCode()));
				line.append(formatString(cProfile.getFaxAreaCode()));
				line.append(formatString(cProfile.getFaxNo()));
				line.append(formatFloat(cProfile.getAccumulatedCapital()));
				line.append(formatString(cProfile.getAuthorisedCapital()));
				line.append(formatString(cProfile.getPaidUpCapital()));
				line.append(formatFloat(cProfile.getAccumulatedShare()));
				line.append(formatFloat(cProfile.getBumiputeraPercentage()));
				line.append(formatFloat(cProfile.getNonBumiPercentage()));
				line.append(formatInt(cProfile.getRegStatus()));
				line.append(formatDateEperunding(cProfile.getEffectDateTo()));
				line.append(formatDateEperunding(cProfile.getEstablishedDate()));
				line.append(formatDateEperunding(cProfile.getEffectDateFrom()));
				line.append(formatString(cProfile.getDisciplinary()));
				line.append(formatString(cProfile.getIsAssociatedCompany()));
				line.append(formatDateEperunding(cProfile.getProfileUpdateDate()));

				line.append(formatString(cProfile.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(cProfile.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDateEperunding(cProfile.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(formatDateEperunding(cProfile.getRegistrationHistory()
						.getDateStart()));
				line.append(formatDateEperunding(cProfile.getRegistrationHistory()
						.getDateEnd()));

				line.append(formatString(cProfile.getCapitalProportion()
						.getBumi()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getNonBumi()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getNonMalaysian()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getGovAgency()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getGovEquity()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getLocalCo()));
				line.append(formatString(cProfile.getCapitalProportion()
						.getForeignCo()));
				line.append(formatFloat(cProfile.getCapitalProportion()
						.getTotalMember()));
				line.append(String.format("%.0f", cProfile
						.getCapitalProportion().getTotalShare()));

				// line.append(System.getProperty("line.separator"));
				writer.write(line.toString());
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

	public void signatoryFileOutPutStream(List<Signatory> content,
			String filePath) {
		String fileName = "Signatory.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line = null;
			writer.write("         " + content.size() + " Rows Selected");
			for (Signatory signatory : content) {
				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));
				line.append(formatString(signatory.getMOFNo()));
				line.append(formatString(signatory.getAuthorizedSignatory().getName()));
				line.append(formatString(signatory.getAuthorizedSignatory().getNewICNo()));
				line.append(formatString(signatory.getAuthorizedSignatory().getPassport()));
				line.append(formatString(signatory.getAuthorizedSignatory().getIsBumi()));
				line.append(String.format("%s", signatory.getAuthorizedSignatory().getDesignation()));

				// line.append(System.getProperty("line.separator"));
				writer.write(line.toString());
			}
			System.out.println("line :" + line.toString());

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

	public void manageProfileFileOutPutStream(
			List<ManagementPersonnel> content, String filePath) {

		String fileName = "ManagementPersonnel.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (ManagementPersonnel mPersonnel : content) {
				line = new StringBuffer();
				line.append(System.getProperty("line.separator"));

				line.append(formatString(mPersonnel.getMOFNo()));

				line.append(formatString(mPersonnel.getPersonnel()
						.getIsManagement()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getIsProfessional()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getIsSemiProf()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getIsClerical()));
				line.append(formatString(mPersonnel.getPersonnel().getName()));
				line.append(formatString(mPersonnel.getPersonnel().getNewICNo()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getPassport()));
				line.append(formatString(mPersonnel.getPersonnel().getIsBumi()));
				line.append(formatInt(mPersonnel.getPersonnel().getCitizenOf()));
				line.append(formatString(mPersonnel.getPersonnel().getRace()));
				line.append(formatString(mPersonnel.getPersonnel()
						.getDesignation()));
				line.append(formatInt(mPersonnel.getPersonnel()
						.getWorkExperience()));

				line.append(formatString(mPersonnel.getQualification()
						.getMajorIn()));
				line.append(formatString(mPersonnel.getQualification()
						.getQualification()));
				line.append(formatInt(mPersonnel.getQualification()
						.getQualificationLevel()));
				line.append(formatString(mPersonnel.getQualification()
						.getInstitute()));
				line.append(formatString(mPersonnel.getQualification()
						.getYear()));
				line.append(formatString(mPersonnel.getQualification()
						.getEducationField()));

				line.append(formatString(mPersonnel.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(mPersonnel.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDateEperunding(mPersonnel.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(String.format("%s", mPersonnel.getExpertise()
						.getCategoryClassification()));

				// line.append(System.getProperty("line.separator"));
				writer.write(line.toString());
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

	public void boardOfDirectorOutPutStream(List<BoardOfDirector> content,
			String filePath) {

		String fileName = "BoardOfDirector.txt";

		Writer writer = null;

		try {

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			StringBuffer line;
			writer.write("         " + content.size() + " Rows Selected");
			// line.append(System.getProperty("line.separator"));
			for (BoardOfDirector bDirector : content) {
				line = new StringBuffer();

				line.append(System.getProperty("line.separator"));

				line.append(formatString(bDirector.getMOFNo()));

				line.append(formatString(bDirector.getDirector().getIsBOD()));
				line.append(formatString(bDirector.getDirector().getIsCEO()));
				line.append(formatString(bDirector.getDirector().getName()));
				line.append(formatString(bDirector.getDirector().getNewICNo()));
				line.append(formatString(bDirector.getDirector().getPassport()));
				line.append(formatString(bDirector.getDirector().getIsBumi()));
				line.append(formatInt(bDirector.getDirector().getCitizenOf()));
				line.append(formatString(bDirector.getDirector().getRace()));
				line.append(formatString(bDirector.getDirector()
						.getDesignation()));
				line.append(formatInt(bDirector.getDirector()
						.getWorkExperience()));

				line.append(formatString(bDirector.getQualification()
						.getMajorIn()));
				line.append(formatString(bDirector.getQualification()
						.getQualification()));
				line.append(formatInt(bDirector.getQualification()
						.getQualificationLevel()));
				line.append(formatString(bDirector.getQualification()
						.getInstitute()));
				line.append(formatString(bDirector.getQualification().getYear()));
				line.append(formatString(bDirector.getQualification().getEducationField()));

				line.append(formatString(bDirector.getProfBodyDetail()
						.getProfBody()));
				line.append(formatString(bDirector.getProfBodyDetail()
						.getProfBodyNo()));
				line.append(formatDateEperunding(bDirector.getProfBodyDetail()
						.getProfBodyExpiry()));

				line.append(String.format("%s", bDirector.getExpertise()
						.getCategoryClassification()));

				// line.append(System.getProperty("line.separator"));
				writer.write(line.toString());
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
	// PHIS PM Modules
	public void votFundFileOutPutStream(List<VotFund> content, String filePath) {
		String SERVICE_CODE = "PHS-090";

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (VotFund votFund : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(votFund.getYear(), 4, "R"));
					line.append(formatStringField(
							votFund.getCode() + votFund.getPtjCode(), 5, "L"));
					line.append(formatStringField(votFund.getDescription(),
							255, "L"));
					line.append(formatStringField(votFund.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

				Encryption encryption = new Encryption();
				encryption.encryptPhis(filePath,
						IntegrationConstants.ENCRYPTED_FILE_PATH, fileName,SERVICE_CODE);

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

	public void votFundDanaFileOutPutStream(List<VotFund> content,
			String filePath) {
		String SERVICE_CODE = "PHS-100";

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (VotFund votFund : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(votFund.getYear(), 4, "R"));
					line.append(formatStringField(votFund.getCode(), 4, "L"));
					line.append(formatStringField(votFund.getDescription(),
							255, "L"));
					line.append(formatStringField(votFund.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

				Encryption encryption = new Encryption();
				encryption.encryptPhis(filePath,
						IntegrationConstants.ENCRYPTED_FILE_PATH, fileName,SERVICE_CODE);

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

	public void programActivityFileOutPutStream(List<ProgramActivity> content,
			String filePath) {
		String SERVICE_CODE = "PHS-110";

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (ProgramActivity prgActivity : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(prgActivity.getYear(), 4,
							"R"));
					line.append(formatStringField(prgActivity.getVotCode()
							+ prgActivity.getCode(), 9, "L"));
					line.append(formatStringField(prgActivity.getDescription(),
							255, "L"));
					line.append(formatStringField(prgActivity.getStatus(), 1,
							"L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

				Encryption encryption = new Encryption();
				encryption.encryptPhis(filePath,
						IntegrationConstants.ENCRYPTED_FILE_PATH, fileName,SERVICE_CODE);

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

	public void projectFileOutPutStream(List<Project> content, String filePath) {
		String SERVICE_CODE = "PHS-120";

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (Project project : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(project.getYear(), 4, "R"));
					line.append(formatStringField(project.getVotCode()+project.getCode()+project.getSetiaCode()+project.getSubSetiaCode(), 16, "L"));
					line.append(formatStringField(project.getDescription(),
							255, "L"));
					line.append(formatStringField(project.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

				Encryption encryption = new Encryption();
				encryption.encryptPhis(filePath,
						IntegrationConstants.ENCRYPTED_FILE_PATH, fileName,SERVICE_CODE);

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

	public void glAccountFileOutPutStream(List<GlAccount> content,
			String filePath) {
		String SERVICE_CODE = "PHS-140";

		String fileName = createFileNamePhis(SERVICE_CODE);

		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath + fileName), "utf-8"));

			if (content.size() != 0) {
				StringBuffer line;
				for (GlAccount glAccount : content) {
					line = new StringBuffer();
					line.append(formatIntegerField(glAccount.getYear(), 4, "R"));
					line.append(formatStringField(glAccount.getCode(), 8, "L"));
					line.append(formatStringField(glAccount.getPtjCode(), 2,
							"L"));
					line.append(formatStringField(glAccount.getDescription(),
							255, "L"));
					line.append(formatStringField(glAccount.getStatus(), 1, "L"));
					line.append(System.getProperty("line.separator"));
					writer.write(line.toString());
				}

				Encryption encryption = new Encryption();
				encryption.encryptPhis(filePath,
						IntegrationConstants.ENCRYPTED_FILE_PATH,  fileName, SERVICE_CODE);

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
	//End of PHIS PM
	
	public String formatString(String input) {

		if (input == null)
			return "NULL|";
		if (input.isEmpty() && input.length() == 0)
			return "|";
		return String.format("%s|", input.trim());
	}

	// for ePerunding
	public String formatDateEperunding(Date date) {

		if (date == null)
			return "|";

		SimpleDateFormat dt1 = new SimpleDateFormat("yyyMMdd");

		return String.format("%s|", dt1.format(date));
	}

	public String formatFloat(float value) {

		return String.format("%.0f|", value);
	}

	public String formatInt(int value) {

		return String.format("%d|", value);

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
	
	public String getValue(String line,int begin,int size){
		  
		  String result = line.substring(begin, begin+size);
//		  char[] result = new char[size];
//		  line.getChars(begin, begin+size, result, 0);
		  return result.trim();
	  }


}
