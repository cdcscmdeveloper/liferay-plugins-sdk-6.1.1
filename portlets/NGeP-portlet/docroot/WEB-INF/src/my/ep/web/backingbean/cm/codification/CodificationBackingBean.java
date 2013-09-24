package my.ep.web.backingbean.cm.codification;

import java.io.FileInputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;

import my.ep.biz.dto.cm.codification.CodificationReqDTO;
import my.ep.biz.dto.cm.codification.CodificationResDTO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
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







	}

	public void unspscSearch(){

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject(ICodificationSO.class);

		log.info("unspscCode:"+this.getManagedBean().getUnspscCode());
		System.out.println("********getUnspscTitle*******"+this.getManagedBean().getUnspscTitle());
		
		CodificationReqDTO reqDTO = new CodificationReqDTO();

		if(this.getManagedBean().getUnspscCode()!=null){
			reqDTO.setUnspscCode(this.getManagedBean().getUnspscCode());
		}
		if(this.getManagedBean().getUnspscTitle()!=null){
			reqDTO.setUnspscTitle(this.getManagedBean().getUnspscTitle());
		}
		
		CodificationResDTO resDTO = unspscSO.findUnspsc(reqDTO);
		this.getManagedBean().setUnspscList(resDTO.getUnspscList());

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

		return NAV_TO_UNSPSC_BULK_UPLOAD+"?faces-redirect=true";

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

		ICodificationSO unspscSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject(ICodificationSO.class);

		PortletRequest request = (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		Long loginUserId = new Long(1);

		UploadedFile item = event.getFile();

		FileInputStream file = (FileInputStream)item.getInputstream();	
		HSSFWorkbook workbook = new HSSFWorkbook(file);	
		CodificationReqDTO reqDTO = new CodificationReqDTO();
		HSSFSheet segment = workbook.getSheetAt(2);


		log.debug("*******segment***********");




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
			reqDTO.setUserID(loginUserId);
			unspsc.setUnspscLevel(0);
			reqDTO.setCmUnspsc(unspsc);
			reqDTO.getUnspscList().add(reqDTO.getCmUnspsc());

		}

		CodificationResDTO resDTO = unspscSO.saveUnspsc(reqDTO);
		log.debug("*******end segment***********");

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
				unspsc.setUnspscParentCode(new Long(parentCode));

			}

			if(row.getCell(2)!=null){
				familyTitle =  row.getCell(2).getStringCellValue();

				unspsc.setUnspscTitle(familyTitle);
			}

			if(row.getCell(3)!=null){
				familyDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(familyDesc);
			}

			reqDTO.setUserID(loginUserId);
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
				unspsc.setUnspscParentCode(new Long(parentCode));
			}

			if(row.getCell(2)!=null){
				classTitle =  row.getCell(2).getStringCellValue();
				unspsc.setUnspscTitle(classTitle);
			}

			if(row.getCell(3)!=null){
				classDesc = row.getCell(3).getStringCellValue();
				unspsc.setUnspscDescription(classDesc);
			}

			reqDTO.setUserID(loginUserId);
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
				unspsc.setUnspscParentCode(new Long(parentCode));
			}

			if(row.getCell(8)!=null && row.getCell(8).toString().trim().length()>0){
				commodityTitle =  row.getCell(8).getStringCellValue();

				unspsc.setUnspscTitle(commodityTitle);

			}

			if(row.getCell(9)!=null && row.getCell(9).toString().trim().length()>0){
				commodityDesc = row.getCell(9).getStringCellValue();

				unspsc.setUnspscDescription(commodityDesc);

			}
			reqDTO.setUserID(loginUserId);
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


}
