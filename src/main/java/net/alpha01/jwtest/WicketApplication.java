package net.alpha01.jwtest;

import net.alpha01.jwtest.pages.ExportPage;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.LoginPage;
import net.alpha01.jwtest.pages.dot.RequirementDotPage;
import net.alpha01.jwtest.pages.dot.TestCaseDotPage;
import net.alpha01.jwtest.pages.plan.AddPlanPage;
import net.alpha01.jwtest.pages.plan.DeletePlanPage;
import net.alpha01.jwtest.pages.plan.PlanPage;
import net.alpha01.jwtest.pages.plan.UpdatePlanPage;
import net.alpha01.jwtest.pages.profiles.AddProfilePage;
import net.alpha01.jwtest.pages.profiles.ProfilesPage;
import net.alpha01.jwtest.pages.profiles.UpdateProfilePage;
import net.alpha01.jwtest.pages.project.AddProjectPage;
import net.alpha01.jwtest.pages.project.DeleteProjectPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.project.ProjectRolesPage;
import net.alpha01.jwtest.pages.project.ProjectVersionPage;
import net.alpha01.jwtest.pages.project.UpdateProjectPage;
import net.alpha01.jwtest.pages.requirement.AddRequirementPage;
import net.alpha01.jwtest.pages.requirement.DeleteRequirementPage;
import net.alpha01.jwtest.pages.requirement.RequirementPage;
import net.alpha01.jwtest.pages.requirement.UpdateRequirementPage;
import net.alpha01.jwtest.pages.result.AddResultPage;
import net.alpha01.jwtest.pages.result.ResultPage;
import net.alpha01.jwtest.pages.session.DeleteSessionPage;
import net.alpha01.jwtest.pages.session.SessionsPage;
import net.alpha01.jwtest.pages.session.StartSessionPage;
import net.alpha01.jwtest.pages.step.AddStepPage;
import net.alpha01.jwtest.pages.step.UpdateStepPage;
import net.alpha01.jwtest.pages.testcase.AddTestCasePage;
import net.alpha01.jwtest.pages.testcase.DeleteTestCasePage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;
import net.alpha01.jwtest.pages.testcase.UpdateTestCasePage;
import net.alpha01.jwtest.pages.user.AddUserPage;
import net.alpha01.jwtest.pages.user.DeleteUserPage;
import net.alpha01.jwtest.pages.user.UpdateUserPage;
import net.alpha01.jwtest.pages.user.UsersPage;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import com.google.code.jqwicket.JQComponentOnBeforeRenderListener;
import com.google.code.jqwicket.JQContributionConfig;

public class WicketApplication extends AuthenticatedWebApplication {

	public WicketApplication() {
	}

	@Override
	protected void init() {
		super.getComponentPreOnBeforeRenderListeners().add(new JQComponentOnBeforeRenderListener(new JQContributionConfig().withDefaultJQueryUi()));
		mountPage("/TestCaseDotPage",TestCaseDotPage.class);
		mountPage("/RequirementDotPage",RequirementDotPage.class);
		mountPage("/RequirementPage",RequirementPage.class);
		mountPage("/DeleteRequirementPage",DeleteRequirementPage.class);
		mountPage("/UpdateRequirementPage",UpdateRequirementPage.class);
		mountPage("/AddRequirementPage",AddRequirementPage.class);
		mountPage("/DeletePlanPage",DeletePlanPage.class);
		mountPage("/UpdatePlanPage",UpdatePlanPage.class);
		mountPage("/AddPlanPage",AddPlanPage.class);
		mountPage("/PlanPage",PlanPage.class);
		mountPage("/AddUserPage",AddUserPage.class);
		mountPage("/UpdateUserPage",UpdateUserPage.class);
		mountPage("/DeleteUserPage",DeleteUserPage.class);
		mountPage("/UsersPage",UsersPage.class);
		mountPage("/LayoutPage",LayoutPage.class);
		mountPage("/ProjectPage",ProjectPage.class);
		mountPage("/ProjectRolesPage",ProjectRolesPage.class);
		mountPage("/UpdateProjectPage",UpdateProjectPage.class);
		mountPage("/DeleteProjectPage",DeleteProjectPage.class);
		mountPage("/AddProjectPage",AddProjectPage.class);
		mountPage("/ProjectVersionPage",ProjectVersionPage.class);
		mountPage("/StartSessionPage",StartSessionPage.class);
		mountPage("/DeleteSessionPage",DeleteSessionPage.class);
		mountPage("/SessionsPage",SessionsPage.class);
		mountPage("/AddResultPage",AddResultPage.class);
		mountPage("/ResultPage",ResultPage.class);
		mountPage("/ExportPage",ExportPage.class);
		mountPage("/AddProfilePage",AddProfilePage.class);
		mountPage("/ProfilesPage",ProfilesPage.class);
		mountPage("/UpdateProfilePage",UpdateProfilePage.class);
		mountPage("/HomePage",HomePage.class);
		mountPage("/LoginPage",LoginPage.class);
		mountPage("/AddTestCasePage",AddTestCasePage.class);
		mountPage("/UpdateTestCasePage",UpdateTestCasePage.class);
		mountPage("/DeleteTestCasePage",DeleteTestCasePage.class);
		mountPage("/TestCasePage",TestCasePage.class);
		mountPage("/AddStepPage",AddStepPage.class);
		mountPage("/UpdateStepPage",UpdateStepPage.class);
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
