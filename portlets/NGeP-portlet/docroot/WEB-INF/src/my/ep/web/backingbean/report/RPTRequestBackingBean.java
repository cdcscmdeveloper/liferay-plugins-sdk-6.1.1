package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.rpt.RPTRequest;
import my.ep.type.aut.SearchOperator;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.RPTRequestManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "rptRequestBackingBean")
@ViewScoped
public class RPTRequestBackingBean extends BaseBackingBean<RPTRequestManagedBean> {
	
	private static final Logger logger = EpLogger
			.getLogger(RPTRequestBackingBean.class);

	private IPagingDataModel<RPTRequest> rptRequestDataModel = new RPTRequestDataModel();

	public RPTRequestBackingBean() {
		this.getManagedBean().setDeleteButtonHide(true);
		this.getLatestStatusCodeForRequests();
	}
	
	private String strLanguage = ResourceBundleUtils.getMessage(
			ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
			new FacesScopes().getUserLocale());
	
	@PostConstruct
	public void init(){
		this.getLatestStatusCodeForRequests();
	}

	public void retrieveRptRequestList() {
		
		debugLogger("retrieveRptRequestList");
		
		rptRequestDataModel.resetData();
		rptRequestDataModel.reloadData();
		
		resetSelectedRPTRequest();
	}

	public void removeRPTRequest() {
		
		debugLogger("removeRPTRequest");
		RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
		
		if (this.getManagedBean().getSelectedRPTRequests() != null) {
			
			debugLogger("getSelectedRPTRequests length["
					+ this.getManagedBean().getSelectedRPTRequests().length + "]");
			
			IRPTRequestSO reqSO = (IRPTRequestSO) BusinessDelegate.getInstance()
					.getServiceObject("rpt", IRPTRequestSO.class);
			
			RPTRequest[] rptRequestArr = this.getManagedBean().getSelectedRPTRequests();
			
			//reload the same object from db
			
			long[] rptRequetsIds = new long[rptRequestArr.length];
			
			for(int i = 0; i<rptRequestArr.length; i++){
				//pmNotifyUserIds[i] = rptRequestArr[i].getNotifyUserId();
			}

			//rptRequestArr = reqSO.getPmReportList(pmNotifyUserIds);

			//end
			
			if(rptRequestArr.length > 0){
				
				this.getManagedBean().setSelectedRPTRequests(rptRequestArr);
				
				for (RPTRequest rptRequest : this.getManagedBean()
						.getSelectedRPTRequests()) {
					reqDTO.getRPTRequestList().add(rptRequest);
				}
				
				RPTRequestResDTO resDTO = reqSO.removeRPTRequestList(reqDTO);
				
				debugLogger("RPTRequestResDTO isSuccessful[" + resDTO.isSuccessful()
						+ "]");
				
				if (resDTO.isSuccessful()) {
					resetSelectedRPTRequest();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									ResourceBundleUtils.getMessage("lbl.portal.notify.recordRemovedSuccesssfully",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
					rptRequestDataModel.reloadData();
				}
				
			} else {
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								ResourceBundleUtils.getMessage("lbl.portal.notify.selectAtLeastARecord",ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()), ""));
				rptRequestDataModel.reloadData();
			}
		}
		
