package net.alpha01.jwtest;

import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LoginPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.requirement.RequirementPage;
import net.alpha01.jwtest.pages.result.ResultPage;
import net.alpha01.jwtest.pages.session.SessionsPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;

import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;

public class WicketApplication extends AuthenticatedWebApplication {

	public WicketApplication() {
	}

	@Override
	protected void init() {
		super.init();
		mountBookmarkablePage("/home", HomePage.class);
		mountBookmarkablePage("/project", ProjectPage.class);
		mountBookmarkablePage("/requirement", RequirementPage.class);
		mountBookmarkablePage("/test", TestCasePage.class);
		mountBookmarkablePage("/sessions", SessionsPage.class);
		mountBookmarkablePage("/result", ResultPage.class);
		getMarkupSettings().setAutomaticLinking(true);
		getDebugSettings().setAjaxDebugModeEnabled(false);
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new JWTestSession(request);
	}

	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return LoginPage.class;
	}

	@Override
	protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
		return JWTestSession.class;
	}

}
