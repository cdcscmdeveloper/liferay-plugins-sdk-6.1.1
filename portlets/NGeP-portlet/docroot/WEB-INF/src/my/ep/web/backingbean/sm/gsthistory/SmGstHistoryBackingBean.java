package my.ep.web.backingbean.sm.gsthistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.gsthistory.SmGstHistoryManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import com.liferay.portal.kernel.util.StringUtil;

@ManagedBean(name = "gstHistoryBackingBean")
@RequestScoped
public class SmGstHistoryBackingBean extends
		BaseReviewApplicationBackingBean<SmGstHistoryManagedBean> {

	private static int SEARCH_BY_EPNO = 1;
	private static int SEARCH_BY_MOFNO = 2;
	private static int SEARCH_BY_COMP_NAME = 3;

	public void preRenderGstHistory() {
		renewSesionObject();
		initLogin();
		getManagedBean().setSearchBy(SEARCH_BY_EPNO);
		if (getManagedBean().isSupplierUser()) {
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			setHqAndBranchGst(supplier);
			getManagedBean().setRenderedBackButton(false);
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/gstHistory/resultGstHistory.xhtml", "");
		} else {
			getManagedBean().setRenderedBackButton(true);
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/gstHistory/searchGstHistory.xhtml", "");
		}
	}

	public void viewGst(SmSupplier supplier) {
		setHqAndBranchGst(supplier);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/gstHistory/resultGstHistory.xhtml", "");
	}

	public void onSearchBy(AjaxBehaviorEvent e) {
		if (getManagedBean().getSearchBy() == SEARCH_BY_EPNO) {
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().setSearchMofNo("");
		} else if (getManagedBean().getSearchBy() == SEARCH_BY_MOFNO) {
			getManagedBean().getSearchSupplier().setCompanyName("");
			getManagedBean().getSearchSupplier().setEpNo("");
		} else if (getManagedBean().getSearchBy() == SEARCH_BY_COMP_NAME) {
			getManagedBean().getSearchSupplier().setEpNo("");
			getManagedBean().setSearchMofNo("");
		}
	}

	private void setHqAndBranchGst(SmSupplier supplier) {
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
		List<SmGstSupplier> declaredGstList = SmServiceLookup
				.getSupplierGstSO().retrieveGstHistoryListBySupplierId(reqDTO)
				.getGstList();
		getManagedBean().setHqGstList(new ArrayList<SmGstSupplier>());
		getManagedBean().setBranchGstList(new ArrayList<SmGstSupplier>());
		if (declaredGstList != null) {
			for (SmGstSupplier declaredGst : declaredGstList) {
				if (declaredGst.getBranchCode() != null) {
					declaredGst.setBranchName(getBranchName(declaredGst));
					getManagedBean().getBranchGstList().add(declaredGst);
				} else {
					getManagedBean().getHqGstList().add(declaredGst);
				}
				declaredGst.setActionedBy(getGstActionedBy(declaredGst));
			}
		}
		// sort hq by declared date
		if (getManagedBean().getHqGstList() != null
				&& !getManagedBean().getHqGstList().isEmpty()) {
			Collections.sort(getManagedBean().getHqGstList(),
					new Comparator<SmGstSupplier>() {
						public int compare(SmGstSupplier o1, SmGstSupplier o2) {
							return o2.getGstDeclaredDate().compareTo(
									o1.getGstDeclaredDate());
						}
					});
		}

		if (getManagedBean().getBranchGstList() != null
				&& !getManagedBean().getBranchGstList().isEmpty()) {
			// sort branch by declared date
			Collections.sort(getManagedBean().getBranchGstList(),
					new Comparator<SmGstSupplier>() {
						public int compare(SmGstSupplier o1, SmGstSupplier o2) {
							return o2.getGstDeclaredDate().compareTo(
									o1.getGstDeclaredDate());
						}
					});
			// sort branch by branch name
			Collections.sort(getManagedBean().getBranchGstList(),
					new Comparator<SmGstSupplier>() {
						@Override
						public int compare(final SmGstSupplier s1,
								final SmGstSupplier s2) {
							return StringUtil.upperCase(s1.getBranchName())
									.compareTo(
											StringUtil.upperCase(s1
													.getBranchName()));
						}
					});
		}

	}

	private String getGstActionedBy(SmGstSupplier gst) {
		String retVal = "";
		if (gst != null) {
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(gst.getChangedBy() != null ? gst.getChangedBy()
					: gst.getCreatedBy());
			PmUser user = SmServiceLookup.getUserSO()
					.retrieveUserByUserId(reqDTO).getPmUser();
			if (user != null) {
				retVal = user.getUserName();
			}
		}
		return retVal;
	}

	private String getBranchName(SmGstSupplier gst) {
		String retVal = "";
		if (gst != null && gst.getBranchCode() != null) {
			SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
			reqDTO.setBranchCode(gst.getBranchCode());
			SmSupplierBranch branch = SmServiceLookup.getSupplierGstSO()
					.retrieveBranchByBranchCode(reqDTO).getSupplierBranch();
			if (branch != null) {
				retVal = branch.getBranchName();
			}
		}
		return retVal;
	}

	public void back() {
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/gstHistory/searchGstHistory.xhtml", "");
	}

	public void search() {
		getManagedBean().searchGstHistory();
	}
}
