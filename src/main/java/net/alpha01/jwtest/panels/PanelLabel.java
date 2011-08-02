package net.alpha01.jwtest.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class PanelLabel extends Panel{
	private static final long serialVersionUID = 1L;

	public PanelLabel(String id,String msg) {
		super(id);
		add(new Label("lbl",msg));
	}

}
