package my.ep.web.backingbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.trackingdiary.TrackingDiaryManagedBean;

@ManagedBean(name = "trackingDiaryBackingBean")
@RequestScoped
public class TrackingDiaryBackingBean extends
		BaseBackingBean<TrackingDiaryManagedBean> {

}

