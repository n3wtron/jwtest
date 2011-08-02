package net.alpha01.jwtest.component;

import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class BookmarkablePageLinkSecure<T> extends BookmarkablePageLink<T> {
	private static final long serialVersionUID = 1L;
	private String[] roles;
	private Boolean forceVisibilty = null;

	@SuppressWarnings("unchecked")
	public BookmarkablePageLinkSecure(String id, @SuppressWarnings("rawtypes") Class pageClass, PageParameters parameters, String... roles) {
		super(id, pageClass, parameters);
		this.roles = roles;

	}

	@SuppressWarnings("unchecked")
	public BookmarkablePageLinkSecure(String id, @SuppressWarnings("rawtypes") Class pageClass, String... roles) {
		super(id, pageClass);
		this.roles = roles;
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
