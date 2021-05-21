package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

/**
 * A single LDAP attribute.
 * 
 * @author Brendan Douglas
 *
 */
public class LdapAttribute {
	private LdapAttributeNameEnum attributeName;
	private String value;
	
	
	public LdapAttributeNameEnum getAttributeName() {
		return attributeName;
	}
	
	public LdapAttribute(LdapAttributeNameEnum attributeName, String value) {
		this.attributeName = attributeName;
		this.value = value;
	}
	
	
	public void setAttributeName(LdapAttributeNameEnum attributeName) {
		this.attributeName = attributeName;
	}
	
	
	public String getValue() {
		return value;
	}
	
	
	public void setValue(String value) {
		this.value = value;
	}
}
