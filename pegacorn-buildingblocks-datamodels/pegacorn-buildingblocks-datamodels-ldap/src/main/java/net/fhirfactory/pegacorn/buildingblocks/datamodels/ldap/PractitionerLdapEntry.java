package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

import java.util.HashMap;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.json.JSONObject;

/**
 * A single practitioner LDAP entry.
 * 
 * @author Brendan Douglas
 *
 */
public class PractitionerLdapEntry {
	private Map<LdapAttributeNameEnum, String> attributes = new HashMap<>();
	
	private String baseDN;

	public PractitionerLdapEntry(String baseDN) {
		this.baseDN = baseDN;
	}

	public PractitionerLdapEntry(Entry entry, String baseDN) throws LdapInvalidAttributeValueException {
		this(baseDN);

		if (entry.get(LdapAttributeNameEnum.GIVEN_NAME.getName()) != null) {
			setGivenName(entry.get(LdapAttributeNameEnum.GIVEN_NAME.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.SURNAME.getName()) != null) {
			setSurname(entry.get(LdapAttributeNameEnum.SURNAME.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.JOB_TITLE.getName()) != null) {
			setJobTitle(entry.get(LdapAttributeNameEnum.JOB_TITLE.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.PERSONAL_TITLE.getName()) != null) {
			setPersonalTitle(entry.get(LdapAttributeNameEnum.PERSONAL_TITLE.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.PREFERRED_NAME.getName()) != null) {
			setPreferredName(entry.get(LdapAttributeNameEnum.PREFERRED_NAME.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.SUFFIX.getName()) != null) {
			setSuffix(entry.get(LdapAttributeNameEnum.SUFFIX.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.EMAIL.getName()) != null) {
			setEmailAddress(entry.get(LdapAttributeNameEnum.EMAIL.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.TELEPHONE_NUMBER.getName()) != null) {
			setTelephoneNumber(entry.get(LdapAttributeNameEnum.TELEPHONE_NUMBER.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.MOBILE.getName()) != null) {
			setMobileNumber(entry.get(LdapAttributeNameEnum.MOBILE.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.PAGER.getName()) != null) {
			setPager(entry.get(LdapAttributeNameEnum.PAGER.getName()).getString());
		}
	
		if (entry.get(LdapAttributeNameEnum.DIVISION.getName()) != null) {
			setDivision(entry.get(LdapAttributeNameEnum.DIVISION.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.DEPARTMENT.getName()) != null) {
			setDepartment(entry.get(LdapAttributeNameEnum.DEPARTMENT.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.BRANCH.getName()) != null) {
			setBranch(entry.get(LdapAttributeNameEnum.BRANCH.getName()).getString());
		}
	
		if (entry.get(LdapAttributeNameEnum.SECTION.getName()) != null) {
			setSection(entry.get(LdapAttributeNameEnum.SECTION.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.SUB_SECTION.getName()) != null) {
			setSubSection(entry.get(LdapAttributeNameEnum.SUB_SECTION.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.BUSINESS_UNIT.getName()) != null) {
			setBusinessUnit(entry.get(LdapAttributeNameEnum.BUSINESS_UNIT.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.EMPLOYEE_NUMBER.getName()) != null) {
			setAgsNumber(entry.get(LdapAttributeNameEnum.EMPLOYEE_NUMBER.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.GS1.getName()) != null) {
			setGS1(entry.get(LdapAttributeNameEnum.GS1.getName()).getString());
		}
		
		if (entry.get(LdapAttributeNameEnum.IRN.getName()) != null) {
			setIRN(entry.get(LdapAttributeNameEnum.IRN.getName()).getString());
		}	
		
		if (entry.get(LdapAttributeNameEnum.ACCOUNT_NAME.getName()) != null) {
			setAccountName(entry.get(LdapAttributeNameEnum.ACCOUNT_NAME.getName()).getString());
		}	
		
		if (entry.get(LdapAttributeNameEnum.TITLE.getName()) != null) {
			setTitle(entry.get(LdapAttributeNameEnum.TITLE.getName()).getString());
		}			
		
		if (entry.get(LdapAttributeNameEnum.SUB_DEPT.getName()) != null) {
			setSubDepartment(entry.get(LdapAttributeNameEnum.SUB_DEPT.getName()).getString());
		}	
		
		if (entry.get(LdapAttributeNameEnum.STREET_ADDRESS.getName()) != null) {
			setStreetAddress(entry.get(LdapAttributeNameEnum.STREET_ADDRESS.getName()).getString());
		}	
		
		if (entry.get(LdapAttributeNameEnum.EXTENSION_ATTRIBUTE_4.getName()) != null) {
			setExtensionAttribute4(entry.get(LdapAttributeNameEnum.EXTENSION_ATTRIBUTE_4.getName()).getString());
		}	
		
		if (entry.get(LdapAttributeNameEnum.OBJECT_CATEGORY.getName()) != null) {
			setObjectCategory(entry.get(LdapAttributeNameEnum.OBJECT_CATEGORY.getName()).getString());
		}	
		
		setCommonName(entry.get(LdapAttributeNameEnum.COMMON_NAME.getName()).getString());
	}
	
	/**
	 * Creates an LDAP entry from a JSON representation of an LDAP entry.  
	 * 
	 * @param baseDN
	 * @param entry
	 */
	public PractitionerLdapEntry(String baseDN, JSONObject entry) {
		this(baseDN);
		
		String[] attributeNames = JSONObject.getNames(entry);
		
		for (String attributeName : attributeNames) {
			LdapAttributeNameEnum attributeEnumVal = LdapAttributeNameEnum.get(attributeName);
			
			attributes.put(attributeEnumVal, entry.get(attributeName).toString());
		}
	}
	
	public String getDN() {
		return LdapAttributeNameEnum.COMMON_NAME.getName() + "=" + (getCommonName().replace(",","\\,")) + "," + baseDN;
	}

	public String getCommonName() {
		return nullSafeGet(LdapAttributeNameEnum.COMMON_NAME);
	}

	public String getGivenName() {
		return nullSafeGet(LdapAttributeNameEnum.GIVEN_NAME);
	}

	public void setGivenName(String firstName) {
		attributes.put(LdapAttributeNameEnum.GIVEN_NAME, firstName);
	}

	public String getSurname() {
		return nullSafeGet(LdapAttributeNameEnum.SURNAME);
	}

	public void setSurname(String lastName) {
		attributes.put(LdapAttributeNameEnum.SURNAME, lastName);
	}

	public String getPersonalTitle() {
		return nullSafeGet(LdapAttributeNameEnum.PERSONAL_TITLE);
	}

	public void setPersonalTitle(String title) {
		attributes.put(LdapAttributeNameEnum.PERSONAL_TITLE, title);
	}

	public String getPreferredName() {
		return nullSafeGet(LdapAttributeNameEnum.PREFERRED_NAME);
	}

	public void setPreferredName(String preferredName) {
		attributes.put(LdapAttributeNameEnum.PREFERRED_NAME, preferredName);
	}

	public String getSuffix() {
		return nullSafeGet(LdapAttributeNameEnum.SUFFIX);
	}

	public void setSuffix(String suffixes) {
		attributes.put(LdapAttributeNameEnum.SUFFIX, suffixes);
	}

	public String getEmailAddress() {
		return nullSafeGet(LdapAttributeNameEnum.EMAIL);
	}

	public void setEmailAddress(String emailAddress) {
		attributes.put(LdapAttributeNameEnum.EMAIL, emailAddress);
	}

	public String getPhoneNumber() {
		return nullSafeGet(LdapAttributeNameEnum.TELEPHONE_NUMBER);
	}

	public void setTelephoneNumber(String phoneNumber) {
		attributes.put(LdapAttributeNameEnum.TELEPHONE_NUMBER, phoneNumber);
	}

	public String getMobileNumber() {
		return nullSafeGet(LdapAttributeNameEnum.MOBILE);
	}

	public void setMobileNumber(String mobileNumber) {
		attributes.put(LdapAttributeNameEnum.MOBILE, mobileNumber);
	}

	public String getPager() {
		return nullSafeGet(LdapAttributeNameEnum.PAGER);
	}

	public void setPager(String pager) {
		attributes.put(LdapAttributeNameEnum.PAGER, pager);
	}

	public String getJobTitle() {
		return nullSafeGet(LdapAttributeNameEnum.JOB_TITLE);
	}

	public void setJobTitle(String jobTitle) {
		attributes.put(LdapAttributeNameEnum.JOB_TITLE, jobTitle);
	}

	public String getBusinessUnit() {
		return nullSafeGet(LdapAttributeNameEnum.BUSINESS_UNIT);
	}

	public void setBusinessUnit(String businessUnit) {
		attributes.put(LdapAttributeNameEnum.BUSINESS_UNIT, businessUnit);
	}

	public String getDivision() {
		return nullSafeGet(LdapAttributeNameEnum.DIVISION);
	}

	public void setDivision(String division) {
		attributes.put(LdapAttributeNameEnum.DIVISION, division);
	}

	public String getBranch() {
		return nullSafeGet(LdapAttributeNameEnum.BRANCH);
	}

	public void setBranch(String branch) {
		attributes.put(LdapAttributeNameEnum.BRANCH, branch);
	}
	
	public String getDepartment() {
		return nullSafeGet(LdapAttributeNameEnum.DEPARTMENT);
	}

	public void setDepartment(String department) {
		attributes.put(LdapAttributeNameEnum.DEPARTMENT, department);
	}

	public String getSection() {
		return nullSafeGet(LdapAttributeNameEnum.SECTION);
	}

	public void setSection(String section) {
		attributes.put(LdapAttributeNameEnum.SECTION, section);
	}

	public String getSubSection() {
		return nullSafeGet(LdapAttributeNameEnum.SUB_SECTION);
	}

	public void setSubSection(String subSection) {
		attributes.put(LdapAttributeNameEnum.SUB_SECTION, subSection);
	}

	public String getAgsNumber() {
		return nullSafeGet(LdapAttributeNameEnum.EMPLOYEE_NUMBER);
	}

	public void setAgsNumber(String agsNumber) {
		attributes.put(LdapAttributeNameEnum.EMPLOYEE_NUMBER, agsNumber);
	}
	
	public String getTitle() {
		return nullSafeGet(LdapAttributeNameEnum.TITLE);
	}

	public void setTitle(String title) {
		attributes.put(LdapAttributeNameEnum.TITLE, title);
	}
	
	public String getGS1() {
		return nullSafeGet(LdapAttributeNameEnum.GS1);
	}

	public void setGS1(String gs1) {
		attributes.put(LdapAttributeNameEnum.GS1, gs1);
	}

	public String getIRN() {
		return nullSafeGet(LdapAttributeNameEnum.IRN);
	}

	public void setIRN(String irn) {
		attributes.put(LdapAttributeNameEnum.IRN, irn);
	}
	
	public void setDN(String dn) {
		attributes.put(LdapAttributeNameEnum.DISTINGUISHED_NAME, dn); 
	}

	public void setCommonName(String commonName) {
		attributes.put(LdapAttributeNameEnum.COMMON_NAME, commonName); 
	}
	
	public String getAccountName() {
		return nullSafeGet(LdapAttributeNameEnum.ACCOUNT_NAME);
	}

	public void setAccountName(String title) {
		attributes.put(LdapAttributeNameEnum.ACCOUNT_NAME, title);
	}
	
	public void setSubDepartment(String subDepartment) {
		attributes.put(LdapAttributeNameEnum.SUB_DEPT, subDepartment);
	}

	public String getSubDepartment() {
		return nullSafeGet(LdapAttributeNameEnum.SUB_DEPT);
	}
	
	public void setStreetAddress(String streetAddress) {
		attributes.put(LdapAttributeNameEnum.STREET_ADDRESS, streetAddress);
	}

	public String getStreetAddress() {
		return nullSafeGet(LdapAttributeNameEnum.STREET_ADDRESS);
	}
	
	public void setExtensionAttribute4(String extensionAttribute4) {
		attributes.put(LdapAttributeNameEnum.EXTENSION_ATTRIBUTE_4, extensionAttribute4);
	}

	public String getExtensionAttribute4() {
		return nullSafeGet(LdapAttributeNameEnum.EXTENSION_ATTRIBUTE_4);
	}
	
	public void setObjectCategory(String objectCategory) {
		attributes.put(LdapAttributeNameEnum.OBJECT_CATEGORY, objectCategory);
	}

	public String getObjectCategory() {
		return nullSafeGet(LdapAttributeNameEnum.OBJECT_CATEGORY);
	}
	
	
	public JSONObject asJson() {
		JSONObject entry = new JSONObject();
		
		for (Map.Entry<LdapAttributeNameEnum, String> attribute : this.getAttributes().entrySet()) {
			entry.put(attribute.getKey().getName(), attribute.getValue());
		}
		
		return entry;
	}
	
	
	@Override
	public String toString() {
		return asJson().toString();
	}
	
	
	public Map<LdapAttributeNameEnum, String> getAttributes() {
		return attributes;
	}
	
	
	private String nullSafeGet(LdapAttributeNameEnum attributeName) {
		if (attributes.get(attributeName) != null) {
			return attributes.get(attributeName);
		}
		
		return "";
	}
}
