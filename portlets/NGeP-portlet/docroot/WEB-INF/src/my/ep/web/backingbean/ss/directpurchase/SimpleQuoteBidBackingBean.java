package my.ep.web.backingbean.ss.directpurchase;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.TreeNode;

import my.ep.biz.service.ss.directpurchase.IQuoteBidSO;
import my.ep.dto.ss.directpurchase.QuoteBidReqDTO;
import my.ep.dto.ss.directpurchase.QuoteBidResDTO;
import my.ep.persistence.entity.ss.directpurchase.ScBusinessNatureQuoteBidCriteria;
import my.ep.persistence.entity.ss.directpurchase.ScQBZone;
import my.ep.persistence.entity.ss.directpurchase.ScQBZoneLocation;
import my.ep.persistence.entity.ss.directpurchase.ScRequestListItem;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SourcingConst;
import my.ep.web.managedbean.ss.directpurchase.SimpleQuoteBidManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "simpleQuoteBidBackingBean")
@SessionScoped
public class SimpleQuoteBidBackingBean extends BaseBackingBean<SimpleQuoteBidManagedBean> {
	public SimpleQuoteBidBackingBean() {
		super();
	}
	public void saveQuoteBid(String action){
		QuoteBidReqDTO	reqDTO	=	new	QuoteBidReqDTO();
		reqDTO.setQuoteBidDtl(this.getManagedBean().getScQuoteBid());
		IQuoteBidSO quoteBidSO = (IQuoteBidSO) BusinessDelegate.getInstance().getServiceObject(SourcingConst.SOURCING_MODULE, IQuoteBidSO.class);
		if("saveQuote".equalsIgnoreCase(action)){
			reqDTO.getQuoteBidDtl().setIsQuote(1);
			QuoteBidResDTO	resDTO	=	quoteBidSO.saveQuoteBidDtl(reqDTO);	
		}
		
	}
	public void	updateZoneLocation(){
		if(getManagedBean().getScQuoteBid().getScQBZones() != null){
			getManagedBean().getScQuoteBid().getScQBZones().add(createScQBZone());
		}else{
			List<ScQBZone> scQBZones	=	new ArrayList<ScQBZone>();
			scQBZones.add(createScQBZone());
			getManagedBean().getScQuoteBid().setScQBZones(scQBZones);
		}
	}
	public void	addBusinessNature(){
		ScBusinessNatureQuoteBidCriteria	businessNature	=	new ScBusinessNatureQuoteBidCriteria();
		getManagedBean().getBusinessNatureCriterias().add(businessNature);
	}
	public void	addItem(){
		ScRequestListItem	item	=	new ScRequestListItem();
		getManagedBean().getQuoteBidItems().add(item);
	}
	private	ScQBZone	createScQBZone(){
		ScQBZone	result	=	new ScQBZone();
		result.setZoneName(getManagedBean().getZoneName());
		List<ScQBZoneLocation> zoneLocations	=	new ArrayList<ScQBZoneLocation>();
		if(getManagedBean().getSelectedNodes() != null){
			for(TreeNode treeNode: getManagedBean().getSelectedNodes()){
				LocationDTO locationDTO	=	(LocationDTO)treeNode.getData();
				if(locationDTO.getState() != null){
					zoneLocations.add(new ScQBZoneLocation(locationDTO.getState()));
				}
				if(locationDTO.getDistrict() != null){
					zoneLocations.add(new ScQBZoneLocation(locationDTO.getDistrict()));
				}
				
			}
		}
		result.setScQBZoneLocations(zoneLocations);
		//Clear data
		getManagedBean().setZoneName("");
		return result;
	}
}
