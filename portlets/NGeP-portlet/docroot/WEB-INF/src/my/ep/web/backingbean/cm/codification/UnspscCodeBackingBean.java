package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.TaskListBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.id.serial.AlphanumericGenerator;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name="unspscCodeBackingBean")
@RequestScoped
public class UnspscCodeBackingBean extends BaseBackingBean<UnspscCodeManagedBean>{


	private static Logger log = Logger.getLogger(UnspscCodeBackingBean.class);

	public UnspscCodeBackingBean(){
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

	}

	public String submitForRequest(String action)throws Exception{

		try{
			System.out.println("action:"+action);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
			TaskListBackingBean taskListBB = new TaskListBackingBean();
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"+this.getManagedBean().getRequestCode().isBrandNew());
			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setUser(this.getManagedBean().getTempUser());
			reqDTO.setTaskId(taskListBB.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setProcessId("Codification.UNSPSCCodeRequest");
			CodificationResDTO resDTO = null;

			if(action.equalsIgnoreCase("SUBMIT")){

				if(this.getManagedBean().getRequestCode().isBrandNew()){
					reqDTO.setUser("roslan");
					resDTO = unspscSO.addCodificationRequest(reqDTO);

					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

					return "unspscRequestForm";

				}else{
					log.info("***update***");
					resDTO = unspscSO.updateCodificationRequest(reqDTO);
				}

			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Submitted Successfully", ""));
		}catch(Exception ex){
			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"BPM  ERROR", ex.getMessage()));

			return "reviewUnspscCode";
		}

		return "taskListView";


	}


	public String submitForMaintenanceRequest(String action)throws Exception{

		try{
			System.out.println("action:"+action);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
			TaskListBackingBean taskListBB = new TaskListBackingBean();
			CodificationReqDTO reqDTO = new CodificationReqDTO();
		
			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setUser(this.getManagedBean().getTempUser());
			reqDTO.setTaskId(taskListBB.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			CodificationResDTO resDTO = null;



			resDTO = unspscSO.updateCodificationRequest(reqDTO);




			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Submitted Successfully", ""));
		}catch(Exception ex){
			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"BPM  ERROR", ex.getMessage()));

			return "reviewDeleteExtension";
		}

