package my.ep.web.backingbean.components.gmap;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.web.util.FacesScopes;

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
	private String latitude;
	private String longitude;
	
	public MapModel retSimpleModel(String mapLatitude, String mapLongitude, String locationName) {
		log.info("in retSimpleModel");
		log.info("latitude: " + latitude);
		log.info("longitude: " + longitude);
		simpleModel = new DefaultMapModel();
		double dlat = Double.valueOf(mapLatitude).doubleValue();
		double dlng = Double.valueOf(mapLongitude).doubleValue();

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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public void updateCoordinates() {
		System.out.println("updateCoordinates");
		FacesScopes fs = new FacesScopes();
	    String lat =  fs.getParameter("lat");
	    String lng = fs.getParameter("lng");
	    System.out.println("lat: " + lat);
	    System.out.println("lng: " + lng);
	}
	
}
