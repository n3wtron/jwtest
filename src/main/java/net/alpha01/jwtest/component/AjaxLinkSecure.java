package net.alpha01.jwtest.component;

import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

public abstract class AjaxLinkSecure<T> extends AjaxLink<T>{
	private static final long serialVersionUID = -5051769168937117733L;
	private String []roles;
	private Boolean forceVisibilty;
	
	public AjaxLinkSecure(String id, IModel<T> model,String ...roles) {
		super(id, model);
		this.roles=roles;
	}
	
	public AjaxLinkSecure(String id,String ...roles) {
		super(id);
		this.roles=roles;
	}
	
	@Override
	public boolean isVisible() {
		if (forceVisibilty != null) {
			return JWTestUtil.isAuthorized(roles).getObject() && forceVisibilty;
		} else {
			return JWTestUtil.isAuthorized(roles).getObject();
		}
	}
	
	@Override
	public boolean isEnabled() {
		return JWTestUtil.isAuthorized(roles).getObject();
	}

	public void forceVisible(boolean b) {
		forceVisibilty=new Boolean(b);
	}
	

}
