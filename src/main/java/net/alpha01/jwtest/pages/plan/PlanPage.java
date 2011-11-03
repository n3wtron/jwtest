package net.alpha01.jwtest.pages.plan;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.component.EmptyLink;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.panels.session.SessionTablePanel;
import net.alpha01.jwtest.panels.testcase.TestCasesTablePanel;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;

import com.google.code.jqwicket.ui.tabs.TabsOptions;
import com.google.code.jqwicket.ui.tabs.TabsWebMarkupContainer;

public class PlanPage extends LayoutPage {
	private static final long serialVersionUID = 1L;

	public PlanPage(PageParameters params) {
		super(params);
		if (params.get("idPlan").isNull()) {
			setResponsePage(HomePage.class);
			error("Error idPlan parameter not found");
			return;
		}
		SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		PlanMapper planMapper = sesTestMapper.getSqlSession().getMapper(PlanMapper.class);
		Plan plan = planMapper.get(BigInteger.valueOf(params.get("idPlan").toLong()));
		String planName=plan.getName();
		if (plan.getNew_version().equals(BigInteger.ZERO)) {
			add(new BookmarkablePageLinkSecure<String>("updatePlanLnk", UpdatePlanPage.class, params, Roles.ADMIN, "PROJECT_ADMIN", "MANAGER").add(new ContextImage("updatePlanImg", "images/update_plan.png")));
			add(new BookmarkablePageLinkSecure<String>("deletePlanLnk", DeletePlanPage.class, params, Roles.ADMIN, "PROJECT_ADMIN", "MANAGER").add(new ContextImage("deletePlanImg", "images/delete_plan.png")));
		} else {
			planName+=" ["+JWTestUtil.translate("obsolete", this)+"]";
			add((new EmptyLink<Void>("updatePlanLnk")).add(new ContextImage("updatePlanImg", "images/update_plan.png")).setVisible(false));
			add((new EmptyLink<Void>("deletePlanLnk")).add(new ContextImage("deletePlanImg", "images/delete_plan.png")).setVisible(false));
		}
		add(new Label("planName", planName));
		TabsOptions options=new TabsOptions().addCssResourceReferences(new CssResourceReference(ProjectPage.class, "tabs.css"));
		TabsWebMarkupContainer content = new TabsWebMarkupContainer("content",options);
		List<TestCase> tests = sesTestMapper.getMapper().getAllByPlan(plan.getId());
		content.add(new TestCasesTablePanel("testsTable", tests, 15, false));
		content.add(new SessionTablePanel("sessionsTable", plan.getId().intValue(), 15));
		add(content);
		sesTestMapper.close();
	}
}
