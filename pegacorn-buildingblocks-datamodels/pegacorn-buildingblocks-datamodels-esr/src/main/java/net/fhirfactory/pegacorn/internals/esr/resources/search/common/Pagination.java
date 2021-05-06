package net.fhirfactory.pegacorn.internals.esr.resources.search.common;

/**
 * Search result paging details.
 * 
 * @author Brendan Douglas
 *
 */
public class Pagination {
    private static Integer DEFAULT_PAGE_SIZE = 25;
	
	private Integer pageSize = DEFAULT_PAGE_SIZE;
	private Integer pageNumber = 0;
	
	public Pagination() {
	}
	
	public Pagination(Integer pageNumber) {
		this(DEFAULT_PAGE_SIZE, pageNumber);
	}
	
	public Pagination(Integer pageSize, Integer pageNumber) {
		
		if (pageSize != null) {
			this.pageSize = pageSize;
		}
		
		if (pageNumber != null) {
			this.pageNumber = pageNumber;
		}
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
}
