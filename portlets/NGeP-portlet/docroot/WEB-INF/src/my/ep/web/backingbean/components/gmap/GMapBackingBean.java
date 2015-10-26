package my.ep.web.backingbean.components.gmap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@ManagedBean(name="gmapBackingBean")
@ViewScoped
public class GMapBackingBean {

	private static Logger log = Logger.getLogger(GMapBackingBean.class);
	
	private MapModel simpleModel;
	private boolean renderMap;
	
	public MapModel retSimpleModel(String mapLatitude, String mapLongitude, String locationName) {
		
		simpleModel = new DefaultMapModel();
		double dlat = Double.valueOf(mapLatitude).doubleValue();
		double dlng = Double.valueOf(mapLongitude).doubleValue();
		log.info("dlat: " + dlat);
		log.info("dlng: " + dlng);

		LatLng coord1 = new LatLng(dlat, dlng);
		simpleModel.addOverlay(new Marker(coord1, locationName));
		
		return simpleModel;
	}

	public MapModel getSimpleModel() {
		return simpleModel;
	}
	public void setSimpleModel(MapModel simpleModel) {
		this.simpleModel = simpleModel;
	}
	
	public boolean isRenderMap() {
		return renderMap;
	}

	public void setRenderMap(boolean renderMap) {
		this.renderMap = renderMap;
	}
}
