package net.alpha01.jwtest.panels.step;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.component.HtmlPropertyColumn;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.pages.step.UpdateStepPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class StepsTablePanel extends Panel {
	private static final long serialVersionUID = 1L;
	private DataTable<Step> dTable;
	private StepDataTable dataProvider;

	class StepDataTable extends SortableDataProvider<Step> {
		private static final long serialVersionUID = 1L;
		private int idTestCase;
		private List<Step> steps;

		public StepDataTable(int idTestCase) {
			this.idTestCase = idTestCase;
		}

		@Override
		public Iterator<? extends Step> iterator(int index, int length) {
			return steps.subList(index, index + length).iterator();
		}

		@Override
		public IModel<Step> model(Step step) {
			IModel<Step> result = new Model<Step>(step);
			result.detach();
			return result;
		}

		@Override
		public int size() {
			SqlSessionMapper<StepMapper> sesMapper = SqlConnection.getSessionMapper(StepMapper.class);
			steps = sesMapper.getMapper().getAll(idTestCase);
			sesMapper.close();
			return steps.size();
		}

	}

	class SelStep extends Panel {
		private static final long serialVersionUID = 1L;

		public SelStep(String id, Model<Boolean> selectedModel) {
			super(id);
			add(new CheckBox("selStep", selectedModel));
		}
	}

	class LnkStep extends Panel {

		private static final long serialVersionUID = 1L;

		public LnkStep(String id, Step step) {
			super(id);
			PageParameters params = new PageParameters();
			params.put("idStep", step.getId());
			BookmarkablePageLink<String> lnk = new BookmarkablePageLink<String>("updLnk", UpdateStepPage.class, params);
			add(lnk);
		}

	}

	@SuppressWarnings("unchecked")
	public StepsTablePanel(String id, BigInteger idTestCase, boolean modLink) {
		super(id);
		dataProvider = new StepDataTable(idTestCase.intValue());

		IColumn<Step>[] columns;
		if (!modLink) {
			columns = new IColumn[4];
		} else {
			columns = new IColumn[5];
		}
		columns[0] = new PropertyColumn<Step>(new Model<String>("Seq"), "sequence");
		columns[1] = new HtmlPropertyColumn<Step>(new StringResourceModel("description", this, null), "description");
		columns[2] = new HtmlPropertyColumn<Step>(new StringResourceModel("step.expected", this, null), "expected_result");
		columns[3] = new HtmlPropertyColumn<Step>(new StringResourceModel("step.failed", this, null), "failed_result");
		if (modLink) {
			columns[4] = new AbstractColumn<Step>(new StringResourceModel("step.update", this, null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Step>> item, String contentId, IModel<Step> model) {
					item.add(new LnkStep(contentId, model.getObject()));
				}
			};
		}

		DataTable<Step> dTable = new DataTableAlternatedRows<Step>("stepDataTable", columns, dataProvider, 20);
		dTable.addTopToolbar(new HeadersToolbar(dTable, dataProvider));
		dTable.addBottomToolbar(new NavigationToolbar(dTable));
		add(dTable);
	}

	@SuppressWarnings("unchecked")
	public StepsTablePanel(String id, BigInteger idTestCase, final Model<HashMap<Step, Model<Boolean>>> selectedStepModel, boolean modLink) {
		super(id);
		StepDataTable dataProvider = new StepDataTable(idTestCase.intValue());
	
		IColumn<Step>[] columns;
		if (!modLink) {
			columns = new IColumn[5];
		} else {
			columns = new IColumn[6];
		}
		columns[0] = new AbstractColumn<Step>(new Model<String>("Sel")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Step>> item, String contentId, IModel<Step> model) {
				if (!selectedStepModel.getObject().containsKey(model.getObject())) {
					selectedStepModel.getObject().put(model.getObject(), new Model<Boolean>(new Boolean(false)));
				}
				item.add(new SelStep(contentId, selectedStepModel.getObject().get(model.getObject())));
			}
		};
		columns[1] = new PropertyColumn<Step>(new Model<String>("Seq"), "sequence");

		columns[2] = new HtmlPropertyColumn<Step>(new StringResourceModel("description", this, null), "description");
		columns[3] = new HtmlPropertyColumn<Step>(new StringResourceModel("step.expected", this, null), "expected_result");
		columns[4] = new HtmlPropertyColumn<Step>(new StringResourceModel("step.failed", this, null), "failed_result");
		if (modLink) {
			columns[5] = new AbstractColumn<Step>(new StringResourceModel("step.update", this, null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Step>> item, String contentId, IModel<Step> model) {
					item.add(new LnkStep(contentId, model.getObject()));
				}
			};
		}
		dTable = new DataTableAlternatedRows<Step>("stepDataTable", columns, dataProvider, 20);
		dTable.addTopToolbar(new HeadersToolbar(dTable, dataProvider));
		dTable.addBottomToolbar(new NavigationToolbar(dTable));
		add(dTable);
	}

	public int getSize() {
		return dataProvider.size();
	}

}
