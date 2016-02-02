package my.ep.web.managedbean.sm.blacklistdirectorowner;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.blacklistDirector.BlacklistDirectorReqDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "blacklistDirectorMB")
@SessionScoped
public class SmBlacklistDirectorManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4846065354985815446L;

	// Main
	private String targetPage;
	private String searchBy;

	// search by company
	private String companySearchBy;
	private String bussRegNo;
	private String companyName;
	private String epNo;

	// search by personnel
	private String directorSearchBy;
	private String name;
	private String identificationNo;

	private List<SmSupplier> companyList;
	private List<SmPersonnel> ownerDirectorList;

	private SmPersonnel directorOwner;
	private SmSupplier supplier;

	private IPagingDataModel<Object[]> supplierDataModel = new SupplierPagingDataModel();

	private class SupplierPagingDataModel extends PagingDataModel<Object[]> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3028215409193408953L;

		public SupplierPagingDataModel() {
			super("companyDtId");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
			reqDTO.setObjArrSearchBean(searchBean);

			try {
				return getSupplierResultBean(reqDTO);
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<Object[]> getSupplierResultBean(
				BlacklistDirectorReqDTO reqDTO) {
			reqDTO.setCompanyName(getCompanyName());
			reqDTO.setRegNo(getBussRegNo());
			reqDTO.setEpNo(getEpNo());
			return SmServiceLookup.getBlacklistDirectorSO()
					.retrieveSupplier(reqDTO).getObjectArrResultBean();
		}
	}

	private IPagingDataModel<Object[]> ownerDirectorDataModel = new OwnerDirectorPagingDataModel();

	private class OwnerDirectorPagingDataModel extends
			PagingDataModel<Object[]> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5742902894022742837L;

		public OwnerDirectorPagingDataModel() {
			super("directorDtId");
		}

		@Override
		protected ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			BlacklistDirectorReqDTO reqDTO = new BlacklistDirectorReqDTO();
			reqDTO.setObjArrSearchBean(searchBean);

			try {
				return getOwnerDirectorResultBean(reqDTO);
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<Object[]> getOwnerDirectorResultBean(
				BlacklistDirectorReqDTO reqDTO) {
			reqDTO.setDirectorIdNo(getIdentificationNo());
			reqDTO.setDirectorName(getName());
			return SmServiceLookup.getBlacklistDirectorSO()
					.retrieveDirectorOrOwner(reqDTO).getObjectArrResultBean();
		}
	}

	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	public String getBussRegNo() {
		return bussRegNo;
	}

	public void setBussRegNo(String bussRegNo) {
		this.bussRegNo = bussRegNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEpNo() {
		return epNo;
	}

	public void setEpNo(String epNo) {
		this.epNo = epNo;
	}

	public String getCompanySearchBy() {
		return companySearchBy;
	}

	public void setCompanySearchBy(String companySearchBy) {
		this.companySearchBy = companySearchBy;
	}

	public String getDirectorSearchBy() {
		return directorSearchBy;
	}

	public void setDirectorSearchBy(String directorSearchBy) {
		this.directorSearchBy = directorSearchBy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

	public IPagingDataModel<Object[]> getSupplierDataModel() {
		return supplierDataModel;
	}

	public void setSupplierDataModel(
			IPagingDataModel<Object[]> supplierDataModel) {
		this.supplierDataModel = supplierDataModel;
	}

	public IPagingDataModel<Object[]> getOwnerDirectorDataModel() {
		return ownerDirectorDataModel;
	}

	public void setOwnerDirectorDataModel(
			IPagingDataModel<Object[]> ownerDirectorDataModel) {
		this.ownerDirectorDataModel = ownerDirectorDataModel;
	}

	public List<SmSupplier> getCompanyList() {
		return companyList;
	}

	public void setCompanyList(List<SmSupplier> companyList) {
		this.companyList = companyList;
	}

	public List<SmPersonnel> getOwnerDirectorList() {
		return ownerDirectorList;
	}

	public void setOwnerDirectorList(List<SmPersonnel> ownerDirectorList) {
		this.ownerDirectorList = ownerDirectorList;
	}

	public SmPersonnel getDirectorOwner() {
		return directorOwner;
	}

	public void setDirectorOwner(SmPersonnel directorOwner) {
		this.directorOwner = directorOwner;
	}

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}
}
