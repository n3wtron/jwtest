package net.alpha01.jwtest.panels.testcase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.CleanTextPropertyColumn;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.component.HtmlPropertyColumn;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;
import net.alpha01.jwtest.pages.result.AddResultPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;
import net.alpha01.jwtest.panels.PanelLink;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class TestCasesTablePanel extends Panel{
	private static final long serialVersionUID = 1L;
	private List<TestCase> testcases;
	private HashMap<TestCase, Model<Boolean>> selectedTests;
	
	
	/**
	 * Data Provider for run Test Table
	 * @author igor
	 *
	 */
	class TestCaseDataProvider extends SortableDataProvider<TestCase>{
		private static final long serialVersionUID = 1L;

		@Override
		public Iterator<? extends TestCase> iterator(int from, int count) {
			return testcases.subList(from, from+count).iterator();
		}

		@Override
		public IModel<TestCase> model(TestCase testCase) {
			Model<TestCase> model = new Model<TestCase>(testCase);
			model.detach();
			return model;
		}

		@Override
		public int size() {
			return testcases.size();
		}
	}

	/**
	 * Data Provider with statistics information 
	 * @author igor
	 *
	 */
	class TestCaseStatDataProvider extends SortableDataProvider<TestCase> {

		private static final long serialVersionUID = 1L;
		private List<TestCase> tests;
		private Requirement req;

		public TestCaseStatDataProvider(Requirement req) {
			this.req = req;
			setSort(new SortParam("name", true));
		}

		@Override
		public Iterator<? extends TestCase> iterator(int start, int length) {
			return getTests().subList(start, start + length).iterator();
		}

		@Override
		public IModel<TestCase> model(TestCase obj) {
			IModel<TestCase> model = new Model<TestCase>(obj);
			model.detach();
			return model;
		}

		@Override
		public int size() {
			SqlSessionMapper<TestCaseMapper> mapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
			Logger.getLogger(getClass()).debug("rieseguo la query ordinando per:" + getSort().getProperty() + " asc:" + getSort().isAscending());
			setTests(mapper.getMapper().getAllStat(new TestCaseSelectSort(req.getId(), getSort().getProperty(), getSort().isAscending())));
			mapper.close();
			return getTests().size();
		}

		public List<TestCase> getTests() {
			return tests;
		}

		public void setTests(List<TestCase> tests) {
			this.tests = tests;
		}
	}
	
	//PANNELS
	class TestLink extends Panel {
		private static final long serialVersionUID = 1L;

		public TestLink(String id,TestCase test) {
			super(id);
			PageParameters params=new PageParameters();
			params.add("idTest", test.getId().intValue());
			BookmarkablePageLink<String> lnk = new BookmarkablePageLink<String>("testLnk",TestCasePage.class,params);
			lnk.add(new Label("testCaseName",test.getName()));
			add(lnk);
		}

	}
	
	class TestSel extends Panel {
		private static final long serialVersionUID = 1L;

		public TestSel(String id, Model<Boolean> bolModel) {
			super(id);
			add(new CheckBox("checkBoxFld", bolModel));
		}

	}

	/**
	 * Constructor for executable/view tests table
	 * @param id
	 * @param testcases
	 * @param rows
	 * @param runnable
	 */
	public TestCasesTablePanel(String id,List<TestCase> testcases,int rows, boolean runnable) {
		super(id);
		this.testcases=testcases;
		TestCaseDataProvider dataProvider=new TestCaseDataProvider();
		ArrayList<IColumn<TestCase>> columnList=new ArrayList<IColumn<TestCase>>();
		columnList.add(new PropertyColumn<TestCase>(new Model<String>("ID"), "id"));
		columnList.add(new AbstractColumn<TestCase>(new StringResourceModel("testcase.name",this,null), "name"){
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TestCase>> item, String contentId, IModel<TestCase> model) {
				item.add(new TestLink(contentId, model.getObject()));
			}
			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		columnList.add(new HtmlPropertyColumn<TestCase>(new StringResourceModel("description", this, null), "description"){
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		if (runnable){
			columnList.add(new AbstractColumn<TestCase>(new StringResourceModel("run", this, null)) {
				private static final long serialVersionUID = 1L;
				@Override
				public void populateItem(Item<ICellPopulator<TestCase>> item, String contenId, IModel<TestCase> model) {
					PageParameters params = new PageParameters();
					params.add("idTest", model.getObject().getId());
					item.add(new PanelLink(contenId, "run", AddResultPage.class, params));
				}
			});
		}
		
		DataTable<TestCase> testcasesTable = new DataTableAlternatedRows<TestCase>("testcasesTable",columnList, dataProvider, rows);
		testcasesTable.addTopToolbar(new HeadersToolbar(testcasesTable, dataProvider));
		testcasesTable.addBottomToolbar(new NavigationToolbar(testcasesTable));
		add(testcasesTable);
		
	}
	
	/**
	 * Constructor for selection testcases table, It must be put inside a form
	 * @param id
	 * @param req
	 * @param rows
	 * @param selectedTestsModel
	 */
	public TestCasesTablePanel(String id,Requirement req,int rows,Model<HashMap<TestCase, Model<Boolean>>> selectedTestsModel) {
		super(id);
		selectedTests=selectedTestsModel.getObject();
		TestCaseStatDataProvider dataProvider=new TestCaseStatDataProvider(req);
		ArrayList<IColumn<TestCase>> columnList=new ArrayList<IColumn<TestCase>>();
		columnList.add(new AbstractColumn<TestCase>(new Model<String>("Sel")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TestCase>> item, String contentId, IModel<TestCase> model) {
				if (!selectedTests.containsKey(model.getObject())) {
					selectedTests.put(model.getObject(), new Model<Boolean>(new Boolean(false)));
				}
				item.add(new TestSel(contentId, selectedTests.get(model.getObject())));
			}

			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		});
		columnList.add(new AbstractColumn<TestCase>(new StringResourceModel("testcase.name",this,null), "name"){
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TestCase>> item, String contentId, IModel<TestCase> model) {
				item.add(new TestLink(contentId, model.getObject()));
			}

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		columnList.add(new CleanTextPropertyColumn<TestCase>(new StringResourceModel("description",this,null), "description", "description"){
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		columnList.add(new TextFilteredPropertyColumn<TestCase, BigDecimal>(new StringResourceModel("testcase.success",this,null), "percSuccess"){
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		});
		
		DataTable<TestCase> testcasesTable = new DataTableAlternatedRows<TestCase>("testcasesTable",columnList, dataProvider, rows);
		testcasesTable.addTopToolbar(new HeadersToolbar(testcasesTable, dataProvider));
		testcasesTable.addBottomToolbar(new NavigationToolbar(testcasesTable));
		add(testcasesTable);
	}
	
	/**
	 * Constructor for selection testcases table, It must be put inside a form
	 * @param id
	 * @param req
	 * @param rows
	 * @param selectedTestsModel
	 */
	public TestCasesTablePanel(String id,Requirement req,int rows) {
		super(id);
		TestCaseStatDataProvider dataProvider=new TestCaseStatDataProvider(req);
		ArrayList<IColumn<TestCase>> columnList=new ArrayList<IColumn<TestCase>>();
		columnList.add(new AbstractColumn<TestCase>(new StringResourceModel("testcase.name",this,null), "name"){
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<TestCase>> item, String contentId, IModel<TestCase> model) {
				item.add(new TestLink(contentId, model.getObject()));
			}

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		columnList.add(new CleanTextPropertyColumn<TestCase>(new StringResourceModel("description",this,null), "description", "description"){
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		columnList.add(new TextFilteredPropertyColumn<TestCase, BigDecimal>(new StringResourceModel("testcase.success",this,null), "percSuccess"){
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		});
		
		DataTable<TestCase> testcasesTable = new DataTableAlternatedRows<TestCase>("testcasesTable",columnList, dataProvider, rows);
		testcasesTable.addTopToolbar(new HeadersToolbar(testcasesTable, dataProvider));
		testcasesTable.addBottomToolbar(new NavigationToolbar(testcasesTable));
		add(testcasesTable);
	}

}
