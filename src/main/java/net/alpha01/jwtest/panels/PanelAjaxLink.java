package net.alpha01.jwtest.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class PanelAjaxLink extends Panel{
	private static final long serialVersionUID = 1L;
	
	protected abstract void onAjaxClick(AjaxRequestTarget target); 
	
	public PanelAjaxLink(String id, String msg) {
		super(id);
		AjaxLink<String> lnk = new AjaxLink<String>("lnk") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onAjaxClick(target);
			}
		};
		lnk.add(new Label("lbl",msg));
		add(lnk);
		
	}

}
