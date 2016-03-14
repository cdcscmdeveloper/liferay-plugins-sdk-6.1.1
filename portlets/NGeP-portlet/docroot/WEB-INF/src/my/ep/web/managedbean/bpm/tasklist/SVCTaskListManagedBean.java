package my.ep.web.managedbean.bpm.tasklist;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.sm.siteVisit.ISiteAssignSO;
import my.ep.dto.sm.siteVisit.SiteAssignReqDTO;
import my.ep.dto.sm.siteVisit.SiteAssignResDTO;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.bpm.tasklist.SVCTaskListBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "svcTasklistMB")
@SessionScoped
public class SVCTaskListManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private String applNo;
	private List<PmState> stateList;
	private List<PmDistrict> districtList;
	private long stateId;
	private long districtId;
	private String supplierName;
	private String epNo;
	private String siteVisitType;
	
	private IPagingDataModel<SmAppl> searchDataModel = new SearchPagingDataModel();


	private class SearchPagingDataModel extends
	PagingDataModel<SmAppl> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new search paging data model.
		 */
		public SearchPagingDataModel() {
			super("rsDt", false);
			// TODO Auto-generated constructor stub
		}

		/**
		 * page Change Listener.
		 * 
		 * @param searchBean
		 *            (ISearchBean<VCmSupplierItem> searchBean(mandatory))
		 * @return ISearchBean<VCmSupplierItem>
		 */
		@Override
		protected ISearchResultBean<SmAppl> pageChangeListener(
				ISearchBean<SmAppl> searchBean) {
			SVCTaskListBackingBean bb = new SVCTaskListBackingBean();
			System.out.println("getApplNo:"+bb.getManagedBean().getApplNo());
			System.out.println("getStateId:"+bb.getManagedBean().getStateId());
			System.out.println("getDistrictId:"+bb.getManagedBean().getDistrictId());
			System.out.println("getSupplierName:"+bb.getManagedBean().getSupplierName());
			System.out.println("getSitevisitType:"+bb.getManagedBean().getSiteVisitType());
			if (bb.getManagedBean().getDistrictId()==0 && bb.getManagedBean().getStateId()==0 && StringUtils.isBlank(bb.getManagedBean().getApplNo()) && 
					StringUtils.isBlank(bb.getManagedBean().getSupplierName()) && StringUtils.isBlank(bb.getManagedBean().getEpNo())) {
				System.out.println("******SearchResultBean******");
				return new SearchResultBean();
			}

			ISiteAssignSO siteAssignSO = (ISiteAssignSO) BusinessDelegate.getInstance().getServiceObject("sm", ISiteAssignSO.class);

			try {
				System.out.println("********************************");
				SiteAssignReqDTO reqDTO = new SiteAssignReqDTO();
				reqDTO.setApplNo(bb.getManagedBean().getApplNo());
				reqDTO.setSupplierName(bb.getManagedBean().getSupplierName());
				reqDTO.setEpNo(bb.getManagedBean().getEpNo());
				reqDTO.setSiteVisitStatuses(getSiteVisitStatusesByType(bb.getManagedBean().getSiteVisitType()));

				if(bb.getManagedBean().getDistrictId() >0){
					reqDTO.setDistrictId(bb.getManagedBean().getDistrictId());
				}

				if(bb.getManagedBean().getDistrictId() ==0){
					if(bb.getManagedBean().getStateId() >0){
						reqDTO.setStateId(bb.getManagedBean().getStateId());
					}
				}
				
				reqDTO.setSearchBean(searchBean);
				

				SiteAssignResDTO resDTO = siteAssignSO.retrievesSiteVisitTasks(reqDTO);


				return resDTO.getSmApplResultBean();
			} catch (Exception e) {
				e.printStackTrace();
				return new SearchResultBean();
			} finally {
				siteAssignSO = null;
			}
		}

	}
	
	private List<Long> getSiteVisitStatusesByType(String svType){
		List<Long> statusLs = new ArrayList<Long>();
		
		 switch(svType)
	      {
	         case SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_ALL :
	        	statusLs.add(my.ep.constant.sm.SupplierManagementConst.PM_STATUS_SV_ASSIGNMENT_AFTER_APPROVAL);
	 			statusLs.add(my.ep.constant.sm.SupplierManagementConst.PM_STATUS_SV_ASSIGNMENT_BEFORE_APPROVAL); 
	            break;
	         case SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_BEFORE :
	        	 statusLs.add(my.ep.constant.sm.SupplierManagementConst.PM_STATUS_SV_ASSIGNMENT_BEFORE_APPROVAL);
	        	 break;
	         case SupplierManagementConst.SITE_VISIT_ASSIGNMENT_TYPE_AFTER :
	        	 statusLs.add(my.ep.constant.sm.SupplierManagementConst.PM_STATUS_SV_ASSIGNMENT_AFTER_APPROVAL);
	            break;
	      }

		 return statusLs;
	}
	
	
	public IPagingDataModel<SmAppl> getSearchDataModel() {
		return searchDataModel;
	}
	public void setSearchDataModel(IPagingDataModel<SmAppl> searchDataModel) {
		this.searchDataModel = searchDataModel;
	}
	public String getApplNo() {
		return applNo;
	}
	public void setApplNo(String applNo) {
		this.applNo = applNo;
	}
	public List<PmState> getStateList() {
		return stateList;
	}
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}
	public List<PmDistrict> getDistrictList() {
		if(districtList==null)
			districtList = new ArrayList<PmDistrict>();
		return districtList;
	}
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}
	public long getStateId() {
		return stateId;
	}
	public void setStateId(long stateId) {
		this.stateId = stateId;
	}
	public long getDistrictId() {
		return districtId;
	}
	public void setDistrictId(long districtId) {
		this.districtId = districtId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getEpNo() {
		return epNo;
	}
	public void setEpNo(String epNo) {
		this.epNo = epNo;
	}
	public String getSiteVisitType() {
		return siteVisitType;
	}
	public void setSiteVisitType(String siteVisitType) {
		this.siteVisitType = siteVisitType;
	}
	
	
	
	
	
	
	
}
