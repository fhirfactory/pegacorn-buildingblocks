package net.fhirfactory.buildingblocks.esr.resources.datatypes;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Details of a role.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleHistoryDetail {
	private String role;
	
	@JsonIgnore
	private Date startDate;
	
	@JsonIgnore
	private Date endDate;
		
	public RoleHistoryDetail() {}
	
	public RoleHistoryDetail(String role, Date startDate, Date endDate) {
		this.role = role;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	
	public String getRole() {
		return role;
	}
	
	
	public void setRole(String role) {
		this.role = role;
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
