package net.alpha01.jwtest.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class HtmlLabel extends Label{
	private static final long serialVersionUID = -8070813620989827666L;

	public HtmlLabel(String id, IModel<?> model) {
		super(id, model);
		setEscapeModelStrings(false);
	}
	
	public HtmlLabel(String id) {
		super(id);
		setEscapeModelStrings(false);
	}

	public HtmlLabel(String id,String value) {
		super(id,value);
		setEscapeModelStrings(false);
	}

}
