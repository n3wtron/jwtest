package net.alpha01.jwtest.panels;

import net.alpha01.jwtest.util.JWTestUtil;

public abstract class PanelLinkAjaxSecure extends PanelAjaxLink{
	private static final long serialVersionUID = 1L;
	private String[] roles;
	public PanelLinkAjaxSecure(String id, String msg, String... roles) {
		super(id, msg);
		this.roles=roles;
	}

	@Override
	public boolean isVisible() {
		return JWTestUtil.isAuthorized(roles).getObject();
	}

}
