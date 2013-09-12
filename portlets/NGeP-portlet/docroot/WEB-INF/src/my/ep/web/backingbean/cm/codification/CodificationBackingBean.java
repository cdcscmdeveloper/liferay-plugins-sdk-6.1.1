package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.dto.cm.codification.UNSPSCReqDTO;
import my.ep.biz.service.cm.codification.IUNSPSCSO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.codification.CodificationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.RemarksVO;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name="codificationBackingBean")
@RequestScoped
public class CodificationBackingBean extends BaseBackingBean<CodificationManagedBean>{

	public static final String NAV_TO_CODIFICATION_VIEW = "codificationView";
	public static final String NAV_TO_EXTENSION_SEARCH = "extensionSearch";
	public static final String NAV_TO_UNSPSC_REQUEST_FORM = "unspscRequestForm";
	public static final String NAV_TO_EXTENSION_REQUEST_FORM="extensionCodeRequestForm";
	public static final String NAV_TO_UNSPSC_SINGLE_UPLOAD="unspscSingleUpload";
	public static final String NAV_TO_EXTENSION_CODE_REVIEW="extensionCodeReview";
	public static final String NAV_TO_UNSPSC_BULK_UPLOAD="unspscBulkUpload";
	public static final String NAV_TO_EDIT_EXTENSION="editExtension";
	public static final String NAV_TO_DELETE_EXTENSION="deleteExtension";
	public static final String NAV_TO_CONTRACTED_ITEM="contractedItem";


	private static Logger log = Logger.getLogger(CodificationBackingBean.class);

	public CodificationBackingBean(){

		this.getManagedBean().setUnspscList(null);
		RemarksVO vo = new RemarksVO();
		vo.setUnspscID(new Long(1));
		vo.setUnspscCode("11223344");
		vo.setUnspscTitle("Car");

		this.getManagedBean().getUnspscList().add(vo);

	}

	public String codificationSearch(){

		return NAV_TO_CODIFICATION_VIEW;
	}


	public String goToExtensionSearch(){

		log.debug("*****************************"+this.getManagedBean().getSelectedUNSPSCObj().getUnspscCode());

		return NAV_TO_EXTENSION_SEARCH;
	}

	public String requestUNSPSCcode(){


		return NAV_TO_UNSPSC_REQUEST_FORM;
	}

	public String singleUpload(){



		return NAV_TO_UNSPSC_SINGLE_UPLOAD;

	}

	public String requestExtensioncode(){



		return NAV_TO_EXTENSION_REQUEST_FORM;

	}

	public String extensionReview(){

		return NAV_TO_EXTENSION_CODE_REVIEW;
	}

	public String bulkUpload(){

		return NAV_TO_UNSPSC_BULK_UPLOAD;

	}

	public String editExtension(){

		return NAV_TO_EDIT_EXTENSION;
	}

	public String deleteExtension(){
		return NAV_TO_DELETE_EXTENSION;
	}

	public String AssignContractedItem(){
		return NAV_TO_CONTRACTED_ITEM;
	}


	public void uploadUNSPSC(FileUploadEvent event) throws Exception{

		IUNSPSCSO unspscSO = (IUNSPSCSO) BusinessDelegate.getInstance().getServiceObject(IUNSPSCSO.class);

		PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		String loginUserId = request.getRemoteUser();

		UploadedFile item = event.getFile();

		FileInputStream file = (FileInputStream)item.getInputstream();	
		HSSFWorkbook workbook = new HSSFWorkbook(file);	
		HSSFSheet segment = workbook.getSheetAt(1);


		UNSPSCReqDTO reqDTO = new UNSPSCReqDTO();

		reqDTO.setUserID(loginUserId);

		log.debug("*******segment***********");
		long key = 0;
		long code =0;
		String title =null;
		String desc = null;



		int lastRowNum = segment.getLastRowNum()+1;
		for(int i = 1; i < lastRowNum; i++) {
			Row row = segment.getRow(i);

			if(row.getCell(0)!=null){
				key = (long) row.getCell(0).getNumericCellValue();



			}

			if(row.getCell(1)!=null){
				code = (long) row.getCell(1).getNumericCellValue();




			}

			if(row.getCell(2)!=null){
				title =  row.getCell(2).getStringCellValue();

			}

			if(row.getCell(3)!=null){
				desc = row.getCell(3).getStringCellValue();


			}


		}
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


}
