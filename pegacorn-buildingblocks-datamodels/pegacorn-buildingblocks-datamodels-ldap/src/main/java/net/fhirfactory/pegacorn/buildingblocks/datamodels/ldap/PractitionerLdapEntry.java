package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

import java.util.HashMap;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;

/**
 * A single practitioner LDAP entry.
 * 
 * @author Brendan Douglas
 *
 */
public class PractitionerLdapEntry {
	private Map<LdapAttributeNameEnum, LdapAttribute> attributes = new HashMap<>();

	private String baseDN;

	public PractitionerLdapEntry(String baseDN) {
		this.baseDN = baseDN;
	}

	public PractitionerLdapEntry(String baseDN, Entry entry) throws LdapInvalidAttributeValueException {
		this.baseDN = baseDN;
	
		setGivenName(entry.get(LdapAttributeNameEnum.GIVEN_NAME.getName()).getString());		
		setSurname(entry.get(LdapAttributeNameEnum.SURNAME.getName()).getString());
		setTitles(entry.get(LdapAttributeNameEnum.JOB_TITLE.getName()).getString());
		setPreferredName(entry.get(LdapAttributeNameEnum.PREFERRED_NAME.getName()).getString());
		setSuffixes(entry.get(LdapAttributeNameEnum.SUFFIXES.getName()).getString());
		setEmailAddress(entry.get(LdapAttributeNameEnum.EMAIL.getName()).getString());
		setPhoneNumber(entry.get(LdapAttributeNameEnum.TELEPHONE_NUMBER.getName()).getString());
		setMobileNumber(entry.get(LdapAttributeNameEnum.MOBILE.getName()).getString());
		setPager(entry.get(LdapAttributeNameEnum.PAGER.getName()).getString());
		setDivision(entry.get(LdapAttributeNameEnum.DIVISION.getName()).getString());
		setBranch(entry.get(LdapAttributeNameEnum.BRANCH.getName()).getString());
		setSection(entry.get(LdapAttributeNameEnum.SECTION.getName()).getString());
		setSubSection(entry.get(LdapAttributeNameEnum.SUB_SECTION.getName()).getString());
		setAgsNumber(entry.get(LdapAttributeNameEnum.EMPLOYEE_NUMBER.getName()).getString());	
		setGS1(entry.get(LdapAttributeNameEnum.GS1.getName()).getString());
		setIRN(entry.get(LdapAttributeNameEnum.IRN.getName()).getString());
	}

	public LdapAttribute getGivenName() {
		return attributes.get(LdapAttributeNameEnum.GIVEN_NAME);
	}

	public void setGivenName(String firstName) {
		attributes.put(LdapAttributeNameEnum.GIVEN_NAME, new LdapAttribute(LdapAttributeNameEnum.GIVEN_NAME, firstName));
	}

	public LdapAttribute getSurname() {
		return attributes.get(LdapAttributeNameEnum.SURNAME);
	}

	public void setSurname(String lastName) {
		attributes.put(LdapAttributeNameEnum.SURNAME, new LdapAttribute(LdapAttributeNameEnum.SURNAME, lastName));
	}

	public LdapAttribute getTitles() {
		return attributes.get(LdapAttributeNameEnum.PERSONAL_TITLE);
	}

	public void setTitles(String titles) {
		attributes.put(LdapAttributeNameEnum.PERSONAL_TITLE, new LdapAttribute(LdapAttributeNameEnum.PERSONAL_TITLE, titles));
	}

	public LdapAttribute getPreferredName() {
		return attributes.get(LdapAttributeNameEnum.PREFERRED_NAME);
	}

	public void setPreferredName(String preferredName) {
		attributes.put(LdapAttributeNameEnum.PREFERRED_NAME, new LdapAttribute(LdapAttributeNameEnum.PREFERRED_NAME, preferredName));
	}

	public LdapAttribute getSuffixes() {
		return attributes.get(LdapAttributeNameEnum.SUFFIXES);
	}

	public void setSuffixes(String suffixes) {
		attributes.put(LdapAttributeNameEnum.SUFFIXES, new LdapAttribute(LdapAttributeNameEnum.SUFFIXES, suffixes));
	}

	public LdapAttribute getEmailAddress() {
		return attributes.get(LdapAttributeNameEnum.EMAIL);
	}

	public void setEmailAddress(String emailAddress) {
		attributes.put(LdapAttributeNameEnum.EMAIL, new LdapAttribute(LdapAttributeNameEnum.EMAIL, emailAddress));
	}

