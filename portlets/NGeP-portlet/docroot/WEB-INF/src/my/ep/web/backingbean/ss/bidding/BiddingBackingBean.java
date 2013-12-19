package my.ep.web.backingbean.ss.bidding;

import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.bidding.BiddingManagedBean;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean(name = "biddingBB")
@RequestScoped
public class BiddingBackingBean extends BaseBackingBean<BiddingManagedBean> {
	private CartesianChartModel linearModel;
	
	public BiddingBackingBean() {
		createLinearModel();
	}
	
	private void createLinearModel() {
		linearModel = new CartesianChartModel();

		LineChartSeries series1 = new LineChartSeries();
		series1.setLabel("Bid Price");
		Calendar c1 = Calendar.getInstance();

		c1.add(Calendar.MINUTE, -30);
		series1.set(c1.getTime(), 100000);
		
		int random = (int) (Math.random() * 1000);
		random = random % 600;
		c1.add(Calendar.SECOND, random);
		series1.set(c1.getTime(), 98000);
		
		random = (int) (Math.random() * 1000);
		random = random % 600;
		c1.add(Calendar.SECOND, random);
		series1.set(c1.getTime(), 95000);
		
		random = (int) (Math.random() * 1000);
		random = random % 600;
		c1.add(Calendar.SECOND, random);
		series1.set(c1.getTime(), 90000);
		
		random = (int) (Math.random() * 1000);
		random = random % 600;
		c1.add(Calendar.SECOND, random);
		series1.set(c1.getTime(), 89000);
		
		linearModel.addSeries(series1);
	}

	/**
	 * @return the linearModel
	 */
	public CartesianChartModel getLinearModel() {
		return linearModel;
	}

	/**
	 * @param linearModel the linearModel to set
	 */
	public void setLinearModel(CartesianChartModel linearModel) {
		this.linearModel = linearModel;
	}
}
