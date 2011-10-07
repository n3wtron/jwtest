package net.alpha01.jwtest.panels.result;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.component.DatePropertyColumn;
import net.alpha01.jwtest.component.HtmlPropertyColumn;
import net.alpha01.jwtest.dao.ResultMapper;
import net.alpha01.jwtest.dao.ResultMapper.ResultSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.result.AddResultPage;
import net.alpha01.jwtest.pages.result.ResultPage;
import net.alpha01.jwtest.pages.session.SessionsPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;
import net.alpha01.jwtest.panels.PanelAjaxLink;
import net.alpha01.jwtest.panels.PanelLabel;
import net.alpha01.jwtest.panels.PanelLink;
import net.alpha01.jwtest.util.GenericComparator;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ResultsTablePanel extends Panel {
	private static final long serialVersionUID = 8036026895705971305L;

	class ResultDataTable extends SortableDataProvider<Result> {
		private static final long serialVersionUID = 1L;
		private int idSession;
		private List<Result> results;

		public ResultDataTable(int idSession) {
			this.idSession = idSession;
			setSort(new SortParam("id", true));
		}

		@Override
		public Iterator<? extends Result> iterator(int index, int length) {
			return results.subList(index, index + length).iterator();
		}

		@Override
		public IModel<Result> model(Result result) {
			if (result == null) {
				return new Model<Result>();
			}
			IModel<Result> model = new Model<Result>(result);
			model.detach();
			return model;
		}

		@Override
		public int size() {
			SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
			ResultSort sort = new ResultSort();
			sort.setId_session(BigInteger.valueOf(idSession));
			sort.setAsc(getSort().isAscending());
			sort.setSortColumn(getSort().getProperty());
			results = sesMapper.getMapper().getAll(sort);
			sesMapper.close();
			Logger.getLogger(getClass()).debug("num Results:" + results.size());
			return results.size();
		}
	}

	class ListResultDataProvider extends SortableDataProvider<Result> {
		private static final long serialVersionUID = 1L;
		private List<Result> results;

		public ListResultDataProvider(List<Result> results) {
			super();
			this.results = results;
		}

		@Override
		public Iterator<? extends Result> iterator(int first, int count) {
			return results.subList(first, first + count).iterator();
		}

		@Override
		public int size() {
			if (getSort() != null) {
				Collections.sort(results, new GenericComparator<Result>(getSort().getProperty(), getSort().isAscending()));
			}
			return results.size();
		}

		@Override
		public IModel<Result> model(Result object) {
			Model<Result> model = new Model<Result>(object);
			model.detach();
			return model;
		}
	}

	public ResultsTablePanel(String id, String title, int idSession, int numRows, boolean deletedLnk) {
		super(id);
		add(new Label("title", title));
		ResultDataTable dataProvider = new ResultDataTable(idSession);
		createTable(dataProvider, numRows, false, deletedLnk, true);
	}

	public ResultsTablePanel(String id, String title, List<Result> results, int numRows, boolean deletedLnk) {
		super(id);
		add(new Label("title", title));
		SortableDataProvider<Result> dataProvider = new ListResultDataProvider(results);
		createTable(dataProvider, numRows, true, deletedLnk, false);
	}

	private void createTable(SortableDataProvider<Result> dataProvider, int numRows, boolean full, boolean deletedLnk, boolean recycleLnk) {
		ArrayList<IColumn<Result>> columns = new ArrayList<IColumn<Result>>();

		columns.add(new AbstractColumn<Result>(new Model<String>("ID"), "id") {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Result>> item, String contentId, IModel<Result> model) {
				item.add(new PanelLink(contentId, model.getObject().getId().toString(), ResultPage.class, new PageParameters().add("idResult", model.getObject().getId().intValue())));
			}
		});
		if (full) {
			columns.add(new AbstractColumn<Result>(new StringResourceModel("start_date", this, null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Result>> item, String contentId, IModel<Result> model) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					item.add(new PanelLink(contentId, sdf.format(model.getObject().getSession().getStart_date()), SessionsPage.class, new PageParameters().add("idSession", model.getObject().getId_session().toString())));
				}
			});
			columns.add(new DatePropertyColumn<Result>(new StringResourceModel("end_date", this, null), "session.end_date", "yyyy-MM-dd-HH:mm"));
			columns.add(new PropertyColumn<Result>(new StringResourceModel("version", this, null), "session.version"));
		} else {
			columns.add(new AbstractColumn<Result>(new Model<String>("TestCase")) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Result>> item, String contentId, IModel<Result> model) {
					item.add(new PanelLink(contentId, model.getObject().getTestCase().toString(), TestCasePage.class, new PageParameters().add("idTest", model.getObject().getId_testcase().intValue())));
				}

				@Override
				public String getCssClass() {
					return "maximizedColumn";
				}
			});
		}
		columns.add(new DatePropertyColumn<Result>(new StringResourceModel("insert_date", this, null), "insert_date", "insert_date", "yyyy-MM-dd-HH:mm"));
		columns.add(new AbstractColumn<Result>(new StringResourceModel("success", this, null), "success") {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Result>> item, String contentId, IModel<Result> model) {
				if (model.getObject().getSuccess()) {
					item.add(new PanelLabel(contentId, "OK"));
				} else {
					item.add(new PanelLabel(contentId, "FAILED"));
				}
			}
		});
		columns.add(new HtmlPropertyColumn<Result>(new StringResourceModel("note", this, null), "note", "note") {
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});

		columns.add(new PropertyColumn<Result>(new StringResourceModel("recycles", this, null), "recycles", "recycles") {
			private static final long serialVersionUID = 1L;

			@Override
			public String getCssClass() {
				return "minimizedColumn";
			}
		});

		if (recycleLnk) {
			columns.add(new AbstractColumn<Result>(new StringResourceModel("recycle", this, null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Result>> item, String contentId, IModel<Result> model) {
					if (model.getObject().getSession().isOpened() && model.getObject().getId_parent() == null) {
						item.add(new PanelLink(contentId, JWTestUtil.translate("recycle", ResultsTablePanel.this), AddResultPage.class, new PageParameters().add("idParent", model.getObject().getId())));
					} else {
						item.add(new EmptyPanel(contentId));
					}
				}
			});
		}

		if (deletedLnk) {
			columns.add(new AbstractColumn<Result>(new Model<String>("delete")) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Result>> item, String contentId, final IModel<Result> model) {
					if (model.getObject().getSession().isOpened()) {
						item.add(new PanelAjaxLink(contentId, "Del") {
							private static final long serialVersionUID = 1L;

							@Override
							protected void onAjaxClick(AjaxRequestTarget target) {
								SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
								if (sesMapper.getMapper().delete(model.getObject()).equals(1)) {
									sesMapper.commit();
								} else {
									error("SQL Error cannot delete result");
									sesMapper.rollback();
								}
								sesMapper.close();
								setResponsePage(SessionsPage.class);
							}
						});
					} else {
						item.add(new EmptyPanel(contentId));
					}
				}
			});
		}
		DataTable<Result> resultsDataTable = new DataTableAlternatedRows<Result>("resultsDataTable", columns, dataProvider, numRows);
		resultsDataTable.addTopToolbar(new HeadersToolbar(resultsDataTable, dataProvider));
		resultsDataTable.addBottomToolbar(new NavigationToolbar(resultsDataTable));
		add(resultsDataTable);
	}

}