	public LdapAttribute getPhoneNumber() {
		return attributes.get(LdapAttributeNameEnum.TELEPHONE_NUMBER);
	}

	public void setPhoneNumber(String phoneNumber) {
		attributes.put(LdapAttributeNameEnum.TELEPHONE_NUMBER, new LdapAttribute(LdapAttributeNameEnum.TELEPHONE_NUMBER, phoneNumber));
	}

	public LdapAttribute getMobileNumber() {
		return attributes.get(LdapAttributeNameEnum.MOBILE);
	}

	public void setMobileNumber(String mobileNumber) {
		attributes.put(LdapAttributeNameEnum.MOBILE, new LdapAttribute(LdapAttributeNameEnum.MOBILE, mobileNumber));
	}

	public LdapAttribute getPager() {
		return attributes.get(LdapAttributeNameEnum.PAGER);
	}

	public void setPager(String pager) {
		attributes.put(LdapAttributeNameEnum.PAGER, new LdapAttribute(LdapAttributeNameEnum.PAGER, pager));
	}

	public LdapAttribute getJobTitle() {
		return attributes.get(LdapAttributeNameEnum.JOB_TITLE);
	}

	public void setJobTitle(String jobTitle) {
		attributes.put(LdapAttributeNameEnum.JOB_TITLE, new LdapAttribute(LdapAttributeNameEnum.JOB_TITLE, jobTitle));
	}

	public LdapAttribute getBusinessUnit() {
		return attributes.get(LdapAttributeNameEnum.BUSINESS_UNIT);
	}

	public void setBusinessUnit(String businessUnit) {
		attributes.put(LdapAttributeNameEnum.BUSINESS_UNIT, new LdapAttribute(LdapAttributeNameEnum.BUSINESS_UNIT, businessUnit));
	}

	public LdapAttribute getDivision() {
		return attributes.get(LdapAttributeNameEnum.DIVISION);
	}

	public void setDivision(String division) {
		attributes.put(LdapAttributeNameEnum.DIVISION, new LdapAttribute(LdapAttributeNameEnum.DIVISION, division));
	}

	public LdapAttribute getBranch() {
		return attributes.get(LdapAttributeNameEnum.BRANCH);
	}

	public void setBranch(String branch) {
		attributes.put(LdapAttributeNameEnum.BRANCH, new LdapAttribute(LdapAttributeNameEnum.BRANCH, branch));
	}

	public LdapAttribute getSection() {
		return attributes.get(LdapAttributeNameEnum.SECTION);
	}

	public void setSection(String section) {
		attributes.put(LdapAttributeNameEnum.SECTION, new LdapAttribute(LdapAttributeNameEnum.SECTION, section));
	}

	public LdapAttribute getSubSection() {
		return attributes.get(LdapAttributeNameEnum.SUB_SECTION);
	}

	public void setSubSection(String subSection) {
		attributes.put(LdapAttributeNameEnum.SUB_SECTION, new LdapAttribute(LdapAttributeNameEnum.SUB_SECTION, subSection));
	}

	public LdapAttribute getAgsNumber() {
		return attributes.get(LdapAttributeNameEnum.EMPLOYEE_NUMBER);
	}

	public void setAgsNumber(String agsNumber) {
		attributes.put(LdapAttributeNameEnum.EMPLOYEE_NUMBER, new LdapAttribute(LdapAttributeNameEnum.EMPLOYEE_NUMBER, agsNumber));
	}
	
	public LdapAttribute getGS1() {
		return attributes.get(LdapAttributeNameEnum.GS1);
	}

	public void setGS1(String gs1) {
		attributes.put(LdapAttributeNameEnum.GS1, new LdapAttribute(LdapAttributeNameEnum.GS1, gs1));
	}

	public LdapAttribute getIRN() {
		return attributes.get(LdapAttributeNameEnum.IRN);
	}

	public void setIRN(String irn) {
		attributes.put(LdapAttributeNameEnum.IRN, new LdapAttribute(LdapAttributeNameEnum.IRN, irn));
	}
	
	public String getDN() {
		return "cn=" + (getCommonName().replace(",","\\,")) + "," + baseDN;
	}

	public String getCommonName() {
		return getSurname().getValue() + ", " + getGivenName().getValue();
	}

	public Map<LdapAttributeNameEnum, LdapAttribute> getAttributes() {
		return attributes;
	}
	
	
	public String getDisplayName() {
		return getCommonName();
	}
}
