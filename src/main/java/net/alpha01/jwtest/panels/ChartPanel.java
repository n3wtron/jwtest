package net.alpha01.jwtest.panels;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.DynamicImageResource;

public class ChartPanel extends Panel{
	private static final long serialVersionUID = 1L;

	public ChartPanel(String id,DynamicImageResource resource) {
		super(id);
		add( new Image("img",resource));
	}

}
