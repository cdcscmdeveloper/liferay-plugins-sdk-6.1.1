package my.ep.web.managedbean.sm.paymentreceipt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.payment.PaymentReqDTO;
import my.ep.dto.sm.payment.PaymentResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.py.PyPayment;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.sm.paymentreceipt.PaymentReceiptBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@ManagedBean(name = "paymentReceiptMB")
@SessionScoped
public class PaymentReceiptManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(PaymentReceiptManagedBean.class);
	
	private List<PyPayment> paymentLs;
	private Date dateFrom;
	private Date dateTo;
	private boolean allType;
	private List<String> selectedTypes;
	private List<SelectItem> paymentTypeSelection;
	private IPagingDataModel<PyPayment> searchDataModel = new SearchPagingDataModel();
	
	private class SearchPagingDataModel extends PagingDataModel<PyPayment> {
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;


		/**
		 * Instantiates a new search paging data model.
		 */
		public SearchPagingDataModel() {
			super("rsDt", true);
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<PyPayment> searchBean(mandatory))
		 * @return ISearchBean<PyPayment>
		 */
		@Override
		protected ISearchResultBean<PyPayment> pageChangeListener(ISearchBean<PyPayment> searchBean) {
			PaymentReceiptBackingBean bb = new PaymentReceiptBackingBean();
			//System.out.println("getApplNo:"+bb.getManagedBean().getApplNo());
			//System.out.println("getStateId:"+bb.getManagedBean().getStateId());
			//System.out.println("getDistrictId:"+bb.getManagedBean().getDistrictId());
			//System.out.println("getSupplierName:"+bb.getManagedBean().getSupplierName());
			//System.out.println("getSitevisitType:"+bb.getManagedBean().getSiteVisitType());
			//System.out.println("getStateLS:"+bb.getManagedBean().getStateLs().size());
			/*if (bb.getManagedBean().getDateFrom() == null && bb.getManagedBean().getDateTo() == null) {
				System.out.println("******SearchResultBean******");
				return new SearchResultBean();		
			}*/

			IPaymentSO paymentSO = SmServiceLookup.getPaymentSO();

			try {
				System.out.println("********************************");
				PaymentReqDTO reqDTO = new PaymentReqDTO();
				reqDTO.setSupplierId(getSupplierProfileByUserId().getSupplierId());

				if(bb.getManagedBean().getDateFrom() != null){
					reqDTO.setDateFrom(bb.getManagedBean().getDateFrom());
				}

				if(bb.getManagedBean().getDateTo() != null){
					reqDTO.setDateTo(bb.getManagedBean().getDateTo());
				}
				//log.info("<<<<<Selected type>>>>>" + bb.getManagedBean().getSelectedTypes().size());
				if(bb.getManagedBean().getSelectedTypes() != null && bb.getManagedBean().getSelectedTypes().size() > 0){
					reqDTO.setSelectedPaymentTypes(bb.getManagedBean().getSelectedTypes());
				}
				
				if(bb.getManagedBean().isAllType()){
					log.info("<<<<<It's all type>>>>>");
					reqDTO.setSelectedPaymentTypes(addAllPaymentTypeToList());
				}
				
				reqDTO.setSearchBean(searchBean);
				

				PaymentResDTO resDTO = paymentSO.retrievePaymentReceiptList(reqDTO);

				return resDTO.getPaymentResultBean();
			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				paymentSO = null;
			}
		}
	}

	private SmSupplier getSupplierProfileByUserId(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		log.info("***pmUser name***" + pmUser.getUserName());
		log.info("***pmUser id***" + pmUser.getUserId());

		if(pmUser != null){
			if(SmApplicationUtil.getCurrentSupplier() != null){
				SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
				return smSupp;
			}
		}

		return null;
	}
	
	private List<String> addAllPaymentTypeToList(){
		List<String> paymentTypeLs = new ArrayList<String>();
		paymentTypeLs.add(SupplierManagementConst.BILL_TYPE_PROCESS_FEE);
		paymentTypeLs.add(SupplierManagementConst.BILL_TYPE_REG_FEE);
		paymentTypeLs.add(SupplierManagementConst.BILL_TYPE_SOFTCERT);
		return paymentTypeLs;
	}

	public List<PyPayment> getPaymentLs() {
		return paymentLs;
	}

	public void setPaymentLs(List<PyPayment> paymentLs) {
		this.paymentLs = paymentLs;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public boolean isAllType() {
		return allType;
	}

	public void setAllType(boolean allType) {
		this.allType = allType;
	}

	public List<String> getSelectedTypes() {
		return selectedTypes;
	}

	public void setSelectedTypes(List<String> selectedTypes) {
		this.selectedTypes = selectedTypes;
	}

	public List<SelectItem> getPaymentTypeSelection() {
		return paymentTypeSelection;
	}

	public void setPaymentTypeSelection(List<SelectItem> paymentTypeSelection) {
		this.paymentTypeSelection = paymentTypeSelection;
	}


	public IPagingDataModel<PyPayment> getSearchDataModel() {
		return searchDataModel;
	}


	public void setSearchDataModel(IPagingDataModel<PyPayment> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}
}
