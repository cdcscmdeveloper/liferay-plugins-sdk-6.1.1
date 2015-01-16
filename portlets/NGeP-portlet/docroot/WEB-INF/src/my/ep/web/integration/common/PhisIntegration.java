package my.ep.web.integration.common;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.com.integration.phis.ptj.deliveryaddress.DeliveryAddressRq_Type;
import my.com.integration.phis.ptj.deliveryaddress.DeliveryAddress_Type;
import my.com.integration.phis.ptj.deliveryaddress.EPMFRq_Type;
import my.com.integration.phis.ptj.deliveryaddress.EPMFRs_Type;
import my.com.integration.phis.ptj.deliveryaddress.PHISMasterDataDeliveryAddressPortTypeProxy;
import my.com.integration.phis.ptj.deliveryaddress.RqHeader_Type;
import my.com.integration.phis.ptj.deliveryaddress.RqUID_Type;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
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
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmProject;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.pm.AddressType;
import my.ep.util.lookupservice.EpEjbRemoteBusinessDelegate;
import my.ep.web.integration.gfmas.GlAccount;
import my.ep.web.integration.gfmas.ProgramActivity;
import my.ep.web.integration.gfmas.Project;
import my.ep.web.integration.gfmas.SSTInformation;
import my.ep.web.integration.gfmas.VotFund;
import my.ep.web.integration.phis.ItemType;
import my.ep.web.integration.phis.MaterialBrand;
import my.ep.web.integration.phis.MaterialClass;
import my.ep.web.integration.phis.MaterialColor;
import my.ep.web.integration.phis.MaterialMeasurement;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class PhisIntegration {
	
	public static void main(String[] args) throws IOException {
		PhisIntegration test = new PhisIntegration();
		//test.creatteFilesforPHIS();
		test.createFileforSSTInformation();
	}
	IVotFundSO votFundSO = EpEjbRemoteBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IVotFundSO.class);
	IProgramActivitySO programActivitySO = EpEjbRemoteBusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProgramActivitySO.class);
	IProjectSetiaSO projectSetiaSO = EpEjbRemoteBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProjectSetiaSO.class);
	IGlAccountSO glAccountSO = EpEjbRemoteBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IGlAccountSO.class);
	IAddressSO addressSO = EpEjbRemoteBusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAddressSO.class);
	
	// method to create file for pharmaniaga for Class,Brand,Measrement,ItemType,Color
	
	public void creatteFilesforPHIS() {

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
			utility.createPhisFileInLocal(itemTypes, mColors, mMeasures,mBrands,mClasses, filePath);
			System.out.println("after..");

		}else{
		System.out.println("not available.");
		}

	}
	
	//create files for Phis SST information
	public void createFileforSSTInformation() {
		IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
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
		FileUtility utility = new FileUtility();
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
			sstInformation.setSstAcknowledgeDate(utility.formatDate(scLoa.getAckDate()));
			sstInformations.add(sstInformation);
		}

		String filePath = IntegrationConstants.FILE_PATH;

		// calling method to save data in files
		utility.sstInformationFileOutPutStream(sstInformations, filePath);

	}
	public void deliveryAddress() {
		{
			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(2207L);
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
				deliveryAddressRq_Type.setNoOfDeliveryAddress(addressTypeLst.size());
				deliveryAddressRq_Type.setPTJCode("42060401");
				DeliveryAddress_Type[] address_Type = new DeliveryAddress_Type[addressTypeLst.size()];
				int i = 0;
				DeliveryAddress_Type deliveryAddress = null;
				for (PmAddressType addressType : addressTypeLst) {
					deliveryAddress= new DeliveryAddress_Type();
					deliveryAddress.setAddress1(addressType.getPmAddress().getAddress1());
					deliveryAddress.setAddress2(addressType.getPmAddress().getAddress2());
					deliveryAddress.setAddress3(addressType.getPmAddress().getAddress3());
					deliveryAddress.setAddress4(addressType.getPmAddress().getAddress4());
					deliveryAddress.setCity(addressType.getPmAddress().getPmState().getStateName());
					deliveryAddress.setDistrict(addressType.getPmAddress().getPmDistrict()
							.getDistrictName());			
					deliveryAddress.setState(addressType.getPmAddress().getPmState().getStateName());
					deliveryAddress.setStatus(addressType.getRecordStatusFlg());
					deliveryAddress.setDeliveryAddressID(addressType.getAddressTypeId().toString());;
					System.out.println("Address1-->"+addressType.getPmAddress().getAddress1());
					address_Type[i] = null;
					i++;
				}			

				deliveryAddressRq_Type.setDeliveryAddressArray(address_Type);
				

				PHISMasterDataDeliveryAddressPortTypeProxy typeProxy = new PHISMasterDataDeliveryAddressPortTypeProxy();
				part.setRqHeader(header_Type);
				part.setDeliveryAddressRq(deliveryAddressRq_Type);	

				rs_Type = typeProxy.inquire(part);
				System.out.println("No of delivery Addreee:::"+part.getDeliveryAddressRq().getNoOfDeliveryAddress());
				System.out.println("Action:::"+part.getDeliveryAddressRq().getAction());
				System.out.println("PTJ CODe:::"+part.getDeliveryAddressRq().getPTJCode());
				System.out.println("No of delivery Addreee"+part.getDeliveryAddressRq().getDeliveryAddressArray());
				DeliveryAddress_Type[] address_Type2 = part.getDeliveryAddressRq().getDeliveryAddressArray();
				for(DeliveryAddress_Type addr:address_Type2){
					System.out.println("Address1"+addr.getAddress1());
					
				}
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("getStatusCode  :"
					+ rs_Type.getRsHeader().getStatus().getStatusCode());
			System.out.println("getStatusDesc :"
					+ rs_Type.getRsHeader().getStatus().getStatusDesc());

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

		List<VotFund> votFundList = new ArrayList<VotFund>();
		VotFund votFnd;
		for (PmVotFund votFund : votlist) {
			Date effdate = votFund.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			votFnd = new VotFund();
			votFnd.setCode(votFund.getVotFundCode());
			votFnd.setDescription(votFund.getDescription());
			if(votFund.getPmPtjGroup()!=null){
			votFnd.setPtjCode(votFund.getPmPtjGroup().getGroupCode());
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
		String dateInString = "20-09-2014";
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

		List<VotFund> votFundList = new ArrayList<VotFund>();
		VotFund votFnd;
		for (PmVotFund votFund : votlist) {
			votFnd = new VotFund();
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
		List<ProgramActivity> prgActList = new ArrayList<ProgramActivity>();
		ProgramActivity prgActivity;
		for (PmPrgActivity programActvity : votlist) {

			System.out.println("VotFundId"
					+ programActvity.getPmVotFund().getVotFundCode());
			Date effdate = programActvity.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			prgActivity = new ProgramActivity();
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

		List<PmProject> projectlist = resDTO.getProjectList();

		List<Project> programList = new ArrayList<Project>();
		Project project;
		for (PmProject pmProject : projectlist) {
			Date effdate = pmProject.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			project = new Project();
			project.setCode(pmProject.getProjectCode());
			project.setVotCode(pmProject.getPmVotFund().getVotFundCode());
			project.setSetiaCode(pmProject.getPmSetias().get(0).getSetiaCode());
			project.setSubSetiaCode(pmProject.getPmSetias().get(0)
					.getPmSubSetias().get(0).getSubSetiaCode());
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
		

		SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String dateInString = "02-07-2014";
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = ft.parse("01/10/2014");
			endDate = ft.parse("30/10/2014");
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

		List<GlAccount> glAccountList = new ArrayList<GlAccount>();
		GlAccount glAccount;
		for (PmGlAccount account : glAccountlist) {
			glAccount = new GlAccount();
			Date effdate = account.getEffDate(); // your date
			Calendar cal = Calendar.getInstance();
			cal.setTime(effdate);
			int year = cal.get(Calendar.YEAR);
			glAccount.setCode(account.getGlAccCode());
			glAccount.setDescription(account.getDescription());
			if(account.getPmPtjGroup()!=null){
			glAccount.setPtjCode(account.getPmPtjGroup().getGroupCode());
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


}
