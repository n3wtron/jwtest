package net.alpha01.jwtest.component;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CleanTextPropertyColumn<T> extends TextFilteredPropertyColumn<T,String>{
	private static final long serialVersionUID = -705271974547167557L;
	public CleanTextPropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
	}
	public CleanTextPropertyColumn(Model<String> model, String string) {
		super(model,string);
	}
	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		item.add(new HtmlLabel(componentId,new CleanTextPropertyModel(rowModel, getPropertyExpression())));
	}

}
