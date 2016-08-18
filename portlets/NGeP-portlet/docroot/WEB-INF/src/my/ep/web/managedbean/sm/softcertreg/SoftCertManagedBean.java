package my.ep.web.managedbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.backingbean.pm.cc.SoftCertMaintenanceBackingBean;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.vo.SoftcertUploadSuppDocVO;

import com.liferay.portal.model.Role;

@ManagedBean(name = "softCertMB")
@SessionScoped
public class SoftCertManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397086927187535118L;

	private List<SmPersonnel> personnelList;
	private List<SmPersonnel> softCertSelectList;
	private List<SmPersonnel> softCertPayList;
	private SmPersonnel softCertSelect;
	private HashMap<Long, BigDecimal> paymentMap = new HashMap<Long, BigDecimal>();
	private BigDecimal totalPaymentAmount;
	private BigDecimal totalAmountBeforeGst;
	private BigDecimal totalGstAmount;
	private BigDecimal gstRate;
	private SmPersonnel loginUserPersonnelInfo;
	private SmPersonnel supplierTmpPersonnel;
	private SoftcertUploadSuppDocVO selectedSuppDoc;
	private String loginRoleName;
	private String taskName;
	private String paymentBtnLbl;
	private String targetPage;
	private String suppDocMode;
	private WorklistItemVO softCertBpmTask;
	private List<SmPersonnel> authorizedPersonnels;
	private boolean alreadyHadFreeSoftcert;
	private boolean declare;
	private String softcertDocNo;
	private List<SoftcertUploadSuppDocVO> softcertUploadSuppDocList;
	private SoftCertMaintenanceBackingBean softCertMaintenanceBackingBean = new SoftCertMaintenanceBackingBean();
	private boolean applySoftcertForOthers;
	private boolean renderRevokeButton;
	private boolean allAuthSuppUserAlreadyRequested;
	private boolean basicAppl;
	private PmDigiSign digiSign;

	@ManagedProperty(value = "#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;

	public SoftCertManagedBean() {
		super();
		FacesScopes sc = new FacesScopes();
		List<Role> loginUserRoles = sc.getUserRoleList();
		List<String> roleNames = new ArrayList<String>();
		for (Role r : loginUserRoles) {
			roleNames.add(r.getName());
		}
		if (sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)) {
			setLoginRoleName(SupplierManagementConst.MOF_SUPPLIER_ADMIN);
		} else {
			setLoginRoleName(SupplierManagementConst.BASIC_SUPPLIER_ADMIN);
		}
	}

	public List<SmPersonnel> getPersonnelList() {
		return personnelList;
	}

	public void setPersonnelList(List<SmPersonnel> personnelList) {
		this.personnelList = personnelList;
	}

	public List<SmPersonnel> getSoftCertSelectList() {
		return softCertSelectList;
	}

	public void setSoftCertSelectList(List<SmPersonnel> softCertSelectList) {
		this.softCertSelectList = softCertSelectList;
	}

	public List<SmPersonnel> getSoftCertPayList() {
		return softCertPayList;
	}

	public void setSoftCertPayList(List<SmPersonnel> softCertPayList) {
		this.softCertPayList = softCertPayList;
	}

	public HashMap<Long, BigDecimal> getPaymentMap() {
		return paymentMap;
	}

	public void setPaymentMap(HashMap<Long, BigDecimal> paymentMap) {
		this.paymentMap = paymentMap;
	}

	public BigDecimal getTotalPaymentAmount() {
		if (totalPaymentAmount == null)
			totalPaymentAmount = BigDecimal.ZERO;
		return totalPaymentAmount;
	}

	public void setTotalPaymentAmount(BigDecimal totalPaymentAmount) {
		this.totalPaymentAmount = totalPaymentAmount;
	}

	public SmPersonnel getLoginUserPersonnelInfo() {
		return loginUserPersonnelInfo;
	}

	public void setLoginUserPersonnelInfo(SmPersonnel loginUserPersonnelInfo) {
		this.loginUserPersonnelInfo = loginUserPersonnelInfo;
	}

	public String getLoginRoleName() {
		return loginRoleName;
	}

	public void setLoginRoleName(String loginRoleName) {
		this.loginRoleName = loginRoleName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public WorklistItemVO getSoftCertBpmTask() {
		return softCertBpmTask;
	}

	public void setSoftCertBpmTask(WorklistItemVO softCertBpmTask) {
		this.softCertBpmTask = softCertBpmTask;
	}

	public List<SmPersonnel> getAuthorizedPersonnels() {
		return authorizedPersonnels;
	}

	public void setAuthorizedPersonnels(List<SmPersonnel> authorizedPersonnels) {
		this.authorizedPersonnels = authorizedPersonnels;
	}

	public String getPaymentBtnLbl() {
		return paymentBtnLbl;
	}

	public void setPaymentBtnLbl(String paymentBtnLbl) {
		this.paymentBtnLbl = paymentBtnLbl;
	}

	public boolean isAlreadyHadFreeSoftcert() {
		return alreadyHadFreeSoftcert;
	}

	public void setAlreadyHadFreeSoftcert(boolean alreadyHadFreeSoftcert) {
		this.alreadyHadFreeSoftcert = alreadyHadFreeSoftcert;
	}

	public BigDecimal getTotalAmountBeforeGst() {
		return totalAmountBeforeGst;
	}

	public void setTotalAmountBeforeGst(BigDecimal totalAmountBeforeGst) {
		this.totalAmountBeforeGst = totalAmountBeforeGst;
	}

	public BigDecimal getTotalGstAmount() {
		return totalGstAmount;
	}

	public void setTotalGstAmount(BigDecimal totalGstAmount) {
		this.totalGstAmount = totalGstAmount;
	}

	public SoftCertMaintenanceBackingBean getSoftCertMaintenanceBackingBean() {
		return softCertMaintenanceBackingBean;
	}

	public void setSoftCertMaintenanceBackingBean(
			SoftCertMaintenanceBackingBean softCertMaintenanceBackingBean) {
		this.softCertMaintenanceBackingBean = softCertMaintenanceBackingBean;
	}

	public BigDecimal getGstRate() {
		return gstRate;
	}

	public void setGstRate(BigDecimal gstRate) {
		this.gstRate = gstRate;
	}

	public List<SoftcertUploadSuppDocVO> getSoftcertUploadSuppDocList() {
		return softcertUploadSuppDocList;
	}

	public void setSoftcertUploadSuppDocList(
			List<SoftcertUploadSuppDocVO> softcertUploadSuppDocList) {
		this.softcertUploadSuppDocList = softcertUploadSuppDocList;
	}

	public SoftcertUploadSuppDocVO getSelectedSuppDoc() {
		return selectedSuppDoc;
	}

	public void setSelectedSuppDoc(SoftcertUploadSuppDocVO selectedSuppDoc) {
		this.selectedSuppDoc = selectedSuppDoc;
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

	public SmPersonnel getSupplierTmpPersonnel() {
		return supplierTmpPersonnel;
	}

	public void setSupplierTmpPersonnel(SmPersonnel supplierTmpPersonnel) {
		this.supplierTmpPersonnel = supplierTmpPersonnel;
	}

	public String getSuppDocMode() {
		return suppDocMode;
	}

	public void setSuppDocMode(String suppDocMode) {
		this.suppDocMode = suppDocMode;
	}

	public boolean isDeclare() {
		return declare;
	}

	public void setDeclare(boolean declare) {
		this.declare = declare;
	}

	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	public String getSoftcertDocNo() {
		return softcertDocNo;
	}

	public void setSoftcertDocNo(String softcertDocNo) {
		this.softcertDocNo = softcertDocNo;
	}

	public boolean isApplySoftcertForOthers() {
		return applySoftcertForOthers;
	}

	public void setApplySoftcertForOthers(boolean applySoftcertForOthers) {
		this.applySoftcertForOthers = applySoftcertForOthers;
	}

	public SmPersonnel getSoftCertSelect() {
		return softCertSelect;
	}

	public void setSoftCertSelect(SmPersonnel softCertSelect) {
		this.softCertSelect = softCertSelect;
	}

	public boolean isRenderRevokeButton() {
		return renderRevokeButton;
	}

	public void setRenderRevokeButton(boolean renderRevokeButton) {
		this.renderRevokeButton = renderRevokeButton;
	}

	public boolean isAllAuthSuppUserAlreadyRequested() {
		return allAuthSuppUserAlreadyRequested;
	}

	public void setAllAuthSuppUserAlreadyRequested(
			boolean allAuthSuppUserAlreadyRequested) {
		this.allAuthSuppUserAlreadyRequested = allAuthSuppUserAlreadyRequested;
	}

	public boolean isBasicAppl() {
		return basicAppl;
	}

	public void setBasicAppl(boolean basicAppl) {
		this.basicAppl = basicAppl;
	}

	public PmDigiSign getDigiSign() {
		return digiSign;
	}

	public void setDigiSign(PmDigiSign digiSign) {
		this.digiSign = digiSign;
	}

}
