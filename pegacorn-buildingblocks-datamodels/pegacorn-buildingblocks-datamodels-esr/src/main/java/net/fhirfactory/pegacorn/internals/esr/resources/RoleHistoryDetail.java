package net.fhirfactory.pegacorn.internals.esr.resources;

import java.util.Date;


/**
 * Details of a role.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleHistoryDetail {
	private String identifier;
	private Date createdDate;
	private Date endDate;
	
	public RoleHistoryDetail() {}
	
	public RoleHistoryDetail(String identifier, Date createdDate, Date endDate) {
		this.identifier = identifier;
		this.createdDate = createdDate;
		this.endDate = endDate;
	}
	
	
	public String getIdentifier() {
		return identifier;
	}
	
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}
	
	public void setCreatedDate(Date created) {
		this.createdDate = created;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
