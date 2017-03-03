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
import my.ep.type.pm.OrganizationType;
import my.ep.type.report.EnumReportType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "flRptBackingBean")
@SessionScoped
public class FlRptBackingBean extends RptBackingBean{

	public FlRptBackingBean() {
		super();
	}
	
	public void onChangeValuesForLOVs() {
		refreshLovData();
	}
	
	private void refreshLovData() {

		if (selectedMinistry != null
				&& selectedMinistry.getPmOrgValidity().getOrgName() != null) {

			if (lovValuesMap != null
					&& !(lovValuesMap.isEmpty())
					&& !(lovValuesMap.get("0").equals(selectedMinistry.getPmOrgValidity().getOrgName()))) {

				selectedPegwaiPengawal = null;
				selectedKumpulanPTJ = null;
				selectedPTJ = null;
			}

			lovValuesMap.put("0", selectedMinistry.getPmOrgValidity().getOrgName());

			if (selectedPegwaiPengawal != null && selectedPegwaiPengawal.getPmOrgValidity() != null) {
				lovValuesMap.put("1", selectedPegwaiPengawal.getPmOrgValidity().getOrgName());
			}
			
			if (selectedKumpulanPTJ != null && selectedKumpulanPTJ.getPmOrgValidity() != null) {
				lovValuesMap.put("2", selectedKumpulanPTJ.getPmOrgValidity().getOrgName());
			}

			if (selectedPTJ != null && selectedPTJ.getPmOrgValidity() != null) {
				lovValuesMap.put("3", selectedPTJ.getPmOrgValidity().getOrgName());
			}

		}
	}
	
	public String constructMinistryConditionFields() {
	
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		
		return sb.toString();
	}
	
	public String constructPegwaiPengawalConditionFields() {
	
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.PENGAWAI_PENGAWAL.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.PENGAWAI_PENGAWAL.value(),
				OrganizationType.MINISTRY.value()));
		sb.append("^");
		
		if (selectedMinistry != null) {
			sb.append(selectedMinistry.getOrgProfileId());
		}

		return sb.toString();
	}
	
	public String constructKumpulanPTJConditionFields() {
	
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DEPARTMENT.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.DEPARTMENT.value(),
				OrganizationType.PENGAWAI_PENGAWAL.value()));
		sb.append("^");
		
		if (selectedPegwaiPengawal != null) {
			selectedPegwaiPengawal.getOrgProfileId();
		}

		return sb.toString();
	}
	
	public String constructPTJConditionFields() {

		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil
				.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		
		if (selectedKumpulanPTJ != null) {
			sb.append(selectedKumpulanPTJ.getOrgProfileId());
		}
		
		return sb.toString();
	}
	
	public void onSupplierNameChangeForFLLOV() {
	
		if (selectedOrgProfile != null
				&& selectedOrgProfile.getSmSupplier() != null) {
			
			flSupplier = selectedOrgProfile
					.getSmSupplier().getCompanyName();

		}
	}

}