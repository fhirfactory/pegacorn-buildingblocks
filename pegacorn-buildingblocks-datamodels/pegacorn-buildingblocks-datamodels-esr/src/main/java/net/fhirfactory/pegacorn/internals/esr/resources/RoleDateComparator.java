package net.fhirfactory.pegacorn.internals.esr.resources;

import java.util.Comparator;

/**
 * A role date end date comparator. The record with the most current end date is ordered first.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleDateComparator implements Comparator<RoleHistoryDetail> {

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
