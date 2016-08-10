package my.ep.web.backingbean.sm.updategstinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.gsthistory.SmGstHistoryBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.updategstinfo.SmUpdateGstInfoManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmGstBranchVO;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "smUpdateGstInfoBackingBean")
@RequestScoped
public class SmUpdateGstInfoBackingBean extends
		BaseBackingBean<SmUpdateGstInfoManagedBean> {

	/**
	 * Declare constructor and initialized login user log key
	 */
	public SmUpdateGstInfoBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * Pre-render update gst terms and condition page
	 */
	public void preRenderGstInfo() {
		renewSesionObject();
		SmCommonUtil.redirectToPage("gstUpdateInfoTnc.xhtml", "", null);
	}

	/**
	 * Proceed to update gst page after accept terms and condition. Initialized
	 * all related gst information
	 */
	public void proceedAfterAcceptTnC() {
		getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
		getManagedBean().setMofAccount(
				SmCommonUtil.getMofAccountBySuppIdAndSuppType(getManagedBean()
						.getSmSupplier().getSupplierId(), getManagedBean()
						.getSmSupplier().getSmAppl().getSupplierType()));
		getManagedBean().setCompanyBasic(
				SmCommonUtil.getLatestRevCompBasicByApplId(getManagedBean()
						.getSmSupplier().getSmAppl().getApplId()));
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		getManagedBean().setSuppBranchList(
				SmServiceLookup.getSupplierSearchSO()
						.retrieveSmSupplierBranchBySupplier(reqDTO)
						.getSmSupplierBranchList());
		initHqGstInfo();
		initBranchGstInfo();
		SmCommonUtil.redirectToPage("gstInfoBasePage.xhtml", "", null);
	}

	/**
	 * Add HQ gst information in session
	 */
	public void addHqGst() {
		getManagedBean().setGstEmpty(false);
		getManagedBean().setHqGstSupplier(new SmGstSupplier());
		getManagedBean().getHqGstSupplier().setIsCurrent(true);
		SmApplicationUtil.initGstSuppDocs(getManagedBean().getHqGstSupplier());
		if (getManagedBean().getBranches() != null
				&& !getManagedBean().getBranches().isEmpty()) {
			for (SmGstBranchVO brTmp : getManagedBean().getBranches()) {
				brTmp.setGst(new SmGstSupplier());
				brTmp.setSameWithHq(true);
				SmCommonUtil.copyHqGstToBranch(getManagedBean()
						.getHqGstSupplier(), brTmp.getGst(), true, brTmp
						.getBranch().getBranchCode());
			}
		}
	}

	/**
	 * Delete HQ gst information in session
	 */
	public void deleteHqGst() {
		if (getManagedBean().getHqGstSupplier() == null) {
			// No GST information to be deleted.
			throw new EpException(MessageConst.ERROR_722612);
		}
		setDeleteHq();
	}

	/**
	 * Assign selected gst supporting document to HQ
	 * 
	 * @param SmGstSupplierDoc
	 *            doc
	 */
	public void assignSelectedHqSupportDoc(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedHqSuppDoc(doc);
	}

	/**
	 * Upload event to handle upload gst supporting documents
	 * 
	 * @param FileUploadEvent
	 *            event
	 */
	public void handleHqSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedHqSuppDoc() != null) {

				// construct file path
				String filePath = getGstSuppDocBasePath();

				// init suppDocFile (currently only default one file)
				SmGstSupplierDoc doc = getManagedBean().getSelectedHqSuppDoc();
				doc.setFileName(StringUtils.replace(event.getFile()
						.getFileName(), " ", "-"));
				doc.setFilePath(filePath);

				// set selected doc to main doc list
				setHqSuppDocFileAttachment();

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	public void onDeleteBrAttachment(SmSupplierBranch br, SmGstSupplierDoc doc) {
		SmGstBranchVO vo = new SmGstBranchVO();
		vo.setBranch(br);
		vo.setDoc(doc);
		getManagedBean().setSelectedBranchDoc(vo);
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteBrDialog.show();");
	}

	public void deleteBrAttachment() {
		SmGstBranchVO mainBr = (SmGstBranchVO) EntityHelper
				.getEntityBySinglePropertyFromList(getManagedBean()
						.getSelectedBranchDoc().getBranch()
						.getSupplierBranchId(), getManagedBean().getBranches(),
						"branch");
		SmGstSupplierDoc mainDoc = (SmGstSupplierDoc) EntityHelper
				.getEntityBySinglePropertyFromList(getManagedBean()
						.getSelectedBranchDoc().getDoc().getPmDocument()
						.getDocumentId(), mainBr.getGst()
						.getSmGstSupplierDocs(), "pmDocument");

		mainDoc.setFileName(StringUtils.EMPTY);
		mainDoc.setFilePath(StringUtils.EMPTY);
	}

	public void onDeleteHqAttachment(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedHqSuppDoc(doc);
		RequestContext.getCurrentInstance().execute(
				"confirmDeleteHqDialog.show();");
	}

	public void deleteHqAttachment() {
		SmGstSupplierDoc mainDoc = (SmGstSupplierDoc) EntityHelper
				.getEntityBySinglePropertyFromList(
						getManagedBean().getSelectedHqSuppDoc().getPmDocument()
								.getDocumentId(), getManagedBean()
								.getHqGstSupplier().getSmGstSupplierDocs(),
						"pmDocument");
		mainDoc.setFileName(StringUtils.EMPTY);
		mainDoc.setFilePath(StringUtils.EMPTY);
	}

	/**
	 * Add Branch gst information in session
	 * 
	 * @param SmGstBranchVO
	 *            branch
	 */
	public void addBranchGst(SmGstBranchVO branch) {
		if (getManagedBean().getBranches() != null
				&& !getManagedBean().getBranches().isEmpty()) {
			for (SmGstBranchVO brTmp : getManagedBean().getBranches()) {
				if (brTmp.getBranch().getSupplierBranchId()
						.compareTo(branch.getBranch().getSupplierBranchId()) == 0) {
					brTmp.setGst(new SmGstSupplier());
					brTmp.getGst().setIsCurrent(true);
					brTmp.getGst().setBranchCode(
							brTmp.getBranch().getBranchCode());
					SmApplicationUtil.initGstSuppDocs(brTmp.getGst());
				}
			}
		}
	}

	/**
	 * Call confirmation dialog when click declare button
	 */
	public void onDeclareGst() {
		validateDeclare();
		RequestContext.getCurrentInstance().execute(
				"confirmDeclareDialog.show();");
	}

	/**
	 * Process save or update declare gst for HQ and branch. Also notify
	 * contract approver on the gst declaration
	 */
	public void declareGst() {

		List<SmGstSupplier> supplierGstList = new ArrayList<SmGstSupplier>();
		SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
				.getHqGstSupplier());
		supplierGstList.add(getManagedBean().getHqGstSupplier());
		if (getManagedBean().getBranches() != null) {
			for (SmGstBranchVO brVO : getManagedBean().getBranches()) {
				if (brVO.getGst() != null) {
					if (brVO.isSameWithHq()) {
						SmCommonUtil.copyHqGstToBranch(getManagedBean()
								.getHqGstSupplier(), brVO.getGst(), true, brVO
								.getBranch().getBranchCode());
					}
					SmCommonUtil.removeNotUploadedGstSuppDoc(brVO.getGst());
					supplierGstList.add(brVO.getGst());
				}
			}
		}
		SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
		reqDTO.setSmGstSupplierList(supplierGstList);
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		SmServiceLookup.getSupplierGstSO().declareSupplierGst(reqDTO);
		renewSesionObject();
		SmCommonUtil.redirectToPage(
				SmApplicationUtil.getApplicationErrorPage(),
				MessageConst.INFO_721062, null);
	}

	/**
	 * Delete branch gst information in session
	 * 
	 * @param SmGstBranchVO
	 *            branch
	 */
	public void deleteBranchGst(SmGstBranchVO branch) {
		if (branch == null || branch.getGst() == null) {
			// No GST information entered to be deleted.
			throw new EpException(MessageConst.ERROR_722612);
		}
		setDeleteBranch(branch);
	}

	/**
	 * Assign selected branch supporting documents to upload
	 * 
	 * @param SmGstBranchVO
	 *            brVO
	 * @param SmGstSupplierDoc
	 *            doc
	 */
	public void assignSelectedBranchSupportDoc(SmGstBranchVO brVO,
			SmGstSupplierDoc doc) {
		brVO.setDoc(doc);
		getManagedBean().setSelectedBranchDoc(brVO);
	}

	/**
	 * Handle file upload event for branch gst supporting document
	 * 
	 * @param FileUploadEvent
	 *            event
	 */
	public void handleBranchSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedBranchDoc() != null) {

				// construct file path
				String filePath = getGstSuppDocBasePath();

				// init suppDocFile (currently only default one file)
				SmGstSupplierDoc doc = getManagedBean().getSelectedBranchDoc()
						.getDoc();
				doc.setFileName(StringUtils.replace(event.getFile()
						.getFileName(), " ", "-"));
				doc.setFilePath(filePath);

				// set selected doc to main doc list
				setBranchSuppDocFileAttachment();

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	/**
	 * Copy HQ gst information to Branch
	 * 
	 * @param SmGstBranchVO
	 *            brVO
	 */
	public void copyHqGstToBranch(SmGstBranchVO brVO) {
		if (brVO.getGst() == null) {
			brVO.setGst(new SmGstSupplier());
			SmApplicationUtil.initGstSuppDocs(brVO.getGst());
		}
		SmCommonUtil.copyHqGstToBranch(getManagedBean().getHqGstSupplier(),
				brVO.getGst(), brVO.isSameWithHq(), brVO.getBranch()
						.getBranchCode());
	}

	/**
	 * Check whether declare button is disable or not
	 * 
	 * @return boolean
	 */
	public boolean isDisableDeclareButton() {
		boolean retVal = false;

		if (getManagedBean().getInitialHqGst() == null
				&& getManagedBean().isGstEmpty()) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Redirect to GST history page
	 */
	public void redirectToHistoryPage() {
		SmGstHistoryBackingBean history = new SmGstHistoryBackingBean();
		history.preRenderGstHistory();
	}

	/**
	 * Get gst upload directory
	 * 
	 * @return String
	 */
	private String getGstSuppDocBasePath() {
		return SmCommonUtil
				.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
				+ getManagedBean().getSmSupplier().getRegNo()
				+ "/"
				+ System.currentTimeMillis() + "/";
	}

	/**
	 * Initialized HQ gst information
	 */
	private void initHqGstInfo() {
		getManagedBean().setHqGstSupplier(
				SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(
						getManagedBean().getSmSupplier().getSupplierId(), null,
						null, true));
		if (getManagedBean().getHqGstSupplier() != null
				&& getManagedBean().getHqGstSupplier().getGstSupplierId() != null) {
			getManagedBean().setGstEmpty(false);
		} else {
			getManagedBean().setGstEmpty(true);
		}
		SmApplicationUtil.initGstSuppDocs(getManagedBean().getHqGstSupplier());
		getManagedBean().setInitialHqGst(getManagedBean().getHqGstSupplier());
	}

	/**
	 * Init Branch gst information
	 */
	private void initBranchGstInfo() {
		List<SmSupplierBranch> supplierBranchList = getManagedBean()
				.getSuppBranchList();
		if (supplierBranchList != null && !supplierBranchList.isEmpty()) {
			getManagedBean().setBranches(new ArrayList<SmGstBranchVO>());
			SmGstBranchVO brVO = null;
			for (SmSupplierBranch br : supplierBranchList) {
				brVO = new SmGstBranchVO();
				brVO.setBranch(br);
				if (getManagedBean().getHqGstSupplier() != null
						&& getManagedBean().getHqGstSupplier().getIsCurrent()) {
					brVO.setGst(SmCommonUtil
							.getSmGstSupplierBySupplierIdAndBranchCode(
									getManagedBean().getSmSupplier()
											.getSupplierId(),
									br.getBranchCode() == null ? br
											.getSupplierBranchId() : br
											.getBranchCode(), null, true));
					if (brVO.getGst() != null) {
						brVO.setSameWithHq(SmApplicationUtil
								.isBranchGstSameAsHq(getManagedBean()
										.getHqGstSupplier(), brVO.getGst()));
						if (EntityHelper.isCollectionEmpty(brVO.getGst()
								.getSmGstSupplierDocs())) {
							SmApplicationUtil.initGstSuppDocs(brVO.getGst());
						}
					}
					// else {
					// brVO.setGst(new SmGstSupplier());
					// brVO.setSameWithHq(true);
					// SmCommonUtil.copyHqGstToBranch(getManagedBean()
					// .getHqGstSupplier(), brVO.getGst(), true, brVO
					// .getBranch().getBranchCode());
					// }
				}
				getManagedBean().getBranches().add(brVO);
			}
		}
	}

	/**
	 * Set deleted HQ value in session
	 */
	private void setDeleteHq() {
		getManagedBean().setGstEmpty(true);
		setDeleteGst(getManagedBean().getHqGstSupplier());
		if (getManagedBean().getBranches() != null) {
			for (SmGstBranchVO br : getManagedBean().getBranches()) {
				if (br.getGst() != null) {
					br.setSameWithHq(false);
					if (br.getGst().getGstSupplierId() != null) {
						setDeleteGst(br.getGst());
					} else {
						br.setGst(null);
					}
				}
			}
		}
	}

	/**
	 * Set deleted branch in session
	 * 
	 * @param SmGstBranchVO
	 *            branch
	 */
	private void setDeleteBranch(SmGstBranchVO branch) {
		for (SmGstBranchVO brVO : getManagedBean().getBranches()) {
			if (brVO.getGst() != null
					&& brVO.getBranch()
							.getSupplierBranchId()
							.compareTo(branch.getBranch().getSupplierBranchId()) == 0) {
				if (brVO.getGst().getGstSupplierId() != null) {
					setDeleteGst(brVO.getGst());
				} else {
					brVO.setGst(null);
				}
				brVO.setSameWithHq(false);
			}
		}
	}

	/**
	 * Set deleted gst values in session
	 * 
	 * @param SmGstSupplier
	 *            deleteGst
	 */
	private void setDeleteGst(SmGstSupplier deleteGst) {
		if (deleteGst.getGstSupplierId() != null) {
			deleteGst.setGstDeletedDate(new Date());
			deleteGst.setIsCurrent(false);
			deleteGst.setGstRegNo(null);
			deleteGst.setGstEffDate(null);
			deleteGst.setGstEndDate(null);
		} else {
			deleteGst = null;
		}
	}

	/**
	 * Set HQ gst file attachment to VO
	 */
	private void setHqSuppDocFileAttachment() {
		Iterator<SmGstSupplierDoc> docVOIter = getManagedBean()
				.getHqGstSupplier().getSmGstSupplierDocs().iterator();
		while (docVOIter.hasNext()) {
			SmGstSupplierDoc docVO = docVOIter.next();
			if (docVO
					.getPmDocument()
					.getDocumentId()
					.compareTo(
							getManagedBean().getSelectedHqSuppDoc()
									.getPmDocument().getDocumentId()) == 0) {

				docVO = getManagedBean().getSelectedHqSuppDoc();

			}
		}
	}

	/**
	 * Set branch gst file attachment to VO
	 */
	private void setBranchSuppDocFileAttachment() {
		Iterator<SmGstBranchVO> brVOIter = getManagedBean().getBranches()
				.iterator();
		while (brVOIter.hasNext()) {
			SmGstBranchVO brVO = brVOIter.next();
			if (brVO.getBranch()
					.getSupplierBranchId()
					.compareTo(
							getManagedBean().getSelectedBranchDoc().getBranch()
									.getSupplierBranchId()) == 0) {
				brVO.getGst()
						.getSmGstSupplierDocs()
						.set(brVO
								.getGst()
								.getSmGstSupplierDocs()
								.indexOf(
										getManagedBean().getSelectedBranchDoc()
												.getDoc()),
								getManagedBean().getSelectedBranchDoc()
										.getDoc());
			}
		}
	}

	/**
	 * Validate gst registration no, effective date and end date entered
	 */
	private void validateDeclare() {
		SmGstSupplier gst = getManagedBean().getHqGstSupplier();

		// validate HQ GST reg no. uniqueness
		SmCommonUtil.validateGstRegNoUniqueness(getManagedBean()
				.getSmSupplier().getRegNo(), gst.getGstRegNo());

		if (gst.getGstEndDate() != null
				&& gst.getGstEffDate().compareTo(gst.getGstEndDate()) > 0) {
			throw new EpException(MessageConst.ERROR_721044);
		} else if (StringUtils.isNotBlank(gst.getGstRegNo())
				&& gst.getGstRegNo().length() != SmRuleHelper.getInstance()
						.getGstRegNumLength()) {
			throw new EpException(MessageConst.ERROR_721027);
		}
		validateBranch();
	}

	/**
	 * Validate branch gst registration no, effective date and end date entered
	 */
	private void validateBranch() {
		if (getManagedBean().getBranches() != null) {
			for (SmGstBranchVO brVO : getManagedBean().getBranches()) {
				if (brVO.getGst() != null && brVO.getGst().getIsCurrent()) {

					if (!brVO.isSameWithHq()
							&& StringUtils.isBlank(brVO.getGst().getGstRegNo())) {
						throw new EpException(SmCommonUtil.getMessage(
								"lbl.sm.gst.branch.req.msg", null));
					}

					// validate HQ Branch reg no. uniqueness
					SmCommonUtil.validateGstRegNoUniqueness(getManagedBean()
							.getSmSupplier().getRegNo(), brVO.getGst()
							.getGstRegNo());

					if (StringUtils.isNotBlank(brVO.getGst().getGstRegNo())
							&& brVO.getGst().getGstRegNo().length() != SmRuleHelper
									.getInstance().getGstRegNumLength()) {
						throw new EpException(MessageConst.ERROR_721027);
					} else if (brVO.getGst().getGstEndDate() != null
							&& brVO.getGst().getGstEffDate()
									.compareTo(brVO.getGst().getGstEndDate()) > 0) {
						throw new EpException(MessageConst.ERROR_721044);
					}
				}
			}
		}
	}
}
