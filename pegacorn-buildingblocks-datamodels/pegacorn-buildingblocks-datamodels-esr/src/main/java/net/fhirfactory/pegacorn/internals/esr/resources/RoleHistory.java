package net.fhirfactory.pegacorn.internals.esr.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Role history.  Provides methods to get current and previous roles.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleHistory {
    private static final Logger LOG = LoggerFactory.getLogger(RoleHistory.class);
	
	private List<RoleDetail> roleHistories;
	
	public RoleHistory() {
		roleHistories = new ArrayList<>();
	}
	
	public void add(String role) {
		roleHistories.add(new RoleDetail(role, new Date(), null));
	}
	
	
	/**
	 * Updates the roles list.  Roles can be added or end dated.
	 * 
	 * @param newRoles
	 */
	public void update(List<String>updateRoleList) {
		
		// Add any new roles
		for (String newRole : updateRoleList) {
			RoleDetail current = getCurrentRole(newRole);
			
			if (current == null) {
				add(newRole);
			}
		}
		
		// Delete (end date) roles removed
		for (RoleDetail roleHistoryDetail : getAllCurrentRoles()) {
			
			// If the existing list contains one not in the new list then it has been removed so end date
			if (!updateRoleList.contains(roleHistoryDetail.getIdentifier())) {
				roleHistoryDetail.setEndDate(new Date());
			}
		}
	}
	
	
	/**
	 * Returns all current roles.
	 * 
	 * @param number
	 * @return
	 */
	public List<RoleDetail>getAllCurrentRoles() {
		List<RoleDetail>current = new ArrayList<>();
		
		for (RoleDetail roleHistoryDetail : roleHistories) {
			if (roleHistoryDetail.getEndDate() == null) {
				current.add(roleHistoryDetail);
			}
		}
		
		return current;
	}
	
	
	/**
	 * Returns all previous roles.
	 * 
	 * @param number
	 * @return
	 */
	public List<RoleDetail>getAllPreviousRoles() {
		List<RoleDetail>previous = new ArrayList<>();
		
		for (RoleDetail roleHistory : roleHistories) {
			if (roleHistory.getEndDate() != null) {
				previous.add(roleHistory);
			}
		}
		
		return previous;
	}
	
	
	/**
	 * Returns all current roles names;
	 * 
	 * @return
	 */
	public List<String> getAllCurrentRolesAsString() {
		List<String>currentAsString = new ArrayList<>();		
		
		for (RoleDetail current : getAllCurrentRoles()) {
			currentAsString.add(current.getIdentifier());
		}
		
		return currentAsString;
	}
	
	
	/**
	 * Returns all previous roles names;
	 * 
	 * @return
	 */
	public List<String> getAllPreviousRolesAsString() {
		List<String>previousAsString = new ArrayList<>();		
		
		for (RoleDetail previous : getAllPreviousRoles()) {
			previousAsString.add(previous.getIdentifier());
		}
		
		return previousAsString;
	}
	
	
	/**
	 * @param num
	 * @param removeDuplicates
	 * @return
	 */
	public List<String> getPreviousRolesAsString(int num, boolean removeDuplicates) {		
		List<RoleDetail>previous = getAllPreviousRoles();
		
		// Step 1: Sort.
		Collections.sort(previous, new RoleDateComparator());
		
		// Step 2: Remove the duplicates.
		if (removeDuplicates) {
			Set<String>uniqueSet = new HashSet<>();
			
			for (Iterator<RoleDetail> it = previous.iterator(); it.hasNext();) {
				
				// Try and add to the set, if the add method returns false, it means the role already exists.
			    if (!uniqueSet.add(it.next().getIdentifier())) {
			        it.remove();
			    }
			}
		}
		
		
		// Step 3: Remove any from the list which are also in the current list.
		Set<String>uniqueSet = new HashSet<>();
		
		// Add all the current roles to the set.
		for (RoleDetail currentRoleDetail : getAllCurrentRoles()) {
			uniqueSet.add(currentRoleDetail.getIdentifier());
		}
		
		// Now attempt to add the previous roles to the set.  If it can't be added it means the previous role is also a current role so remove from the final list.
		for (Iterator<RoleDetail> it = previous.iterator(); it.hasNext();) {
			
			// Try and add to the set, if the add method returns false, it means the role already exists.
		    if (!uniqueSet.add(it.next().getIdentifier())) {
		        it.remove();
		    }
		}		


		// Step 4: Return the result up to the num size.
		List<String>finalRoleList = new ArrayList<>();
			
		for (RoleDetail previousRoleDetail : previous) {
			finalRoleList.add(previousRoleDetail.getIdentifier());
		}
			
		
		if (previous.size() < num) {
			return finalRoleList;
		}
			
		return finalRoleList.subList(0, num);
	}
	
	
	public List<RoleDetail>getFirst(int number) {
		return roleHistories;
	}

	
	/**
	 * Rrturns the current {@link RoleDetail} for the supplied role.
	 * 
	 * @param role
	 * @return
	 */
	public RoleDetail getCurrentRole(String role) {
		for (RoleDetail roleDetail : roleHistories) {
			if (roleDetail.getEndDate() == null && roleDetail.getIdentifier().equals(role)) {
				return roleDetail;
			}
		}
		
		return null;
	}

	public boolean isEmpty() {
		for (RoleDetail roleHistory : roleHistories) {
			if (roleHistory.getEndDate() == null) {
				return false;
			}
		}
		
		return true;
	}


	public List<RoleDetail> getRoleHistories() {
		return roleHistories;
	}


	public void setRoleHistories(List<RoleDetail> roleHistories) {
		this.roleHistories = roleHistories;
	}	
}
