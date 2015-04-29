package my.ep.web.integration.common;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.glaccount.GlAccountReqDTO;
import my.ep.dto.pm.common.glaccount.GlAccountResDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityReqDTO;
import my.ep.dto.pm.common.programactivity.ProgramActivityResDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaReqDTO;
import my.ep.dto.pm.common.projectsetia.ProjectSetiaResDTO;
import my.ep.dto.pm.common.votfund.VotFundReqDTO;
import my.ep.dto.pm.common.votfund.VotFundResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.type.pm.AddressType;
import my.ep.util.lookupservice.EpEjbRemoteBusinessDelegate;
import my.ep.web.integration.phis.ItemType;
import my.ep.web.integration.phis.MasterDataMaterialInfo;
import my.ep.web.integration.phis.MaterialBrand;
import my.ep.web.integration.phis.MaterialClass;
import my.ep.web.integration.phis.MaterialColor;
import my.ep.web.integration.phis.MaterialMeasurement;
import my.ep.web.integration.phis.PhisGlAccount;
import my.ep.web.integration.phis.PhisProgramActivity;
import my.ep.web.integration.phis.PhisProject;
import my.ep.web.integration.phis.PhisVotFund;
import my.ep.web.integration.phis.masterdatamateriainfo.PHISMasterDataMaterialInfomationPortTypeProxy;
import my.ep.web.integration.phis.ptj.deliveryaddress.DeliveryAddressRq_Type;
import my.ep.web.integration.phis.ptj.deliveryaddress.DeliveryAddress_Type;
import my.ep.web.integration.phis.ptj.deliveryaddress.EPMFRq_Type;
import my.ep.web.integration.phis.ptj.deliveryaddress.EPMFRs_Type;
import my.ep.web.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortTypeProxy;
import my.ep.web.integration.phis.ptj.deliveryaddress.RqHeader_Type;
import my.ep.web.integration.phis.ptj.deliveryaddress.RqUID_Type;
import my.ep.web.util.lookupservice.BusinessDelegate;

/*
* @ Ganga prasade
*/
public class PhisIntegration {
	
