/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.quatationtender;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.constant.sc.QTConstants;

/**
 *  View Quotation / Tender Details GeneralInfo Backing Bean.
 * @author Manga Garikapati.
 *
 */
@ManagedBean(name = "ViewQtDetailsGeneralInfoBB")
@RequestScoped
public class ViewQtDetailsGeneralInfoBackingBean{
	
	
	/**
	 * navigate to View QtDetails General Info portlet.
	 */
	public void  navigatetoViewQtDetailsGeneralInfo(String backUrl,ScQt scQt,boolean amounts){
		
	try {
		
		if(null!=scQt){
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		//Bug_30922
		qtBackingBean.getManagedBean().setQt(null);
		qtBackingBean.retrieveQuationTenderDetails(scQt.getQtId());
		//qtBackingBean.editQuationTenderDetails(qtBackingBean.getManagedBean().getQt(), true);
		qtBackingBean.getManagedBean().setViewOriginalUrl(backUrl);
		qtBackingBean.getManagedBean().setShowAndHideAmounts(amounts);
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						QTConstants.VIEW_QTDETAILS_GENERAL_INFO);
	
	
	} finally{
		
	}
	}
	
	
	
	/**
	 * navigate to original page
	 * @return navigation to original page.
	 */
	public String navigateToCurrentPage(){
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		return qtBackingBean.getManagedBean().getViewOriginalUrl();
	}
}
