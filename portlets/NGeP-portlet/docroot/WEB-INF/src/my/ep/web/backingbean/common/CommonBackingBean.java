/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.common.CommonManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Backing Bean For Contract Details
 * 
 * @author kieyun
 * @since 13/11/2013	 
 */
@ManagedBean(name="commonBackingBean")
@RequestScoped
public class CommonBackingBean extends BaseBackingBean<CommonManagedBean> {
	private String orderNo;

	private static Logger log = Logger.getLogger(CommonBackingBean.class);
	
	public void generateReport() {
		log.info("generateReport() : "+getManagedBean().getReportDisplayName());
		try {
			generateReport(getManagedBean().getReportName(), getManagedBean().getParamMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void generateReportByOrderNo(){
		//orderNo=orderNo.substring(1,orderNo.indexOf("_No"));
		log.info("generateReport() : "+getManagedBean().getReportDisplayName()+" Order NO:" +orderNo);
		try {
			Map<String,String> paramMap=new HashMap<String, String>();
			paramMap.put(ReportConst.PARAM_DOC_NO,orderNo);
			paramMap.put(ReportConst.PARAM_USER_ID,getFacesScopes().getUserSessionBean().getUserId()+"");
			generateReport(getManagedBean().getReportName(), paramMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public void testPushBiddingSchedule(){
		IBiddingSO biddingSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
		List<Long> scheduleIds = new ArrayList<Long>();
		Long scheduleId = Long.valueOf(this.getManagedBean().getReportName());
		scheduleIds.add(scheduleId);
		log.info("The schedule is ==> " + scheduleId);
		biddingSO.updateBiddingListToCache(scheduleIds);
	}
}
