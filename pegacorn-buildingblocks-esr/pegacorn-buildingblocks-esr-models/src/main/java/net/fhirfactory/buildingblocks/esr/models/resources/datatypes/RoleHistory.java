package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Role history.  Provides methods to get current and previous roles.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleHistory {
    private static final Logger LOG = LoggerFactory.getLogger(RoleHistory.class);
	
	private List<RoleHistoryDetail> roleHistories;
	
	public RoleHistory() {
		roleHistories = new ArrayList<>();
	}
	
	public void add(String role) {
		roleHistories.add(new RoleHistoryDetail(role, new Date(), null));
	}
	
	
	/**
	 * Updates the roles list.  Roles can be added or end dated.
	 * 
	 * @param newRoles
	 */
	public void update(List<String>updateRoleList) {
		
		// Add any new roles
		for (String newRole : updateRoleList) {
			RoleHistoryDetail current = getCurrentRole(newRole);
			
			if (current == null) {
				add(newRole);
			}
		}
		
		// Delete (end date) roles removed
		for (RoleHistoryDetail roleHistoryDetail : getAllCurrentRoles()) {
			
			// If the existing list contains one not in the new list then it has been removed so end date
			if (!updateRoleList.contains(roleHistoryDetail.getRole())) {
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
	public List<RoleHistoryDetail>getAllCurrentRoles() {
		List<RoleHistoryDetail>current = new ArrayList<>();
		
		for (RoleHistoryDetail roleHistoryDetail : roleHistories) {
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
	public List<RoleHistoryDetail>getAllPreviousRoles() {
		List<RoleHistoryDetail>previous = new ArrayList<>();
		
		for (RoleHistoryDetail roleHistory : roleHistories) {
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
	public List<String> getAllCurrentPractitionerRolesSet() {
		List<String>currentAsString = new ArrayList<>();		
		
		for (RoleHistoryDetail current : getAllCurrentRoles()) {
			currentAsString.add(current.getRole());
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
		
		for (RoleHistoryDetail previous : getAllPreviousRoles()) {
			previousAsString.add(previous.getRole());
		}
		
		return previousAsString;
	}
	
	
	/**
	 * @param num
	 * @param removeDuplicates
	 * @return
	 */
	public List<String> getPreviousRolesAsString(int num, boolean removeDuplicates) {		
		List<RoleHistoryDetail>previous = getAllPreviousRoles();
		
		// Step 1: Sort.
		Collections.sort(previous, new RoleEndDateComparator());
		
		// Step 2: Remove the duplicates.
		if (removeDuplicates) {
			Set<String>uniqueSet = new HashSet<>();
			
			for (Iterator<RoleHistoryDetail> it = previous.iterator(); it.hasNext();) {
				
				// Try and add to the set, if the add method returns false, it means the role already exists.
			    if (!uniqueSet.add(it.next().getRole())) {
			        it.remove();
			    }
			}
		}
		
		
		// Step 3: Remove any from the list which are also in the current list.
		Set<String>uniqueSet = new HashSet<>();
		
		// Add all the current roles to the set.
		for (RoleHistoryDetail currentRoleDetail : getAllCurrentRoles()) {
			uniqueSet.add(currentRoleDetail.getRole());
		}
		
		// Now attempt to add the previous roles to the set.  If it can't be added it means the previous role is also a current role so remove from the final list.
		for (Iterator<RoleHistoryDetail> it = previous.iterator(); it.hasNext();) {
			
			// Try and add to the set, if the add method returns false, it means the role already exists.
		    if (!uniqueSet.add(it.next().getRole())) {
		        it.remove();
		    }
		}		


		// Step 4: Return the result up to the num size.
		List<String>finalRoleList = new ArrayList<>();
			
		for (RoleHistoryDetail previousRoleDetail : previous) {
			finalRoleList.add(previousRoleDetail.getRole());
		}
			
		
		if (previous.size() < num) {
			return finalRoleList;
		}
			
		return finalRoleList.subList(0, num);
	}
	
	
	public List<RoleHistoryDetail>getFirst(int number) {
		return roleHistories;
	}

	
	/**
	 * Rrturns the current {@link RoleHistoryDetail} for the supplied role.
	 * 
	 * @param role
	 * @return
	 */
	public RoleHistoryDetail getCurrentRole(String role) {
		for (RoleHistoryDetail roleDetail : roleHistories) {
			if (roleDetail.getEndDate() == null && roleDetail.getRole().contains(role)) {
				return roleDetail;
			}
		}
		
		return null;
	}

	public boolean isEmpty() {
		for (RoleHistoryDetail roleHistory : roleHistories) {
			if (roleHistory.getEndDate() == null) {
				return false;
			}
		}
		
		return true;
	}


	public List<RoleHistoryDetail> getRoleHistories() {
		return roleHistories;
	}


	public void setRoleHistories(List<RoleHistoryDetail> roleHistories) {
		this.roleHistories = roleHistories;
	}	
	
	
	public RoleHistoryDetail getMostRecentSelection() {
		if (roleHistories.isEmpty()) {
			return null;
		}
		
		// Order the records and select the first
		Collections.sort(roleHistories, new RoleStartDateComparator());
		
		return roleHistories.get(0);	
	}
}


/**
 * A role date end date comparator. The record with the most current end date is ordered first.
 * 
 * @author Brendan Douglas
 *
 */
class RoleEndDateComparator implements Comparator<RoleHistoryDetail> {

	@Override
	public int compare(RoleHistoryDetail first, RoleHistoryDetail second) {
		if (first.getEndDate() == null && second.getEndDate() == null) {
			return 0;
		}

		if (first == null) {
			return -1;
		}

		if (second == null) {
			return 1;
		}

		return second.getEndDate().compareTo(first.getEndDate());
	}
}


/**
 * A role date end date comparator. The record with the most current start date is ordered first.
 * 
 * @author Brendan Douglas
 *
 */
class RoleStartDateComparator implements Comparator<RoleHistoryDetail> {

	@Override
	public int compare(RoleHistoryDetail first, RoleHistoryDetail second) {
		if (first.getStartDate() == null && second.getStartDate() == null) {
			return 0;
		}

		if (first == null) {
			return -1;
		}

		if (second == null) {
			return 1;
		}

		return second.getStartDate().compareTo(first.getStartDate());
	}

}
