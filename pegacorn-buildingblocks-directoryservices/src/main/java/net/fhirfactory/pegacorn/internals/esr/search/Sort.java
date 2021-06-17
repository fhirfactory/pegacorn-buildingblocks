package net.fhirfactory.pegacorn.internals.esr.search;

/**
 * Details used for sorting search results.
 * 
 * @author Brendan Douglas
 *
 */
public class Sort {

	private SortOrderEnum sortOrder = SortOrderEnum.ASCENDING;
	private SortParamNames sortBy = SortParamNames.SIMPLIFIED_ID;

	public Sort() {
	}

	public Sort(String sortBy, String sortOrder) {
		
		if (sortBy != null) {
			this.sortBy = SortParamNames.get(sortBy);
		}

		if (sortOrder != null) {
		    this.sortOrder = SortOrderEnum.get(sortOrder);
		}
	}

	public SortOrderEnum getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrderEnum sortOrder) {
		this.sortOrder = sortOrder;
	}

	public SortParamNames getSortBy() {
		return sortBy;
	}

	public void setSortBy(SortParamNames sortBy) {
		this.sortBy = sortBy;
	}

	public boolean isAscendingOrder() {
		return sortOrder == SortOrderEnum.ASCENDING;
	}

	public boolean isDescendingOrder() {
		return sortOrder == SortOrderEnum.DESCENDING;
	}
}
