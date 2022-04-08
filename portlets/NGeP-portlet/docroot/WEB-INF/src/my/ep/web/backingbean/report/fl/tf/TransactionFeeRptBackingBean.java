package my.ep.web.backingbean.report.fl.tf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.FulfilmentStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.agoffice.AgOfficeReqDTO;
import my.ep.dto.pm.common.agoffice.AgOfficeResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmAgOffice;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.integration.PmInterfaceAgOff;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.AccountingOfficeSearchBean;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.managedbean.report.fl.tf.TransactionFeeRptManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backing Bean for Transaction Fee Rpt.
 *
 * @author razin
 * @since 08/06/2021
 */
@ManagedBean(name = "tfRptBB")
@SessionScoped
public class TransactionFeeRptBackingBean extends FlReportBackingBean<TransactionFeeRptManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The search accounting office. */
	private IPagingDataModel<PmAgOffice> searchAccountingOffice = new SearchOfficePagingDataModel();

	private static final Logger log = EpLogger
			.getLogger(TransactionFeeRptBackingBean.class);

	public TransactionFeeRptBackingBean() {
		setDefaultTransactionDate();
	}

	public void resetAction(){
		getManagedBean().setPmAccountingOffice(null);
		getManagedBean().setCheckSFOneOff(false);
		getManagedBean().setCheckSFPeriodic(false);
		getManagedBean().setDocumentType(null);
		setDefaultTransactionDate();

		resetSearchCriteriaValues();
	}

	public void setDefaultTransactionDate() {
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(fromDate.get(Calendar.YEAR), 00, 01, 0, 00, 0);
		getManagedBean().setTransactionFromDate(fromDate.getTime());
		getManagedBean().setTransactionToDate(new Date());
	}
	public void generateReport(){
		LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);

		if(getManagedBean().getPmAccountingOffice() == null){
			throw new EpException(MessageConst.ERROR_REPORT_780065);
		}
		if( getManagedBean().getReportCode().equalsIgnoreCase("FL_071") ){
			if( getManagedBean().isCheckSFOneOff() == false &&  getManagedBean().isCheckSFPeriodic() == false){
				throw new EpException(MessageConst.ERROR_REPORT_780066);
			}
		}
		if(getManagedBean().getDocumentType() == null){
			throw new EpException(MessageConst.ERROR_REPORT_780067);
		}

		log.info("********Report Code*************" + getManagedBean().getReportCode());

		retrieveReportValueFromDb();

		//add ag office name
		if (getManagedBean().getPmAccountingOffice().getOfficeCode() !=null ) {
			//params.put(FlReportConst.FL_RPT_AG_OFFICE, getManagedBean().getAgOff().getOffCode());
			params.put(FlReportConst.FL_RPT_AG_OFFICE, getManagedBean().getPmAccountingOffice().getOfficeCode());
		}else{
			params.put(FlReportConst.FL_RPT_AG_OFFICE, null);
		}


		//add PSF one off
		if (getManagedBean().isCheckSFOneOff() == true ) {
			params.put(FlReportConst.FL_RPT_PSF_ONE_OFF, "Y");
		}else{
			params.put(FlReportConst.FL_RPT_PSF_ONE_OFF, "N");
		}

		//add PSF period
		if (getManagedBean().isCheckSFPeriodic() == true) {
			params.put(FlReportConst.FL_RPT_PSF_PERIOD, "Y");
		}else{
			params.put(FlReportConst.FL_RPT_PSF_PERIOD, "N");
		}

		params.put(FlReportConst.FL_RPT_TRANSACTION_FROM_DATE, df.format(getManagedBean().getTransactionFromDate()));
		params.put(FlReportConst.FL_RPT_TRANSACTION_TO_DATE, df.format(getManagedBean().getTransactionToDate()));
		checkOneYearDifferent(getManagedBean().getTransactionFromDate(), getManagedBean().getTransactionToDate());

		generateFlReport(params);
	}

	public void selectedAgOff(){
		if(agOffice == null){
			PmAgOffice agoff = new PmAgOffice();
			agoff.setOfficeName("All");
			getManagedBean().setPmAccountingOffice(agoff);
		}else{
			agOffice.setOfficeName(agOffice.getPmAgValidity().getOfficeName());
			getManagedBean().setPmAccountingOffice(agOffice);
			log.info(getManagedBean().getPmAccountingOffice().getPmAgValidity().getOfficeName());
		}
	}

	private PmAgOffice agOffice;

	public PmAgOffice getAgOffice() {
		return agOffice;
	}

	public void setAgOffice(PmAgOffice agOffice) {
		this.agOffice = agOffice;
	}

	/**
	 * The Class SearchOfficePagingDataModel.
	 */
	private class SearchOfficePagingDataModel extends PagingDataModel<PmAgOffice> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search office paging data model.
		 */
		public SearchOfficePagingDataModel() {
			super("accountingSR",true);
		}

		@Override
		public ISearchResultBean<PmAgOffice> pageChangeListener(ISearchBean<PmAgOffice> searchBean) {

			IAgOfficeSO agOfficeSO;

			try {
				//AccountingOfficeSearchBean officeSearchBean= getManagedBean().getAccountingOfficeSearchBean();
				String inputAg = getManagedBean().getInputAgOff();
				AgOfficeReqDTO reqDTO = new AgOfficeReqDTO();
				if (StringUtils.isNotBlank(inputAg)){
					if(getManagedBean().getOffDropdown().equalsIgnoreCase("officeName")){
						reqDTO.setAgOfficeDesc(inputAg);
						reqDTO.setAgOfficeCode(null);
					}else if(getManagedBean().getOffDropdown().equalsIgnoreCase("officeCode")){
						reqDTO.setAgOfficeDesc(null);
						reqDTO.setAgOfficeCode(inputAg);
					}else{
						reqDTO.setAgOfficeDesc(null);
						reqDTO.setAgOfficeCode(null);
					}
				}else{
					reqDTO.setAgOfficeDesc(null);
					reqDTO.setAgOfficeCode(null);
				}
				log.info("29554: setAgOfficeDesc: "+reqDTO.getAgOfficeDesc());
				log.info("29554: setAgOfficeCode: "+reqDTO.getAgOfficeCode());

				reqDTO.setOfficeSearchBean(searchBean);
				agOfficeSO = (IAgOfficeSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAgOfficeSO.class);
				AgOfficeResDTO resDTO= agOfficeSO.searchAgOfficeList(reqDTO);
				getManagedBean().setLazy(true);
				if (resDTO.isSuccessful()) {
					return resDTO.getOfficeResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(MessageConst.ERROR_770005);
			}

			return null;
		}


	}
	/**
	 * reset search bean fields.
	 */
	public void resetOfficeSearchBean() {
		this.agOffice = null;
		if(this.getManagedBean().getIsAllAgOff() == null){
			this.getManagedBean().setIsAllAgOff("false");
		}
		this.getManagedBean().setOffDropdown("officeCode");
		this.getManagedBean().setAccountingOfficeSearchBean(new AccountingOfficeSearchBean());
	}

	/**
	 * Search accounting office.
	 */
	public void searchAccountingOffice() {
		searchAccountingOffice.reloadData();
	}

	/**
	 * Gets the search accounting office.
	 * 
	 * @return the searchAccountingOffice
	 */
	public IPagingDataModel<PmAgOffice> getSearchAccountingOffice() {
		return searchAccountingOffice;
	}

	/**
	 * Sets the search accounting office.
	 * 
	 * @param searchAccountingOffice
	 *            the searchAccountingOffice to set
	 */
	public void setSearchAccountingOffice(IPagingDataModel<PmAgOffice> searchAccountingOffice) {
		this.searchAccountingOffice = searchAccountingOffice;
	}

	public void allAgOffChecked(AjaxBehaviorEvent e) {
		String valCheck = (String) e.getComponent().getAttributes().get("value");
		this.getManagedBean().setInputAgOff(null);
		this.getManagedBean().setIsAllAgOff(valCheck);
		searchAccountingOffice();
	}
	public boolean isSelectButtonDisable() {
		if(getManagedBean().getIsAllAgOff() != null){
			if(getManagedBean().getIsAllAgOff().equalsIgnoreCase("true")){
				return false;
			}
		}
		if(getAgOffice() !=null) {
			return false;
		}
		return true;
	}

}
