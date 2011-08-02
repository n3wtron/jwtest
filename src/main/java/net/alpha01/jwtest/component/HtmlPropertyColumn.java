package net.alpha01.jwtest.component;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class HtmlPropertyColumn<T> extends TextFilteredPropertyColumn<T,String>{
	private static final long serialVersionUID = 9087832091533454608L;
	public HtmlPropertyColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
		super(displayModel, sortProperty, propertyExpression);
	}
	
	public HtmlPropertyColumn(IModel<String> model, String string) {
		super(model,string);
	}
	
	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		item.add(new HtmlLabel(componentId,new PropertyModel<T>(rowModel, getPropertyExpression())));
	}

}
