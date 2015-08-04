package my.ep.web.backingbean.sm.updategstinfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.updategstinfo.SmUpdateGstInfoManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmGstBranchVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "smUpdateGstInfoBackingBean")
@RequestScoped
public class SmUpdateGstInfoBackingBean extends
		BaseBackingBean<SmUpdateGstInfoManagedBean> {

	public SmUpdateGstInfoBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	public void preRenderGstInfo() {
		renewSesionObject();
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
		SmCommonUtil.redirectToPage("gstInfoBasePage.xhtml", "");
	}

	public void addHqGst() {
		getManagedBean().setGstEmpty(false);
		getManagedBean().setHqGstSupplier(new SmGstSupplier());
		getManagedBean().getHqGstSupplier().setIsCurrent(true);
		initSuppDocs(getManagedBean().getHqGstSupplier());
	}

	public void deleteHqGst() {
		if (getManagedBean().getHqGstSupplier() == null) {
			// No GST information to be deleted.
			throw new EpException(MessageConst.ERROR_722612);
		}
		setDeleteHq();
	}

	public void assignSelectedHqSupportDoc(SmGstSupplierDoc doc) {
		getManagedBean().setSelectedHqSuppDoc(doc);
	}

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
					initSuppDocs(brTmp.getGst());
				}
			}
		}
	}

	public void onDeclareGst() {
		RequestContext.getCurrentInstance().execute(
				"confirmDeclareDialog.show();");
	}

	public void declareGst() {
		if (noGstInfoEntered(getManagedBean().getHqGstSupplier())) {
			// No GST information to be declared
			throw new EpException(MessageConst.ERROR_722613);
		} else {
			List<SmGstSupplier> supplierGstList = new ArrayList<SmGstSupplier>();
			SmCommonUtil.removeNotUploadedGstSuppDoc(getManagedBean()
					.getHqGstSupplier());
			supplierGstList.add(getManagedBean().getHqGstSupplier());
			if (getManagedBean().getBranches() != null) {
				for (SmGstBranchVO brVO : getManagedBean().getBranches()) {
					if (brVO.getGst() != null) {
						if (brVO.getGst().getSmGstSupplierDocs() != null) {
							SmCommonUtil.removeNotUploadedGstSuppDoc(brVO
									.getGst());
						}
						supplierGstList.add(brVO.getGst());
					}
				}
			}
			SupplierGstReqDTO reqDTO = new SupplierGstReqDTO();
			reqDTO.setSmGstSupplierList(supplierGstList);
			reqDTO.setSupplierId(getManagedBean().getSmSupplier()
					.getSupplierId());
			SmServiceLookup.getSupplierGstSO().declareSupplierGst(reqDTO);
			renewSesionObject();
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721062);
		}
	}

	public void deleteBranchGst(SmGstBranchVO branch) {
		if (branch == null || branch.getGst() == null) {
			// No GST information entered to be deleted.
			throw new EpException(MessageConst.ERROR_722612);
		}
		setDeleteBranch(branch);
	}

	public void assignSelectedBranchSupportDoc(SmGstBranchVO brVO,
			SmGstSupplierDoc doc) {
		brVO.setDoc(doc);
		getManagedBean().setSelectedBranchDoc(brVO);
	}

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

	public void copyHqGstToBranch(SmGstBranchVO brVO) {
		if (brVO.getGst() == null) {
			brVO.setGst(new SmGstSupplier());
		}
		if (brVO.isSameWithHq()) {
			brVO.getGst().setGstRegNo(
					getManagedBean().getHqGstSupplier().getGstRegNo());
			brVO.getGst().setGstEffDate(
					getManagedBean().getHqGstSupplier().getGstEffDate());
			brVO.getGst().setGstEndDate(
					getManagedBean().getHqGstSupplier().getGstEndDate());
		} else {
			brVO.getGst().setGstRegNo(null);
			brVO.getGst().setGstEffDate(null);
			brVO.getGst().setGstEndDate(null);
		}
		brVO.getGst().setIsCurrent(true);
		brVO.getGst().setBranchCode(brVO.getBranch().getBranchCode());
	}

	public boolean isDisableDeclareButton() {
		boolean retVal = false;

		if (getManagedBean().getInitialHqGst() == null
				&& getManagedBean().isGstEmpty()) {
			retVal = true;
		}
		return retVal;
	}

	private String getGstSuppDocBasePath() {
		return SmCommonUtil
				.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH)
				+ getManagedBean().getSmSupplier().getRegNo()
				+ "/"
				+ System.currentTimeMillis() + "/";
	}

	private void initHqGstInfo() {
		getManagedBean().setHqGstSupplier(
				SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(
						getManagedBean().getSmSupplier().getSupplierId(), null,
						null));
		if (getManagedBean().getHqGstSupplier() != null
				&& getManagedBean().getHqGstSupplier().getGstSupplierId() != null) {
			getManagedBean().setGstEmpty(false);
		} else {
			getManagedBean().setGstEmpty(true);
		}
		initSuppDocs(getManagedBean().getHqGstSupplier());
		getManagedBean().setInitialHqGst(getManagedBean().getHqGstSupplier());
	}

	private void initBranchGstInfo() {
		getManagedBean()
				.setInitialBranchGstList(new ArrayList<SmGstSupplier>());
		List<SmSupplierBranch> supplierBranchList = getManagedBean()
				.getSuppBranchList();
		if (supplierBranchList != null && !supplierBranchList.isEmpty()) {
			getManagedBean().setBranches(new ArrayList<SmGstBranchVO>());
			SmGstBranchVO brVO = null;
			for (SmSupplierBranch br : supplierBranchList) {
				brVO = new SmGstBranchVO();
				brVO.setBranch(br);
				brVO.setGst(SmCommonUtil
						.getSmGstSupplierBySupplierIdAndBranchCode(
								getManagedBean().getSmSupplier()
										.getSupplierId(), br.getBranchCode(),
								null));
				if (brVO.getGst() != null) {
					if (brVO.getGst()
							.getGstRegNo()
							.equals(getManagedBean().getHqGstSupplier()
									.getGstRegNo())
							&& DateUtils.isSameDay(getManagedBean()
									.getHqGstSupplier().getGstEffDate(), brVO
									.getGst().getGstEffDate())) {
						brVO.setSameWithHq(true);
					}
					initSuppDocs(brVO.getGst());
					getManagedBean().getInitialBranchGstList().add(
							brVO.getGst());
				}
				getManagedBean().getBranches().add(brVO);
			}
		}
	}

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

	private void setDeleteGst(SmGstSupplier deleteGst) {
		if (deleteGst.getGstSupplierId() != null) {
			deleteGst.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
			deleteGst.setGstDeletedDate(new Date());
			deleteGst.setIsCurrent(false);
		} else {
			deleteGst = null;
		}
	}

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

	private void initSuppDocs(SmGstSupplier gst) {
		List<PmDocument> suppDocList = SmServiceLookup.getDocumentSO()
				.retrieveGstSuppDocuments(new DocumentReqDTO())
				.getDocumentList();

		if (gst != null
				&& (gst.getSmGstSupplierDocs() == null || gst
						.getSmGstSupplierDocs().isEmpty())) {
			gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
			SmGstSupplierDoc suppDoc = null;
			for (PmDocument doc : suppDocList) {
				suppDoc = new SmGstSupplierDoc();
				suppDoc.setPmDocument(doc);
				suppDoc.setSmGstSupplier(gst);
				gst.getSmGstSupplierDocs().add(suppDoc);
			}
		}
	}

	private void validateDeclare() {
		StringBuffer sb = new StringBuffer();
		SmGstSupplier gst = getManagedBean().getHqGstSupplier();
		if (StringUtils.isNotBlank(gst.getGstRegNo())
				&& gst.getGstEffDate() == null) {
			// GST Effective date is required
			sb.append(
					getEffDateRequiredMsg(getManagedBean().getCompanyBasic()
							.getCompanyName())).append("<br />");
		} else if (StringUtils.isBlank(gst.getGstRegNo())
				&& gst.getGstEffDate() != null) {
			// GST Registration no is required
			sb.append(
					getRegNoRequiredMsg(getManagedBean().getCompanyBasic()
							.getCompanyName())).append("<br />");
		}
		validateBranch(sb);
	}

	private void validateBranch(StringBuffer sb) {
		if (getManagedBean().getBranches() != null) {
			for (SmGstBranchVO brVO : getManagedBean().getBranches()) {
				if (brVO.getGst() != null && brVO.getGst().getIsCurrent()) {
					if (StringUtils.isNotBlank(brVO.getGst().getGstRegNo())
							&& brVO.getGst().getGstEndDate() == null) {
						// GST Effective date is required
						sb.append(
								getEffDateRequiredMsg(brVO.getBranch()
										.getBranchName())).append("<br />");
					} else if (StringUtils.isBlank(brVO.getGst().getGstRegNo())
							&& brVO.getGst().getGstEndDate() != null) {
						// GST Registration no is required
						sb.append(
								getRegNoRequiredMsg(brVO.getBranch()
										.getBranchName())).append("<br />");
					}
				}
			}
		}
		if (sb.length() > 0) {
			throw new EpException(sb.toString());
		}
	}

	private boolean noGstInfoEntered(SmGstSupplier gst) {
		boolean retVal = false;
		if (gst != null && StringUtils.isBlank(gst.getGstRegNo())
				&& gst.getGstEffDate() == null && gst.getGstEndDate() == null) {
			retVal = true;
		}
		return retVal;
	}

	private String getRegNoRequiredMsg(String companyName) {
		return SmCommonUtil.getMessage("lbl.sm.gst.regno.required.msg",
				new String[] { companyName });
	}

	private String getEffDateRequiredMsg(String companyName) {
		return SmCommonUtil.getMessage("lbl.sm.gst.effdate.required.msg",
				new String[] { companyName });
	}
}
