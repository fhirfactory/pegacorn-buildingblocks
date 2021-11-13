package net.fhirfactory.pegacorn.model.ui.resources.simple.datatypes;

import java.util.ArrayList;

public class IdentifierDESet {
    ArrayList<IdentifierESDT> identifiers;

    public IdentifierDESet(){
        this.identifiers = new ArrayList<>();
    }

    public ArrayList<IdentifierESDT> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(ArrayList<IdentifierESDT> identifiers) {
        this.identifiers = identifiers;
    }

    public void addIdentifier(IdentifierESDT identifier){
        if(identifiers.contains(identifier)){
            return;
        } else {
            identifiers.add(identifier);
        }
    }
}
