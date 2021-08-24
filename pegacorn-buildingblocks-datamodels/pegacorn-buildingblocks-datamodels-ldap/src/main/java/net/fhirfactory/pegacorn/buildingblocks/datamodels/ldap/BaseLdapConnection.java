package net.fhirfactory.pegacorn.buildingblocks.datamodels.ldap;

import java.io.IOException;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
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
		
		getLogger().info("Brendan: name and credentials: {} --> {}", name, credentials);
	}
	
	public boolean isConnected() {
		return connection.isConnected();
	}
	
	
	/**
	 * Returns an entry from a given name and a surname.
	 * 
	 * @param givenName
	 * @param surname
	 * @return
	 * @throws LdapException
	 * @throws CursorException 
	 */
	public PractitionerLdapEntry getEntry(String givenName, String surname) throws LdapException, CursorException {
		
		 // Create the SearchRequest object
	    SearchRequest searchRequest = new SearchRequestImpl();
	    searchRequest.setScope(SearchScope.ONELEVEL);
	    searchRequest.addAttributes("*","+");
	    searchRequest.setTimeLimit(0);
	    searchRequest.setBase(new Dn(baseDN));
	    searchRequest.setFilter("(&(objectclass=*)(sn=" + surname + ")(gn=" + givenName + "))");

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
	
	
	public PractitionerLdapEntry getEntry(String emailAddress) throws LdapException, CursorException {
		
		 // Create the SearchRequest object
	    SearchRequest searchRequest = new SearchRequestImpl();
	    searchRequest.setScope(SearchScope.ONELEVEL);
	    searchRequest.addAttributes("*","+");
	    searchRequest.setTimeLimit(0);
	    searchRequest.setBase(new Dn(baseDN));
	    searchRequest.setFilter("(&(objectclass=*)(mail=" + emailAddress + "))");

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
	
	
	public boolean exists(String emailAddress) throws CursorException, LdapException {
		return getEntry(emailAddress) != null;
	}
	
	
	public boolean exists(String givenName, String surname) throws CursorException, LdapException {
		return getEntry(givenName, surname) != null;
	}
}
