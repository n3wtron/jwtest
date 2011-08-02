package net.alpha01.jwtest.pages.plan;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.panels.session.SessionTablePanel;
import net.alpha01.jwtest.panels.testcase.TestCasesTablePanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
public class PlanPage extends LayoutPage {
	public PlanPage(PageParameters params) {
		super(params);
		if (!params.containsKey("idPlan")) {
			setResponsePage(HomePage.class);
			error("Error idPlan parameter not found");
			return;
		}
		add(new BookmarkablePageLinkSecure<String>("updatePlanLnk",UpdatePlanPage.class,params,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("updatePlanImg", "images/update_plan.png")));
		add(new BookmarkablePageLinkSecure<String>("deletePlanLnk",DeletePlanPage.class,params,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("deletePlanImg", "images/delete_plan.png")));
		
		SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		PlanMapper planMapper = sesTestMapper.getSqlSession().getMapper(PlanMapper.class);
		Plan plan = planMapper.get(BigInteger.valueOf(params.getAsInteger("idPlan")));
		add(new Label("planName",plan.getName()));
		List<TestCase> tests = sesTestMapper.getMapper().getAllByPlan(plan.getId());
		add(new TestCasesTablePanel("testsTable", tests, 15, false));
		add(new SessionTablePanel("sessionsTable", plan.getId().intValue(), 15));
		sesTestMapper.close();
	}
}
