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

import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.constant.sc.QTConstants;

@ManagedBean(name = "ViewSPBackingBean")
@RequestScoped
public class ViewSupplierProposalBackingBean{
	
	/**
	 * navigate to View Supplier Proposal portlet.
	 */
	public void navigatetoViewSupplierProposal(String backUrl, Long proposalId) {
		try {
			SPDetailBackingBean spDtlBB = new SPDetailBackingBean();
			spDtlBB.retriveSupplierProposalDtl(proposalId, backUrl, true, true);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.NAV_TO_PROPOSAL_MAIN_PAGE);	
		} finally {

		}
	}
		
	/**
	 * navigate to original page
	 * @return navigation to original page.
	 */
	public String navigateToCurrentPage() {
		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();

		return qtBackingBean.getManagedBean().getViewOriginalUrl();
	}
}