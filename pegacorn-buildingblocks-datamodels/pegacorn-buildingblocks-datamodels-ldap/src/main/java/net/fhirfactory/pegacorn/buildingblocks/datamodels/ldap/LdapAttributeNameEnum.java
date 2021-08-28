package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

/**
 * An enum of all the practitioner LDAP attributes.
 * 
 * @author Brendan Douglas
 *
 */
public enum LdapAttributeNameEnum {
	GIVEN_NAME("givenName"),
	SURNAME("sn"),
	JOB_TITLE("jobTitle"),
	MOBILE("mobile"),
	TELEPHONE_NUMBER("telephoneNumber"),
	EMPLOYEE_NUMBER("AGS"),
	PERSONAL_TITLE("personalTitle"),
	TITLE("TITLE"),
	PREFERRED_NAME("preferredFirstName"),
	SUFFIX("suffix"),
	BUSINESS_UNIT("businessUnit"),
	BRANCH("branch"),
	DIVISION("division"),
	SECTION("section"),
	SUB_SECTION("subSection"),
	DEPARTMENT("department"),
	EMAIL("mail"),
	PAGER("pager"),
	GS1("GS1"),
	IRN("IRN"),
	COMMON_NAME("cn"),
	DISTINGUISHED_NAME("dn"),
	ACCOUNT_NAME("sAMAccountName"),
	STREET_ADDRESS("streetAddress"),
	SUB_DEPT("SUBDEPT"),
	EXTENSION_ATTRIBUTE_4("extensionAttribute4"),
	OBJECT_CATEGORY("objectCategory");
	
	
	
	private String name;
	
	LdapAttributeNameEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	
	public static LdapAttributeNameEnum get(String name) {
		for (LdapAttributeNameEnum attribute : values()) {
			if (attribute.getName().equalsIgnoreCase(name)) {
				return attribute;
			}
		}
		
		throw new IllegalArgumentException("Unknown value: " + name);
	}
}