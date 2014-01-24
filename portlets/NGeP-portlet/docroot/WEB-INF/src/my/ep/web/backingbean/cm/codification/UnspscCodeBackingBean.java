package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constants.CDProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.jaxb.cd.requestdata.CDRequestDataType;
import my.ep.persistence.entity.cm.catalog.CmBrand;
import my.ep.persistence.entity.cm.catalog.CmColor;
import my.ep.persistence.entity.cm.catalog.CmMeasurement;
import my.ep.persistence.entity.cm.catalog.CmRecommendCode;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmRequestRemark;
import my.ep.persistence.entity.cm.catalog.CmType;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name="unspscCodeBackingBean")
@RequestScoped
public class UnspscCodeBackingBean extends BaseBackingBean<UnspscCodeManagedBean>{


	private static Logger log = Logger.getLogger(UnspscCodeBackingBean.class);

	private SearchFilterBean colorFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean typeFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean brandFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean measurementFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());

	public UnspscCodeBackingBean(){
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

	}

	public boolean isCatalogueAdmin() {
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CM_ADMIN");
	}

	public boolean isSupplierUser(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_USER");
	}

	public boolean isSupplierAdmin(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("SUPPLIER_ADMIN");
	}

	public boolean isCodeAdmin(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_OFFICER");
	}

	public boolean isRequisitioner(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("REQUISITIONER");
	}

	public boolean isCdCommittee(){
		FacesScopes fs = new FacesScopes();
		return fs.hasRoleUser("CD_COMMITTEE");
	}







	public String submitForRequest(String action)throws Exception{

		try{
			System.out.println("action:"+action);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"+this.getManagedBean().getRequestCode().isBrandNew());
			
			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);
			reqDTO.setToken(token);
			CodificationResDTO resDTO = null;

			if(action.equalsIgnoreCase("SUBMIT")){

				if(this.getManagedBean().getRequestCode().isBrandNew()){

					resDTO = unspscSO.addCodificationRequest(reqDTO);

					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

					System.out.println("***************BB end*****************");

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

			return "unspscRequestForm";
		}

		return "taskListView";


	}



	public String bpmTesting(String action)throws Exception{

		try{
			System.out.println("action:"+action);
			CodificationBackingBean codeBB = new CodificationBackingBean();
			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"+this.getManagedBean().getRequestCode().isBrandNew());

			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);

			if(action.equalsIgnoreCase("RECOMMEND")){

				if(!(this.getManagedBean().getRequestCode().getCmRecommendCodes()!=null && this.getManagedBean().getRequestCode().getCmRecommendCodes().size()>0)){
					System.out.println("**********warning***************");

					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Recommendation required",""));

					return "reviewUnspscCode";
				}


				for(CmRecommendCode unspsc: this.getManagedBean().getRequestCode().getCmRecommendCodes()){

					unspsc.setCmRequestCode(this.getManagedBean().getRequestCode());

				}

			}

			System.out.println("size:"+this.getManagedBean().getRequestCode().getCmRecommendCodes().size()); 
			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());

			CodificationResDTO resDTO = null;



			if(this.getManagedBean().getRequestCode().isBrandNew()){

				resDTO = unspscSO.addCodificationRequest(reqDTO);

				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

				return "unspscRequestForm";

			}else{
				log.info("***update***");
				System.out.println("size:"+reqDTO.getRequestCode().getCmRecommendCodes().size()); 
				resDTO = unspscSO.updateCodificationRequest(reqDTO);
			}



			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Submitted Successfully", ""));
		}catch(Exception ex){
			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"BPM  ERROR", ex.getMessage()));

			return "reviewUnspscCode";
		}

		return "BPMtesting";


	}


	public String selectSubmitRecommend(String action)throws Exception{

		try{

			System.out.println("this.getManagedBean().getRequestCode().getCmRecommendCodes().size()"+this.getManagedBean().getRequestCode().getCmRecommendCodes().size());
			if(this.getManagedBean().getRequestCode().getCmRecommendCodes().size()>1 ){
				System.out.println("***************more than 1*****************");
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Select 1 recommendation to proceed",""));

				return "selectRecommendUnspscCode";
			}

			CodificationBackingBean codeBB = new CodificationBackingBean();
			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();

			CodificationReqDTO reqDTO = new CodificationReqDTO();
			log.info("new:"+this.getManagedBean().getRequestCode().isBrandNew());

			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setProcessId(CDProcessNameConst.UNSPSC_CODE_REQUEST_PROCESS_ID);


			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());

			CodificationResDTO resDTO = null;


			log.info("***update***");
			System.out.println("size:"+reqDTO.getRequestCode().getCmRecommendCodes().size()); 
			resDTO = unspscSO.updateCodificationRequest(reqDTO);




			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO," Submitted Successfully", ""));
		}catch(Exception ex){
			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"BPM  ERROR", ex.getMessage()));

			return "selectRecommendUnspscCode";
		}

		return "BPMtesting";


	}



	public String submitForMaintenanceRequest(String action)throws Exception{

		try{
			System.out.println("action:"+action);

			ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();
			CodificationReqDTO reqDTO = new CodificationReqDTO();

			reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
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

		//return "taskListView";
		return "BPMtesting";

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


		if(this.getManagedBean().getRequestCode().getCmRecommendCodes()!=null && this.getManagedBean().getRequestCode().getCmRecommendCodes().size()>0){


			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"CM ADMIN Only Can Have 1 Recommendation",""));
			return "reviewUnspscCode";
		}


		return "verifyUnspscSearch?faces-redirect=true";
	}

	public String navigateCodeAdminToSearch(){
		log.info("*****navigateCodeAdminToSearch******");



		return "verifyUnspscSearch?faces-redirect=true";
	}


	public String createTempCode(){

		log.info("ID:"+this.getManagedBean().getRequestCode().getRequestId());

		return "unspscSingleUpload?faces-redirect=true";
	}

	public String savetempCode(String outcome){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		try {

			if(outcome.equalsIgnoreCase("SUBMIT")){
				System.out.println("***this.getManagedBean().isNewSegment()"+this.getManagedBean().isNewSegment());
				
				if(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode().isEmpty()
						|| this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode().isEmpty()
						|| this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode().isEmpty()
						||this.getManagedBean().getSelectedLovUnspsc().getUnspscCode().isEmpty()){
					
					
					
					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Please Complete Temporary Code Before Proceed",""));

					return "reviewBeforeCreateTempCode";
					
				}



					if(this.getManagedBean().isNewSegment()){
						CmUnspsc unspsc = new CmUnspsc();
						unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());	
						unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscKey());
						unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscTitle());
						unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscDescription());
						unspsc.setUnspscLevel(0);

						reqDTO.setCmUnspsc(unspsc);
						reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

						//resDTO =  unspscSO.saveUnspsc(reqDTO);
					}
				////// start family
				System.out.println("***this.getManagedBean().isNewFamily()"+this.getManagedBean().isNewFamily());
				if(this.getManagedBean().isNewFamily()){
					CmUnspsc unspsc = new CmUnspsc();
					System.out.println("this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()"+this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());	
					unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscDescription());
					unspsc.setUnspscLevel(1);
					unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getFamilyList().add(reqDTO.getCmUnspsc());
					//resDTO = unspscSO.saveFamilyList(reqDTO);
				}

				/////start class	
				System.out.println("***this.getManagedBean().isNewClass()"+this.getManagedBean().isNewClass());
				if(this.getManagedBean().isNewClass()){
					CmUnspsc unspsc = new CmUnspsc();
					System.out.println("this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()"+this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());
					unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());	
					unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspscClass().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspscClass().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspscClass().getUnspscDescription());
					unspsc.setUnspscLevel(2);
					unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getClassList().add(reqDTO.getCmUnspsc());
					//resDTO = unspscSO.saveClassList(reqDTO);

				}
				////start commodity
				System.out.println("***this.getManagedBean().isNewCommodity()"+this.getManagedBean().isNewCommodity());
				if(this.getManagedBean().isNewCommodity()){
					CmUnspsc unspsc = new CmUnspsc();
					unspsc.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());	
					unspsc.setUnspscKey(this.getManagedBean().getSelectedLovUnspsc().getUnspscKey());
					unspsc.setUnspscTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
					unspsc.setUnspscDescription(this.getManagedBean().getSelectedLovUnspsc().getUnspscDescription());
					unspsc.setUnspscLevel(3);
					unspsc.setUnspscParentCode(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode());

					reqDTO.setCmUnspsc(unspsc);
					reqDTO.getCommodityList().add(reqDTO.getCmUnspsc());

					//resDTO = unspscSO.saveCommodityList(reqDTO);
				}

				String check68 = (String)String.valueOf(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode()).substring(6, 8);
				String check46 = (String)String.valueOf(this.getManagedBean().getSelectedLovUnspscClass().getUnspscCode()).substring(4, 6);
				String check24 = (String)String.valueOf(this.getManagedBean().getSelectedLovUnspscfamily().getUnspscCode()).substring(2, 4);
				String check02 = (String)String.valueOf(this.getManagedBean().getSelectedLovUnspscSegment().getUnspscCode()).substring(0, 2);

				String tempCode = check02 + check24 +check46 +check68;
				System.out.println("tempCode:"+tempCode);
				this.getManagedBean().getRequestCode().setTempCode(tempCode);
				this.getManagedBean().getRequestCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());

				this.getManagedBean().getRequestCode().setRequestType(true);
				this.getManagedBean().getRequestCode().setRequestBrand(true);
				this.getManagedBean().getRequestCode().setRequestColor(true);
				this.getManagedBean().getRequestCode().setRequestMeasurement(true);
				reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
				reqDTO.setToken(token);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setOutCome(outcome);
				System.out.println("**********savetempCode************");


				reqDTO.setCodeCommittees("user003,user051");

				resDTO = unspscSO.updateCodificationRequest(reqDTO);
			}else if(outcome.equalsIgnoreCase("RECOMMEND")){


				if(!(this.getManagedBean().getRequestCode().getCmRecommendCodes()!=null && this.getManagedBean().getRequestCode().getCmRecommendCodes().size()>0)){
					System.out.println("**********warning***************");

					FacesContext facesContext = FacesContext.getCurrentInstance();
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Recommendation required",""));

					return "reviewBeforeCreateTempCode";
				}

				for(CmRecommendCode code: this.getManagedBean().getRequestCode().getCmRecommendCodes()){

					code.setCmRequestCode(this.getManagedBean().getRequestCode());

				}

				reqDTO.setRequestCode(this.getManagedBean().getRequestCode());
				reqDTO.setToken(token);
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setOutCome(outcome);

				resDTO = unspscSO.updateCodificationRequest(reqDTO);

			}
		} catch (Exception ex) {

			System.out.println("***************Exception*****************");
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"BPM  ERROR", ex.getMessage()));

			return "reviewBeforeCreateTempCode";
		}


		//BpmResponseDTO responseDTO = updateTask(resDTO.getRequestCode(),taskListBB.getManagedBean().getTaskId());
		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		//return "taskListView";
		return "BPMtesting";

	}

	public String createExtCode(){

		log.info("**************************");
		//this.getManagedBean().getRequestCode()

		return "extensionCodeSingleCreation.xhtml"; 
	}


	/*public String saveExtCode(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
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
		CmBrand cmBrand = new CmBrand();

		log.info("****************************************************");
		cmBrand.setBrandName(this.getManagedBean().getRequestCode().getBrandName());
		cmBrand.setBrandCode(brand);
		cmBrand.setUnspscId(resDTO.getCommodityObj());
		cmBrand.setRecordStatus(true);
		reqDTO.setCmBrand(cmBrand);

		//CodificationResDTO resBrandDTO = unspscSO.addNewBrand(reqDTO);
		//item.setCmBrand(resBrandDTO.getCmBrand());
		/////////////type//////////////////
		CmType cmType = new CmType();

		cmType.setTypeName(this.getManagedBean().getRequestCode().getTypeName());
		cmType.setTypeCode(type);
		cmType.setUnspscId(resDTO.getCommodityObj());
		cmType.setRecordStatus(true);

		reqDTO.setCmType(cmType);
		//CodificationResDTO resTypeDTO = unspscSO.addNewType(reqDTO);
		//item.setCmType(resTypeDTO.getCmType());
		/////////////measurement///////////////////////////

		CmMeasurement cmMeasurement = new CmMeasurement();

		cmMeasurement.setMeasurementName(this.getManagedBean().getRequestCode().getMeasurementName());
		cmMeasurement.setMeasurementCode(measurement);
		cmMeasurement.setUnspscId(resDTO.getCommodityObj());
		cmMeasurement.setRecordStatus(true);

		reqDTO.setCmMeasurement(cmMeasurement);
		//CodificationResDTO resMeasurementDTO = unspscSO.addNewMeasurement(reqDTO);
		//item.setCmMeasurement(resMeasurementDTO.getCmMeasurement());
		//////////////////////////////////////

		CmColor cmColor = new CmColor();
		cmColor.setColorName(this.getManagedBean().getRequestCode().getColorName());
		cmColor.setColorCode(color);
		cmColor.setRecordStatus(true);
		reqDTO.setCmColor(cmColor);

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
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setToken(token);
		reqDTO.setOutCome("CREATE");
		resDTO = unspscSO.addNewItem(reqDTO);

		System.out.println("****************save Item End******************");


		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		//return "taskListView";
		return "BPMtesting";

	}*/




	public void addMoreRequest(){

		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		CodificationReqDTO reqDTO = new CodificationReqDTO();
		//reqDTO.setParentCode(this.getManagedBean().getUnspscCode());
		reqDTO.setUnspscCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());

		CodificationResDTO resDTO = codificationSO.searchCommodityObj(reqDTO);

		this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());
		this.getManagedBean().setTypeList(resDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(resDTO.getCommodityObj().getCmBrand());		
		this.getManagedBean().setMeasurementList(resDTO.getCommodityObj().getCmMeasurement());
		resDTO = codificationSO.searchColor(reqDTO);
		this.getManagedBean().setColorList(resDTO.getColorList());

		this.getManagedBean().setRequestExtCode(new CmRequestCode());
		this.getManagedBean().getRequestExtCode().setTempCode(this.getManagedBean().getSelectedLovUnspsc().getUnspscCode());
		this.getManagedBean().getRequestExtCode().setTempTitle(this.getManagedBean().getSelectedLovUnspsc().getUnspscTitle());

	}

	public void submitToBPM(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		reqDTO.setRequestExtList(this.getManagedBean().getRequestExtCodeList());
		reqDTO.setToken(token);






		CodificationResDTO resDTO = unspscSO.addExtensionRequest(reqDTO);


		FacesContext facesContext = FacesContext.getCurrentInstance();
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

	}


	public void addNewRequest(){
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		if(!this.getManagedBean().getRequestExtCode().isRequestType()){

			System.out.println("this.getManagedBean().getTypeId()"+this.getManagedBean().getTypeId());
			ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
			typeFilterBean.setSearchField("typeId");
			typeFilterBean.setSearchValue(this.getManagedBean().getTypeId());
			searchBeanColor.getDataFilterBeans().add(typeFilterBean);

			reqDTO.setTypeSearchBean(searchBeanColor);

			resDTO = unspscSO.searchTypeObj(reqDTO);

			this.getManagedBean().getRequestExtCode().setTypeName(resDTO.getTypeList().get(0).getTypeName());
			this.getManagedBean().getRequestExtCode().setTypeId(this.getManagedBean().getTypeId());

		}

		if(!this.getManagedBean().getRequestExtCode().isRequestBrand()){

			ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
			brandFilterBean.setSearchField("brandId");
			brandFilterBean.setSearchValue(this.getManagedBean().getBrandId());
			searchBeanColor.getDataFilterBeans().add(brandFilterBean);

			reqDTO.setBrandSearchBean(searchBeanColor);

			resDTO = unspscSO.searchBrandObj(reqDTO);

			this.getManagedBean().getRequestExtCode().setBrandName(resDTO.getBrandList().get(0).getBrandName());
			this.getManagedBean().getRequestExtCode().setBrandId(this.getManagedBean().getBrandId());

		}

		if(!this.getManagedBean().getRequestExtCode().isRequestColor()){

			ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
			colorFilterBean.setSearchField("colorId");
			colorFilterBean.setSearchValue(this.getManagedBean().getColorId());
			searchBeanColor.getDataFilterBeans().add(colorFilterBean);

			reqDTO.setColorSearchBean(searchBeanColor);

			resDTO = unspscSO.searchColorObj(reqDTO);

			this.getManagedBean().getRequestExtCode().setColorName(resDTO.getColorList().get(0).getColorName());
			this.getManagedBean().getRequestExtCode().setColorId(this.getManagedBean().getColorId());

		}

		if(!this.getManagedBean().getRequestExtCode().isRequestMeasurement()){

			ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
			measurementFilterBean.setSearchField("measurementId");
			measurementFilterBean.setSearchValue(this.getManagedBean().getMeasurementId());
			searchBeanColor.getDataFilterBeans().add(measurementFilterBean);

			reqDTO.setMeasurementSearchBean(searchBeanColor);

			resDTO = unspscSO.searchMeasurementObj(reqDTO);

			this.getManagedBean().getRequestExtCode().setColorId(this.getManagedBean().getMeasurementId());
			this.getManagedBean().getRequestExtCode().setMeasurementName(resDTO.getMeasurementList().get(0).getMeasurementName());
		}

		this.getManagedBean().getRequestExtCodeList().add(this.getManagedBean().getRequestExtCode());

	}



	public String submitToReviewExt(String action){
		System.out.println("***action***"+action);
		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		CodificationResDTO resDTO = null;

		try{

			System.out.println("face.getUserScreenName()"+face.getUserScreenName()); 


			Map temp = new HashMap();

			temp.put("user003", "user003");
			temp.put("user051", "user051");

			if(face.hasRoleUser("CM_ADMIN")){
				System.out.println("*******CM ADMIN*********");
				reqDTO.setCodeCommittees("user003,user051");
			}else if(face.hasRoleUser("CD_COMMITTEE")){
				System.out.println("*******CD_COMMITTEE*********:"+temp.size());
				if(!temp.isEmpty() && temp.size() > 0 ){ 

					Iterator itMap = temp.entrySet().iterator();

					while(itMap.hasNext()) {
						Map.Entry entry = (Map.Entry)itMap.next();
						String key = (String)entry.getKey();
						log.info("mapKey:"+key);
						if(!key.equalsIgnoreCase(face.getUserScreenName())){
							reqDTO.setCodeCommittees((String)entry.getValue());
						}
					}

				}
			}


			for(CmRequestCode code:this.getManagedBean().getReviewExtCodeList()){

				if(code.getColorNameDD()!=null){
					ISearchBean<CmColor> searchBeanColor = new SearchBean<CmColor>();
					colorFilterBean.setSearchField("colorId");
					colorFilterBean.setSearchValue(code.getColorNameDD());
					searchBeanColor.getDataFilterBeans().add(colorFilterBean);

					reqDTO.setColorSearchBean(searchBeanColor);

					resDTO = unspscSO.searchColorObj(reqDTO);
					code.setColorName(resDTO.getColorList().get(0).getColorName());
					code.setColorId(code.getColorNameDD());
					code.setRequestColor(false);


				}
				if(code.getBrandNameDD()!=null){

					ISearchBean<CmBrand> searchBeanColor = new SearchBean<CmBrand>();
					brandFilterBean.setSearchField("brandId");
					brandFilterBean.setSearchValue(code.getBrandNameDD());
					searchBeanColor.getDataFilterBeans().add(brandFilterBean);

					reqDTO.setBrandSearchBean(searchBeanColor);

					resDTO = unspscSO.searchBrandObj(reqDTO);

					code.setBrandName(resDTO.getBrandList().get(0).getBrandName());
					code.setBrandId(code.getBrandNameDD());
					code.setRequestBrand(false);

				}

				if(code.getTypeNameDD()!=null){

					ISearchBean<CmType> searchBeanColor = new SearchBean<CmType>();
					typeFilterBean.setSearchField("typeId");
					typeFilterBean.setSearchValue(code.getTypeNameDD());
					searchBeanColor.getDataFilterBeans().add(typeFilterBean);

					reqDTO.setTypeSearchBean(searchBeanColor);

					resDTO = unspscSO.searchTypeObj(reqDTO);


					code.setTypeName(resDTO.getTypeList().get(0).getTypeName());
					code.setTypeId(code.getTypeNameDD());
					code.setRequestType(false);
				}

				if(code.getMeasurementNameDD()!=null){

					ISearchBean<CmMeasurement> searchBeanColor = new SearchBean<CmMeasurement>();
					measurementFilterBean.setSearchField("measurementId");
					measurementFilterBean.setSearchValue(code.getMeasurementNameDD());
					searchBeanColor.getDataFilterBeans().add(measurementFilterBean);

					reqDTO.setMeasurementSearchBean(searchBeanColor);

					resDTO = unspscSO.searchMeasurementObj(reqDTO);

					code.setMeasurementName(resDTO.getMeasurementList().get(0).getMeasurementName());
					code.setMeasurementId(code.getMeasurementNameDD());
					code.setRequestMeasurement(false);
				}

				/////// checking remark required 1
				if(!code.isAddedRemark()){
					if(code.getReviewedMeasurementName()!=null && code.getReviewedMeasurementName().trim().length()>0){

						if(!code.getReviewedColorName().equalsIgnoreCase(code.getColorName())||
								!code.getReviewedMeasurementName().equalsIgnoreCase(code.getMeasurementName())||
								!code.getReviewedTypeName().equalsIgnoreCase(code.getTypeName())||
								!code.getReviewedBrandName().equalsIgnoreCase(code.getBrandName())){

							code.setRemarkRequired(true);

						}
					}
				}


				/////// checking remark required 2 and consensus checking

				if(code.getReviewedBrandNameII()!=null && code.getReviewedBrandNameII().trim().length()>0){

					if(!code.getReviewedBrandNameII().equalsIgnoreCase(code.getReviewedBrandName()) ||
							!code.getReviewedTypeNameII().equalsIgnoreCase(code.getReviewedTypeName()) ||
							!code.getReviewedMeasurementNameII().equalsIgnoreCase(code.getReviewedMeasurementName()) ||
							!code.getReviewedColorNameII().equalsIgnoreCase(code.getReviewedColorName())){
						System.out.println("********setConsensus false**********");
						reqDTO.setConsensus(false);

						if(!code.isAddedRemark()){
							code.setRemarkRequiredII(true);
						}


					}
				}





				reqDTO.getRequestExtList().add(code);

			}

			for(CmRequestRemark remark: this.getManagedBean().getReviewExtCodeList().get(0).getCmRequestRemarks()){
				remark.setCmRequestCode(this.getManagedBean().getReviewExtCodeList().get(0));
			}




			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome(action);
			reqDTO.setToken(token);

			/*StringBuffer buffer = new StringBuffer();
		List<Role> userRoleList = face.getUserRoleList();
		for (Role role : userRoleList) {
			System.out.println("name"+role.getName());
			buffer.append(role.getName()+",");
		}

		System.out.println("*******buffer*******"+buffer.toString());

		System.out.println(buffer.toString().substring(0,buffer.toString().length()-1));*/







			System.out.println("getCodeCommittees"+reqDTO.getCodeCommittees());
			System.out.println("consensus"+reqDTO.isConsensus());





			if(action.equalsIgnoreCase("PUBLISH")){



				reqDTO.setUnspscCode(this.getManagedBean().getReviewExtCodeList().get(0).getTempCode());
				resDTO = unspscSO.searchCommodityObj(reqDTO);

				reqDTO.setCmUnspsc(resDTO.getCommodityObj());
				reqDTO.setUnspscId(resDTO.getCommodityObj().getUnspscId());

				resDTO = unspscSO.publishExtensionRequest(reqDTO);

			}


			if(!action.equalsIgnoreCase("PUBLISH")){

				for(CmRequestCode code:this.getManagedBean().getReviewExtCodeList()){
					System.out.println("code.isRemarkRequired"+code.isRemarkRequired());
					System.out.println("code.isRemarkRequiredII"+code.isRemarkRequiredII());
					System.out.println("******getReviewedBrandNameII********"+code.getReviewedBrandNameII());
					if(code.getReviewedBrandNameII()!=null && code.getReviewedBrandNameII().trim().length()>0){
						if(code.isRemarkRequiredII()){

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Remark Required2", ""));
							return "reviewApproveExtensionCodeII";
						}
					}else{
						System.out.println("******else********");
						if(code.isRemarkRequired()){

							FacesContext facesContext = FacesContext.getCurrentInstance();
							facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Remark Required", ""));
							return "reviewApproveExtensionCode";
						}
					}






				}

				if(!reqDTO.isConsensus()){
					reqDTO.setCodeCommittees("user003,user051");
					reqDTO.setRequestExtList(null);
					for(CmRequestCode code:this.getManagedBean().getReviewExtCodeList()){
						code.setReviewedBrandName("");
						code.setReviewedBrandNameII("");
						code.setReviewedColorName("");
						code.setReviewedColorNameII("");
						code.setReviewedMeasurementName("");
						code.setReviewedMeasurementNameII("");
						code.setReviewedTypeName("");
						code.setReviewedTypeNameII("");

						reqDTO.getRequestExtList().add(code);
					}
				}

				resDTO = unspscSO.updateExtensionRequest(reqDTO);
			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));

		}catch(Exception ex){

		}

		//return "taskListView";
		return "BPMtesting";
	}


	public void preRenderExtensionMaintenanceView(){

		System.out.println("**********preRenderExtensionView**************");
		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:"+taskId);

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();


		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		CDRequestDataType unspsc = (CDRequestDataType)obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
		codeDTO.setRequestId(new Long(unspsc.getRequestId()));
		RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);

		UnspscCodeBackingBean unspscBB = new UnspscCodeBackingBean();
		unspscBB.getManagedBean().setRequestCode(resDTO.getRequestCode());

		this.getManagedBean().setAction(res.getAction());
		this.getManagedBean().setTaskId(taskId);

	}

	public void preRenderExtensionView(){

		System.out.println("**********preRenderExtensionView**************");

		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();

		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:"+taskId);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();

		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();


		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		
		CDRequestDataType unspsc = (CDRequestDataType)obj;
		
		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
		ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();

		CodificationReqDTO reqCodeDTO = new CodificationReqDTO();
		RequestCodeResDTO resDTO = null;
		this.getManagedBean().setReviewExtCodeList(null);

		if(unspsc.getItemId()!=null && unspsc.getItemId().size()>0){
			for(String itemId:unspsc.getItemId()){
				System.out.println("itemId:"+itemId);
				codeDTO.setRequestId(new Long(itemId));
				resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
				this.getManagedBean().getReviewExtCodeList().add(resDTO.getRequestCode());
			}
		}else{
			codeDTO.setRequestId(new Long(unspsc.getRequestId()));
			resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);
			this.getManagedBean().getReviewExtCodeList().add(resDTO.getRequestCode());
		}


		for(CmRequestCode code : this.getManagedBean().getReviewExtCodeList()){

			if(!code.isRequestColor()){
				code.setReviewedColorName(code.getColorName());
				code.setReviewedColorNameII(code.getColorName());
			}

			if(!code.isRequestBrand()){
				code.setReviewedBrandName(code.getBrandName());
				code.setReviewedBrandNameII(code.getBrandName());
			}

			if(!code.isRequestType()){
				code.setReviewedTypeName(code.getTypeName());
				code.setReviewedTypeNameII(code.getTypeName());
			}

			if(!code.isRequestMeasurement()){
				code.setReviewedMeasurementName(code.getMeasurementName());
				code.setReviewedMeasurementNameII(code.getMeasurementName());
			}

			code.setRemarkRequired(false);
			code.setRemarkRequiredII(false);
			code.setAddedRemark(false);
			for(CmRequestRemark remark: code.getCmRequestRemarks()){


				userReqDTO.setUserId(remark.getChangedBy());
				System.out.println("remark.getChangedBy()"+remark.getChangedBy());
				UserResDTO userResDTO = userSO.retrieveUserDetails(userReqDTO);
				System.out.println("userResDTO.getPmUser().getUserName():"+userResDTO.getPmUser().getUserName());
				remark.setCreatedUser(userResDTO.getPmUser().getUserName());
			}

		}


		reqCodeDTO.setUnspscCode(resDTO.getRequestCode().getTempCode());
		CodificationResDTO resCodeDTO = codificationSO.searchCommodityObj(reqCodeDTO);

		this.getManagedBean().setTypeList(resCodeDTO.getCommodityObj().getCmType());
		this.getManagedBean().setBrandList(resCodeDTO.getCommodityObj().getCmBrand());		
		this.getManagedBean().setMeasurementList(resCodeDTO.getCommodityObj().getCmMeasurement());
		resCodeDTO = codificationSO.searchColor(reqCodeDTO);
		this.getManagedBean().setColorList(resCodeDTO.getColorList());

		this.getManagedBean().setAction(res.getAction());
		this.getManagedBean().setTaskId(taskId);

		System.out.println("********res.getTaskName()********"+res.getTaskName());
		if(res.getTaskName().equalsIgnoreCase("Approve Request I")){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewApproveExtensionCode.xhtml");
		}else if(res.getTaskName().equalsIgnoreCase("Approve Request II")){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewApproveExtensionCodeII.xhtml");
		}else if(res.getTaskName().equalsIgnoreCase("Publish Extension Code")){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewApproveExtensionCodeII.xhtml");
		}else{
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewExtensionCodeFlow.xhtml");
		}

		//


	}



	public void preRenderView() {
		System.out.println("**********preRenderView**************");
		BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		CodificationBackingBean codeBB = new CodificationBackingBean();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:"+taskId);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();


		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();

		CDRequestDataType unspsc = (CDRequestDataType)obj;

		IRequestCodeSO requestSO = (IRequestCodeSO) BusinessDelegate.getInstance().getServiceObject("cm",IRequestCodeSO.class);
		RequestCodeReqDTO codeDTO = new RequestCodeReqDTO();
		System.out.println("unspsc.getRequestId()"+unspsc.getRequestId());
		codeDTO.setRequestId(new Long(unspsc.getRequestId()));
		RequestCodeResDTO resDTO = requestSO.retrieveUnspscCodeReview(codeDTO);


		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setRequestCode(resDTO.getRequestCode());
		/*if(resDTO.getRequestCode().getCmRecommendCodes()!=null && resDTO.getRequestCode().getCmRecommendCodes().size()>0){
			//codeBB.getManagedBean().setRecommendUnspscList(recommendUnspscList)
		}*/

		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setAction(res.getAction());

		if(res.getTaskName().equalsIgnoreCase("Create Temporary Code")){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewBeforeCreateTempCode");
		}else if(res.getTaskName().equalsIgnoreCase("Create Extension Code")){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewBeforeCreateExtCode");
		}else if(res.getTaskName().equalsIgnoreCase("Select Recommended")){
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "selectRecommendUnspscCode.xhtml");
		}else{
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "reviewUnspscCode");

		}


	}


	public String deleteExtensionCode(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm",ICodificationSO.class);

		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
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
			reqDTO.setToken(token);
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutCome("SUBMIT");
			reqDTO.setRequestCode(code);
			reqDTO.setProcessId(CDProcessNameConst.EXTENSION_CODE_MAINTENANCE_PROCESS_ID);



			CodificationResDTO resDTO = unspscSO.addCodificationRequest(reqDTO);


			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Submitted Successfully", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return "extensionSearch";
	}



	public SearchFilterBean getColorFilterBean() {
		return colorFilterBean;
	}



	public void setColorFilterBean(SearchFilterBean colorFilterBean) {
		this.colorFilterBean = colorFilterBean;
	}



	public SearchFilterBean getTypeFilterBean() {
		return typeFilterBean;
	}



	public void setTypeFilterBean(SearchFilterBean typeFilterBean) {
		this.typeFilterBean = typeFilterBean;
	}



	public SearchFilterBean getBrandFilterBean() {
		return brandFilterBean;
	}



	public void setBrandFilterBean(SearchFilterBean brandFilterBean) {
		this.brandFilterBean = brandFilterBean;
	}



	public SearchFilterBean getMeasurementFilterBean() {
		return measurementFilterBean;
	}



	public void setMeasurementFilterBean(SearchFilterBean measurementFilterBean) {
		this.measurementFilterBean = measurementFilterBean;
	}


	public String backToReviewUnspsc(){


		return "reviewBeforeCreateTempCode?faces-redirect=true";
	}


	public String requestExtensionCode(){
		CodificationBackingBean codeBB = new CodificationBackingBean();

		this.getManagedBean().getSelectedLovUnspsc().setUnspscCode(codeBB.getManagedBean().getSelectedUNSPSCObj().getUnspscCode());
		this.getManagedBean().getSelectedLovUnspsc().setUnspscTitle(codeBB.getManagedBean().getSelectedUNSPSCObj().getUnspscTitle());
		return "extensionCodeRequestForm?faces-redirect=true";
	}

	public String backToExtensionSearch(){

		return "extensionSearch?faces-redirect=true";
	}

	public void removeRecommendCode(int index){

		this.getManagedBean().getRequestCode().getCmRecommendCodes().remove(index);

	}


	public void addNewBpmRemark(){



		CmRequestRemark remark = new CmRequestRemark();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		System.out.println("pmUser:"+pmUser);
		if (pmUser != null) {	
			System.out.println("pmUser.getUserName():"+pmUser.getUserName());
			remark.setCreatedUser(pmUser.getUserName());
		}


		System.out.println("remark.getCreatedUser():"+remark.getCreatedUser());

		this.getManagedBean().getReviewExtCodeList().get(0).getCmRequestRemarks().add(remark);

		for(CmRequestCode code: this.getManagedBean().getReviewExtCodeList()){
			code.setRemarkRequired(false);
			code.setRemarkRequiredII(false);
			code.setAddedRemark(true);
		}

	}


	public void selectRecommendCode(){

		this.getManagedBean().getRequestCode().setCmRecommendCodes(null);
		this.getManagedBean().getRequestCode().getCmRecommendCodes().add(this.getManagedBean().getSelectedRecommendCode());
	}

}
