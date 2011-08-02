package net.alpha01.jwtest.pages.testcase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.Dependency;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.panels.attachment.AddAttachmentPanel;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","MANAGER"})
public class UpdateTestCasePage extends LayoutPage {
	private TestCase testCase = new TestCase();
	private ArrayList<TestCase> dependencies=new ArrayList<TestCase>();

	public UpdateTestCasePage(final PageParameters params) {
		super(params);
		if (!params.containsKey("idTest")) {
			error("Parametro idTest non trovato");
			setResponsePage(ProjectPage.class);
		}
		TestCaseMapper testMapper = SqlConnection.getMapper(TestCaseMapper.class);
		testCase = testMapper.get(BigInteger.valueOf(params.getInt("idTest")));
		dependencies=(ArrayList<TestCase>) testMapper.getDependencies(testCase.getId());

		BookmarkablePageLink<Void> testcaseLnk=new BookmarkablePageLink<Void>("testcaseLnk", TestCasePage.class, new PageParameters("idTest="+testCase.getId()));
		testcaseLnk.add(new Label("testcaseName", testCase.getName()));
		add(testcaseLnk);
		
		Form<TestCase> updForm = new Form<TestCase>("updForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				SqlSessionMapper<TestCaseMapper> sesMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
				try {
					if (sesMapper.getMapper().update(testCase).equals(1)) {
						info("TestCase updated");
						sesMapper.getMapper().deleteDependencies(testCase.getId());
						Iterator<TestCase> itd = dependencies.iterator();
						while (itd.hasNext()){
							TestCase dep=itd.next();
							sesMapper.getMapper().addDependency(new Dependency(testCase.getId(), dep.getId()));
						}
						sesMapper.commit();
						sesMapper.close();
						setResponsePage(TestCasePage.class, params);
					} else {
						sesMapper.rollback();
						sesMapper.close();
						error("ERROR: TestCase not added");
					}
				} catch (PersistenceException e) {
					e.printStackTrace();
					error("ERROR: TestCase chiave duplicata");
				}
			}
		};
		updForm.add(new TextField<String>("nameFld", new PropertyModel<String>(testCase, "name")));
		updForm.add(new TextArea<String>("descriptionFld", new PropertyModel<String>(testCase, "description")));
		updForm.add(new TextArea<String>("expectedResultFld", new PropertyModel<String>(testCase, "expected_result")));
		
		SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		
		//Dependency
		List<TestCase> allTests=sesTestMapper.getMapper().getAll(new TestCaseSelectSort(testCase.getId_requirement(), "name", true));
		//delete itself from dependencies list
		allTests.remove(testCase);
		sesTestMapper.close();
		
		updForm.add(new ListMultipleChoice<TestCase>("dependencyFld",new Model<ArrayList<TestCase>>(dependencies),allTests));

		add(updForm);
		add(new AddAttachmentPanel("attachmentPanel",testCase));
	}

}
