package my.ep.web.backingbean.sm.updategstinfo;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.updategstinfo.SmUpdateGstInfoManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmGstBranchVO;

@ManagedBean(name = "smUpdateGstInfoBackingBean")
@RequestScoped
public class SmUpdateGstInfoBackingBean extends
		BaseBackingBean<SmUpdateGstInfoManagedBean> {

	public SmUpdateGstInfoBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	public void preRenderGstInfo() {
		getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
		SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
		gstReqDTO.setSupplierId(getManagedBean().getSmSupplier()
				.getSupplierId());
		gstReqDTO.setBranchCode(null);
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
		getManagedBean().setMofAccount(
				SmCommonUtil.getMofAccountBySuppIdAndSuppType(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmSupplier().getSmAppl().getSupplierType()));
		getManagedBean().setCompanyBasic(
				SmCommonUtil.getLatestRevCompBasicByApplId(getManagedBean()
						.getSmSupplier().getSmAppl().getApplId()));
		setBranchesGstToVO();
		SmCommonUtil.redirectToPage("gstInfoBasePage.xhtml", "");
	}

	private void setBranchesGstToVO() {
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
			}
		}
	}
}
