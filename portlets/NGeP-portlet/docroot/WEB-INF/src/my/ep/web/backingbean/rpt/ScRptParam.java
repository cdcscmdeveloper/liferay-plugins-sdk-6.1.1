package my.ep.web.backingbean.rpt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.logging.LogKey;
import my.ep.type.report.EnumReportType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.RptConst;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class ScRptParam extends RptBackingBean{

	public ScRptParam(String reportId, String reportName, boolean isExcel,
			boolean isTempAccess) {
		super(reportId, reportName, isExcel, isTempAccess);
		// TODO Auto-generated constructor stub
	}
	
//	public void generateScReport() {
//
//		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
//				CommonConstants.DEFAULT_DATE_FORMAT);
//		
//		HashMap<String, String> h = new HashMap<String, String>();
//
//		h.put(ReportConst.CT_AMP_SUPPLIER_NAME_EQ, ctSupplierName);
//		h.put(ReportConst.CT_AMP_CIRCULAR_NAME_EQ, ctCircularName);
//		h.put(ReportConst.CT_AMP_TERMINATION_TYPE_EQ, ctTerminationType);
//		h.put(ReportConst.CT_AMP_STATUS_EQ, ctStatus);
//		h.put(ReportConst.CT_AMP_STATUS_EQ, ctStatusTwo);
//		h.put(ReportConst.CT_AMP_CONTRACT_NO_EQ, ctContractNo);
//		h.put(ReportConst.CT_AMP_PHYSICALCONTRACT_NO_EQ, ctPhysicalContractNo);
//		h.put(ReportConst.CT_AMP_AGREEMENT_EQ, ctAgreement);
//		h.put(ReportConst.CT_AMP_COMPLETE_EQ, ctComplete);
//		h.put(ReportConst.CT_AMP_STATUS_EQ, ctContrMontrStatus);
//		h.put(ReportConst.FL_AMP_FROM_DATE_EQ, outputDateFormat.format(fromDate));
//		h.put(ReportConst.FL_AMP_TO_DATE_EQ, outputDateFormat.format(toDate));
//		h.put(ReportConst.CT_AMP_CIRCULAR_FROM_DATE_EQ, outputDateFormat.format(ctCircularEffectiveFromDate));
//		h.put(ReportConst.CT_AMP_CIRCULAR_TO_DATE_EQ, outputDateFormat.format(ctCircularEffectiveToDate));
//		h.put(ReportConst.CT_AMP_PROCUREMENTMODE_EQ, ctProcurementMode);
//		h.put(ReportConst.CT_AMP_GROUP_EQ, ctGroupOne);
//		h.put(ReportConst.CT_AMP_GROUP_EQ, ctGroupTwo);
//		h.put(ReportConst.CT_AMP_GROUP_EQ, ctGroupThree);
//		h.put(ReportConst.CT_AMP_PPRODUCTSERVICE_EQ, ctProductService);
//		h.put(ReportConst.CT_AMP_PLOANO_EQ, ctLoanNo);
//		h.put(ReportConst.CT_AMP_PLOAPHYSICALNO_EQ, ctLOAPhysicalNo);
//		h.put(ReportConst.CT_AMP_PMINISTRY_EQ, commonMinistry);
//		h.put(ReportConst.CT_AMP_PPEGAIPENGAWAI_EQ, commonPegaWaiPengawal);
//		h.put(ReportConst.CT_AMP_PKUMPULAN_EQ, commonKumpulanPTJ);
//		h.put(ReportConst.CT_AMP_PPTJNAME_EQ, commonPtj);
//		h.put(ReportConst.CT_AMP_USERTYPE_EQ, ctUserType);
//		h.put(ReportConst.CT_AMP_SUBTYPE_EQ, ctSubType);
//	
//		for (Map.Entry<String, String> entries : h.entrySet()) {
//			 
//			 Entry<String, String> thisEntry =  entries;
//			 
//			 String key = thisEntry.getKey();
//			 String value = thisEntry.getValue();
//			 
//			 //reportWebUrl +=  key + "='" + value + "'";
//		}
//	}
}
