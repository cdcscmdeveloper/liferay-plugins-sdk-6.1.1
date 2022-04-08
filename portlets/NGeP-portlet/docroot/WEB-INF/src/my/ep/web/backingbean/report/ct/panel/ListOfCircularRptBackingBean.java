/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct.panel;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.backingbean.report.ct.CtReportBaseBackingBean;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.managedbean.report.ct.panel.ListOfCircularRptManagedBean;
import my.ep.web.util.DateUtils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class ListOfCircularRptBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "panelListOfCircularRptBB")
@ViewScoped
public class ListOfCircularRptBackingBean extends CtReportBaseBackingBean<ListOfCircularRptManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ListOfCircularRptBackingBean.class);
	
	public ListOfCircularRptBackingBean() {
		// set default date value
		getManagedBean().setCircularEffFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setCircularEffToDate(DateUtils.getCurrentDateWithoutTime());
	}

	/**
	 * The method used to reset the search criteria for report.
	 */
	@Override
	public void resetSearch() {
		log.debug("resetSearch: panelListOfCircularRptBB");
		getManagedBean().setPopUpMsg(null);
		getManagedBean().setReportUrl(null);
		getManagedBean().setCircularNo(null);
		getManagedBean().setCircularName(null);
		getManagedBean().setCircularEffFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setCircularEffToDate(DateUtils.getCurrentDateWithoutTime());
	}

	/**
	 * The method used to construct the parameter.
	 */
	@Override
	public LinkedHashMap<String, String> getparams() {
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		if (StringUtils.isNotEmpty(getManagedBean().getCircularNo())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_NO, getManagedBean()
					.getCircularNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getCircularName())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_NAME, getManagedBean()
					.getCircularName());
		}
		if (getManagedBean().getCircularEffFromDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getCircularEffFromDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_FROM_DATE, df.format(
					getManagedBean().getCircularEffFromDate()));
		}
		if (getManagedBean().getCircularEffToDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getCircularEffToDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_CIRCULAR_TO_DATE, df.format(
					getManagedBean().getCircularEffToDate()));
		}
		
		if (MapUtils.isNotEmpty(map)) {
			// mandatory map values
			map.put(CtReportConst.PARAM_KEY_REPORT, getManagedBean()
					.getReportName());
			map.put(CtReportConst.PARAM_KEY_REPORT_ID, getManagedBean()
					.getReportCode());
			map.put(CtReportConst.PARAM_KEY_USER_ID, getFacesScopes()
					.getUserSessionBean().getUserId().toString());
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}		
		return map;
	}	
}
