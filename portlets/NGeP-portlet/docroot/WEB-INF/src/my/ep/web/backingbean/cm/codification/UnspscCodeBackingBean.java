package my.ep.web.backingbean.cm.codification;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.cm.codification.UnspscCodeManagedBean;
import my.ep.web.vo.RemarksVO;

import org.apache.log4j.Logger;

@ManagedBean(name="unspscCodeBackingBean")
@RequestScoped
public class UnspscCodeBackingBean extends BaseBackingBean<UnspscCodeManagedBean>{

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
	

	private static Logger log = Logger.getLogger(UnspscCodeBackingBean.class);

	public UnspscCodeBackingBean(){

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


}
