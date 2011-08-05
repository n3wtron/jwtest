package net.alpha01.jwtest.panels;

import java.io.File;

import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.PageParameters;

public class PanelLinkSecure extends PanelLink{
	private static final long serialVersionUID = 1L;
	private String[] roles;
	public PanelLinkSecure(String id, String msg, Class<? extends LayoutPage> pageClass, PageParameters params,String... roles) {
		super(id, msg, pageClass, params);
		this.roles=roles;
	}

	public PanelLinkSecure(String id, String msg, File file, String fileName,String... roles) {
		super(id, msg, file, fileName);
		this.roles=roles;
	}

	@Override
	public boolean isVisible() {
		return JWTestUtil.isAuthorized(roles).getObject();
	}

}
