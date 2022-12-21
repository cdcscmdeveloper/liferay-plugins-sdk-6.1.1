package my.ep.web.managedbean.cm.codification;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.cm.codification.IRequestCodeSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.cm.codification.RequestCodeReqDTO;
import my.ep.dto.cm.codification.RequestCodeResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.codification.RequestCodeBackingBean;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "requestCOdeMB")
@SessionScoped
public class RequestCodeManagedBean extends BaseManagedBean {



	private String supplierName;
	private String docNo;
	private long statusID;
	private List<SelectItem> statusCriteria;

	private IPagingDataModel<Object[]> requestCodeSearchDataModel = new RequestCodeSearchDataModel();



	private class RequestCodeSearchDataModel extends
	PagingDataModel<Object[]> {

		public RequestCodeSearchDataModel() {
			super("cdTaskDetId");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {

			RequestCodeBackingBean bb = new RequestCodeBackingBean();
			UnspscCodeBackingBean unspsc = new UnspscCodeBackingBean();
			RequestCodeReqDTO reqDTO = new RequestCodeReqDTO();
			IRequestCodeSO requestCodeSO = (IRequestCodeSO) BusinessDelegate
					.getInstance().getServiceObject("cm",
							IRequestCodeSO.class);
			RequestCodeResDTO resDTO = null;
			try {

			
				
				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
						.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();

				PmUser pmUser = new FacesScopes().getUserSessionBean();
				supplierReqDTO.setUserId(pmUser.getUserId());

				SupplierSearchResDTO smResDTO = supplierSearchSO
						.retriveSupplierProfiles(supplierReqDTO);
				
				String suppName = null;

				if(smResDTO.getResultBean()!=null && smResDTO.getResultBean().getResultList()!=null && smResDTO.getResultBean().getResultList().size()>0){
					
					suppName = smResDTO.getResultBean().getResultList().get(0).getCompanyName();
					reqDTO.setSupplierName(suppName);
				}

				if(!unspsc.isAllSupplier()){
					if(bb.getManagedBean().getSupplierName()!=null && bb.getManagedBean().getSupplierName().trim().length()>0){
						reqDTO.setSupplierName(bb.getManagedBean().getSupplierName());
					}
				}

				if(bb.getManagedBean().getDocNo()!=null && bb.getManagedBean().getDocNo().trim().length()>0){

					reqDTO.setDocNo(bb.getManagedBean().getDocNo());

				}
				
				if(bb.getManagedBean().getStatusID()>0){
					reqDTO.setStatusID(bb.getManagedBean().getStatusID());
				}
				
				
				String languageCode = new FacesScopes().getUserLocale().getLanguage();
				reqDTO.setLanguageCode(languageCode);



				reqDTO.setCodificationTaskDetSearchBean(searchBean);

				resDTO = requestCodeSO.retrieveCurrentCdTaskDet(reqDTO);


			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				requestCodeSO = null;
			}
			if (resDTO != null) {
				System.out.println("*******end************");
				return resDTO.getCodificationTaskDetResultBean();
			}
			return null;
		}

	}



	public IPagingDataModel<Object[]> getRequestCodeSearchDataModel() {
		return requestCodeSearchDataModel;
	}



	public void setRequestCodeSearchDataModel(
			IPagingDataModel<Object[]> requestCodeSearchDataModel) {
		this.requestCodeSearchDataModel = requestCodeSearchDataModel;
	}



	public String getSupplierName() {
		return supplierName;
	}



	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}



	public String getDocNo() {
		return docNo;
	}



	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}



	public long getStatusID() {
		return statusID;
	}



	public void setStatusID(long statusID) {
		this.statusID = statusID;
	}



	public List<SelectItem> getStatusCriteria() {
		if(statusCriteria==null)
			statusCriteria = new ArrayList<SelectItem>();
		return statusCriteria;
	}



	public void setStatusCriteria(List<SelectItem> statusCriteria) {
		this.statusCriteria = statusCriteria;
	}
	
	
	
	






}
