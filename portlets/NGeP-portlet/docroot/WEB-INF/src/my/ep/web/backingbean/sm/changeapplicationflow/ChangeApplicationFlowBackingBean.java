package my.ep.web.backingbean.sm.changeapplicationflow;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;
import my.ep.dto.pm.common.trackingdiary.TrackingDiaryResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmTrackingDiary;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.changeapplicationflow.ChangeApplicationFlowManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.primefaces.context.RequestContext;

@ManagedBean(name = "changeApplicationFlowBackingBean")
@RequestScoped
public class ChangeApplicationFlowBackingBean extends
		BaseReviewApplicationBackingBean<ChangeApplicationFlowManagedBean> {

	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderChangeApplicationFlow() {
		initDataForSearchSupplier();
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/changeApplicationFlow/searchChangeApplicationFlow.xhtml",
						"", null);
	}

	/**
	 * Navigate to change application flow screen
	 * 
	 * @param Long
	 *            applId
	 */
	public void navigateToChangeApplicationFlow(Long applId) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		getManagedBean().setSmAppl(smAppl);
		getManagedBean().setSmSupplier(
				getManagedBean().getSmAppl().getSmSupplier());
		getManagedBean().setBasicInfo(
				SmApplicationUtil.getSmSupplierBasicInfoVO(getManagedBean()
						.getBasicInfo(), getManagedBean().getSmSupplier(),
						getManagedBean().getSmAppl()));
		initWorkflowStatus(getManagedBean().getSmAppl());
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/changeApplicationFlow/changeApplicationFlow.xhtml",
				"", null);
	}

	/**
	 * Back to Cancel MOF Search Page
	 */
	public void backToSearchApplFlow() {
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/changeApplicationFlow/searchChangeApplicationFlow.xhtml",
						"", null);
	}

	/**
	 * Show confirmation dialog
	 */
	public void showConfirmDialog() {
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
	}

	/**
	 * Submit change application flow
	 */
	public void submitChangeApplicationFlow() {
		PmStatusDesc newStatus = SmCommonUtil
				.getStatusDescByStatusId(getManagedBean().getSelectedStatusId());
		SmCommonUtil.redirectToPage(
				SmApplicationUtil.getApplicationErrorPage(),
				MessageConst.INFO_721224,
				new String[] { getManagedBean().getSmAppl().getApplNo(),
						getManagedBean().getCurrStatusName(),
						newStatus.getStatusName() });
	}

	/**
	 * Initialized data for search supplier
	 */
	private void initDataForSearchSupplier() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(true);
		getManagedBean().setRenderedBackButton(true);
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());
	}

	/**
	 * Initialized current and work flow status select items
	 * 
	 * @param SmAppl
	 *            appl
	 */
	private void initWorkflowStatus(SmAppl appl) {
		if (appl.getPmStatus() != null
				&& !EntityHelper.isCollectionEmpty(appl.getPmStatus()
						.getPmStatusDescs())) {
			getManagedBean().setSelectedStatusId(
					appl.getPmStatus().getStatusId());
			getManagedBean().setStatusItems(new ArrayList<SelectItem>());
			PmStatusDesc currStatus = SmCommonUtil.getStatusDescByStatusId(appl
					.getPmStatus().getStatusId());
			if (currStatus != null) {
				getManagedBean().setCurrStatusName(currStatus.getStatusName());
			}
		}

		TrackingDiaryReqDTO reqDTO = new TrackingDiaryReqDTO();
		SearchBean<PmTrackingDiary> searchBean = new SearchBean<PmTrackingDiary>();
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("docNo", SearchOperator.OPERATOR_EQUALS
						.value(), appl.getApplNo()));
		searchBean.getSortColumns().add(new SearchOrderBean("actionedDate"));
		reqDTO.setSearchBean(searchBean);
		
		setSearchBeanDescription(searchBean, appl.getApplNo());

		TrackingDiaryResDTO resDTO = SmServiceLookup.getTrackingDiarySO()
				.retrieveTrackingDiaryBySearchBean(reqDTO);
		List<PmTrackingDiary> trackingDiaryList = resDTO.getResultBean()
				.getResultList();

		SelectItem item = null;
		PmStatusDesc pd = null;
		if (!EntityHelper.isCollectionEmpty(trackingDiaryList)) {
			for (PmTrackingDiary td : trackingDiaryList) {
				if (td.getStatusId() != null) {
					pd = SmCommonUtil.getStatusDescByStatusId(td.getStatusId());
					if (pd != null) {
						item = new SelectItem();
						item.setValue(pd.getPmStatus().getStatusId());
						item.setLabel(pd.getStatusName());
						getManagedBean().getStatusItems().add(item);
					}
				}
			}
		}
	}
	
	/**
	 * set search bean description for tracking purpose
	 *
	 */
	private void setSearchBeanDescription(ISearchBean<PmTrackingDiary> searchBean, String docNo) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[docNo=%s]", docNo));
		searchBean.setDescription("searchTrackingDiary" + sb.toString() + "[Caller:ChangeApplicationFlowBackingBean.initWorkflowStatus]");
	}
}
