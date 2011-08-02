package net.alpha01.jwtest.panels;

import net.alpha01.jwtest.dot.AbstractGraphResource;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class DotPanel extends Panel{
	private static final long serialVersionUID = 1L;

	public DotPanel(String id,final AbstractGraphResource resource) {
		super(id);
		Image dotImg = new Image("dotImg",resource){
			private static final long serialVersionUID = 1L;
			protected void onAfterRender() {
				super.onAfterRender();
				resource.getTmpFile().delete();
			};
		};
		dotImg.add(new AttributeModifier("usemap",new Model<String>(resource.getUseMapName())));
		add(dotImg);
		add (new Label("dotMap",resource.getMap()).setEscapeModelStrings(false));
	}

}
