package my.ep.web.backingbean.components.gmap;

import java.io.Serializable;
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
public class GMapBackingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GMapBackingBean.class);
	
	private MapModel simpleModel;
	private boolean renderMap;
	private String latitude;
	private String longitude;
	
//	public MapModel retSimpleModel(String mapLatitude, String mapLongitude, String locationName) {
	public MapModel retSimpleModel(String locationName) {
		log.info("in retSimpleModel");
		log.info("latitude: " + this.getLatitude());
		log.info("longitude: " + this.getLongitude());
		simpleModel = new DefaultMapModel();
		double dlat = Double.valueOf(this.getLatitude()).doubleValue();
		double dlng = Double.valueOf(this.getLongitude()).doubleValue();

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
	    this.setLatitude(lat);
	    this.setLongitude(lng);
	    log.info("lat: " + this.getLatitude());
	    log.info("lng: " + this.getLongitude());
	}
	
}
