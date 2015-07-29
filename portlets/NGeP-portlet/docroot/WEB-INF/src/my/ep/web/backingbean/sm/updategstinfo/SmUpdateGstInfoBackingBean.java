package my.ep.web.backingbean.sm.updategstinfo;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.updategstinfo.SmUpdateGstInfoManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmGstBranchVO;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "smUpdateGstInfoBackingBean")
@RequestScoped
public class SmUpdateGstInfoBackingBean extends
		BaseBackingBean<SmUpdateGstInfoManagedBean> {

	public SmUpdateGstInfoBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	public void preRenderGstInfo() {
		getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
		getManagedBean().setMofAccount(
				SmCommonUtil.getMofAccountBySuppIdAndSuppType(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmSupplier().getSmAppl().getSupplierType()));
		getManagedBean().setCompanyBasic(
				SmCommonUtil.getLatestRevCompBasicByApplId(getManagedBean()
						.getSmSupplier().getSmAppl().getApplId()));
		initHqGstInfo();
		initBranchGstInfo();
		SmCommonUtil.redirectToPage("gstInfoBasePage.xhtml", "");
	}

	public void addHqGst() {
		getManagedBean().setGstEmpty(false);
		getManagedBean().setHqGstSupplier(new SmGstSupplier());
	}

	public void onSaveHqGst() {
		getManagedBean()
				.setSaveHqGstConfirmMsg(
						getManagedBean().getHqGstSupplier().getGstSupplierId() != null ? SmCommonUtil
								.getMessage("lbl.sm.gst.update.confirm.msg",
										null) : SmCommonUtil.getMessage(
								"lbl.sm.gst.add.confirm.msg", null));
	}

	public void saveHqGst() {
		if (getManagedBean().getHqGstSupplier().getGstSupplierId() == null) {
			getManagedBean().getHqGstSupplier().setSmSupplier(
					getManagedBean().getSmSupplier());
			if (StringUtils.isNotBlank(getManagedBean().getSmSupplier()
					.getSmAppl().getApplNo())) {
				getManagedBean().getHqGstSupplier().setApplNo(
						getManagedBean().getSmSupplier().getSmAppl()
								.getApplNo());
			}
		}
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setSmGstSupplier(getManagedBean().getHqGstSupplier());
		SmServiceLookup.getSupplierGstSO().saveSmSupplierGst(reqDTO);
		initHqGstInfo();
		showHqSuccessMessage(MessageConst.INFO_721219);
	}

	public void deleteHqGst() {
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setSmGstSupplier(getManagedBean().getHqGstSupplier());
		SmServiceLookup.getSupplierGstSO().deleteSmSupplierGst(reqDTO);
		initHqGstInfo();
		showHqSuccessMessage(MessageConst.INFO_721220);
	}

	private void showHqSuccessMessage(String msgKey) {
		FacesContext fc = FacesContext.getCurrentInstance();
		SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO, MessageUtils
				.getMessage(msgKey, MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale()), null);
	}

	public void addBranchGst(SmGstBranchVO branch) {
		if (getManagedBean().getBranches() != null
				&& !getManagedBean().getBranches().isEmpty()) {
			for (SmGstBranchVO brTmp : getManagedBean().getBranches()) {
				if (brTmp.getBranch().getSupplierBranchId()
						.compareTo(branch.getBranch().getSupplierBranchId()) == 0) {
					brTmp.setGst(new SmGstSupplier());
				}
			}
		}
	}

	public void onSaveBranchGst(SmGstBranchVO branch) {
		branch.setGst(new SmGstSupplier());
		getManagedBean().setSelectedBranchGst(branch);
		getManagedBean().setSaveBranchGstConfirmMsg(
				branch.getGst().getGstSupplierId() != null ? SmCommonUtil
						.getMessage("lbl.sm.gst.update.confirm.msg", null)
						: SmCommonUtil.getMessage("lbl.sm.gst.add.confirm.msg",
								null));
	}

	public void onDeleteBranchGst(SmGstBranchVO branch) {
		getManagedBean().setSelectedBranchGst(branch);
	}

	public void saveBranchGst() {
		if (getManagedBean().getSelectedBranchGst().getGst().getGstSupplierId() == null) {
			getManagedBean().getSelectedBranchGst().getGst()
					.setSmSupplier(getManagedBean().getSmSupplier());
			if (StringUtils.isNotBlank(getManagedBean().getSmSupplier()
					.getSmAppl().getApplNo())) {
				getManagedBean()
						.getSelectedBranchGst()
						.getGst()
						.setApplNo(
								getManagedBean().getSmSupplier().getSmAppl()
										.getApplNo());
			}
		}
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setSmGstSupplier(getManagedBean().getSelectedBranchGst()
				.getGst());
		SmServiceLookup.getSupplierGstSO().saveSmSupplierGst(reqDTO);
		initBranchGstInfo();
		showBranchSuccessMessage("lbl.sm.gst.branch.success.updated.msg",
				getManagedBean().getSelectedBranchGst().getBranch()
						.getBranchName());
	}

	public void deleteBranchGst() {
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setSmGstSupplier(getManagedBean().getSelectedBranchGst()
				.getGst());
		SmServiceLookup.getSupplierGstSO().deleteSmSupplierGst(reqDTO);
		initBranchGstInfo();
		showBranchSuccessMessage("lbl.sm.gst.branch.success.deleted.msg",
				getManagedBean().getSelectedBranchGst().getBranch()
						.getBranchName());
	}

	private void showBranchSuccessMessage(String msgKey, String branchName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String[] params = { "" };
		params[0] = branchName;
		SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO,
				SmCommonUtil.getMessage(msgKey, params), null);
	}

	private void initHqGstInfo() {
		getManagedBean()
				.setHqGstSupplier(
						SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(
								getManagedBean().getSmSupplier()
										.getSupplierId(), null));
		if (getManagedBean().getHqGstSupplier() != null
				&& getManagedBean().getHqGstSupplier().getGstSupplierId() != null) {
			getManagedBean().setGstEmpty(false);
		} else {
			getManagedBean().setGstEmpty(true);
		}
	}

	private void initBranchGstInfo() {
		if (getManagedBean().getSmSupplier().getSmAppl()
				.getSmSupplierBranches() != null
				&& !getManagedBean().getSmSupplier().getSmAppl()
						.getSmSupplierBranches().isEmpty()) {
			getManagedBean().setBranches(new ArrayList<SmGstBranchVO>());
			SmGstBranchVO brVO = null;
			for (SmSupplierBranch br : getManagedBean().getSmSupplier()
					.getSmAppl().getSmSupplierBranches()) {
				brVO = new SmGstBranchVO();
				brVO.setBranch(br);
				brVO.setGst(SmCommonUtil
						.getSmGstSupplierBySupplierIdAndBranchCode(
								getManagedBean().getSmSupplier()
										.getSupplierId(), br.getBranchCode()));
				getManagedBean().getBranches().add(brVO);
			}
		}
	}
}
