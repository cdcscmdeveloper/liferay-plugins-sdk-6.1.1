package my.ep.web.integration.common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.lookupservice.EpEjbRemoteBusinessDelegate;
import my.ep.web.integration.crm.Appl;
import my.ep.web.integration.crm.BelongsTo;
import my.ep.web.integration.crm.Bidang;
import my.ep.web.integration.crm.Branch;
import my.ep.web.integration.crm.MainContact;
import my.ep.web.integration.crm.SoftCert;
import my.ep.web.integration.crm.SupplierInfo;
import my.ep.web.integration.crm.SupplierUserInfo;
import my.ep.web.integration.gfmas.SSTInformation;
import my.ep.web.integration.phis.ItemType;
import my.ep.web.integration.phis.MaterialBrand;
import my.ep.web.integration.phis.MaterialClass;
import my.ep.web.integration.phis.MaterialColor;
import my.ep.web.integration.phis.MaterialMeasurement;

public class Test {

	public static void main(String[] args) throws IOException {
		Test test = new Test();
		test.run();
	}

	public List<SupplierUserInfo> crmSupplierUserInfo() {

		List<SupplierUserInfo> supplierUserInfoList = null;
		ISupplierSearchSO supplierSearchSO = EpEjbRemoteBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);

		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String dateInString = "05-12-2013";
			date = sdf.parse(dateInString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("date :" + date);

		reqDTO.setCreateDate(date);

		SupplierSearchResDTO resDTO = supplierSearchSO
				.getSupplierUserInfoforCRM(reqDTO);

		if (resDTO.isSuccessful()) {
			System.out.println("resDTO.getSmSuppliers().size() :"
					+ resDTO.getSmSuppliers().size());
			if (resDTO.getSmSuppliers().size() > 0) {
				supplierUserInfoList = new ArrayList<SupplierUserInfo>();
				SupplierUserInfo supplierUserInfo = null;
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {
					if (smSupplier.getSmAppls().size() > 0) {
						for (SmAppl smAppl : smSupplier.getSmAppls()) {
							if (smAppl.getSmPersonnels().size() > 0) {
								for (SmPersonnel smPersonnel : smAppl
										.getSmPersonnels()) {
									supplierUserInfo = new SupplierUserInfo();
									smPersonnel.getIdentificationType()
											.getPmParameterDescs();
									supplierUserInfo.setUserName(smPersonnel
											.getName());
									supplierUserInfo.setIdentityNo(smPersonnel
											.getIdentificationNo());
									supplierUserInfo.setIdentityType("IC");
									supplierUserInfo.setNationality("MY");
									supplierUserInfo
											.setRecordStatus(smPersonnel
													.getRecordStatusFlg()
													.toString());
									supplierUserInfo.setEmail(smPersonnel
											.getEmail());
									supplierUserInfo.setMobileNo(smPersonnel
											.getMobileNo());
									supplierUserInfo.setPosition(smPersonnel
											.getRoleName());
									supplierUserInfo.setAccessStatus("1"); // need
																			// to
																			// check

								}
								if (smAppl.getSmSupplierBranches().size() > 0) {
									List<BelongsTo> belongsToList = new ArrayList<BelongsTo>();
									BelongsTo belongsTo = null;
									for (SmSupplierBranch supplierBranch : smAppl
											.getSmSupplierBranches()) {
										belongsTo = new BelongsTo();
										belongsTo.setBranchID(supplierBranch
												.getSupplierBranchId());
										belongsTo.setSupplierID(smSupplier
												.getSupplierId().toString());
										belongsToList.add(belongsTo);
									}
									supplierUserInfo
											.setBelongsToList(belongsToList);
								}

							}
						}
					}

					if (smSupplier.getSmSoftcertRequests().size() > 0) {
						List<SoftCert> softCertList = new ArrayList<SoftCert>();
						SoftCert softCert = null;
						for (SmSoftcertRequest smSoftcertRequest : smSupplier
								.getSmSoftcertRequests()) {
							softCert = new SoftCert();
							softCert.setDateFrom(smSoftcertRequest
									.getPmDigiCert().getValidFrom());
							softCert.setDateTo(smSoftcertRequest
									.getPmDigiCert().getValidTo());
							softCert.setSerialNo(smSoftcertRequest
									.getPmDigiCert().getCertSerialNo());
							softCertList.add(softCert);

						}
						supplierUserInfo.setSoftCertList(softCertList);
					}

				}
				supplierUserInfoList.add(supplierUserInfo);
			}
		}
		return supplierUserInfoList;

	}

