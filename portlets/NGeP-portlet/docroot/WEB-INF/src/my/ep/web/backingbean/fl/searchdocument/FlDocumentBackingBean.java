package my.ep.web.backingbean.fl.searchdocument;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.dto.fl.docsearch.FlDocSearchReqDTO;
import my.ep.dto.fl.docsearch.FlDocSearchResDTO;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.fl.pr.PurchaseRequestBackingBean;
import my.ep.web.managedbean.fl.searchdocument.FlDocumentManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name="documentBackingBean")
@RequestScoped
public class FlDocumentBackingBean extends BaseBackingBean<FlDocumentManagedBean>{

	private static Logger log = Logger.getLogger(FlDocumentBackingBean.class);
	public static final String NAV_TO_MAINTAIN_PR = "MaintainPR";
	public static final String NAV_TO_MAINTAIN_PO = "MaintainPO";
	public static final String NAV_TO_MAINTAIN_CO = "MaintainCO";
	

	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());


	public FlDocumentBackingBean(){

		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public void advanceSearch(){

		log.debug("#########advanceSearch#############"+this.getManagedBean().isAdvanceSearchInd());
		log.debug("getSearchType:"+this.getManagedBean().getSearchType());
		if(this.getManagedBean().getSearchType().equalsIgnoreCase("S")){
			this.getManagedBean().setAdvanceSearchInd(false);
		}else{
			this.getManagedBean().setAdvanceSearchInd(true);
		}


	}

	
	

	public void searchDocument(){
		log.info("*******we are in searchDocument************");  
		IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentDocumentSO.class);
		ISearchBean<FlFulfilmentRequest> searchBean = new SearchBean<FlFulfilmentRequest>();
		String enteredDocType=this.getManagedBean().getDocType();
		if(enteredDocType!=null){
			statusFilterBean.setSearchField("docType");
			statusFilterBean.setSearchValue(enteredDocType);
			searchBean.getDataFilterBeans().add(statusFilterBean);
		}
		FlDocSearchReqDTO reqDTO = new FlDocSearchReqDTO();
		reqDTO.setSearchBean(searchBean);
		FlDocSearchResDTO resDTO = flDocumentSO.searchFulfilmentDoc(reqDTO);
		this.getManagedBean().setDocumentItemList(resDTO.getDocumentItemList());

	}
	public String navigateToFulfillmentDet(int index){
		IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentDocumentSO.class);
		String flRequestId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentReqId");
		log.info("fulfilmentReqId in nav"+flRequestId);
		log.info("*****this.getManagedBean().getMaintainItemList().get(index).getCmImages().size()*****:"+this.getManagedBean().getDocumentItemList().get(index).getDocType());
		String docType=this.getManagedBean().getDocumentItemList().get(index).getDocType();
		if(docType.equals("PR")){
			FlDocSearchReqDTO reqDTO = new FlDocSearchReqDTO();
			reqDTO.setFlRequestId(Long.valueOf(flRequestId));
			//FlDocSearchResDTO resDTO = flDocumentSO.retrieveFulfilmentDoc(reqDTO);
			//System.out.println("Title In BB:: "+resDTO.getFulfilmentRequest().getTitle());
			PurchaseRequestBackingBean prBB=new PurchaseRequestBackingBean();
			prBB.getManagedBean().setDocProcess("prProcess");
			prBB.getManagedBean().setFulfilmentRequest(this.getManagedBean().getDocumentItemList().get(index));
			
			return NAV_TO_MAINTAIN_PR;
		}else if(docType.equals("PO")){
			return NAV_TO_MAINTAIN_PO;
		}else if(docType.equals("CO")){
			return NAV_TO_MAINTAIN_CO;			
		}

		return NAV_TO_MAINTAIN_PR;
	}
	public void selectedUser(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getTempLoginId());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setTempLoginId(id);

	}
	public void selectedDoctype(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getTempDocType());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setTempDocType(id);

	}
	public void cancelDoc(int index) {
		System.out.println("Weare in cancel doc method::: ");
		IFulfilmentDocumentSO flDocumentSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject("fl",IFulfilmentDocumentSO.class);
		String flRequestId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("fulfilmentReqId");
		log.info("fulfilmentReqId in cancel method:: "+flRequestId);
		FlDocSearchReqDTO reqDTO = new FlDocSearchReqDTO();
		reqDTO.setFulfilmentRequest(this.getManagedBean().getDocumentItemList().get(index));
		FlDocSearchResDTO resDTO = flDocumentSO.cancelFulfilmentDoc(reqDTO);
	}
	
	

	

	


}
