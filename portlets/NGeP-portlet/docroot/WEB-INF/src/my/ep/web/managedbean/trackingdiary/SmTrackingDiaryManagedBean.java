package my.ep.web.managedbean.trackingdiary;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.IViewSupplierItemSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.web.backingbean.cm.codification.UnspscCodeBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.util.EpPropertiesUtils;
import org.apache.commons.lang.StringUtils;
import my.ep.constant.MessageConst;
import javax.faces.context.FacesContext;
import my.ep.util.MessageUtils;

@ManagedBean(name = "smTrackingDiaryManagedBean")
@SessionScoped
public class SmTrackingDiaryManagedBean<PmTrackingDiary> extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}
