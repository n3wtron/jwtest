package net.alpha01.jwtest.panels.requirement;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.RequirementType;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.requirement.RequirementPage;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class RequirementsTablePanel extends Panel {
	private static final long serialVersionUID = 1L;

	class ReqLink extends Panel {
		private static final long serialVersionUID = 301061076205756618L;

		public ReqLink(String id, Requirement req) {
			super(id);
			PageParameters params = new PageParameters();
			params.put("idReq", req.getId());
			BookmarkablePageLink<RequirementPage> lnk = new BookmarkablePageLink<RequirementPage>("lnk", RequirementPage.class, params);
			lnk.add(new Label("name", req.getName()));
			add(lnk);
		}
	}

	class RequirementsDataProvider extends SortableDataProvider<Requirement> implements IFilterStateLocator<Requirement> {

		private static final long serialVersionUID = -3781684038864926619L;
		private List<Requirement> reqs;
		private int idProject;
		private Requirement filter = new Requirement();

		public RequirementsDataProvider(int idProject) {
			this.idProject = idProject;
			setSort("num", true);
		}

		@Override
		public Iterator<? extends Requirement> iterator(int from, int length) {
			return reqs.subList(from, from + length).iterator();
		}

		@Override
		public IModel<Requirement> model(Requirement model) {
			IModel<Requirement> result = new Model<Requirement>(model);
			result.detach();
			return result;
		}

		@Override
		public void setSort(String property, boolean ascending) {
			Logger.getLogger(getClass()).debug("ordinamento per " + property + " asc:" + ascending);
			super.setSort(property, ascending);
		}

		@Override
		public int size() {
			SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
			Logger.getLogger(getClass()).debug("rieseguo la query ordinando per:" + getSort().getProperty() + " asc:" + getSort().isAscending());
			RequirementSelectSort sortObj = new RequirementSelectSort(BigInteger.valueOf(idProject), getSort().getProperty(), getSort().isAscending());
			if (filter != null) {
				sortObj.setType(filter.getType());
				sortObj.setName(filter.getName());
				sortObj.setDescription(filter.getDescription());
				sortObj.setNum(filter.getNum());
			}
			reqs = sesMapper.getMapper().getAll(sortObj);
			sesMapper.close();
			return reqs.size();
		}

		@Override
		public Requirement getFilterState() {
			return filter;
		}

		@Override
		public void setFilterState(Requirement reqFilter) {
			this.filter = reqFilter;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RequirementsTablePanel(String id, int idProject) {
		super(id);
		IColumn<Requirement>[] columns = new IColumn[4];
		SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
		ArrayList<RequirementType> allTypes = (ArrayList<RequirementType>) sesMapper.getMapper().getTypes();
		sesMapper.close();
		columns[0] = new ChoiceFilteredPropertyColumn<Requirement, RequirementType>(new StringResourceModel("requirement.type", this, null), "type", "type", new Model(allTypes)){
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		};
		columns[1] = new TextFilteredPropertyColumn<Requirement, String>(new Model<String>("Num"), "num", "num"){
			private static final long serialVersionUID = 1L;			
			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		};
		columns[2] = new TextFilteredPropertyColumn<Requirement, String>(new StringResourceModel("requirement.name", this, null), "name", "name") {
			private static final long serialVersionUID = -5517507346403267858L;
			@Override
			public void populateItem(Item<ICellPopulator<Requirement>> item, String contentId, final IModel<Requirement> model) {
				item.add(new ReqLink(contentId, model.getObject()));
			}

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		};
		columns[3] = new PropertyColumn<Requirement>(new Model<String>("N.Test"), "ntest", "ntest"){
			private static final long serialVersionUID = 1L;
			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		};
		RequirementsDataProvider dataProvider = new RequirementsDataProvider(idProject);
		DataTable<Requirement> requirementsTable = new DataTableAlternatedRows<Requirement>("requirementsTable", columns, dataProvider, 30);
		requirementsTable.addTopToolbar(new HeadersToolbar(requirementsTable, dataProvider));
		requirementsTable.addBottomToolbar(new NavigationToolbar(requirementsTable));
		add(requirementsTable);
	}
}