		resetSelectedRPTRequest();
	}

	public void selectedRPTRequest() {
		
		debugLogger("=== selectedNotify ===");
		
		RPTRequest rptRequest = this.getManagedBean().getSelectedRPTRequest();
		
	}
	
	public void rowSelectUnselectCheckbox(){
		
		if (this.getManagedBean().getSelectedRPTRequests() != null) {
			
			RPTRequest[] rptRequestArr = this.getManagedBean().getSelectedRPTRequests();
			
			if(rptRequestArr != null && rptRequestArr.length > 0){

				this.getManagedBean().setDeleteButtonHide(false);
			
			} else {
			
				this.getManagedBean().setDeleteButtonHide(true);
			}
			
		}
	}

	/**
	 * @author Jimmy
	 */

	private class RPTRequestDataModel extends PagingDataModel<RPTRequest> {
		
		public RPTRequestDataModel() {
			super("reportTable");
		}

		@Override
		public ISearchResultBean<RPTRequest> pageChangeListener(
				ISearchBean<RPTRequest> searchBean) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("reportTable's pageChangeListener");
			}

			IRPTRequestSO reportSO;
			
			try {
			
				FacesScopes facesScopes = new FacesScopes();
				PmUser user = facesScopes.getUserSessionBean();

				if (user == null) {
					logger.error("User is empty.");
				} else {
					reportSO = (IRPTRequestSO) BusinessDelegate.getInstance()
							.getServiceObject("rpt",
									IRPTRequestSO.class);

					// Filter By - recipient Id
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("createdBy",
									SearchOperator.OPERATOR_EQUALS.value(),
									user.getUserId()));

					// Order by created date.
					searchBean.getSortColumns().add(
							new SearchOrderBean("createdDate",
									false));
						
					RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
					reqDTO.setSearchBean(searchBean);
					RPTRequestResDTO resDTO = reportSO.searchRPTRequestList(reqDTO);
					
					ISearchResultBean<RPTRequest> searchResultBean= resDTO.getSearchResultBean();
					List<RPTRequest> rptRequestList = searchResultBean.getResultList();
					
					for(int i = 0; i<rptRequestList.size(); i++){
						
						RPTRequest rptRequest = rptRequestList.get(i);
						
						RPTRequestBackingBean rptRequestBackingBean = new RPTRequestBackingBean();
						rptRequestBackingBean.getLatestStatusCode(rptRequest);
					}
					
					if (resDTO.isSuccessful()) {
						return resDTO.getSearchResultBean();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				reportSO = null;
			}

			return null;
		}
	}

	/**
	 * @return the rptRequestDataModel
	 */
	
	public IPagingDataModel<RPTRequest> getRptRequestDataModel() {
		return rptRequestDataModel;
	}

	/**
	 * @param setrptRequestDataModel
	 *            the specMasterDataModel to set
	 */
	
	public void setrptRequestDataModel(
			IPagingDataModel<RPTRequest> specMasterDataModel) {
		this.rptRequestDataModel = specMasterDataModel;
	}
	
	public void searchNotify() {
		
		//this.getManagedBean().setSelectedNotify(new PmNotifyUser());
		
		rptRequestDataModel.resetData();
		rptRequestDataModel.reloadData();
		
		resetSelectedRPTRequest();
	}
	
	private void resetSelectedRPTRequest() {
		getManagedBean().setSelectedRPTRequests(null);
	}

	private void debugLogger(Object message) {
		
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		
		logger.info(message);
	}
	
	public void getLatestStatusCode(RPTRequest rptRequest){
		
		IRPTRequestSO reqSO = (IRPTRequestSO) BusinessDelegate.getInstance()
				.getServiceObject("rpt", IRPTRequestSO.class);
		
		reqSO.updateStatusCode(rptRequest);
		
		rptRequestDataModel.reloadData();
	}
	
	public void preRender(){
		
		this.getLatestStatusCodeForRequests();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
		.handleNavigation(fc, null, "rptrequestcontent");
	}
	
	
	public void getLatestStatusCodeForRequests(){
		
		rptRequestDataModel.reloadData();
	}
	
	public void generateReport(RPTRequest rptRequest){
		
		String reportCode = rptRequest.getReportCode();
		
		IPMReportSO reqSO = (IPMReportSO) BusinessDelegate.getInstance()
				.getServiceObject("rpt", IPMReportSO.class);
		
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(reportCode);
		PMReportResDTO resDTO = reqSO.retriveReportByrptId(reqDTO);
		
		
		HttpURLConnection urlConnection = null;
		URL obj;
		BufferedInputStream buf;
		
		String gnrtdURL = "http://ngepdevrps01.ngep.com:5901/reports/rwservlet/getjobid" + rptRequest.getJobId() + "?server=RptSvr_NGEPDevRPS01_asinst_FT";
		
		try {
			
			obj = new URL(gnrtdURL);
			urlConnection = (HttpURLConnection) obj.openConnection();
			buf = new BufferedInputStream(urlConnection.getInputStream());
			
			PortletResponse portletResponse = (PortletResponse) FacesContext
					.getCurrentInstance().getExternalContext().getResponse();
			HttpServletResponse res = PortalUtil
					.getHttpServletResponse(portletResponse);
			writeReportDataToPDFOrExcel(reportCode, resDTO.getPmReportDtls().getReportTitle(), DateUtils.getCurrentDate(), buf, res);
		     
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}
	}

	private void writeReportDataToPDFOrExcel(String reportCode, String reportTitle,
			Timestamp time, BufferedInputStream buf, HttpServletResponse res)
			throws Exception{
		
		if (reportCode.equals(ReportConst.SM_LIST_OF_SOFTCERT_PAYMENT_RPT_VAL) || 
			reportCode.equals(ReportConst.SM_REJECTED_SOFTCERT_RPT_VAL) ) {
			
			res.setHeader(ReportConst.CONTENT_DISPOSITION,
					ReportConst.ATTACHMENTNFILENAMEEQUIFON + reportTitle
							+ ReportConst.UNDERSCORE
							+ ReportConst.REPORT_DATE_TIME.format(new Date())
							+ ReportConst.DOTXLSEIFON);//
			res.setHeader(ReportConst.CONTENT_TRANSFER_ENCODING,
					ReportConst.BINARY);
			res.setContentType(ReportConst.APPLICATIONEIFONXLS);
		} else {
			res.setHeader(ReportConst.CONTENT_DISPOSITION,
					ReportConst.ATTACHMENTNFILENAMEEQUIFON + reportTitle
							+ ReportConst.UNDERSCORE
							+ ReportConst.REPORT_DATE_TIME.format(new Date())
							+ ReportConst.DOTPDFEIFON);//
			res.setHeader(ReportConst.CONTENT_TRANSFER_ENCODING,
					ReportConst.BINARY);
			res.setContentType(ReportConst.APPLICATIONEIFONPDF);
		}
		
		res.flushBuffer();
		
		OutputStream out = res.getOutputStream();

		byte[] b = new byte[4096];
		
		int size;
		
		while ((size = buf.read(b, 0, b.length)) != -1) {
			out.write(b, 0, size);
		}
		
		buf.close();
		out.close();
	}
		
	public String getTrimmedStatusCode(String reportStatusStr){
		
		int reportStatus = Integer.parseInt(reportStatusStr);
		
		if(reportStatus == 1 || reportStatus == 2 || reportStatus == 3
		   || reportStatus == 9 || reportStatus == 10){
			return "In Progress";
		} else if (reportStatus == 4){
			return "Success";
		} else {
			return "Fail";
		}
	}
	
	public String getReportName(String reportCode){
		
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(reportCode);
		
		PMReportResDTO resDTO = reportSO.retriveReportByrptId(reqDTO);
		PMReport pmReport = resDTO.getPmReportDtls();
		
		String submodule = reportSO.retriveSubModuleNameByReportCode(strLanguage, reportCode);
		
		if(submodule != null && !submodule.isEmpty()){
		
			System.out.println("1");
			
			StringBuilder str = new StringBuilder();
			str.append(submodule);
			str.append("/");
			str.append(pmReport.getReportTitle());
		
			return str.toString();
			
		} else {
			return pmReport.getReportTitle();
		}
	}
	
	public String getReportModule(String reportCode){
		
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		
		return reportSO.retriveModuleNameByReportCode(strLanguage, reportCode);
		
	}

	public boolean isSuccess(String reportStatus){
		
		if(reportStatus != null && reportStatus.equals("4")){
			return true;
		}
		
		return false;
	}
	
	public boolean isInProgress(String reportStatus){
		
		
		if(reportStatus != null){
			if(reportStatus.equals("1") ||
			   reportStatus.equals("2") ||
			   reportStatus.equals("3") ||
			   reportStatus.equals("9") ||
			   reportStatus.equals("10")){
					return true;
			}
		}
		
		return false;
	}
}