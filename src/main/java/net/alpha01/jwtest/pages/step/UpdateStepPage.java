package net.alpha01.jwtest.pages.step;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.exceptions.JWTestException;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;
import net.alpha01.jwtest.util.TestCaseUtil;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@AuthorizeInstantiation(value = { Roles.ADMIN, "PROJECT_ADMIN", "MANAGER" })
public class UpdateStepPage extends LayoutPage {
	private static final long serialVersionUID = 1L;
	private Step step;

	public UpdateStepPage(final PageParameters params) {
		super(params);
		if (params.get("idStep").isNull()) {
			error("Parameter idStep not found");
			setResponsePage(ProjectPage.class);
		}
		SqlSessionMapper<StepMapper> sesMapper = SqlConnection.getSessionMapper(StepMapper.class);
		step = sesMapper.getMapper().get(BigInteger.valueOf(params.get("idStep").toInt()));

		TestCaseMapper testMapper = sesMapper.getSqlSession().getMapper(TestCaseMapper.class);
		TestCase test = testMapper.get(step.getId_testcase());
		PageParameters testCaseParam = new PageParameters();
		testCaseParam.add("idTest", test.getId());
		BookmarkablePageLink<String> testCaseLnk = new BookmarkablePageLink<String>("testCaseLnk", TestCasePage.class, testCaseParam);
		testCaseLnk.add(new Label("testCaseName", test.getName()));
		add(testCaseLnk);

		// Update FORM
		Form<Step> updForm = new Form<Step>("updForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				SqlSessionMapper<StepMapper> sesAddMapper = SqlConnection.getSessionMapper(StepMapper.class);
				//check the testCase to check if it's necessary to create a new testcase version
				TestCase test = sesAddMapper.getSqlSession().getMapper(TestCaseMapper.class).get(step.getId_testcase());
				if (TestCaseUtil.isUsedTestCase(test)){
					List<TestCase> testDeps = sesAddMapper.getSqlSession().getMapper(TestCaseMapper.class).getDependencies(test.getId());
					try {
						HashMap<BigInteger, BigInteger> stepsMod=TestCaseUtil.updateTestCase(test, testDeps);
						//change testcase id association of step
						step.setId_testcase(test.getId());
						//change id of this step to new step version id
						step.setId(stepsMod.get(step.getId()));
					} catch (JWTestException e) {
						error(e.getMessage());
						return;
					}
				}
				try {
					if (sesAddMapper.getMapper().update(step).equals(1)) {
						sesAddMapper.commit();
						sesAddMapper.close();
						PageParameters testParams = new PageParameters();
						testParams.add("idTest", step.getId_testcase());
						setResponsePage(TestCasePage.class, testParams);
						info("Step Updated");
					} else {
						sesAddMapper.rollback();
						sesAddMapper.close();
						error("ERROR: Step not updated");
					}
				} catch (PersistenceException e) {
					sesAddMapper.rollback();
					sesAddMapper.close();
					error("ERROR: " + e.getMessage());
				}
			}
		};
		updForm.add(new TextField<String>("sequenceFld", new PropertyModel<String>(step, "sequence")));
		updForm.add(new TextArea<String>("descriptionFld", new PropertyModel<String>(step, "description")));
		updForm.add(new TextArea<String>("expectedResultFld", new PropertyModel<String>(step, "expected_result")));
		updForm.add(new TextArea<String>("failedResultFld", new PropertyModel<String>(step, "failed_result")));
		add(updForm);
		sesMapper.close();
	}

}
