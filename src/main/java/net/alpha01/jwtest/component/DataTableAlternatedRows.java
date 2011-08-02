package net.alpha01.jwtest.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class DataTableAlternatedRows<T> extends DataTable<T> {
	private static final long serialVersionUID = 1L;

	public DataTableAlternatedRows(String id, IColumn<T>[] columns, IDataProvider<T> dataProvider, int rowsPerPage) {
		super(id, columns, dataProvider, rowsPerPage);
	}

	protected Item<T> newRowItem(String id, int index, IModel<T> model) {
		Item<T> item = new Item<T>(id, index, model);
		String cssClass;
		if (index % 2 == 0) {
			cssClass = "pairRow";
		} else {
			cssClass = "unpairRow";
		}
		if (getRowCount() == 1) {
			cssClass += " lastRow";
		} else {
			int lastRow;
			if ((getCurrentPage() + 1) * getRowsPerPage() >= getRowCount()) {
				// last page
				lastRow = getRowCount() - (getCurrentPage() * getRowsPerPage());
			} else {
				// intermediate Page
				lastRow = getRowsPerPage();
			}
			if (getRowCount() > 1 && (index + 1 == lastRow)) {
				cssClass += " lastRow";
			}
		}
		item.add(new AttributeModifier("class", true, new Model<String>(cssClass)));

		return item;
	};

}
