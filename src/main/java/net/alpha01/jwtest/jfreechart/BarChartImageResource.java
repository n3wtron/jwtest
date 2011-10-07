package net.alpha01.jwtest.jfreechart;

import java.awt.Color;
import java.awt.Paint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.wicket.request.resource.DynamicImageResource;
import org.apache.wicket.request.resource.IResource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartImageResource extends DynamicImageResource {

	private static final long serialVersionUID = 6297733885226499779L;
	private JFreeChart chart;
	private int height, width;

	public BarChartImageResource(String title, HashMap<String, BigDecimal> values, String categoryAxisLabel, String valueAxisLabel, int width, int height) {
		this.setHeight(height);
		this.setWidth(width);
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Iterator<Entry<String, BigDecimal>> itv = values.entrySet().iterator();
		while (itv.hasNext()) {
			Entry<String, BigDecimal> entry = itv.next();
			dataset.setValue(entry.getValue(), categoryAxisLabel, entry.getKey());
		}
		chart = ChartFactory.createBarChart3D(title, categoryAxisLabel, valueAxisLabel, dataset, PlotOrientation.VERTICAL, false, true, false);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer renderer = new BarRenderer3D() {
			private static final long serialVersionUID = 1L;

			@Override
			public Paint getItemPaint(int row, int column) {
				float value = dataset.getValue(row, column).floatValue();
				if (value < 50) {
					return Color.RED;
				} else {
					if (value == 100) {
						return Color.GREEN;
					} else {
						return Color.YELLOW;
					}
				}
			}
		};
		plot.setRenderer(renderer);
		plot.setForegroundAlpha(0.6f);
		plot.setBackgroundPaint(Color.WHITE);
	}

	@Override
	protected byte[] getImageData(IResource.Attributes attributes) {
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
