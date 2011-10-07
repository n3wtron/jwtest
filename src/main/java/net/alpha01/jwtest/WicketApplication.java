package net.alpha01.jwtest;

import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LoginPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.requirement.RequirementPage;
import net.alpha01.jwtest.pages.result.ResultPage;
import net.alpha01.jwtest.pages.session.SessionsPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

public class WicketApplication extends AuthenticatedWebApplication {

	public WicketApplication() {
	}

	@Override
	protected void init() {
		super.init();
		mountPage("/home", HomePage.class);
		mountPage("/project", ProjectPage.class);
		mountPage("/requirement", RequirementPage.class);
		mountPage("/test", TestCasePage.class);
		mountPage("/sessions", SessionsPage.class);
		mountPage("/result", ResultPage.class);
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