	public List<SupplierInfo> crmSupplierInfo() {
		List<SupplierInfo> supplierInfoList = null;
		ISupplierSearchSO supplierSearchSO = EpEjbRemoteBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);

		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String dateInString = "05-12-2013";
			date = sdf.parse(dateInString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("date :" + date);

		reqDTO.setCreateDate(date);

		SupplierSearchResDTO resDTO = supplierSearchSO
				.getSupplierInfoforCRM(reqDTO);

		if (resDTO.isSuccessful()) {
			System.out.println("resDTO.getSmSuppliers().size() :"
					+ resDTO.getSmSuppliers().size());
			if (resDTO.getSmSuppliers().size() > 0) {
				supplierInfoList = new ArrayList<SupplierInfo>();
				SupplierInfo supplierInfo = null;
				for (SmSupplier smSupplier : resDTO.getSmSuppliers()) {

					supplierInfo = new SupplierInfo();
					supplierInfo.setCompanyName(smSupplier.getCompanyName());
					supplierInfo.setSupplierID(smSupplier.getSupplierId()
							.toString());
					if (smSupplier.getSmAppls().size() > 0) {
						List<Appl> applList = new ArrayList<Appl>();
						List<MainContact> mainContactList = new ArrayList<MainContact>();
						List<Branch> branchList = new ArrayList<>();
						List<Bidang> bidangList = new ArrayList<Bidang>();
						Appl Appl = null;
						for (SmAppl smAppl : smSupplier.getSmAppls()) {
							if (smAppl.getSmSupplierBranches().size() > 0) {

								Branch branch = null;
								for (SmSupplierBranch smSupplierBranch : smAppl
										.getSmSupplierBranches()) {
									branch = new Branch();
									branch.setBranchName(smSupplierBranch
											.getBranchName());
									branch.setBranchID(smSupplierBranch
											.getSupplierBranchId().toString());
									branch.setAddress1(smSupplierBranch
											.getSmAddress().getAddress1());
									branch.setAddress2(smSupplierBranch
											.getSmAddress().getAddress2());
									branch.setAddress3(smSupplierBranch
											.getSmAddress().getAddress3());
									branch.setAddress4(smSupplierBranch
											.getSmAddress().getAddress4());
									branch.setZipCode(smSupplierBranch
											.getSmAddress().getPostcode());
									/*branch.setDistrict(smSupplierBranch
											.getSmAddress().getPmDistrict()
											.getDistrictName());*/
									/*branch.setCity(smSupplierBranch
											.getSmAddress().getPmCity()
											.getCityName());
									branch.setStateName(smSupplierBranch
											.getSmAddress().getPmState()
											.getStateName());*/
									/*branch.setCountryName(smSupplierBranch
											.getSmAddress().getPmCountry()
											.getCountryName());*/
									if (null != smAppl.getSmCompanyBasic()) {
										branch.setWebsite(smAppl
												.getSmCompanyBasic()
												.getWebsite());
										branch.setPhone(smAppl
												.getSmCompanyBasic()
												.getPhoneNo());
										branch.setFax(smAppl
												.getSmCompanyBasic().getFaxNo());
									}
									branchList.add(branch);
								}
								supplierInfo.setBranchList(branchList);
							}
							if (null != smAppl) {
								Appl = new Appl();
								Appl.setApplNo(smAppl.getApplNo());
								Appl.setApplStatus(smSupplier.getRegNo());
								if (smSupplier.getSmMofAccounts().size() > 0) {
									Appl.setAppType("M"); // B-basic M-Mof
									for (SmMofAccount smMofAccount : smSupplier
											.getSmMofAccounts()) {
										Appl.setMofNo(smMofAccount.getMofNo());
										Appl.setExpiryDate(smMofAccount
												.getExpDate());
									}
								} else {
									Appl.setAppType("B");
									Appl.setMofNo(null);
								}
								Appl.setRegNo(smSupplier.getRegNo());
								if (null != smAppl.getSmCompanyBasic()) {
									Appl.setCapitalPaidUp(smAppl
											.getSmCompanyBasic()
											.getPaidUpCapital().toString());
								}
								applList.add(Appl);
							}
							supplierInfo.setSmApplList(applList);

							if (smAppl.getSmSupplierCategories().size() > 0) {

								Bidang bidang = null;
								for (SmSupplierCategory smSupplierCategory : smAppl
										.getSmSupplierCategories()) {
									bidang = new Bidang();
									bidang.setKodBidangCategoryL1(smSupplierCategory
											.getPmCategoryL1()
											.getCategoryL1Code());
									bidang.setKodBidangCategoryL2(smSupplierCategory
											.getPmCategoryL2()
											.getCategoryL2Code());
									bidang.setKodBidangCategoryL3(smSupplierCategory
											.getPmCategoryL3()
											.getCategoryL3Code());
									bidang.setDescriptionCategoryL1(smSupplierCategory
											.getPmCategoryL1()
											.getCategoryName());
									bidang.setDescriptionCategoryL2(smSupplierCategory
											.getPmCategoryL2()
											.getCategoryName());
									bidang.setDescriptionCategoryL3(smSupplierCategory
											.getPmCategoryL3()
											.getCategoryName());
									bidangList.add(bidang);
								}
								supplierInfo.setBidangList(bidangList);
							}

							if (smAppl.getSmPersonnels().size() > 0) {

								MainContact mainContact = null;
								for (SmPersonnel smPersonnel : smAppl
										.getSmPersonnels()) {
									mainContact = new MainContact();
									mainContact.setContactName(smPersonnel
											.getName());
									mainContact.setMobileNo(smPersonnel
											.getMobileNo());
									mainContact.setPhoneNo(smPersonnel
											.getPhoneNo());
									mainContactList.add(mainContact);

								}
								supplierInfo
										.setMainContactList(mainContactList);
							}

						}

					}

				}
				supplierInfoList.add(supplierInfo);
			}
		}
		return supplierInfoList;
	}

