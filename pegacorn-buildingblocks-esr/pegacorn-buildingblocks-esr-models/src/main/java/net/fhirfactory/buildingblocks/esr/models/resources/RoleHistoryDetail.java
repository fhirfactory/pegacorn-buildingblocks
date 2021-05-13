package net.fhirfactory.buildingblocks.esr.models.resources;

import java.util.Date;


/**
 * Details of a role.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleHistoryDetail {
	private String identifier;
	private Date startDate;
	private Date endDate;
	
	public RoleHistoryDetail() {}
	
	public RoleHistoryDetail(String identifier, Date startDate, Date endDate) {
		this.identifier = identifier;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	
	public String getIdentifier() {
		return identifier;
	}
	
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
