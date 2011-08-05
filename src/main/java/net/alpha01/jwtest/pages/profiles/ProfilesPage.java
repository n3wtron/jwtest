package net.alpha01.jwtest.pages.profiles;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.panels.profiles.ProfileTablePanel;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.image.ContextImage;

public class ProfilesPage extends LayoutPage {

	public ProfilesPage(){
		super();
		//LINK
		add(new BookmarkablePageLinkSecure<String>("addProfileLnk", AddProfilePage.class, Roles.ADMIN,"PROJECT_ADMIN").add(new ContextImage("addProfileImg", "images/add_profile.png")));
		add(new ProfileTablePanel("profileTable",JWTestSession.getProject(),JWTestUtil.isAuthorized(Roles.ADMIN,"PROJECT_ADMIN").getObject()));
	}
	
}