	public void run1() {
		IQTAwardSO qtAwardSO = EpEjbRemoteBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

		SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = ft.parse("04/03/2013");
			endDate = ft.parse("20/05/2015");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setStartDate(startDate);
		reqDTO.setEndDate(endDate);

		AwardSupplierResDTO resDTO = qtAwardSO
				.retrieveLoaListByDateRange(reqDTO);

		List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
		System.out.println("List: " + list.size());
		List<ScLoa> scLoaList = resDTO.getLoaResultBean().getResultList();
		List<SSTInformation> sstInformations = new ArrayList<SSTInformation>();
		SSTInformation sstInformation;
		for (ScLoa scLoa : scLoaList) {
			sstInformation = new SSTInformation();
			sstInformation.setRecordType(0);
			sstInformation.setSSTNumber(scLoa.getLoaId().toString());
			sstInformation.setPhysicalSSTNumber(scLoa.getLoaNo());
			sstInformation.setSapVendorCode(scLoa.getScLoiLoa().getSmSupplier().getSapVendorCode());
			sstInformation.setKumpPtj(scLoa.getScLoiLoa().getPmOrgProfile().getPmOrgValidity().getOrgCode());
			sstInformation.setSstAcknowledgeDate(formatDate(scLoa.getAckDate()));
			sstInformations.add(sstInformation);
		}

		FileUtility utility = new FileUtility();
		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.sstInformationFileOutPutStream(sstInformations, filePath);

	}

