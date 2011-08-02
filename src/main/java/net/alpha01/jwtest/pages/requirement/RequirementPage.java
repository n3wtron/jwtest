package net.alpha01.jwtest.pages.requirement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.component.HtmlLabel;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;
import net.alpha01.jwtest.jfreechart.BarChartImageResource;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.dot.TestCaseDotPage;
import net.alpha01.jwtest.pages.testcase.AddTestCasePage;
import net.alpha01.jwtest.panels.ChartPanel;
import net.alpha01.jwtest.panels.CloseablePanel;
import net.alpha01.jwtest.panels.attachment.AttachmentPanel;
import net.alpha01.jwtest.panels.testcase.TestCasesTablePanel;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class RequirementPage extends LayoutPage {
	private Requirement req;

	private Model<Requirement> destinationReqModel = new Model<Requirement>();
	private HashMap<TestCase, Model<Boolean>> selectedTests = new HashMap<TestCase, Model<Boolean>>();

	public RequirementPage(PageParameters params) {
		super(params);
		SqlSessionMapper<RequirementMapper> sesReqMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
		req = sesReqMapper.getMapper().get(BigInteger.valueOf(params.getAsInteger("idReq")));

		// LABELS
		add(new Label("requirementName", new PropertyModel<String>(req, "name")));
		add(new HtmlLabel("requirementDescription", new PropertyModel<String>(req, "description")));

		// LINK
		PageParameters reqParams = new PageParameters();
		reqParams.add("idReq", req.getId().toString());
		add(new BookmarkablePageLinkSecure<String>("addTestLnk", AddTestCasePage.class, reqParams,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("addTestImg", "images/add_test.png")));
		add(new BookmarkablePageLinkSecure<String>("delRequirementLnk", DeleteRequirementPage.class, reqParams,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("deleteRequirementImg", "images/delete_requirement.png")));
		add(new BookmarkablePageLinkSecure<String>("updRequirementLnk", UpdateRequirementPage.class, reqParams,Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("updateRequirementImg", "images/update_requirement.png")));
		
		// GRAPHS
		HashMap<String, BigDecimal> dataValues = new HashMap<String, BigDecimal>();
		TestCaseMapper testMapper = sesReqMapper.getSqlSession().getMapper(TestCaseMapper.class);
		Iterator<TestCase> itc = testMapper.getAllStat(new TestCaseSelectSort(req.getId(), "name", true)).iterator();
		while (itc.hasNext()) {
			TestCase testC = itc.next();
			if (testC.getNresults().intValue() > 0) {
				dataValues.put(testC.getName(), testC.getPercSuccess());
			}
		}
		if (dataValues.size() > 0) {
			final DynamicImageResource resource = new BarChartImageResource("", dataValues, "TestCases", "Success", 600, 300);
			add (new CloseablePanel("chartPanel","Graph", false){
				private static final long serialVersionUID = 1L;

				@Override
				public Panel getContentPanel(String id) {
					return new ChartPanel(id, resource);
				}
			});
			
		} else {
			add(new EmptyPanel("chartPanel"));
		}

		final Model<Boolean> isAuthorized = JWTestUtil.isAuthorized(Roles.ADMIN, "PROJECT_ADMIN", "MANAGER");
		//ATTACHMENTS TABLE
		final Model<AttachmentPanel> attachPanelModel=new Model<AttachmentPanel>();
		CloseablePanel attachmentsPanel;
		add(attachmentsPanel = new CloseablePanel("attachmentsPanel",JWTestUtil.translate("attachments",this),false){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel getContentPanel(String id) {
				attachPanelModel.setObject(new AttachmentPanel(id, req, false, isAuthorized.getObject(), isAuthorized.getObject()));
				return attachPanelModel.getObject();
			}
		});
		if (attachPanelModel.getObject().getSize()==0){
			attachmentsPanel.setVisible(false);
		}
		
		// TEST TABLE
		TestCasesTablePanel testsTable;
		if (isAuthorized.getObject()) {
			testsTable = new TestCasesTablePanel("testTable", req, 15, new Model<HashMap<TestCase, Model<Boolean>>>(selectedTests));
		} else {
			testsTable = new TestCasesTablePanel("testTable", req, 15);
		}

		// SELECTION FORM
		Form<String> testsForm = new Form<String>("testsForm");
		testsForm.add(testsTable);

		// DELETE BUTTON
		testsForm.add(new Button("deleteBtn") {
			private static final long serialVersionUID = -1664877195014185574L;

			@Override
			public boolean isVisible() {
				return isAuthorized.getObject();
			}

			@Override
			public boolean isEnabled() {
				return isAuthorized.getObject();
			}

			@Override
			public void onSubmit() {
				SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
				Iterator<Entry<TestCase, Model<Boolean>>> itT = selectedTests.entrySet().iterator();
				boolean sqlError = false;
				try {
					int nSelTest =0;
					while (itT.hasNext() && !sqlError) {
						Entry<TestCase, Model<Boolean>> testBool = itT.next();
						if (testBool.getValue().getObject().booleanValue()) {
							nSelTest++;
							// delete test
							if (sesTestMapper.getMapper().delete(testBool.getKey()).equals(1)) {
								info("TestCase " + testBool.getKey().getName() + " deleted");
							} else {
								// ERROR
								error("SQL ERROR in " + testBool.getKey().getName());
								sqlError = true;
							}
						}
					}
					if (nSelTest==0){
						warn(JWTestUtil.translate("testcase.not.selected",this));
					}
					if (sqlError) {
						sesTestMapper.rollback();
					} else {
						sesTestMapper.commit();
					}
				} catch (PersistenceException e) {
					sesTestMapper.rollback();
				}
				selectedTests.clear();
			}
			
		});

		// MOVE BUTTON
		List<Requirement> reqs = sesReqMapper.getMapper().getAll(new RequirementSelectSort(getSession().getCurrentProject().getId(), "name", true));
		sesReqMapper.close();

		testsForm.add(new Button("moveBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return isAuthorized.getObject();
			}

			@Override
			public boolean isEnabled() {
				return isAuthorized.getObject();
			}

			@Override
			public void onSubmit() {
				if (destinationReqModel.getObject() == null) {
					error("Destination Requirement not selected");
					return;
				}
				SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
				Iterator<Entry<TestCase, Model<Boolean>>> itT = selectedTests.entrySet().iterator();
				boolean sqlError = false;
				try {
					while (itT.hasNext() && !sqlError) {
						Entry<TestCase, Model<Boolean>> testBool = itT.next();
						if (testBool.getValue().getObject().booleanValue()) {
							// move test
							Logger.getLogger(getClass()).debug("destination requirement id:" + destinationReqModel.getObject().getId());
							testBool.getKey().setId_requirement(destinationReqModel.getObject().getId());
							if (sesTestMapper.getMapper().update(testBool.getKey()).equals(1)) {
								info("TestCase " + testBool.getKey().getName() + " updated");
							} else {
								// ERROR
								error("SQL ERROR in " + testBool.getKey().getName());
								sqlError = true;
							}
						}
					}
					if (sqlError) {
						sesTestMapper.rollback();
					} else {
						sesTestMapper.commit();
					}
				} catch (PersistenceException e) {
					sesTestMapper.rollback();
				}
				selectedTests.clear();
			}
		});
		
		//COPY BUTTON
		testsForm.add(new Button("copyBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return isAuthorized.getObject();
			}

			@Override
			public boolean isEnabled() {
				return isAuthorized.getObject();
			}

			@Override
			public void onSubmit() {
				if (destinationReqModel.getObject() == null) {
					error("Destination Requirement not selected");
					return;
				}
				SqlSessionMapper<TestCaseMapper> sesTestMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
				StepMapper stepMapper = sesTestMapper.getSqlSession().getMapper(StepMapper.class);
				Iterator<Entry<TestCase, Model<Boolean>>> itT = selectedTests.entrySet().iterator();
				boolean sqlError = false;
				try {
					while (itT.hasNext() && !sqlError) {
						Entry<TestCase, Model<Boolean>> testBool = itT.next();
						if (testBool.getValue().getObject().booleanValue()) {
							// copy test
							//retrieve steps
							List<Step> steps = stepMapper.getAll(testBool.getKey().getId().intValue());
							
							testBool.getKey().setId_requirement(destinationReqModel.getObject().getId());
							if (sesTestMapper.getMapper().add(testBool.getKey()).equals(1)) {
								//copying steps
								for (Step nstep :steps){
									nstep.setId_testcase(testBool.getKey().getId());
									if(!stepMapper.add(nstep).equals(1)){
										sqlError=true;
									}
								}
								if (sqlError){
									error("ìError copying steps");
								}else{
									info("TestCase " + testBool.getKey().getName() + " copied");
								}
							} else {
								// ERROR
								error("SQL ERROR in " + testBool.getKey().getName());
								sqlError = true;
							}
						}
					}
					if (sqlError) {
						sesTestMapper.rollback();
					} else {
						sesTestMapper.commit();
					}
				} catch (PersistenceException e) {
					sesTestMapper.rollback();
				}
				selectedTests.clear();
			}
		});

		reqs.remove(req);
		DropDownChoice<Requirement> requirementsMoveList = new DropDownChoice<Requirement>("requirementsMoveList", destinationReqModel, reqs){
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return isAuthorized.getObject();
			}

			@Override
			public boolean isEnabled() {
				return isAuthorized.getObject();
			}
		};
		testsForm.add(requirementsMoveList);
		
		
		//DOT LINK
		testsForm.add(new BookmarkablePageLink<String>("dotGraphLnk",TestCaseDotPage.class,new PageParameters("idReq="+req.getId())));
		add(testsForm);
		
	}


}
