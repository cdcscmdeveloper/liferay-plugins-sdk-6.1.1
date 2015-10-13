package my.ep.web.backingbean.components.gmap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@ManagedBean(name="gmapBackingBean")
@ViewScoped
public class GMapBackingBean {

	private MapModel simpleModel;
	private boolean renderMap;
	
	public MapModel retSimpleModel(String mapLatitude, String mapLongitude) {
		
		simpleModel = new DefaultMapModel();
		double dlat = Double.valueOf(mapLatitude).doubleValue();
		double dlng = Double.valueOf(mapLongitude).doubleValue();
		System.out.println("dlat: " + dlat);
		System.out.println("dlng: " + dlng);
//		System.out.println("dlat: " + getLat());
//		System.out.println("dlng: " + getLng());
		
		LatLng coord1 = new LatLng(dlat, dlng);
		simpleModel.addOverlay(new Marker(coord1, "Konyaalti"));
		
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
