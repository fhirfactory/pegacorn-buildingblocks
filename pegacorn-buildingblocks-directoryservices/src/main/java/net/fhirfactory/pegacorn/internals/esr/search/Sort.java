package net.fhirfactory.pegacorn.internals.esr.search;

/**
 * Details used for sorting search results.
 * 
 * @author Brendan Douglas
 *
 */
public class Sort {

	private SortOrderEnum sortOrder = SortOrderEnum.ASCENDING;
	private String sortBy = "simplifiedID";

	public Sort() {
	}

	public Sort(String sortBy, String sortOrder) {
		
		if (sortBy != null) {
			this.sortBy = sortBy;
		}

		if (sortOrder != null) {
			if (sortOrder.equals("ascending")) {
				this.sortOrder = SortOrderEnum.ASCENDING;
			} else if (sortOrder.equals("descending")) {
				this.sortOrder = SortOrderEnum.DESCENDING;
			}
		}
	}

	public SortOrderEnum getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrderEnum sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public boolean isAscendingOrder() {
		return sortOrder == SortOrderEnum.ASCENDING;
	}

	public boolean isDescendingOrder() {
		return sortOrder == SortOrderEnum.DESCENDING;
	}
}
