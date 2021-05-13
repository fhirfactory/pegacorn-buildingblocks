package net.fhirfactory.buildingblocks.esr.models.resources;

import java.util.Comparator;

/**
 * A role date end date comparator. The record with the most current start date is ordered first.
 * 
 * @author Brendan Douglas
 *
 */
public class RoleStartDateComparator implements Comparator<RoleHistoryDetail> {

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
