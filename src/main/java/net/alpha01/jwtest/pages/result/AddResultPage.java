package net.alpha01.jwtest.pages.result;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Attachment;
import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.HtmlLabel;
import net.alpha01.jwtest.dao.ResultMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.exceptions.JWTestException;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.requirement.RequirementPage;
import net.alpha01.jwtest.pages.session.SessionsPage;
import net.alpha01.jwtest.panels.attachment.AddAttachmentPanel;
import net.alpha01.jwtest.panels.step.StepsTablePanel;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;

@AuthorizeInstantiation(value = { Roles.ADMIN, "PROJECT_ADMIN", "TESTER", "MANAGER" })
public class AddResultPage extends LayoutPage {
	private static final long serialVersionUID = 1L;
	private Result result = new Result();
	private Result parent;
	private BigInteger idTest;
	private FileUploadField attachmentFld;
	private Model<String> attachmentDescription=new Model<String>("");

	public AddResultPage(PageParameters params) {
		super(params);
		if (params.get("idTest").isNull() && params.get("idParent").isNull()) {
			error("parameter idTest not found");
			Logger.getLogger(getClass()).error("parameter idTest not found");
			setResponsePage(SessionsPage.class);
		}
		// retreive testCase information
		SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		if (!params.get("idParent").isNull()){
			ResultMapper resMapper = sesTestMapper.getSqlSession().getMapper(ResultMapper.class);
			parent=resMapper.get(BigInteger.valueOf(params.get("idParent").toLong()));
			idTest=parent.getTestCase().getId();
			result.setId_parent(parent.getId());
			result.setId_testcase(idTest);
			result.setId_session(parent.getSession().getId());
		}else{
			idTest=BigInteger.valueOf(params.get("idTest").toLong());
			result.setId_testcase(BigInteger.valueOf(params.get("idTest").toLong()));
			result.setId_session(getSession().getCurrentSession().getId());	
		}
		
		add(new Label("sessionName", getSession().getCurrentSession().toString()));
		final TestCase testCase = sesTestMapper.getMapper().get(result.getId_testcase());
		sesTestMapper.close();

		BookmarkablePageLink<String> reqLnk = new BookmarkablePageLink<String>("reqLnk", RequirementPage.class, new PageParameters().add("idReq" , testCase.getId_requirement()));
		reqLnk.add(new Label("reqName", testCase.getRequirement().getName()));
		add(reqLnk);

		add(new Label("testCaseName", testCase.getName()));
		add(new HtmlLabel("testCaseDescription", testCase.getDescription()));
		add(new HtmlLabel("testCaseExpectedResult", testCase.getExpected_result()));

		Form<String> addForm = new Form<String>("addForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<ResultMapper> sesResultMapper = SqlConnection.getSessionMapper(ResultMapper.class);
				try {
					if (sesResultMapper.getMapper().add(result).equals(1)) {
						info("Result added");
						//update number of recycles
						if (parent!=null){
							parent.setRecycles(parent.getRecycles().add(BigInteger.ONE));
							sesResultMapper.getMapper().update(parent);
						}
						sesResultMapper.commit();
						
						FileUpload fupload= attachmentFld.getFileUpload();
						if (fupload!=null){
							Attachment attachment=new Attachment(JWTestSession.getProject());
							attachment.setDescription(attachmentDescription.getObject());
							try {
								AddAttachmentPanel.uploadAttachment(fupload, attachment, result);
							} catch (JWTestException e) {
								Logger.getLogger(getClass()).error("add result attachment failure");
								error("add attachment error");
								return;
							}
						}
						
						TestCaseMapper testMapper = sesResultMapper.getSqlSession().getMapper(TestCaseMapper.class);
						List<TestCase> testcases = testMapper.getAllUncheckedBySession(AddResultPage.this.getSession().getCurrentSession().getId());
						if (testcases.size()>0){
							//go to next testcase
							setResponsePage(AddResultPage.class,new PageParameters().add("idTest",testcases.get(0).getId()));
						}else{
							//return to session main page
							setResponsePage(SessionsPage.class);
						}
					} else {
						error("SQL Error");
						sesResultMapper.rollback();
					}
				} catch (PersistenceException e) {
					Logger.getLogger(getClass()).error("SQL Error", e);
				}
				sesResultMapper.close();
			};
		};
		addForm.add(new Form<String>("skipForm").add(new Button("skipBtn"){
			private static final long serialVersionUID = 1L;
			
			@Override
			public void onSubmit() {
				SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
				List<TestCase> testcases = sesTestMapper.getMapper().getAllUncheckedBySession(AddResultPage.this.getSession().getCurrentSession().getId());
				int currTestPos = testcases.indexOf(testCase);
				sesTestMapper.close();
				Logger.getLogger(getClass()).debug("Test skipped");
				if (currTestPos!=-1 && (testcases.size()-1)>currTestPos){
					//go to next testcase
					setResponsePage(AddResultPage.class,new PageParameters().add("idTest",testcases.get(currTestPos+1).getId()));
				}else{
					//return to session main page
					setResponsePage(SessionsPage.class);
				}
			}
		}));
		
		List<Boolean> boolList = new ArrayList<Boolean>();
		boolList.add(true);
		boolList.add(false);
		addForm.add(new DropDownChoice<Boolean>("successFld", new PropertyModel<Boolean>(result, "success"), boolList, new IChoiceRenderer<Boolean>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(Boolean val) {
				if (val) {
					return "Success";
				} else {
					return "Failed";
				}
			}

			@Override
			public String getIdValue(Boolean val, int arg1) {
				if (val) {
					return "success";
				} else {
					return "failed";
				}
			}
		}).setRequired(true));
		addForm.add(new TextArea<String>("noteFld", new PropertyModel<String>(result, "note")));
		
		//ATTACHMENT
		addForm.add(attachmentFld=new FileUploadField("attachmentFld"));
		addForm.add(new TextField<String>("attachmentDescriptionFld", attachmentDescription));
		addForm.setMultiPart(true);
		addForm.setMaxSize(Bytes.megabytes(5));
		
		add(addForm);

		StepsTablePanel stepsTable = new StepsTablePanel("stepsTable", idTest, false);
		if (stepsTable.getSize() == 0) {
			add(new EmptyPanel("stepsTable"));
		} else {
			add(stepsTable);
		}
	}
}
