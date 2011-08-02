package net.alpha01.jwtest.pages.testcase;

import java.math.BigInteger;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.requirement.RequirementPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","MANAGER"})
public class DeleteTestCasePage extends LayoutPage {
	private TestCase test;
	public DeleteTestCasePage(final PageParameters params){
		super(params);
		if (!params.containsKey("idTest")) {
			error("Parametro idTest non trovato");
			setResponsePage(ProjectPage.class);
		}
		SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		test= sesTestMapper.getMapper().get(BigInteger.valueOf(params.getInt("idTest")));
		sesTestMapper.close();
		add (new Label("testcaseName",test.getName()));
		Form<Project> delTestcaseForm = new Form<Project>("delTestcaseForm");
		delTestcaseForm.add(new Button("YesBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				 SqlSessionMapper<TestCaseMapper> sesDelTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
				if (sesDelTestMapper.getMapper().delete(test).equals(1)){
					info("Testcase deleted");
					sesDelTestMapper.commit();
					sesDelTestMapper.close();
					PageParameters reqParams=new PageParameters();
					reqParams.put("idReq", test.getId_requirement());
					setResponsePage(RequirementPage.class,reqParams);
				}else{
					sesDelTestMapper.rollback();
					sesDelTestMapper.close();
					error("ERROR: Testcase not deleted SQL ERROR");
				}
			}
		});
		
		delTestcaseForm.add(new Button("NoBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(TestCasePage.class,params);
			}
		});
		add(delTestcaseForm);
	}
}