	public void run() {

		ICodificationSO codificationSO = (ICodificationSO) EpEjbRemoteBusinessDelegate
				.getInstance().getServiceObject("cm", ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "25-02-2006";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		reqDTO.setCreateDate(date);
		CodificationResDTO resDTO = codificationSO
				.searchItemByCreateDate(reqDTO);

		if (resDTO.isSuccessful()) {
			
			System.out.println("inside successful");
			FileUtility utility = new FileUtility();
			List<CmType> cmTypeList = resDTO.getTypeList();
			List<ItemType> itemTypes = new ArrayList<ItemType>();
			ItemType itemType;
		
			for (CmType cmType : cmTypeList) {
				itemType = new ItemType();
				itemType.setItemCode(cmType.getTypeCode().trim());
				itemType.setItemDescription(cmType.getTypeName().trim());
				itemType.setItemAction(checkAction(cmType.getCreatedDate(),cmType.getChangedDate()).trim());
				itemType.setItemOldcode("");
				if(!itemTypes.contains(itemType)){
					itemTypes.add(itemType);
				}
			}

			List<CmBrand> cmBrandList = resDTO.getBrandList();
			List<MaterialBrand> mBrands = new ArrayList<MaterialBrand>();
			MaterialBrand mBrand;
			
			for (CmBrand cmBrand : cmBrandList) {
				mBrand = new MaterialBrand();
				mBrand.setMaterialBrandCode(cmBrand.getBrandCode().trim());
				mBrand.setMaterialBrandDescription(cmBrand.getBrandName().trim());
				mBrand.setMaterialBrandAction(checkAction(
						cmBrand.getCreatedDate(), cmBrand.getChangedDate()).trim());
				mBrand.setMaterialOldBrandCode("");
				if(!mBrands.contains(mBrand)){
					mBrands.add(mBrand);
				}
			}

			List<CmMeasurement> cmMeasureList = resDTO.getMeasurementList();
			List<MaterialMeasurement> mMeasures = new ArrayList<MaterialMeasurement>();
			MaterialMeasurement mMeasure;
			
			for (CmMeasurement cmMeasure : cmMeasureList) {
				mMeasure = new MaterialMeasurement();
				mMeasure.setMeasurementCode(cmMeasure.getMeasurementCode().trim());
				mMeasure.setMeasurementDescription(cmMeasure
						.getMeasurementName().trim());
				mMeasure.setMeasurementAction(checkAction(
						cmMeasure.getCreatedDate(), cmMeasure.getChangedDate()).trim());
				mMeasure.setMeasurementOldCode("");
				if(!mMeasures.contains(mMeasure)){
					mMeasures.add(mMeasure);
				}
			}

			List<CmColor> cmColorList = resDTO.getColorList();
			List<MaterialColor> mColors = new ArrayList<MaterialColor>();
			MaterialColor mColor;
			
			for (CmColor cmColor : cmColorList) {
				mColor = new MaterialColor();
				mColor.setMaterialColorCode(cmColor.getColorCode().trim());
				mColor.setMaterialColorDescription(cmColor.getColorName().trim());
				mColor.setMaterialColorAction(checkAction(
						cmColor.getCreatedDate(), cmColor.getChangedDate()).trim());
				mColor.setMaterialOldColorCode("");
				if(!mColors.contains(mColor)){
					mColors.add(mColor);
				}
			}
			
			List<CmUnspsc> cmUnspscList = resDTO.getClassList();
			List<MaterialClass> mClasses = new ArrayList<MaterialClass>();
			MaterialClass mClass;
			
			
			for (CmUnspsc cmUnspsc : cmUnspscList) {
				mClass = new MaterialClass();
				mClass.setMaterialClassCode(cmUnspsc.getUnspscParentCode().trim());
				mClass.setMaterialClassAction(checkAction(cmUnspsc.getCreatedDate(), cmUnspsc.getChangedDate()).trim());
				mClass.setMaterialOldClassCode("");
				
				if(cmUnspsc.getUnspscParentCode()==null){
					mClass.setMaterialClassDescription(cmUnspsc.getUnspscDescription().trim());
				}else{
						for(int i= cmUnspsc.getUnspscLevel()+1;i>0;i--){
							if(cmUnspsc.getUnspscParentCode()!=null){
							cmUnspsc = codificationSO.retrieveCmUnspscByUnspscCode(cmUnspsc.getUnspscParentCode());
							if(null!=cmUnspsc.getUnspscDescription()){
							mClass.setMaterialClassDescription(cmUnspsc.getUnspscDescription().trim());
							}
							}
						}
				}
				if(!mClasses.contains(mClass)){
					mClasses.add(mClass);
				}
			}
			
			String filePath = IntegrationConstants.FILE_PATH;

			// calling method to save data in files
			System.out.println("before ");
			utility.createPhisFileInLocal(itemTypes, mColors, mMeasures,
					mBrands,mClasses, filePath);
			System.out.println("after..");

		}else{
		System.out.println("not available.");
		}

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

}
