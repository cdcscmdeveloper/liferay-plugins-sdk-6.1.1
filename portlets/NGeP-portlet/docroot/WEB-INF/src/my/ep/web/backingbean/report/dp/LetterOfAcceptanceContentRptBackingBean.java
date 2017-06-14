/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.report.dp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.dp.LetterOfAcceptanceContentRptManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class LetterOfAcceptanceContentRptBackingBean.
 * 
 * @author Venkat Chanumolu
 * @since 08.06.2017
 */
@ManagedBean(name = "loaContentRptBackingBean")
@ViewScoped
public class LetterOfAcceptanceContentRptBackingBean extends
		DpReportBackingBean<LetterOfAcceptanceContentRptManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(LetterOfAcceptanceContentRptBackingBean.class);

	/** The sc loa id no. */
	private String scLOAIdNo;

	/** The report value. */
	private String reportValue;

	/** The values empty. */
	boolean valuesEmpty = false;

	/**
	 * @return the valuesEmpty
	 */
	public boolean isValuesEmpty() {
		return valuesEmpty;
	}

	/**
	 * @param valuesEmpty
	 *            the valuesEmpty to set
	 */
	public void setValuesEmpty(boolean valuesEmpty) {
		this.valuesEmpty = valuesEmpty;
	}

	/**
	 * @return the scLOAIdNo
	 */
	public String getScLOAIdNo() {
		return scLOAIdNo;
	}

	/**
	 * @param scLOAIdNo
	 *            the scLOAIdNo to set
	 */
	public void setScLOAIdNo(String scLOAIdNo) {
		this.scLOAIdNo = scLOAIdNo;
	}

	/**
	 * @return the reportValue
	 */
	public String getReportValue() {
		return reportValue;
	}

	/**
	 * @param reportValue
	 *            the reportValue to set
	 */
	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}

	/**
	 * getScLOAReports - Gets the sc loa reports.
	 * 
	 * @return the sc loaReportLList
	 */
	public void getScLOAReports() {

		if (StringUtils.isNotEmpty(this.getScLOAIdNo())
				&& this.getScLOAIdNo().startsWith(ReportConst.LA_VALUE)) {

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

			reqDTO.setLoaNo(this.getScLOAIdNo());
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtlByLoaNo(
					reqDTO);

			AwardSupplierReqDTO reqDTOLOA = new AwardSupplierReqDTO();
			if (resDTO.getScLoa() != null) {

				reqDTOLOA.setScLoa(resDTO.getScLoa());

				AwardSupplierResDTO reportsResDTO = getQtAwardSO()
						.retrieveLOADtl(reqDTOLOA);

				this.getManagedBean().setScLoa(reportsResDTO.getScLoa());

				this.getManagedBean().setLoaReportDocumentList(
						reportsResDTO.getScLoa().getScLoaReports());

				this.getManagedBean().setScQt(reportsResDTO.getScQt());

			}

		} else {
			this.getManagedBean().setLoaReportDocumentList(
					new ArrayList<ScLoaReport>());
		}
	}

	/**
	 * getQtAwardSO - Gets the qt award so.
	 * 
	 * @return the qtAwardSO
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}

	public void generateReport(LinkedHashMap<String, String> params) {
		generateDpReport(params);
	}

	/**
	 * downloadAllReports - Download all reports as pdf or xlsx files in Letter
	 * of Acceptance.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		log.info("ReportBackingBean-downloadAllReports");

		if (CollectionUtils.isNotEmpty(this.getManagedBean()
				.getLoaReportDocumentList())) {

			LOADetailBackingBean loaDetailAll = new LOADetailBackingBean();

			if (this.getManagedBean().getScLoa() != null) {

				loaDetailAll.getManagedBean().setScLoa(
						this.getManagedBean().getScLoa());
				loaDetailAll
						.getManagedBean()
						.getScLoa()
						.setPmStatus(
								this.getManagedBean().getScLoa().getPmStatus());
				loaDetailAll
						.getManagedBean()
						.getScLoa()
						.setScLoaReports(
								(this.getManagedBean().getScLoa()
										.getScLoaReports()));
				loaDetailAll.getManagedBean().setScQt(
						this.getManagedBean().getScQt());

				if (this.getManagedBean().getScLoa().getContractSigner() == null) {
					this.getManagedBean().getScLoa()
							.setContractSigner(new PmUser());
					loaDetailAll.getManagedBean().setContractSignerUser(
							this.getManagedBean().getScLoa()
									.getContractSigner());
				} else {
					loaDetailAll.getManagedBean().setContractSignerUser(
							this.getManagedBean().getScLoa()
									.getContractSigner());
				}

				getSuppLoiLoaMapData(loaDetailAll);
				loaDetailAll.downloadAllReports();
			}
		} else {
			if (StringUtils.isNotEmpty(this.getScLOAIdNo())) {
				this.setValuesEmpty(false);
				// throw new EpException(MessageConst.ERROR_REPORT_780026);
				throw new EpException(MessageConst.ERROR_REPORT_780026,
						new String[] { this.getScLOAIdNo() });
			} else {
				this.setValuesEmpty(true);
				if (this.getReportValue()
						.trim()
						.equalsIgnoreCase(ReportConst.SC_S1_LOA_CONTENT_RPT_VAL)) {
					if (StringUtils.isBlank(this.getScLOAIdNo())) {
						throw new EpException(MessageConst.ERROR_REPORT_780003);
					}
				}
			}

		}
	}

	/**
	 * getSuppLoiLoaMapData - Gets the supp loi loa map data.
	 * 
	 * @param loaDetail
	 *            the loa detail
	 * @return the suppLoiLoaMap
	 */
	private void getSuppLoiLoaMapData(LOADetailBackingBean loaDetail) {
		log.info("ReportBackingBean-getSuppLoiLoaMapData");
		if (this.getManagedBean().getScQt() != null) {
			retrieveSuppLoiLoaMapForReport(this.getManagedBean().getScQt()
					.getQtId());
		}
		loaDetail.getManagedBean().setSuppLoiLoaMap(
				this.getManagedBean().getSuppLoiLoaMap());

	}

	/**
	 * retrieveSuppLoiLoaMapForReport - will retrieving SuppLoiLoaMap based on
	 * qtid.
	 * 
	 * @param qtId
	 *            the qt id
	 */
	private void retrieveSuppLoiLoaMapForReport(Long qtId) {
		log.info("ReportBackingBean-retrieveSuppLoiLoaMapForReport");

		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setDocId(qtId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLOILOAList(reqDTO);

			if (resDTO.isSuccessful()) {

				this.getManagedBean().setSuppLoiLoaMap(
						resDTO.getLoiLoaSupplierMap());

			}

		} finally {
			qtAwardSO = null;
		}

	}

	@Override
	public void resetSearch() {
		// TODO Auto-generated method stub

	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * downloadReport - Download report as pdf or xlsx files in Letter of
	 * Acceptance.
	 * 
	 * @param scLoaReport
	 *            the sc loa report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {

		log.info("ReportBackingBean-downloadReport");

		LOADetailBackingBean loaDetail = new LOADetailBackingBean();

		loaDetail.getManagedBean().setScLoa(this.getManagedBean().getScLoa());
		loaDetail.getManagedBean().setScQt(this.getManagedBean().getScQt());

		if (this.getManagedBean().getScLoa().getContractSigner() == null) {
			this.getManagedBean().getScLoa().setContractSigner(new PmUser());
			loaDetail.getManagedBean().setContractSignerUser(
					this.getManagedBean().getScLoa().getContractSigner());
		} else {
			loaDetail.getManagedBean().setContractSignerUser(
					this.getManagedBean().getScLoa().getContractSigner());
		}

		getSuppLoiLoaMapData(loaDetail);

		if (scLoaReport != null) {
			loaDetail.downloadReport(scLoaReport);
		}
	}
}
