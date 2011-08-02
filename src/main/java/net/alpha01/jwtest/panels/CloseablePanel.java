package net.alpha01.jwtest.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public abstract class CloseablePanel extends Panel{
	private static final long serialVersionUID = 1L;
	private Panel contentPanel;
	private Model<Boolean> openedModel=new Model<Boolean>();
	public CloseablePanel(String id,String title, boolean opened) {
		super(id);
		this.openedModel.setObject(opened);
		this.contentPanel=getContentPanel("contentPanel");
		contentPanel.setOutputMarkupId(true);
		add(new Label("title", title));
		add(contentPanel);
		add(new AjaxLink<String>("lnk") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				openedModel.setObject(!openedModel.getObject().booleanValue());
				refreshPanel();
				target.addComponent(contentPanel);
			}
		});
		refreshPanel();
	}
	
	private void refreshPanel(){
		if (openedModel.getObject()){
			contentPanel.add(new AttributeModifier("style",true,new Model<String>("") ));
		}else{
			contentPanel.add(new AttributeModifier("style",true,new Model<String>("display:none;") ));
		}
	}
	
	public abstract Panel getContentPanel(String id);

}
