package net.alpha01.jwtest.pages.step;

import java.math.BigInteger;

import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;
import net.alpha01.jwtest.panels.step.StepsTablePanel;

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
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","MANAGER"})
public class AddStepPage extends LayoutPage{
	private static final long serialVersionUID = 1L;
	private TestCase test;
	private Step step=new Step();
	public AddStepPage(final PageParameters params){
		super(params);
		if (params.get("idTest").isNull()){
			error("Parameter idTest not found");
			setResponsePage(ProjectPage.class);
		}
		SqlSessionMapper<TestCaseMapper> sesMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		test=sesMapper.getMapper().get(BigInteger.valueOf(params.get("idTest").toInt()));
		
		PageParameters testCaseParam = new PageParameters();
		testCaseParam.add("idTest", test.getId());
		BookmarkablePageLink<String> testCaseLnk = new BookmarkablePageLink<String>("testCaseLnk",TestCasePage.class,testCaseParam);
		testCaseLnk.add (new Label("testCaseName",test.getName()));
		add(testCaseLnk);
		
		//Add FORM
		Form<Step> addForm=new Form<Step>("addForm"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit() {
				step.setId_testcase(test.getId());
				SqlSessionMapper<StepMapper> sesAddMapper = SqlConnection.getSessionMapper(StepMapper.class);
				try{
				if(sesAddMapper.getMapper().add(step).equals(1)){
					sesAddMapper.commit();
					sesAddMapper.close();
					setResponsePage(AddStepPage.class,params);
					info("Step Added");
				}else{
					sesAddMapper.rollback();
					sesAddMapper.close();
					error("ERROR: Step not added");
				}
				}catch (PersistenceException e){
					sesAddMapper.rollback();
					sesAddMapper.close();
					error("ERROR: "+e.getMessage());
				}
			}
		};
		addForm.add(new TextField<String>("sequenceFld",new PropertyModel<String>(step, "sequence")));
		addForm.add(new TextArea<String>("descriptionFld",new PropertyModel<String>(step, "description")));
		addForm.add(new TextArea<String>("expectedResultFld",new PropertyModel<String>(step, "expected_result")));
		addForm.add(new TextArea<String>("failedResultFld",new PropertyModel<String>(step, "failed_result")));
		add(addForm);

		sesMapper.close();
		add(new StepsTablePanel("stepsTable", test.getId(),true));
	}

}
