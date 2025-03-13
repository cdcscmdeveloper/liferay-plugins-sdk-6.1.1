package my.ep.web.backingbean.sm.compexp;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.dto.sm.compexp.SmCompExpResDTO;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.primefaces.context.RequestContext;

/**
 * BackingBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "compExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Prerender method for screen Update Company Experience Information
	 */
	public void preRenderCompExp() {
		renewSesionObject();
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		this.getManagedBean().setSmDraftList(new ArrayList<SmCompExpDraft>());
		this.getManagedBean().setCtDraftList(new ArrayList<SmCompExpDraft>());
		SmCommonUtil.redirectToPage("smCompExpTnc.xhtml", "", null, null);
	}

	/**
	 * To redirect to main SM Update Company Experience Information screen
	 */
	public void proceedAfterAcceptTnC() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setSupplierId(this.getManagedBean().getSupplier().getSupplierId());

		SmCompExpResDTO resDTO;
		resDTO = smCompExpSo().retrieveCompExpDraftListBySupplierId(reqDTO);

		for (SmCompExpDraft draf : resDTO.getDraftList()) {
			if (draf.getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_SM)) {
				this.getManagedBean().getSmDraftList().add(draf);
			} else if (draf.getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_CT)
					|| draf.getSourceType().getParameterId().equals(ParameterConst.SOURCE_TYPE_ID_QT)) {
				this.getManagedBean().getCtDraftList().add(draf);
			}
		}

		SmCommonUtil.redirectToPage("smCompExpMain.xhtml", "", null, null);
	}

	/**
	 * To redirect to SM Update Company Experience Information Draft screen
	 */
	public void redirectToDraft() {
		SmCommonUtil.redirectToPage("smCompExpDraft.xhtml", "", null, null);
	}

	/**
	 * To redirect to draft screen
	 * 
	 * @param draft
	 * @param fromSM
	 * @param fromCT
	 */
	public void onClickDraftAction(SmCompExpDraft draft, boolean fromSM, boolean fromCT) {
		
	}

	private ISmCompExpSO smCompExpSo() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
	}

}