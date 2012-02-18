package net.alpha01.jwtest.pages.plan;

import java.math.BigInteger;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.exceptions.JWTestException;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.util.PlanUtil;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","MANAGER"})
public class DeletePlanPage extends LayoutPage {
	private static final long serialVersionUID = 1L;

	public DeletePlanPage(final PageParameters params){
		super(params);
		if (params.get("idPlan").isNull()){
			error("idPlan parameters not found");
			setResponsePage(ProjectPage.class);
			return;
		}
		SqlSessionMapper<TestCaseMapper> sesTestMapper=SqlConnection.getSessionMapper(TestCaseMapper.class);
		final Plan plan = sesTestMapper.getSqlSession().getMapper(PlanMapper.class).get(BigInteger.valueOf(params.get("idPlan").toLong()));
		sesTestMapper.close();
		
		add (new Label("planName",plan.getName()));
		Form<Project> delProjectForm = new Form<Project>("delForm");
		delProjectForm.add(new Button("YesBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				try {
					PlanUtil.deletePlan(plan);
					setResponsePage(ProjectPage.class);
				} catch (JWTestException e) {
					error(e.getMessage());
				}
			}
		});
		
		delProjectForm.add(new Button("NoBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(PlanPage.class,params);
			}
		});
		add(delProjectForm);
	}
}
