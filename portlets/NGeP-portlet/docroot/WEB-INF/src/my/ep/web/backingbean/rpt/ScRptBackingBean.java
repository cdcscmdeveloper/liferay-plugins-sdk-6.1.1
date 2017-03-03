package my.ep.web.backingbean.rpt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.type.report.EnumReportType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "scRptBackingBean")
@SessionScoped
public class ScRptBackingBean extends RptBackingBean{

	public ScRptBackingBean() {
		super();
	}
	
	private boolean isEmpty(String[] arr){
		return arr == null || arr.length == 0;
	}
	
	public void onSelectEffectedValues() {
	
		if (!isEmpty(scProcureAllList)) {
			
			scProcureQuoteEditView = true;
			scProcureTenderEditView = true;
			scProcureRestrctdEditView = true;
			scQuotationList = null;
			scTenderList = null;
			scRstrctdTndrList = null;
			
		} else if (!isEmpty(scQuotationList) && !isEmpty(scTenderList)) {
			
			scProcureAllEditView = true;
			scProcureRestrctdEditView = true;
			
		} else if (!isEmpty(scRstrctdTndrList) && !isEmpty(scTenderList)) { 
			
			scProcureAllEditView = true;
			scProcureQuoteEditView = true;
			
		} else if (!isEmpty(scRstrctdTndrList) && !isEmpty(scQuotationList)) { 
			
			scProcureAllEditView = true;
			scProcureTenderEditView = true;
			
		} else {
			
			scProcureAllEditView = false;
			scProcureQuoteEditView = false;
			scProcureTenderEditView = false;
			scProcureRestrctdEditView = false;
			
		}
	}
	
	public void quotationNumberValidation() {
	
		if (StringUtils.isNotEmpty(this.getScQuotationNo())) {
			specialCharValidation();
		}
	}
	
	public void specialCharValidation() {
	
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
		
		Matcher match;
		
		String[] reportArr = {ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL,
							  ReportConst.SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL,
							  ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL,
							  ReportConst.SC_S1_LOA_RPT_VAL};
		
		if(isMatch(reportValue, reportArr)){
			match = pattern.matcher(scProcessRptQuotationNo);
		} else {
			match = pattern.matcher(scQuotationNo);
		}
		
		if (match.matches()) {
		
			if(isMatch(reportValue, reportArr)){
				retrieveSimpleQuoteNo();
			} else {
				getQuotationNumber();
			}
	
		} else {
		
			this.setScQuotationNo(null);
			this.setScProcessRptQuotationNo(null);
			throw new EpException(MessageConst.ERROR_REPORT_780006);
			
		}
	}
	
