package net.alpha01.jwtest.panels.attachment;

import java.util.ArrayList;
import java.util.List;

import net.alpha01.jwtest.beans.Attachment;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.dao.AttachmentMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.panels.PanelAjaxLink;
import net.alpha01.jwtest.panels.PanelLink;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

public class AttachmentPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private DataTable<Attachment> dataTable;
	private List<Attachment> attachments;
	public AttachmentPanel(String id, Requirement req, boolean small, boolean modLink, boolean delLink) {
		super(id);
		SqlSessionMapper<AttachmentMapper> sesMapper = SqlConnection.getSessionMapper(AttachmentMapper.class);
		attachments = sesMapper.getMapper().getByRequirement(req.getId());
		sesMapper.close();

		renderTable(attachments, modLink, delLink);
	}

	public AttachmentPanel(String id, TestCase test, boolean small, Boolean modLink, Boolean delLink) {
		super(id);
		SqlSessionMapper<AttachmentMapper> sesMapper = SqlConnection.getSessionMapper(AttachmentMapper.class);
		attachments = sesMapper.getMapper().getByTestcase(test.getId());
		sesMapper.close();

		renderTable(attachments, modLink, delLink);
	}

	public AttachmentPanel(String id, Result res, boolean small, Boolean modLink, Boolean delLink) {
		super(id);
		SqlSessionMapper<AttachmentMapper> sesMapper = SqlConnection.getSessionMapper(AttachmentMapper.class);
		attachments = sesMapper.getMapper().getByResult(res.getId());
		sesMapper.close();

		renderTable(attachments, modLink, delLink);
	}

	private void renderTable(final List<Attachment> attachments, boolean modLink, boolean delLink) {
		List<IColumn<Attachment>> columns = new ArrayList<IColumn<Attachment>>();
		columns.add(new AbstractColumn<Attachment>(new StringResourceModel("name", null)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Attachment>> item, String contentId, IModel<Attachment> model) {
				item.add(new PanelLink(contentId, model.getObject().getName(), AttachmentUtil.getFile(model.getObject()),model.getObject().getName()));
			}
			
			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		columns.add(new PropertyColumn<Attachment>(new StringResourceModel("description", null), "description"){
			private static final long serialVersionUID = 1L;
			@Override
			public String getCssClass() {
				return "maximizedColumn";
			}
		});
		if (delLink) {
			columns.add(new AbstractColumn<Attachment>(new StringResourceModel("delete", null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Attachment>> item, String contentId, final IModel<Attachment> model) {
					item.add(new PanelAjaxLink(contentId, "del") {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onAjaxClick(AjaxRequestTarget target) {
							SqlSessionMapper<AttachmentMapper> sesMapper = SqlConnection.getSessionMapper(AttachmentMapper.class);
							if (sesMapper.getMapper().delete(model.getObject()).equals(1)) {
								info("Attachment removed");
								AttachmentUtil.remove(model.getObject());
								sesMapper.commit();
							} else {
								error("SQL Error Attachment not removed");
								sesMapper.rollback();
							}
							attachments.remove(model.getObject());
							sesMapper.close();
							target.add(dataTable);
						}
					});
				}
			});
		}
		ListDataProvider<Attachment> dataProvider = new ListDataProvider<Attachment>(attachments);
		dataTable = new DataTableAlternatedRows<Attachment>("attachmentTable", columns, dataProvider, attachments.size()+1);
		dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
		dataTable.setOutputMarkupId(true);
		dataTable.addTopToolbar(new NoRecordsToolbar(dataTable));
		add(dataTable);
	}
	
	public int getSize(){
		return attachments.size();
	}

}
