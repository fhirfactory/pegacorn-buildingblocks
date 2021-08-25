package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	
	@Override
	public boolean equals(Object obj) {
		PractitionerLdapEntry other = (PractitionerLdapEntry)obj;
		
		for (Map.Entry<LdapAttributeNameEnum, String> attribute : this.getAttributes().entrySet()) {
			String otherAttribute = other.getAttributes().get(attribute.getKey());
			
			if (!otherAttribute.equals(attribute.getValue())) {
				return false; // Don't match.
			}
		}
		
		return true;
	}

	
	/**
	 * Returns a list of the attributes which have changed.
	 * 
	 * @param other
	 * @return
	 */
	public List<LdapAttributeNameEnum>getModifiedAttributeNames(PractitionerLdapEntry other) {
		List<LdapAttributeNameEnum> changed = new ArrayList<>();
		
		for (Map.Entry<LdapAttributeNameEnum, String> attribute : this.getAttributes().entrySet()) {
			String otherAttribute = other.getAttributes().get(attribute.getKey());
			
			if (!otherAttribute.equals(attribute.getValue())) {
				changed.add(attribute.getKey());
			}
		}		
		
		return changed;
	}
}