	public List<String> retrieveSimpleQuoteNo() {
	
		simpleQuoteNoList = new ArrayList<String>();
		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<String> smplQtNoList = reportSO.retrieveSimpleQuoteNo(this
					.getScProcessRptQuotationNo());
			if (smplQtNoList.size() > 0) {
				for (int i = 0; i < smplQtNoList.size(); i++) {
					simpleQuoteNoList.add(smplQtNoList.get(i));
				}
			} else {
				this.setScProcessRptQuotationNo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780021);
			}
		}
		
		return simpleQuoteNoList;
	}
	
	public void getQuotationNumber() {
		
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		
		if (StringUtils.isNotEmpty(this.getScQuotationNo())) {
			
			tenderReqDTO.setQtTndrNo(this.getScQuotationNo().toUpperCase());
			
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			
			QuationTenderResDTO resDTO = detailSO
					.retriveQtByQtNumber(tenderReqDTO);
			
			ScQt scqt = resDTO.getQtDetail();
		
			if (scqt != null) {
				if (scqt.getQtId() != null) {
					this.setScQuotationId(scqt.getQtId().toString());
				} else {
					this.setScQuotationNo(null);
					throw new EpException(MessageConst.ERROR_REPORT_780005);
				}
			} else {
				this.setScQuotationNo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780005);
			}
		}
	}
	
	public void simpleQuoteNoValidation() {
	
		getIisDPReportsStatus();
		
		if (StringUtils.isNotEmpty(this.getScProcessRptQuotationNo())) {
			specialCharValidation();
		}
	}
	
	private void getIisDPReportsStatus() {
	
		if (StringUtils.isNotEmpty(this.getModuleValue())
				&& (this.getModuleValue()
						.equalsIgnoreCase(ModuleCodeConst.DIRECT_PURCHASE))) {

			if (StringUtils.isNotEmpty(this.getReportValue())) {

				switch (EnumReportType.valueOf(this.getReportValue())) {

				case SC_050:
				case SC_051:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this.getScPIno())
								|| StringUtils.isNotEmpty(this
										.getScCommonStatus())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this.getScPIno())
							|| StringUtils.isNotEmpty(this.getScCommonStatus())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_055:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this
								.getScPurchaseInDefaultStatus())
								|| StringUtils.isNotEmpty(this.getScPIDNo())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPType())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPName())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPNo())
								&& StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this
							.getScPurchaseInDefaultStatus())
							|| StringUtils.isNotEmpty(this.getScPIDNo())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPType())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPName())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_067:

					if ((approverNayakTarafList.size() > 0)

							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this
								.getScPurchaseInDefaultStatus())
								|| StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this.getScPIDNo())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPType())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPName())
								|| StringUtils.isNotEmpty(this
										.getScContractOrPKPNo())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this
							.getScPurchaseInDefaultStatus())
							|| StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this.getScPIDNo())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPType())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPName())
							|| StringUtils.isNotEmpty(this
									.getScContractOrPKPNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_058:

				case SC_056:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this
										.getScRequestNoteStatus())
								|| StringUtils.isNotEmpty(this
										.getScProcurementType())
								|| StringUtils.isNotEmpty(this
										.getScRequestQuoteNo())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this
									.getScRequestNoteStatus())
							|| StringUtils.isNotEmpty(this
									.getScProcurementType())
							|| StringUtils.isNotEmpty(this
									.getScRequestQuoteNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_060:

				case SC_062:

					if ((approverNayakTarafList.size() > 0)
							|| ((authoriztionList != null) && (authoriztionList
									.contains(ReportConst.RN_APPROVER)))) {
						if (StringUtils.isNotEmpty(this.getScSupplier())
								|| StringUtils.isNotEmpty(this
										.getScProcurementType())
								|| StringUtils.isNotEmpty(this
										.getScProcessRptQuotationNo())
								|| StringUtils.isNotEmpty(this
										.getScSimpleQuoteStatus())
								|| StringUtils.isNotEmpty(this
										.getScRequisenerId())
								|| StringUtils.isNotEmpty(this.getScPtj())) {
							this.setValuesEmpty(false);
						} else {
							this.setValuesEmpty(true);
						}
					} else if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this
									.getScProcurementType())
							|| StringUtils.isNotEmpty(this
									.getScProcessRptQuotationNo())
							|| StringUtils.isNotEmpty(this
									.getScSimpleQuoteStatus())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_068:
					if (StringUtils.isNotEmpty(this
							.getScProcessRptQuotationNo())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				case SC_069:
					if (StringUtils.isNotEmpty(this.getScSupplier())
							|| StringUtils.isNotEmpty(this
									.getScSimpleQuoteType())
							|| StringUtils.isNotEmpty(this.getScSOAStatus())
							|| StringUtils.isNotEmpty(this
									.getScProcessRptQuotationNo())
							|| StringUtils.isNotEmpty(this.getScLOANo())
							|| StringUtils.isNotEmpty(this.getScRequisenerId())
							|| StringUtils.isNotEmpty(this.getScPtj())) {
						this.setValuesEmpty(false);
					} else {
						this.setValuesEmpty(true);
					}
					break;

				default: {
					System.out
							.println("In getIisDPReportsStatus unknown report value" + this.getReportValue());
					log.info("In getIisDPReportsStatus unknown report value \t" + this.getReportValue());
				}
				}

			}
		}

	}
	
	public void onChangeScRequisitionerType() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScPtjType() {
		getIisDPReportsStatus();
	}
	
	private IQTAwardSO getQtAwardSO() {
	
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
		
	}
	
	public void onSupplierNameChangeForLOV() {
	
		if (selectedOrgProfile != null
				&& selectedOrgProfile != null && smSupplier != null) {
		
			scSupplier = selectedOrgProfile
					.getSmSupplier().getCompanyName();
			
		}
		
		getIisDPReportsStatus();
	}
	
	public void onChangeScLOANo() {
		getIisDPReportsStatus();
	}
	
	String scLOAIdNo = "";
	
	public void getScLOAReports() {

		if (scLOAIdNo.startsWith(ReportConst.LA_VALUE)) {

			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

			reqDTO.setLoaNo(scLOAIdNo);
			
			AwardSupplierResDTO resDTO = getQtAwardSO().retrieveLOADtlByLoaNo(reqDTO);

			AwardSupplierReqDTO reqDTOLOA = new AwardSupplierReqDTO();
	
			if (resDTO.getScLoa() != null) {

				reqDTOLOA.setScLoa(resDTO.getScLoa());

				AwardSupplierResDTO reportsResDTO = getQtAwardSO()
						.retrieveLOADtl(reqDTOLOA);

				scLoa = reportsResDTO.getScLoa();

				loaReportDocumentList = reportsResDTO.getScLoa().getScLoaReports();

				scQt = reportsResDTO.getScQt();

			}

		} else {
			
			loaReportDocumentList = new ArrayList<ScLoaReport>();
		}
	}
	
	public void onChangeScContractOrPKPName() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScContractOrPKPNo() {
		getIisDPReportsStatus();
	}
	
	public void onChangePurchaseInDefaultStatus() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScSOAStatus() {
		getIisDPReportsStatus();
	}
	
	public void onChangeScSimpleQuoteTypeView() {
		getIisDPReportsStatus();
	}
}