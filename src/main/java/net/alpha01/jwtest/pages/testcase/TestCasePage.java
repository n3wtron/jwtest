package net.alpha01.jwtest.pages.testcase;

import java.awt.Color;
import java.awt.Paint;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.component.EmptyLink;
import net.alpha01.jwtest.component.HtmlLabel;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.ResultMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.jfreechart.PieChartImageResource;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.pages.requirement.RequirementPage;
import net.alpha01.jwtest.pages.step.AddStepPage;
import net.alpha01.jwtest.panels.CloseablePanel;
import net.alpha01.jwtest.panels.attachment.AttachmentPanel;
import net.alpha01.jwtest.panels.result.ResultsTablePanel;
import net.alpha01.jwtest.panels.step.StepsTablePanel;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class TestCasePage extends LayoutPage {
	private static final long serialVersionUID = 1L;
	private TestCase test;
	private HashMap<Step, Model<Boolean>> selectedStep = new HashMap<Step, Model<Boolean>>();
	private Model<HashMap<Step, Model<Boolean>>> selecteStepModel = new Model<HashMap<Step, Model<Boolean>>>(selectedStep);

	public TestCasePage(PageParameters params) {
		super(params);
		if (params.get("idTest").isNull()) {
			error("Parametro idTest non trovato");
			setResponsePage(ProjectPage.class);
		}
		SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		test = sesTestMapper.getMapper().getStat(BigInteger.valueOf(params.get("idTest").toLong()));
		String testCaseName=test.getName();
		if (!test.getNew_version().equals(BigInteger.ZERO)){
			testCaseName+=" ["+JWTestUtil.translate("obsolete", this)+"]";
		}
		add(new Label("testCaseName", testCaseName));
		add(new HtmlLabel("testCaseDescription", test.getDescription()));
		add(new HtmlLabel("testCaseExpectedResult", test.getExpected_result()));

		// REQ Link
		RequirementMapper reqMapper = sesTestMapper.getSqlSession().getMapper(RequirementMapper.class);
		Requirement req = reqMapper.get(test.getId_requirement());

		PageParameters reqParams = new PageParameters();
		reqParams.add("idReq", req.getId());
		BookmarkablePageLink<String> requirementLnk = new BookmarkablePageLink<String>("requirementLnk", RequirementPage.class, reqParams);
		requirementLnk.add(new Label("requirementName", req.getName()));
		add(requirementLnk);

		// LINKS
		PageParameters lnkParam = new PageParameters();
		lnkParam.add("idTest", test.getId());
		if (test.getNew_version().equals(BigInteger.ZERO)){
			add(new BookmarkablePageLinkSecure<String>("addStepLnk", AddStepPage.class, lnkParam,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("addStepImg", "images/add_step.png")));
			add(new BookmarkablePageLinkSecure<String>("updTestLnk", UpdateTestCasePage.class, lnkParam,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("updateTestImg", "images/update_test.png")));
		}else{
			add((new EmptyLink<Void>("addStepLnk")).add(new ContextImage("addStepImg", "images/add_step.png")).setVisible(false));
			add((new EmptyLink<Void>("updTestLnk")).add(new ContextImage("updateTestImg", "images/update_test.png")).setVisible(false));
		}
		add(new BookmarkablePageLinkSecure<String>("delTestLnk", DeleteTestCasePage.class, lnkParam,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("deleteTestImg", "images/delete_test.png")));

		final Model<Boolean> isAuthorized = JWTestUtil.isAuthorized(Roles.ADMIN, "PROJECT_ADMIN", "MANAGER");
		//ATTACHMENTS TABLE
		final Model<AttachmentPanel> attachPanelModel=new Model<AttachmentPanel>();
		CloseablePanel attachmentsPanel;
		add(attachmentsPanel = new CloseablePanel("attachmentsPanel",JWTestUtil.translate("attachments",this),false){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel getContentPanel(String id) {
				attachPanelModel.setObject(new AttachmentPanel(id, test, false, isAuthorized.getObject(), isAuthorized.getObject()));
				return attachPanelModel.getObject();
			}
		});
		
		if (attachPanelModel.getObject().getSize()==0){
			attachmentsPanel.setVisible(false);
		}
		
		
		// GRAPH
		HashMap<String, BigDecimal> graphValues = new HashMap<String, BigDecimal>();
		HashMap<String, Paint> graphColors = new HashMap<String, Paint>();
		if (test.getNresults().intValue() > 0) {
			graphValues.put("SUCCESS", test.getPercSuccess());
			graphColors.put("SUCCESS", Color.GREEN);
			graphValues.put("FAILED", BigDecimal.valueOf(100 - test.getPercSuccess().floatValue()));
			graphColors.put("FAILED", Color.RED);
			add(new Image("chartImage", new PieChartImageResource(null, graphValues, graphColors, 500, 300)));
		} else {
			add(new Image("chartImage","chartImage").setVisible(false));
		}

		final Model<Boolean> updatePermission=JWTestUtil.isAuthorized(Roles.ADMIN,"PROJECT_ADMIN","MANAGER");
		Form<String> selForm = new Form<String>("selForm");
		if (!updatePermission.getObject()){
			selForm.add(new StepsTablePanel("stepsPanel", test.getId(), false));
		}else{
			selForm.add(new StepsTablePanel("stepsPanel", test.getId(), selecteStepModel, true));
		}
		selForm.add(new Button("delBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return updatePermission.getObject();
			}
			
			@Override
			public boolean isEnabled() {
				return updatePermission.getObject();
			}

			@Override
			public void onSubmit() {
				SqlSessionMapper<StepMapper> sesDelStepMapper = SqlConnection.getSessionMapper(StepMapper.class);
				Iterator<Entry<Step, Model<Boolean>>> itS = selecteStepModel.getObject().entrySet().iterator();
				try {
					while (itS.hasNext()) {
						Entry<Step, Model<Boolean>> entryStep = itS.next();
						if (entryStep.getValue().getObject().booleanValue()) {
							sesDelStepMapper.getMapper().delete(entryStep.getKey());
							info(entryStep.getKey() + " deleted");
						}
					}
					sesDelStepMapper.commit();
					sesDelStepMapper.close();
					super.onSubmit();
				} catch (PersistenceException e) {
					Logger.getLogger(getPageClass()).error(e.getMessage(), e);
					error(e.getMessage());
					sesDelStepMapper.rollback();
					sesDelStepMapper.close();
				}
			}
		});
		add(selForm);
		
		//RESULTS Table
		ResultMapper resMapper = sesTestMapper.getSqlSession().getMapper(ResultMapper.class);
		List<Result> results = resMapper.getAllByTestCase(test.getId());
		add(new ResultsTablePanel("resultsTable", JWTestUtil.translate("results", this),results, 5,updatePermission.getObject() ));
		sesTestMapper.close();
	}
}
