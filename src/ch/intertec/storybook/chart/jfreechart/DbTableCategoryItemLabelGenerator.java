package ch.intertec.storybook.chart.jfreechart;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

import ch.intertec.storybook.model.DbTable;
import ch.intertec.storybook.model.Location;

public class DbTableCategoryItemLabelGenerator implements
		CategoryItemLabelGenerator {

	@Override
	public String generateColumnLabel(CategoryDataset dataset, int column) {
		// not used
		return "x";
	}

	@Override
	public String generateLabel(CategoryDataset dataset, int row, int column) {
		DbTable dbTable = (DbTable) dataset.getRowKey(row);
		if(dbTable instanceof Location){
			return ((Location)dbTable).getName();
		}
		return dbTable.toString();
	}

	@Override
	public String generateRowLabel(CategoryDataset dataset, int row) {
		// not used
		return "x";
	}
}
