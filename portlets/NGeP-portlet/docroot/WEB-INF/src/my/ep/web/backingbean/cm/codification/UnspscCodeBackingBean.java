package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.jaxb.cd.unspscdata.CDUNSPSCDataType;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

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

	public void submitForRequest(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		log.info("new:"+this.getManagedBean().getRequestCode().isBrandNew());
		reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
		CodificationResDTO resDTO = null;
		if(this.getManagedBean().getRequestCode().isBrandNew()){
			 resDTO = unspscSO.addCodificationRequest(reqDTO);
			 BpmResponseDTO responseDTO = initiateTask(resDTO.getRequestCode());
		}else{
			log.info("***update***");
			 resDTO = unspscSO.updateCodificationRequest(reqDTO);
			 BpmResponseDTO responseDTO = updateTask(resDTO.getRequestCode());
		}
		

	}
	
	
	public BpmResponseDTO updateTask(CmRequestCode request){
		System.out.println("initiateTask******");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("lailah","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		log.info("request.getTaskId():"+request.getTaskId());
	////taskID , reqDTO.setOutcome("SUBMIT"), 	bpmSO.updateTask(reqDTO)
		reqDTO.setOutcome("SUBMIT");
		CDUNSPSCDataType obj = new CDUNSPSCDataType();
		obj.setRequestId(String.valueOf(request.getRequestId()));
		//obj.setPtj(value);
		reqDTO.setPojo(obj);
		
		reqDTO.setTaskId(request.getTaskId());
		BpmResponseDTO res = bpmSO.updateTask(reqDTO);
	
		log.info("1:"+res.isSuccessful());
		log.info("2:"+res.getErrorReason());
		//log.info("ID:"+obj.getRequestId());

		//--
		//	  Object asd = res.getPojo();
		//	  CDUNSPSCDataType qwe = (CDUNSPSCDataType)asd;
		//	  qwe.getPtj();
		//--
		return res;
	}



	public BpmResponseDTO initiateTask(CmRequestCode request){
		System.out.println("initiateTask******");
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String ctx = bpmSO.authenticate("roslan","welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);

		//  reqDTO.setProcessId("Procurement_Strategy.EndorseBudget");
		reqDTO.setProcessId("Codification.RequestUNSPSC");
		reqDTO.setOutcome("SUBMIT");
		CDUNSPSCDataType obj = new CDUNSPSCDataType();
		obj.setRequestId(String.valueOf(request.getRequestId()));
		//obj.setPtj(value);
		reqDTO.setPojo(obj);
		BpmResponseDTO res = bpmSO.initiateAndUpdateTask(reqDTO);
	
		log.info("1:"+res.isSuccessful());
		log.info("2:"+res.getErrorReason());
		log.info("ID:"+obj.getRequestId());

		//--
		//	  Object asd = res.getPojo();
		//	  CDUNSPSCDataType qwe = (CDUNSPSCDataType)asd;
		//	  qwe.getPtj();
		//--
		return res;
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
	
	public void savetempCode(){
		
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		
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
			
		//resDTO =  unspscSO.saveUnspsc(reqDTO);
		////// start family
		
		unspsc.setUnspscCode(this.getManagedBean().getFamilyCode());	
		unspsc.setUnspscKey(this.getManagedBean().getFamilyKey());
		unspsc.setUnspscTitle(this.getManagedBean().getFamilyTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getFamilyDesc());
        unspsc.setUnspscLevel(1);
        unspsc.setUnspscParentCode(this.getManagedBean().getSegmentCode());
        
        reqDTO.setCmUnspsc(unspsc);
        reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
		//resDTO = unspscSO.saveFamilyList(reqDTO);
		
		/////start class	
	
		unspsc.setUnspscCode(this.getManagedBean().getClassCode());	
		unspsc.setUnspscKey(this.getManagedBean().getClassKey());
		unspsc.setUnspscTitle(this.getManagedBean().getClassTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getClassDesc());
        unspsc.setUnspscLevel(2);
        unspsc.setUnspscParentCode(this.getManagedBean().getFamilyCode());
        
        reqDTO.setCmUnspsc(unspsc);
        reqDTO.getClassList().add(reqDTO.getCmUnspsc());
        //resDTO = unspscSO.saveClassList(reqDTO);
        
        ////start commodity
		
    	unspsc.setUnspscCode(this.getManagedBean().getCommodityCode());	
		unspsc.setUnspscKey(this.getManagedBean().getCommodityKey());
		unspsc.setUnspscTitle(this.getManagedBean().getCommodityTitle());
		unspsc.setUnspscDescription(this.getManagedBean().getCommodityDesc());
        unspsc.setUnspscLevel(3);
        unspsc.setUnspscParentCode(this.getManagedBean().getClassCode());
        
        reqDTO.setCmUnspsc(unspsc);
        reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());
        
        //resDTO = unspscSO.saveCommodityList(reqDTO);
        
        String check68 = (String)String.valueOf(this.getManagedBean().getCommodityCode()).substring(6, 8);
		String check46 = (String)String.valueOf(this.getManagedBean().getClassCode()).substring(4, 6);
		String check24 = (String)String.valueOf(this.getManagedBean().getFamilyCode()).substring(2, 4);
		String check02 = (String)String.valueOf(this.getManagedBean().getSegmentCode()).substring(0, 2);
        
        String tempCode = check02 + check24 +check46 +check68;
        log.info(tempCode);
        
        this.getManagedBean().getRequestCode().setTempCode(tempCode);
        reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
        resDTO = unspscSO.updateCodificationRequest(reqDTO);
        

		BpmResponseDTO responseDTO = updateTask(resDTO.getRequestCode());
          
		
	}
	
	public String createExtCode(){
		
		return "../cm/codification/extensionCodeSingleCreation.xhtml"; 
	}
	
	
	



}
