package my.ep.rest.sc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.managedbean.sc.noticeboard.NoticeBoardManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sc.helper.LocationSingleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.primefaces.model.TreeNode;

/**
 * 
 * @author shafful TO ALLOW CALLING NOTICE BOARD FROM EXTERNAL SOURCE
 */

@ManagedBean(name = "noticeBoardGfmasRest")
@ViewScoped
@Path("/noticeboard")
public class NoticeBoardGfmasRest extends SourcingBaseBackingBean<NoticeBoardManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String MAIN_PAGE = "nbMainPage";
	private static final String QT_DETAILS_PAGE = "/xhtml/sc/noticeboard/qtdetails/nbQuotationNTendenDetail.xhtml";

	// /START 32821 TO CREATE USER IF ONLY EXIST IN PM
	/**
	 * TO ALLOW NOTICE BOARD FROM EXTERNAL SOURCE
	 * 
	 * @param loginId
	 * @return
	 */
	@GET
	@Path("/viewQuotationTender/{qtId}")
	public void noticeBoardGfmas(@PathParam("qtId") Long qtId) {
		log.debug("** Qtn / TenderDetsils **");
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		this.getManagedBean().setPreviousPage(MAIN_PAGE);
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			reqDTO.setQtId(qtId);
			this.getManagedBean().setQtId(qtId);
			NoticeBoardResDTO resDTO = qtNoticeBoardSO.retrieveQtNoticeDtl(reqDTO);
			ScQt scQt = resDTO.getScQt();

			this.getManagedBean().setScQt(resDTO.getScQt());
			this.getManagedBean().setContactPersons(resDTO.getContactPersonResultBean().getResultList());

			// Setting ministry name
			this.getManagedBean().setMinistryName(retrieveMinistryByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId()));

			// Get sample submission details
			List<ScQtSampleSubmission> sampleAddrList = resDTO.getSampleSubmissionDtls();

			if (!scQt.isZonal()) {
				if (null != resDTO.getLocCriteria()) {

					this.getManagedBean().setLocationNodes(LocationSingleton.getInstance().constuctTreeNode(resDTO.getLocCriteria()));

				}
				ScQtBsv scQtBsv = resDTO.getScQtBsv();

				ScQtSampleSubmission sampleAddr = null;
				if (sampleAddrList != null && !sampleAddrList.isEmpty()) {
					sampleAddr = sampleAddrList.get(0);
				}

				BriefingZoneBean briefingZoneBean = new BriefingZoneBean(null, resDTO.getBriefingResultBean(), resDTO.getSiteVisitResultBean(), scQtBsv, sampleAddr);
				this.getManagedBean().setNormalBsvBean(briefingZoneBean);
			} else if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls = resDTO.getZones();
				// Need to construct tab bean for zone

				List<BriefingZoneBean> bsvZoneBeans = new ArrayList<>();

				if (null != zoneDtls) {
					boolean isBsvRequired = scQt.isBsvRequired();
					List<TreeNode> zoneTree = new ArrayList<>();

					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						if (!isBsvRequired) {
							zoneTree.add(LocationSingleton.getInstance().constuctTreeNode(specZoneDtl, specZoneDtl.getTplSpecZoneLocs()));
						}
						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
						briefingReqDTO.setQtId(scQt.getQtId());
						briefingReqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
						IQTBriefingSO briefingSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTBriefingSO.class);
						BriefingResDTO briefingResDTO = briefingSO.retrieveBriefingSiteVisitDtls(briefingReqDTO);
						ScQtSampleSubmission sampleAddress = null;
						for (ScQtSampleSubmission sampleaddr : sampleAddrList) {
							TplSpecZoneDtl addrZoneDtl = sampleaddr.getTplSpecZoneDtl();
							if (addrZoneDtl != null && specZoneDtl.getSpecZoneId().equals(addrZoneDtl.getSpecZoneId())) {
								sampleAddress = sampleaddr;
							}
						}
						BriefingZoneBean briefingZoneBean = new BriefingZoneBean(specZoneDtl, briefingResDTO.getBriefingList(), briefingResDTO.getSiteVisitList(),
								briefingResDTO.getScQtBsv(), sampleAddress);
						bsvZoneBeans.add(briefingZoneBean);
					}
					this.getManagedBean().setLocationNodes(LocationSingleton.getInstance().constuctTreeNode(zoneDtls.get(0).getTplSpecZoneLocs()));
					if (!isBsvRequired) {
						this.getManagedBean().setZoneTree(zoneTree);
					}
				}

				this.getManagedBean().setZoneBean(bsvZoneBeans);
				this.getManagedBean().setNormalBsvBean(bsvZoneBeans.get(0));
			}

			getManagedBean().setScQtDocReports(resDTO.getScQtDocReports());
			String selectedTab = getManagedBean().getSelectedTab();

			if (ParameterConst.NOTICE_BOARD_STATUS_CHANGES.equals(selectedTab)) {
				populateQtAmendIntoAmendmentBeanMap(resDTO.getQtAmendMentBeans());
			} else {
				this.getManagedBean().setAmendmentDateMap(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String pageUrl = QT_DETAILS_PAGE;
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication().getNavigationHandler().handleNavigation(context, null, pageUrl);
	}

	private void populateQtAmendIntoAmendmentBeanMap(List<QtAmendMentBean> qtAmendMentBeans) {
		Map<String, Date> amendmentBeanMap = new HashMap<>();

		if (CollectionUtils.isNotEmpty(qtAmendMentBeans)) {
			for (QtAmendMentBean amendMentBean : qtAmendMentBeans) {
				if (amendMentBean.getValueMap() != null) {
					for (Entry<String, QtAmendMentValueBean> amendMentBeanValue : amendMentBean.getValueMap().entrySet()) {
						QtAmendMentValueBean valueBean = amendMentBeanValue.getValue();
						Date approvalDate = valueBean.getApprovalDate();
						if (approvalDate != null && BooleanUtils.isTrue(valueBean.getIsApproved())) {
							Date prevApprovalDate = amendmentBeanMap.get(amendMentBeanValue.getKey());
							if (prevApprovalDate != null && prevApprovalDate.after(approvalDate)) {
								continue;
							}

							// proposal start date not same date only put into
							// map
							if (QTFieldConst.FIELD_QT_PROPOSAL_START_DATE.equals(amendMentBeanValue.getKey())) {
								if (valueBean.getOldValue() != null
										&& valueBean.getNewValue() != null
										&& DateUtils.setDefaultDateTime((Date) valueBean.getOldValue()).equals(
												DateUtils.setDefaultDateTime((Date) valueBean.getNewValue()))) {
									continue;
								}
							}
							amendmentBeanMap.put(amendMentBeanValue.getKey(), approvalDate);
						}
					}

				}
			}
		}

		this.getManagedBean().setAmendmentDateMap(amendmentBeanMap);
	}
}
