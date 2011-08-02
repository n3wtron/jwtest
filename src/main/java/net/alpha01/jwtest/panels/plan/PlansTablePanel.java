package net.alpha01.jwtest.panels.plan;

import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.component.DatePropertyColumn;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.plan.PlanPage;
import net.alpha01.jwtest.panels.PanelLink;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class PlansTablePanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	class PlanDataTable extends SortableDataProvider<Plan> {
		private static final long serialVersionUID = 1L;
		private int idProject=-1;
		private List<Plan> plans;

		public PlanDataTable(int idProject) {
			this.idProject = idProject;
		}

		public PlanDataTable(List<Plan> plans) {
			this.plans=plans;
		}

		@Override
		public Iterator<? extends Plan> iterator(int index, int length) {
			return plans.subList(index, index + length).iterator();
		}

		@Override
		public IModel<Plan> model(Plan plan) {
			IModel<Plan> result = new Model<Plan>(plan);
			result.detach();
			return result;
		}

		@Override
		public int size() {
			if (idProject!=-1){
				SqlSessionMapper<PlanMapper> sesMapper = SqlConnection.getSessionMapper(PlanMapper.class);
				plans = sesMapper.getMapper().getAllStat(idProject);
				sesMapper.close();
			}
			return plans.size();
		}

	}
	
	public PlansTablePanel(String id, int idProject, int rows) {
		super(id);
		PlanDataTable dataProvider=new PlanDataTable(idProject);
		createTable(dataProvider,rows);
		
	}
	
	public PlansTablePanel(String id, List<Plan> plans, int rows) {
		super(id);
		PlanDataTable dataProvider=new PlanDataTable(plans);
		createTable(dataProvider,rows);
	}
	
	private void createTable(PlanDataTable dataProvider,int rows){
		@SuppressWarnings("unchecked")
		IColumn<Plan>[] columns=new IColumn[5];
		columns[0]=new PropertyColumn<Plan>(new Model<String>("ID"), "id");
		//columns[1]=new PropertyColumn<Plan>(new StringResourceModel("name", this, null), "name");
		columns[1]=new AbstractColumn<Plan>(new StringResourceModel("name", this, null)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Plan>> item, String contentId, IModel<Plan> model) {
				PageParameters params =new PageParameters();
				params.put("idPlan",model.getObject().getId());
				item.add(new PanelLink(contentId, model.getObject().getName(), PlanPage.class, params));
			}
		};
		columns[2]=new DatePropertyColumn<Plan>(new StringResourceModel("date", this, null), "creation_date","dd-MM-yyyy HH:mm");
		columns[3]=new PropertyColumn<Plan>(new StringResourceModel("session.num", this, null), "nSessions");
		columns[4]=new PropertyColumn<Plan>(new StringResourceModel("test.num", this, null), "nTests");
		DataTable<Plan> plansDataTable=new DataTableAlternatedRows<Plan>("plansDataTable", columns, dataProvider, rows);
		plansDataTable.addTopToolbar(new HeadersToolbar(plansDataTable, dataProvider));
		plansDataTable.addBottomToolbar(new NavigationToolbar(plansDataTable));
		add(plansDataTable);
	}

}
