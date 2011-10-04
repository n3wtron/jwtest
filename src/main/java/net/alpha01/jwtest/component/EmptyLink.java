package net.alpha01.jwtest.component;

import org.apache.wicket.markup.html.link.Link;

public class EmptyLink<T> extends Link<T>{

	private static final long serialVersionUID = 1L;

	public EmptyLink(String id) {
		super(id);
	}

	@Override
	public void onClick() {
		
	}

}