		return "taskListView";


	}








	public void uploadUNSPSC(FileUploadEvent event) throws Exception{

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		UploadedFile item = event.getFile();

		FileInputStream file = (FileInputStream)item.getInputstream();	

		HSSFWorkbook workbook = new HSSFWorkbook(file);	

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		HSSFSheet segment = workbook.getSheetAt(2);


		log.info("*******segment***********");

		long key = 0;
		long code =0;
		String title =null;
		String desc = null;



		int lastRowNum = segment.getLastRowNum()+1;
		for(int i = 1; i < lastRowNum; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = segment.getRow(i);

			if(row.getCell(0)!=null){
				key = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(key);

			}

			if(row.getCell(1)!=null){
				code = (long) row.getCell(1).getNumericCellValue();

				unspsc.setUnspscCode(String.valueOf(code));
			}

			if(row.getCell(2)!=null){
				title =  row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(title);

			}

			if(row.getCell(3)!=null){
				desc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(desc);

			}

			unspsc.setUnspscLevel(0);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

		}

		CodificationResDTO resDTO = unspscSO.saveUnspsc(reqDTO);
		log.info("*******end Family***********");

		///////////////////////////////////////////////////////////////////////

		HSSFSheet family = workbook.getSheetAt(3);

		long familyKey = 0;
		long familyCode =0;
		String familyTitle =null;
		String familyDesc = null;


		int lastRowNumFamily = family.getLastRowNum()+1;
		for(int i = 1; i < lastRowNumFamily; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = family.getRow(i);

			if(row.getCell(0)!=null){
				familyKey = (long) row.getCell(0).getNumericCellValue();

				unspsc.setUnspscKey(familyKey);
			}

			if(row.getCell(1)!=null){
				familyCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(familyCode);

				unspsc.setUnspscCode(String.valueOf(familyCode));
				unspsc.setUnspscParentCode(parentCode);

			}

			if(row.getCell(2)!=null){
				familyTitle =  row.getCell(2).getStringCellValue();

				unspsc.setUnspscTitle(familyTitle);
			}

			if(row.getCell(3)!=null){
				familyDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(familyDesc);
			}


			unspsc.setUnspscLevel(1);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());


		}
		resDTO = unspscSO.saveFamilyList(reqDTO);

		///////////////////////////////////////////////////////////////////////

		HSSFSheet classes = workbook.getSheetAt(4);

		long classKey = 0;
		long classCode =0;
		String classTitle =null;
		String classDesc = null;

		int lastRowNumClass = classes.getLastRowNum()+1;
		for(int i = 1; i < lastRowNumClass; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = classes.getRow(i);

			if(row.getCell(0)!=null){
				classKey = (long) row.getCell(0).getNumericCellValue();
				unspsc.setUnspscKey(classKey);

			}

			if(row.getCell(1)!=null){

				classCode = (long) row.getCell(1).getNumericCellValue();

				String parentCode = getParentCode(classCode);

				unspsc.setUnspscCode(String.valueOf(classCode));
				unspsc.setUnspscParentCode(parentCode);
			}

			if(row.getCell(2)!=null){
				classTitle =  row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(classTitle);
			}

			if(row.getCell(3)!=null){
				classDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(classDesc);
			}


			unspsc.setUnspscLevel(2);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getClassList().add(reqDTO.getCmUnspsc());

		}


		resDTO = unspscSO.saveClassList(reqDTO);

		HSSFSheet commodity = workbook.getSheetAt(5);

		long commodityKey = 0;
		long commodityCode =0;
		String commodityTitle =null;
		String commodityDesc = null;

		int lastRowNumCommodity = commodity.getLastRowNum()+1;


		for(int i = 1; i < lastRowNumCommodity; i++) {
			CmUnspsc unspsc = new CmUnspsc();
			Row row = commodity.getRow(i);

			if(row.getCell(6)!=null && row.getCell(6).toString().trim().length()>0){

				commodityKey = (long) row.getCell(6).getNumericCellValue();

				unspsc.setUnspscKey(commodityKey);

			}

			if(row.getCell(7)!=null && row.getCell(7).toString().trim().length()>0){

				commodityCode = (long) row.getCell(7).getNumericCellValue();

				String parentCode = getParentCode(commodityCode);

				unspsc.setUnspscCode(String.valueOf(commodityCode));
				unspsc.setUnspscParentCode(parentCode);
			}

			if(row.getCell(8)!=null && row.getCell(8).toString().trim().length()>0){
				commodityTitle =  row.getCell(8).getStringCellValue();

				unspsc.setUnspscTitle(commodityTitle);

			}

			if(row.getCell(9)!=null && row.getCell(9).toString().trim().length()>0){
				commodityDesc = row.getCell(9).getStringCellValue();

				unspsc.setUnspscDescription(commodityDesc);

			}

			unspsc.setUnspscLevel(3);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());


		}


		resDTO = unspscSO.saveCommodityList(reqDTO);



	}

	public String getParentCode(long code){

		String parent = null;

		String check68 = (String)String.valueOf(code).substring(6, 8);
		String check46 = (String)String.valueOf(code).substring(4, 6);
		String check24 = (String)String.valueOf(code).substring(2, 4);

		if(!check68.equalsIgnoreCase("00")){
			log.debug("commodity");
			parent = (String)String.valueOf(code).substring(0, 6) + "00";

			log.debug("parent:"+parent);
		}else{
			if(!check46.equalsIgnoreCase("00")){
				log.debug("class");
				parent = (String)String.valueOf(code).substring(0, 4) + "0000";
				log.debug("parent:"+parent);
			}else{
				if(!check24.equalsIgnoreCase("00")){
					log.debug("family");
					parent = (String)String.valueOf(code).substring(0, 2) + "000000";
					log.debug("parent:"+parent);
				}else{
					log.debug("segment");
				}

			}

		}

		return parent;
	}

	public Long checkUnspscLevel(long code){

		long level = 0;

		String check68 = (String)String.valueOf(code).substring(6, 8);
		String check46 = (String)String.valueOf(code).substring(4, 6);
		String check24 = (String)String.valueOf(code).substring(2, 4);

		if(!check68.equalsIgnoreCase("00")){
			log.debug("commodity");
			level = 3;

		}else{
			if(!check46.equalsIgnoreCase("00")){
				log.debug("class");
				level = 2;
			}else{
				if(!check24.equalsIgnoreCase("00")){
					log.debug("family");
					level = 1;
				}else{
					log.debug("segment");
					level = 0;
				}

			}

		}

		return level;
	}

	public String navigateToSearch(){
		log.info("*****navigateToSearch******");

		return "../cm/codification/verifyUnspscSearch.xhtml";
	}


	public String createTempCode(){

		log.info("ID:"+this.getManagedBean().getRequestCode().getRequestId());

		return "../cm/codification/unspscSingleUpload.xhtml";
	}

	public String savetempCode(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		TaskListBackingBean taskListBB = new TaskListBackingBean();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		CmUnspsc unspsc = new CmUnspsc();
		unspsc.setUnspscCode(this.getManagedBean().getSegmentCode());	
		unspsc.setUnspscKey(this.getManagedBean().getSegmentKey());
		unspsc.setUnspscTitle(this.getManagedBean().getSegmentTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getSegmentDesc());
		unspsc.setUnspscLevel(0);

		reqDTO.setCmUnspsc(unspsc);
		reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

		resDTO =  unspscSO.saveUnspsc(reqDTO);
		////// start family

		unspsc.setUnspscCode(this.getManagedBean().getFamilyCode());	
		unspsc.setUnspscKey(this.getManagedBean().getFamilyKey());
		unspsc.setUnspscTitle(this.getManagedBean().getFamilyTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getFamilyDesc());
		unspsc.setUnspscLevel(1);
		unspsc.setUnspscParentCode(this.getManagedBean().getSegmentCode());

		reqDTO.setCmUnspsc(unspsc);
		reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
		resDTO = unspscSO.saveFamilyList(reqDTO);

		/////start class	

		unspsc.setUnspscCode(this.getManagedBean().getClassCode());	
		unspsc.setUnspscKey(this.getManagedBean().getClassKey());
		unspsc.setUnspscTitle(this.getManagedBean().getClassTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getClassDesc());
		unspsc.setUnspscLevel(2);
		unspsc.setUnspscParentCode(this.getManagedBean().getFamilyCode());

		reqDTO.setCmUnspsc(unspsc);
		reqDTO.getClassList().add(reqDTO.getCmUnspsc());
		resDTO = unspscSO.saveClassList(reqDTO);

		////start commodity

		unspsc.setUnspscCode(this.getManagedBean().getCommodityCode());	
		unspsc.setUnspscKey(this.getManagedBean().getCommodityKey());
		unspsc.setUnspscTitle(this.getManagedBean().getCommodityTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getCommodityDesc());
		unspsc.setUnspscLevel(3);
		unspsc.setUnspscParentCode(this.getManagedBean().getClassCode());

		reqDTO.setCmUnspsc(unspsc);
		reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());

		resDTO = unspscSO.saveCommodityList(reqDTO);

		String check68 = (String)String.valueOf(this.getManagedBean().getCommodityCode()).substring(6, 8);
		String check46 = (String)String.valueOf(this.getManagedBean().getClassCode()).substring(4, 6);
		String check24 = (String)String.valueOf(this.getManagedBean().getFamilyCode()).substring(2, 4);
		String check02 = (String)String.valueOf(this.getManagedBean().getSegmentCode()).substring(0, 2);

		String tempCode = check02 + check24 +check46 +check68;
		log.info(tempCode);

		this.getManagedBean().getRequestCode().setTempCode(tempCode);
		reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
		reqDTO.setUser("lailah");
		reqDTO.setTaskId(taskListBB.getManagedBean().getTaskId());
		reqDTO.setOutCome("CREATE");
		try {
			resDTO = unspscSO.updateCodificationRequest(reqDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//BpmResponseDTO responseDTO = updateTask(resDTO.getRequestCode(),taskListBB.getManagedBean().getTaskId());
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		return "taskListView";

	}

	public String createExtCode(){

		log.info("**************************");
		//this.getManagedBean().getRequestCode()

		return "../cm/codification/extensionCodeSingleCreation.xhtml"; 
	}


	public String saveExtCode(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		TaskListBackingBean taskListBB = new TaskListBackingBean();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		CmItem item = new CmItem();

		System.out.println("this.getManagedBean().getRequestCode().getTempCode():"+this.getManagedBean().getRequestCode().getTempCode());
		reqDTO.setUnspscCode(this.getManagedBean().getRequestCode().getTempCode());
		resDTO = unspscSO.searchCommodityObj(reqDTO);



		AlphanumericGenerator brandCode = new AlphanumericGenerator(false, "000");
		AlphanumericGenerator typeCode = new AlphanumericGenerator(false, "00");
		AlphanumericGenerator measurementCode = new AlphanumericGenerator(false, "000");
		AlphanumericGenerator colorCode = new AlphanumericGenerator(false, "00");

		String brand = brandCode.nextStringIdentifier();
		String type = typeCode.nextStringIdentifier();
		String measurement = measurementCode.nextStringIdentifier();
		String color = colorCode.nextStringIdentifier();
		log.info("brand:"+brand);

		//////////brand//////////
		System.out.println("getUnspscCode:"+resDTO.getCommodityObj().getUnspscCode());

		log.info("****************************************************");
		this.getManagedBean().getCmBrand().setBrandCode(brand);
		this.getManagedBean().getCmBrand().setUnspscId(resDTO.getCommodityObj());
		this.getManagedBean().getCmBrand().setRecordStatus(true);
		reqDTO.setCmBrand(this.getManagedBean().getCmBrand());

		//CodificationResDTO resBrandDTO = unspscSO.addNewBrand(reqDTO);
		//item.setCmBrand(resBrandDTO.getCmBrand());
		/////////////type//////////////////

		this.getManagedBean().getCmType().setTypeCode(type);
		this.getManagedBean().getCmType().setUnspscId(resDTO.getCommodityObj());
		this.getManagedBean().getCmType().setRecordStatus(true);

		reqDTO.setCmType(this.getManagedBean().getCmType());
		//CodificationResDTO resTypeDTO = unspscSO.addNewType(reqDTO);
		//item.setCmType(resTypeDTO.getCmType());
		/////////////measurement///////////////////////////

		this.getManagedBean().getCmMeasurement().setMeasurementCode(measurement);
		this.getManagedBean().getCmMeasurement().setUnspscId(resDTO.getCommodityObj());
		this.getManagedBean().getCmMeasurement().setRecordStatus(true);

		reqDTO.setCmMeasurement(this.getManagedBean().getCmMeasurement());
		//CodificationResDTO resMeasurementDTO = unspscSO.addNewMeasurement(reqDTO);
		//item.setCmMeasurement(resMeasurementDTO.getCmMeasurement());
		//////////////////////////////////////

		this.getManagedBean().getCmColor().setColorCode(color);
		this.getManagedBean().getCmColor().setRecordStatus(true);
		reqDTO.setCmColor(this.getManagedBean().getCmColor());

		//CodificationResDTO resColorDTO = unspscSO.addNewColor(reqDTO);
		//item.setCmColor(resColorDTO.getCmColor());
		System.out.println("************New Extension End***************");

		String extensionCode = type+brand+measurement+color;

		log.info("extensionCode:"+extensionCode);


		item.setRecordStatus(true);
		item.setCmUnspsc(resDTO.getCommodityObj());
		item.setExtensionCode(extensionCode);
		reqDTO.setCmItem(item);
		reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
		reqDTO.setTaskId(taskListBB.getManagedBean().getTaskId());
		reqDTO.setUser("khai");
		reqDTO.setOutCome("CREATE");
		resDTO = unspscSO.addNewItem(reqDTO);

		System.out.println("****************save Item End******************");


		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		return "taskListView";

	}


	public void addNewRequest(){


		if(!this.getManagedBean().getRequestExtCode().isRequestType()){
			this.getManagedBean().getRequestExtCode().setTypeName(this.getManagedBean().getTypeName());
		}

		if(!this.getManagedBean().getRequestExtCode().isRequestBrand()){
			this.getManagedBean().getRequestExtCode().setBrandName(this.getManagedBean().getBrandName());
		}

		if(!this.getManagedBean().getRequestExtCode().isRequestColor()){
			this.getManagedBean().getRequestExtCode().setColorName(this.getManagedBean().getColorName());
		}

		if(!this.getManagedBean().getRequestExtCode().isRequestMeasurement()){
			this.getManagedBean().getRequestExtCode().setMeasurementName(this.getManagedBean().getMeasurementName());
		}

		this.getManagedBean().getRequestExtCodeList().add(this.getManagedBean().getRequestExtCode());

	}

	public void addMoreRequest(){

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		//reqDTO.setParentCode(this.getManagedBean().getUnspscCode());
		reqDTO.setUnspscCode("10161509");

		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

		this.getManagedBean().setTypeList(resDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(resDTO.getCommodityObj().getCmBrand());		
		this.getManagedBean().setMeasurementList(resDTO.getCommodityObj().getCmMeasurement());
		resDTO = codificationSO.searchColor(reqDTO);
		this.getManagedBean().setColorList(resDTO.getColorList());

		this.getManagedBean().setRequestExtCode(new CmRequestCode());


	}

	public void submitToBPM(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setRequestExtList(this.getManagedBean().getRequestExtCodeList());

		CodificationResDTO resDTO = unspscSO.addExtensionRequest(reqDTO);


		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

	}



	public String submitToReviewExt(String action){
		System.out.println("***action***"+action);
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		TaskListBackingBean taskListBB = new TaskListBackingBean();
		for(CmRequestCode code:this.getManagedBean().getReviewExtCodeList()){

			if(code.getColorNameDD()!=null){
				code.setColorName(code.getColorNameDD());
			}
			if(code.getBrandNameDD()!=null){
				code.setBrandName(code.getBrandNameDD());
			}

			if(code.getTypeNameDD()!=null){
				code.setTypeName(code.getTypeNameDD());
			}

			if(code.getMeasurementNameDD()!=null){
				code.setMeasurementName(code.getMeasurementNameDD());
			}
			code.setRequestType(false);
			code.setRequestColor(false);
			code.setRequestMeasurement(false);
			code.setRequestBrand(false);
			reqDTO.getRequestExtList().add(code);

		}
		reqDTO.setTaskId(taskListBB.getManagedBean().getTaskId());
		reqDTO.setOutCome(action);
		reqDTO.setUser(this.getManagedBean().getTempUser());


		CodificationResDTO resDTO = unspscSO.updateExtensionRequest(reqDTO);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		return "taskListView";

	}



	/*	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");


		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();


		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		CDRequestDataType unspsc = (CDRequestDataType)obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
		codeDTO.setRequestId(new Long(unspsc.getRequestId()));
		RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);


		this.getManagedBean().setRequestCode(resDTO.getRequestCode());
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setAction(res.getAction());

	}*/


	public String deleteExtensionCode(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		TaskListBackingBean taskListBB = new TaskListBackingBean();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CmRequestCode code = new CmRequestCode();
		CodificationBackingBean cdBB = new CodificationBackingBean();
		try {

			code.setItemId(cdBB.getManagedBean().getDeleteItem().getItemId());
			code.setTempCode(cdBB.getManagedBean().getDeleteItem().getCmUnspsc().getUnspscCode());
			code.setTempTitle(cdBB.getManagedBean().getDeleteItem().getCmUnspsc().getUnspscTitle());
			code.setBrandName(cdBB.getManagedBean().getDeleteItem().getCmBrand().getBrandName());
			code.setItemName(cdBB.getManagedBean().getDeleteItem().getItemName());
			code.setTypeName(cdBB.getManagedBean().getDeleteItem().getCmType().getTypeName());
			code.setMeasurementName(cdBB.getManagedBean().getDeleteItem().getCmMeasurement().getMeasurementName());
			code.setColorName(cdBB.getManagedBean().getDeleteItem().getCmColor().getColorName());
			reqDTO.setUser("lailah");
			reqDTO.setTaskId(taskListBB.getManagedBean().getTaskId());
			reqDTO.setOutCome("SUBMIT");
			reqDTO.setRequestCode(code);
			reqDTO.setProcessId("Codification.ExtensionCodeMaintenance");



			CodificationResDTO resDTO = unspscSO.addCodificationRequest(reqDTO);


			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return "extensionSearch";
	}






}
