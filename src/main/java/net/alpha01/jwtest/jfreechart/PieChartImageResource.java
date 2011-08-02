package net.alpha01.jwtest.jfreechart;

import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

public class PieChartImageResource extends DynamicImageResource{

	private static final long serialVersionUID = 6297733885226499779L;
	private JFreeChart chart;
	private int height,width;
	
	public PieChartImageResource(String title, HashMap<String,BigDecimal> values,HashMap<String,Paint> colors,int width, int height){
		this.setHeight(height);
		this.setWidth(width);
		final DefaultPieDataset dataset = new DefaultPieDataset();
		Iterator<Entry<String,BigDecimal>> itv = values.entrySet().iterator();
		
		while (itv.hasNext()){
			Entry<String,BigDecimal> entry=itv.next();
			dataset.setValue(entry.getKey(), entry.getValue());
		}
		chart = ChartFactory.createPieChart3D(title, dataset, true, false, false);
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		Iterator<Entry<String,Paint>> itc = colors.entrySet().iterator();
		while (itc.hasNext()){
			Entry<String,Paint>entryColor=itc.next();
			plot.setSectionPaint(entryColor.getKey(), entryColor.getValue());
		}
		plot.setNoDataMessage("No Data");
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		plot.setBackgroundPaint(Color.WHITE);
	}
	
	@Override
	protected byte[] getImageData() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ChartUtilities.writeBufferedImageAsPNG(out, chart.createBufferedImage(width, height));
			out.close();
		} catch (IOException e) {
			return null;
		}
		return out.toByteArray();
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
