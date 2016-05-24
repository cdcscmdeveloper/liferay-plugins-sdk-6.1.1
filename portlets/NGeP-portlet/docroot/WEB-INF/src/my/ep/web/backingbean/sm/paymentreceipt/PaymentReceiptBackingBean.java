package my.ep.web.backingbean.sm.paymentreceipt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.py.PyPaymentResponse;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.sm.paymentreceipt.PaymentReceiptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "paymentReceiptBB")
@RequestScoped
public class PaymentReceiptBackingBean extends BaseBackingBean<PaymentReceiptManagedBean>{
	public static Logger log = Logger.getLogger(PaymentReceiptBackingBean.class);
	private static String PAYMENT_RECEIPT_MAIN = "/xhtml/sm/paymentReceipt/paymentReceiptMain.xhtml";
	
	public void preRenderPaymentReceipt(){
/*		SmSupplier supplier = getSupplierProfileByUserId();
		if(supplier != null){
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			PaymentResDTO resDTO = SmServiceLookup.getPaymentSO().retrievePaymentReceiptLsBySupplierId(reqDTO);
			
			getManagedBean().setPaymentLs(resDTO.getPyPaymentLs());
		}*/
		getManagedBean().getSearchDataModel().reloadData();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,PAYMENT_RECEIPT_MAIN);
	}
	
	public void printReceipt(Long paymentId) throws IOException{
		//String paymentId = this.getManagedBean().getPaymentId().toString();
		//Long paymentId = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paymentId")); 
		log.info(">>>>Payment Id>>>>" + paymentId);
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ReportConst.PARAM_PAYMENT_ID, paymentId + "");
		
		generateReport(ReportConst.SM_PAYMENT_RECEIPT_JSP, paramMap);
	}
	
	public PyPaymentResponse getLatestPaymentResponse(Long paymentId){
		if(paymentId != null){
			PaymentReqDTO reqDTO = new PaymentReqDTO();
			reqDTO.setPaymentId(paymentId);
			reqDTO.setResponseStatus(SupplierManagementConst.MOL_SUCCESS);
			
			PaymentResDTO resDTO = SmServiceLookup.getPaymentSO().
					retrieveLatestPaymentRespByPaymentIdAndStatus(reqDTO);
			return resDTO.getPaymentResponse();
		}
		
		return null;
	}
	
	public void searchAction() {
		this.getManagedBean().getSearchDataModel().reloadData();
	}
	
	/**
	 * reset search criteria
	 */
	public void resetSearchCriteria(){
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setAllType(false);
		getManagedBean().setSelectedTypes(new ArrayList<String>());

//		getManagedBean().getSearchDataModel().getList().clear();
	}
	
	/**
	 * Payment Type Onchange Listener
	 */
	public void paymentTypeOnChange(){
		if(getManagedBean().getSelectedTypes() != null && getManagedBean().getSelectedTypes().size() > 0){
			for(String pymtType : getManagedBean().getSelectedTypes()){
				if(getManagedBean().getSelectedTypes() != null && getManagedBean().getSelectedTypes().size() > 0){
					if(pymtType.equals("")){
						getManagedBean().setAllType(true);
						break;
					}
				}else{
					getManagedBean().setAllType(false);
				}
			}
		}else{
			getManagedBean().setAllType(false);
		}
	}
}
