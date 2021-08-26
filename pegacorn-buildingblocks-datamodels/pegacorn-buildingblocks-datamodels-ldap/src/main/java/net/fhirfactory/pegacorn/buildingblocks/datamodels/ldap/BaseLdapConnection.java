package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.filter.FilterEncoder;
import org.apache.directory.api.ldap.model.message.Response;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchResultEntry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;


/**
 * Base class for all LDAP connections.  Contains common method to work witjh LDAP.
 * 
 * @author Brendan Douglas
 *
 */
public abstract class BaseLdapConnection {
	protected String host;
	protected int port;
	protected String name;
	protected String credentials;
	protected String baseDN;
	
	protected LdapConnection connection;
	
    
    public abstract Logger getLogger();
	
	public BaseLdapConnection() throws LdapException {		
		this.host = System.getenv("LDAP_SERVER_HOST_NAME");
		this.port = Integer.parseInt(System.getenv("LDAP_SERVER_BASE_PORT"));
		this.name = System.getenv("LDAP_SERVER_BIND_DN");
		this.credentials = System.getenv("LDAP_SERVER_BIND_PASSWORD");
		this.baseDN = System.getenv("LDAP_SERVER_BASE_DN");
	}
	
	
	public void close() throws IOException {
		if (connection != null && !connection.isConnected()) {
			connection.close();
		}
	}

	
	public void connect() throws LdapException {
		if (connection != null && connection.isConnected()) {
			return;
		}
		
		connection = new LdapNetworkConnection(host, port, true);
		connection.bind(name, credentials);
		connection.setTimeOut(0);
	}
	
	public boolean isConnected() {
		return connection.isConnected();
	}
	
	
	public PractitionerLdapEntry getEntry(String commonName) throws LdapException, CursorException {
	
		 // Create the SearchRequest object
	    SearchRequest searchRequest = new SearchRequestImpl();
	    searchRequest.setScope(SearchScope.ONELEVEL);
	    searchRequest.addAttributes("*","+");
	    searchRequest.setTimeLimit(0);
	    searchRequest.setBase(new Dn(baseDN));    
	    searchRequest.setFilter(FilterEncoder.format("(&(objectclass=*)(cn={0}))", commonName));

		 SearchCursor searchCursor = connection.search(searchRequest);

		 while (searchCursor.next()) {
	        Response response = searchCursor.get();
			
			if (response instanceof SearchResultEntry) {
				
				Entry resultEntry = ((SearchResultEntry) response).getEntry();
		
				return new PractitionerLdapEntry(resultEntry, baseDN);
	        }
		 }
		 
		 return null;
	}
	
	
	/**
	 * Gets the attributes for an entry.
	 * 
	 * @param commonName
	 * @return
	 * @throws LdapException
	 * @throws CursorException
	 */
	public List<Attribute>getAttributes(String commonName) throws LdapException, CursorException {
		 // Create the SearchRequest object
	    SearchRequest searchRequest = new SearchRequestImpl();
	    searchRequest.setScope(SearchScope.ONELEVEL);
	    searchRequest.addAttributes("*");
	    searchRequest.setTimeLimit(0);
	    searchRequest.setBase(new Dn(baseDN));    
	    searchRequest.setFilter(FilterEncoder.format("(&(objectclass=*)(cn={0}))", commonName));

		 SearchCursor searchCursor = connection.search(searchRequest);
		 
		 List<Attribute>attributes = new ArrayList<>();

		 while (searchCursor.next()) {
	        Response response = searchCursor.get();
			
			if (response instanceof SearchResultEntry) {
				
				Entry resultEntry = ((SearchResultEntry) response).getEntry();
				
				for (Attribute attribute : resultEntry.getAttributes()) {
					if (!attribute.getId().startsWith("objectclass")) { // ignore this one.
						attributes.add(attribute);
					}
				}
				
				 return attributes;	
	        }
		 }	
		 
		 return null;
	}
	
	
	public boolean exists(String dn) throws CursorException, LdapException {
		return getEntry(dn) != null;
	}
}