	public static void main(String[] args) throws IOException {
		PhisIntegration test = new PhisIntegration();
//		test.createFilesforPHIS();
//		GfmasIntegration gfmasIntegration = new GfmasIntegration();
//		gfmasIntegration.masterDataVendorGfmasInbound();
//		gfmasIntegration.votGfmasInbound();
//		gfmasIntegration.masterDataMaterialInfoGfmas();
//		test.masterDataMaterialInfoPHIS();
//		gfmasIntegration.sstInformationGfmas();
//		EperundingIntegration eperundingIntegration = new EperundingIntegration();
//		eperundingIntegration.createFilesforEperunding();
		test.deliveryAddress();
	}
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
	IAddressSO addressSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAddressSO.class);
	ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
			.getInstance().getServiceObject("cm", ICodificationSO.class);
	
	// method to create file for pharmaniaga for Class,Brand,Measrement,ItemType,Color
	
	public void createFilesforPHIS() {
		System.out.println("inside phis...");

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "30-03-2015";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		reqDTO.setCreateDate(date);
		//reqDTO.setCompanyName("PHARMANIAGA LOGISTICS SDN BHD");
		CodificationResDTO resDTO = codificationSO.searchItemByCreateDate(reqDTO);

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
				itemType.setItemAction(utility.checkAction(cmType.getCreatedDate(),cmType.getChangedDate()).trim());
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
				mBrand.setMaterialBrandAction(utility.checkAction(
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
				mMeasure.setMeasurementAction(utility.checkAction(
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
				mColor.setMaterialColorAction(utility.checkAction(
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
				mClass.setMaterialClassAction(utility.checkAction(cmUnspsc.getCreatedDate(), cmUnspsc.getChangedDate()).trim());
				mClass.setMaterialOldClassCode("");
				mClass.setMaterialClassDescription(cmUnspsc.getUnspscTitle().trim());
				
			/*	if(cmUnspsc.getUnspscParentCode()==null){
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
				}*/
				if(!mClasses.contains(mClass)){
					mClasses.add(mClass);
				}
			}
			
			String filePath = IntegrationConstants.FILE_PATH;

			// calling method to save data in files
			utility.createPhisFileInLocal(itemTypes, mColors, mMeasures,mBrands,mClasses, filePath);
			
//			utility.setMasterDataMaterialInfo(resDTO.getCmSupplierItemsList());
			

		}else{
		System.out.println("not available.");
		}

	}
	
	public void deliveryAddress() {
		{
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(254582L);
			reqDTO.setAddressType(AddressType.DELIVERY_ADDRESS.value());
			
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);
			List<PmAddressType> addressTypeLst = resDTO.getAddressTypeList();
			EPMFRs_Type rs_Type = null;
			
			EPMFRq_Type part = new EPMFRq_Type();
			try {

				RqHeader_Type header_Type = new RqHeader_Type();
				header_Type.setConsumerID("EPP-001");
				header_Type.setIpAddress("192.168.62.17");
				header_Type.setOrgUnit("1");
				header_Type.setSegment("2");

				RqUID_Type rqUID_Type = new RqUID_Type();
				rqUID_Type.setAsyncRqUID("001");
				rqUID_Type.setRqUID("PTG");
				header_Type.setUID(rqUID_Type);	

				DeliveryAddressRq_Type deliveryAddressRq_Type = new DeliveryAddressRq_Type();
				deliveryAddressRq_Type.setAction("CRE");
				deliveryAddressRq_Type.setPTJCode("42060401");
				DeliveryAddress_Type[] address_Type = new DeliveryAddress_Type[1];
				int i = 0;
				DeliveryAddress_Type deliveryAddress = null;
				
				for (PmAddressType addressType : addressTypeLst) {
					
					deliveryAddress= new DeliveryAddress_Type();
					deliveryAddress.setAddress1(addressType.getPmAddress().getAddress1());
					deliveryAddress.setAddress2(addressType.getPmAddress().getAddress2());
					deliveryAddress.setAddress3(addressType.getPmAddress().getAddress3());
					deliveryAddress.setAddress4(addressType.getPmAddress().getAddress4());
					deliveryAddress.setCity(addressType.getPmAddress().getPmState().getStateName());
					deliveryAddress.setDistrict(addressType.getPmAddress().getPmDistrict().getDistrictName());			
					deliveryAddress.setState(addressType.getPmAddress().getPmState().getStateName());
					deliveryAddress.setStatus(addressType.getRecordStatusFlg());
					deliveryAddress.setDeliveryAddressID(addressType.getAddressTypeId().toString());;
					address_Type[i] = deliveryAddress;
					i++;
				}			
				
				deliveryAddressRq_Type.setNoOfDeliveryAddress(address_Type.length);
				deliveryAddressRq_Type.setDeliveryAddressArray(address_Type);

				PHISMasterDataDeliveryAddressPortTypeProxy typeProxy = new PHISMasterDataDeliveryAddressPortTypeProxy();
				part.setRqHeader(header_Type);
				part.setDeliveryAddressRq(deliveryAddressRq_Type);	

				rs_Type = typeProxy.inquire(part);
//				System.out.println("No of delivery Addreee:::"+part.getDeliveryAddressRq().getNoOfDeliveryAddress());
//				System.out.println("Action:::"+part.getDeliveryAddressRq().getAction());
//				System.out.println("PTJ CODe:::"+part.getDeliveryAddressRq().getPTJCode());
//				System.out.println("No of delivery Addreee"+part.getDeliveryAddressRq().getDeliveryAddressArray());
				DeliveryAddress_Type[] address_Type2 = part.getDeliveryAddressRq().getDeliveryAddressArray();
//				
//				my.ep.web.integration.phis.ptj.deliveryaddress
				System.out.println("address_Type2 :"+address_Type2.length);
				
				
				for(DeliveryAddress_Type addr:address_Type2){
					System.out.println("Address1"+addr.getAddress1());
				}
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			System.out.println("getStatusCode  :"+ rs_Type.getRsHeader().getStatus().getStatusCode());
			System.out.println("getStatusDesc :"+ rs_Type.getRsHeader().getStatus().getStatusDesc());

		}
	}

	public void votFund() {

		VotFundReqDTO reqDTO = new VotFundReqDTO();		
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "20-09-2014";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		reqDTO.setCreatedDate(date);

		VotFundResDTO resDTO = votFundSO.retrieveVotFundByCreatedDate(reqDTO);

		List<PmVotFund> votlist = resDTO.getVotFundList();

		List<PhisVotFund> votFundList = new ArrayList<PhisVotFund>();
		PhisVotFund votFnd;
		for (PmVotFund votFund : votlist) {
			Date effdate = votFund.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			votFnd = new PhisVotFund();
			votFnd.setCode(votFund.getVotFundCode());
			votFnd.setDescription(votFund.getDescription());
			if(votFund.getPmPtjGroup()!=null){
				votFnd.setPtjCode(votFund.getPmPtjGroup().getGroupCode());
			}else{
				votFnd.setPtjCode(IntegrationConstants.EMPTY_TRING);
			}
			votFnd.setYear(year);
			votFnd.setStatus("C");
			votFundList.add(votFnd);
		}

		FileUtility utility = new FileUtility();
		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.votFundFileOutPutStream(votFundList, filePath);

	}

	public void votFundDana() {

    VotFundReqDTO reqDTO = new VotFundReqDTO();
		
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "31-03-2015";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		reqDTO.setCreatedDate(date);
		reqDTO.setCreatedDate(date);

		VotFundResDTO resDTO = votFundSO
				.retrieveVotFundDanaByCreatedDate(reqDTO);

		List<PmVotFund> votlist = resDTO.getVotFundList();

		List<PhisVotFund> votFundList = new ArrayList<PhisVotFund>();
		PhisVotFund votFnd;
		for (PmVotFund votFund : votlist) {
			votFnd = new PhisVotFund();
			votFnd.setCode(votFund.getVotFundCode());
			votFnd.setDescription(votFund.getDescription());
			Date effdate = votFund.getEffDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			votFnd.setYear(year);
			votFnd.setStatus("C");
			votFundList.add(votFnd);
		}

		FileUtility utility = new FileUtility();
		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.votFundDanaFileOutPutStream(votFundList, filePath);

	}

	public void programActivity() {

		ProgramActivityReqDTO reqDTO = new ProgramActivityReqDTO();
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "20-09-2014";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		reqDTO.setCreatedDate(date);

		ProgramActivityResDTO resDTO = programActivitySO
				.retrieveProgramActivityByCreateDate(reqDTO);

		List<PmPrgActivity> votlist = resDTO.getPrgActivityList();

		System.out.println("list: " + votlist.size());
		List<PhisProgramActivity> prgActList = new ArrayList<PhisProgramActivity>();
		PhisProgramActivity prgActivity;
		for (PmPrgActivity programActvity : votlist) {

			System.out.println("VotFundId"
					+ programActvity.getPmVotFund().getVotFundCode());
			Date effdate = programActvity.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			prgActivity = new PhisProgramActivity();
			prgActivity.setCode(programActvity.getPrgActivityCode());
			prgActivity.setDescription(programActvity.getDescription());
			prgActivity.setVotCode(programActvity.getPmVotFund()
					.getVotFundCode());
			prgActivity.setYear(year);
			prgActivity.setStatus("C");
			prgActList.add(prgActivity);
		}

		FileUtility utility = new FileUtility();
		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.programActivityFileOutPutStream(prgActList, filePath);

	}

	public void project() {		
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "20-09-2014";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}	

		ProjectSetiaReqDTO reqDTO = new ProjectSetiaReqDTO();
		
		reqDTO.setCreateDate(date);

		ProjectSetiaResDTO resDTO = projectSetiaSO
				.retrieveProjectByCreateDate(reqDTO);

		List<PmSubSetia> projectlist = resDTO.getSubSetiaList();

		List<PhisProject> programList = new ArrayList<PhisProject>();
		PhisProject project;
		for (PmSubSetia pmProject : projectlist) {
			Date effdate = pmProject.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			project = new PhisProject();
			project.setCode(pmProject.getProjectCode());
			project.setVotCode(pmProject.getPmVotFund().getVotFundCode());
			project.setSetiaCode(pmProject.getSetiaCode());
			project.setSubSetiaCode(pmProject.getSubSetiaCode());
			project.setDescription(pmProject.getDescription());
			project.setYear(year);
			project.setStatus("C");
			programList.add(project);
		}

		FileUtility utility = new FileUtility();
		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.projectFileOutPutStream(programList, filePath);

	}

	public void glAccount() {

		
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "31-03-2015";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		

		GlAccountReqDTO reqDTO = new GlAccountReqDTO();
		reqDTO.setCreatedDate(date);
		//reqDTO.setStartDate(startDate);
		//reqDTO.setEndDate(endDate);

		GlAccountResDTO resDTO = glAccountSO
				.retrieveGlAccountByCreateDate(reqDTO);

		List<PmGlAccount> glAccountlist = resDTO.getGlAccountList();

		List<PhisGlAccount> glAccountList = new ArrayList<PhisGlAccount>();
		PhisGlAccount glAccount;
		for (PmGlAccount account : glAccountlist) {
			glAccount = new PhisGlAccount();
			Date effdate = account.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			glAccount.setCode(account.getGlAccCode());
			glAccount.setDescription(account.getDescription());
			if(account.getPmPtjGroup()!=null){
				glAccount.setPtjCode(account.getPmPtjGroup().getGroupCode());
			}else{
				glAccount.setPtjCode(IntegrationConstants.EMPTY_TRING);
			}
			glAccount.setYear(year);
			glAccount.setStatus("C");
			glAccountList.add(glAccount);
		}

		FileUtility utility = new FileUtility();
		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.glAccountFileOutPutStream(glAccountList, filePath);

	}
	
	public void masterDataMaterialInfoPHIS(){

		

		CodificationReqDTO reqDTO = new CodificationReqDTO();

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "30-03-2015";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		reqDTO.setCreateDate(date);
		//reqDTO.setCompanyName("PHARMANIAGA LOGISTICS SDN BHD");
		CodificationResDTO resDTO = codificationSO.searchItemByCreateDate(reqDTO);
		
		List<MasterDataMaterialInfo> materilInfoList = null;
		FileUtility fileUtility = new FileUtility();
		if (resDTO.isSuccessful()) {
			
			materilInfoList = fileUtility.setPHISMasterDataMaterialInfo(resDTO);
		}
		try{
			my.ep.web.integration.phis.masterdatamateriainfo.EPMFRq_Type epmfRq_Type = new my.ep.web.integration.phis.masterdatamateriainfo.EPMFRq_Type();
		
			my.ep.web.integration.phis.masterdatamateriainfo.RqHeader_Type rqHeader_Type = new my.ep.web.integration.phis.masterdatamateriainfo.RqHeader_Type();
			
			rqHeader_Type.setConsumerID(IntegrationConstants.CONSUMER_ID);
			rqHeader_Type.setIpAddress(IntegrationConstants.SERVICE_HOST);
			rqHeader_Type.setOrgUnit("PHS-020");
			rqHeader_Type.setSegment(IntegrationConstants.SEQUENCE_NO);
			
			my.ep.web.integration.phis.masterdatamateriainfo.RqUID_Type rqUID_Type = new my.ep.web.integration.phis.masterdatamateriainfo.RqUID_Type();
			rqUID_Type.setAsyncRqUID("PHS-020");
			rqUID_Type.setRqUID(IntegrationConstants.SEQUENCE_NO);
		
			rqHeader_Type.setUID(rqUID_Type);
		
		
		for(MasterDataMaterialInfo materialInfo:materilInfoList){
		
			my.ep.web.integration.phis.masterdatamateriainfo.MaterialInformationRq_Type materialInformationRq_Type = new my.ep.web.integration.phis.masterdatamateriainfo.MaterialInformationRq_Type();
			materialInformationRq_Type.setProductCode(materialInfo.getProductCode());
			materialInformationRq_Type.setCodeType(materialInfo.getCodeType());
			materialInformationRq_Type.setKodTindakan(materialInfo.getKodTindakan());
		
			materialInformationRq_Type.setOldProductCode(materialInfo.getOldProductCode());
			materialInformationRq_Type.setItemDescription(materialInfo.getItemDescription());
			materialInformationRq_Type.setStartEffectiveDate(materialInfo.getStartEffectiveDate());
			materialInformationRq_Type.setEndEffectiveDate(materialInfo.getEndEffectiveDate());
		
			materialInformationRq_Type.setCommodityName(materialInfo.getCommodityName());
			materialInformationRq_Type.setBaseUnitOfMeasure(materialInfo.getBaseUnitOfMeasure());
			materialInformationRq_Type.setSegment(materialInfo.getSegment());       //size 2
			materialInformationRq_Type.setFamily(materialInfo.getFamily());        //size 4    
			materialInformationRq_Type.setAltUnitOfMeasurement(materialInfo.getAltUnitOfMeasurement());
		
			epmfRq_Type.setMaterialInformationRq(materialInformationRq_Type);
			epmfRq_Type.setRqHeader(rqHeader_Type);
			
			PHISMasterDataMaterialInfomationPortTypeProxy typeProxy = new PHISMasterDataMaterialInfomationPortTypeProxy();
			
			    my.ep.web.integration.phis.masterdatamateriainfo.EPMFRs_Type epmfRs_Type = typeProxy.inquire(epmfRq_Type);
			
				System.out.println("getStatusCode :"+epmfRs_Type.getRsHeader().getStatus().getStatusCode());
				System.out.println("getStatusDesc :"+epmfRs_Type.getRsHeader().getStatus().getStatusDesc());
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	
	}


}
