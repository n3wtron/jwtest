package net.alpha01.jwtest.pages.plan;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;
import net.alpha01.jwtest.exceptions.JWTestException;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.panels.DoubleMultipleChoicePanel;
import net.alpha01.jwtest.util.PlanUtil;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","MANAGER"})
public class UpdatePlanPage extends LayoutPage{
	private Plan plan;
	private ArrayList<TestCase> testCases;
	private ArrayList<Requirement> selReqs=new ArrayList<Requirement>();
	
	public UpdatePlanPage(final PageParameters params){
		super(params);
		if (!params.containsKey("idPlan")){
			error("idPlan parameters not found");
			setResponsePage(ProjectPage.class);
			return;
		}
		SqlSessionMapper<TestCaseMapper> sesTestMapper=SqlConnection.getSessionMapper(TestCaseMapper.class);
		plan = sesTestMapper.getSqlSession().getMapper(PlanMapper.class).get(BigInteger.valueOf(params.getAsInteger("idPlan")));
		testCases=(ArrayList<TestCase>) sesTestMapper.getMapper().getAllByPlan(plan.getId());
		
		
		Form<String> addPlanForm = new Form<String>("addPlanForm"){
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<PlanMapper> sesPlanMapper= SqlConnection.getSessionMapper(PlanMapper.class);
				TestCaseMapper testMapper=sesPlanMapper.getSqlSession().getMapper(TestCaseMapper.class);	
				HashSet<TestCase> testCasesSet = new HashSet<TestCase>(testCases);
				
				//check TestCases Dependencies
				testCasesSet=addTestCaseDependencies(testCasesSet,testMapper);
				
				//add TestCase selected by requirements
				Iterator<Requirement> itr = selReqs.iterator();
				while (itr.hasNext()){
					Requirement req=itr.next();
					Iterator<TestCase> itc = testMapper.getAll(new TestCaseSelectSort(req.getId(), "name", true)).iterator();
					while (itc.hasNext()){
						TestCase tc = itc.next();
						testCasesSet.add(tc);
					}
				}
				try {
					PlanUtil.updatePlan(plan, testCasesSet);
					setResponsePage(PlanPage.class,new PageParameters("idPlan="+plan.getId().toString()));
				} catch (JWTestException e) {
					error(e.getMessage());
				}
				sesPlanMapper.close();
			}

			/**
			 * Get All TestCase dependencies of testcases hashset
			 * @param testCases
			 * @param testMapper
			 * @return
			 */
			private HashSet<TestCase> addTestCaseDependencies(Collection<TestCase> testCases, TestCaseMapper testMapper) {
				HashSet<TestCase> result=new HashSet<TestCase>(testCases);
				Iterator<TestCase> itt= testCases.iterator();
				while (itt.hasNext()){
					TestCase tc = itt.next();
					List<TestCase> deps =testMapper.getDependencies(tc.getId());
					result.addAll(deps);
					result.addAll(addTestCaseDependencies(deps, testMapper));
				}
				return result;
			};
		};
		
		addPlanForm.add(new TextField<String>("nameFld",new PropertyModel<String>(plan, "name")).setRequired(true));
		
		
		//retreive requirements
		RequirementMapper reqMapper = sesTestMapper.getSqlSession().getMapper(RequirementMapper.class);
		List<Requirement> allReq= reqMapper.getAll(new RequirementSelectSort(getSession().getCurrentProject().getId(), "id", true));
		addPlanForm.add(new ListMultipleChoice<Requirement>("requirements", new Model<ArrayList<Requirement>>(selReqs),allReq,new IChoiceRenderer<Requirement>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(Requirement req) {
				return req.getName()+" (tests:"+req.getNtest()+")";
			}

			@Override
			public String getIdValue(Requirement req, int arg1) {
				return req.getId().toString();
			}
		}));
		
		//TestCases
		List<TestCase> allTests=sesTestMapper.getMapper().getAllByProject(getSession().getCurrentProject().getId());
		allTests.removeAll(testCases);
		sesTestMapper.close();
		addPlanForm.add(new DoubleMultipleChoicePanel<TestCase>("testCasesSelectionPanel", new Model<ArrayList<TestCase>>(testCases), allTests,new IChoiceRenderer<TestCase>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(TestCase test) {
				return test.getRequirement()+"::"+test.getName();
			}

			@Override
			public String getIdValue(TestCase test, int index) {
				return test.getId().toString();
			}
		}));
		
		add(addPlanForm);
	}
	
}
