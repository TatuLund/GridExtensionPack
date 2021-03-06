package org.vaadin.teemusa.gridextensions.demo;

import java.util.Random;

import org.vaadin.teemusa.gridextensions.client.tableselection.TableSelectionState.TableSelectionMode;
import org.vaadin.teemusa.gridextensions.pagedcontainer.PageChangeEvent;
import org.vaadin.teemusa.gridextensions.pagedcontainer.PageChangeListener;
import org.vaadin.teemusa.gridextensions.pagedcontainer.PagedContainer;
import org.vaadin.teemusa.gridextensions.pagedcontainer.PagedContainer.PagingControls;
import org.vaadin.teemusa.gridextensions.tableselection.TableSelectionModel;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.GridContextClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class DemoContentLayout extends VerticalLayout {

	public DemoContentLayout() {

		// Set up the PagedGridContainer with a backing Container.Indexed
		final PagedContainer container = new PagedContainer(createContainer());

		final Grid grid = new Grid(container);

		final PagingControls controls = container.setGrid(grid);
		controls.setPageLength(5);
		// Show page number 3 initially; Pages are 0-based indices
		controls.setPage(3);

		// Show it in the middle of the screen
		setStyleName("demoContentLayout");
		setSizeFull();
		addComponent(grid);
		setComponentAlignment(grid, Alignment.MIDDLE_CENTER);

		// TODO: Change this to use the new Vaadin ContextMenu Add-on
		grid.addContextClickListener(new ContextClickListener() {

			private GridContextMenu menu = new GridContextMenu(grid);

			@Override
			public void contextClick(ContextClickEvent event) {
				GridContextClickEvent e = (GridContextClickEvent) event;
				menu.setItemId(e.getItemId());
				menu.setColumn(grid.getColumn(e.getPropertyId()));
				menu.open(event.getClientX(), event.getClientY());
			}
		});

		final TableSelectionModel tableSelect = new TableSelectionModel();
		grid.setSelectionModel(tableSelect);
		tableSelect.setMode(TableSelectionMode.CTRL);

		HorizontalLayout tableSelectionControls = new HorizontalLayout();
		tableSelectionControls.setCaption("Table Selection Controls");

		// Controls for testing different TableSelectionModes
		for (final TableSelectionMode t : TableSelectionMode.values()) {
			tableSelectionControls.addComponent(new Button(t.toString(), new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					tableSelect.setMode(t);
				}
			}));
		}

		addComponent(tableSelectionControls);
		setComponentAlignment(tableSelectionControls, Alignment.BOTTOM_CENTER);

		// Controls for paging container. Next/Prev buttons and a jump to page
		// text field.
		HorizontalLayout pageControls = new HorizontalLayout();
		pageControls.addComponent(new Button("Previous page", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				controls.previousPage();
			}
		}));

		// Text field only allows integer values
		final TextField textField = new TextField("Page count: " + controls.getPageCount());
		textField.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				try {
					Integer.parseInt(value.toString());
				} catch (NumberFormatException e) {
					throw new InvalidValueException("Could not convert value to int");
				}
			}
		});

		// Starting value of 4 (page index 3)
		textField.setValue("" + (controls.getPage() + 1));
		textField.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				controls.setPage(Integer.parseInt(event.getProperty().getValue().toString()) - 1);
			}
		});

		controls.addPageChangeListener(new PageChangeListener() {

			@Override
			public void pageCountChange(PageChangeEvent event) {
				textField.setCaption("Page count: " + controls.getPageCount());
			}

			@Override
			public void pageChange(PageChangeEvent event) {
				textField.setValue("" + (controls.getPage() + 1));

			}
		});

		pageControls.addComponent(textField);
		pageControls.addComponent(new Button("Next page", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				controls.nextPage();
			}
		}));
		pageControls.addComponent(new Button("Random page length", new ClickListener() {

			private Random r = new Random();

			@Override
			public void buttonClick(ClickEvent event) {
				controls.setPageLength(3 + r.nextInt(8));
			}
		}));

		addComponent(pageControls);
		setComponentAlignment(pageControls, Alignment.BOTTOM_CENTER);

		setMargin(true);

		grid.setEditorEnabled(true);
		for (Column c : grid.getColumns()) {
			c.setHidable(true);
		}
	}

	@SuppressWarnings("unchecked")
	private Indexed createContainer() {
		Indexed wrappedContainer = new IndexedContainer();
		wrappedContainer.addContainerProperty("foo", String.class, "foo");
		wrappedContainer.addContainerProperty("bar", Integer.class, 0);
		// km contains double values from 0.0 to 2.0
		wrappedContainer.addContainerProperty("km", Double.class, 0);

		for (int i = 0; i <= 30; ++i) {
			Object itemId = wrappedContainer.addItem();
			Item item = wrappedContainer.getItem(itemId);
			item.getItemProperty("foo").setValue("foo");
			item.getItemProperty("bar").setValue(i);
			item.getItemProperty("km").setValue(i / 5.0d);
		}

		return wrappedContainer;
	}

}
