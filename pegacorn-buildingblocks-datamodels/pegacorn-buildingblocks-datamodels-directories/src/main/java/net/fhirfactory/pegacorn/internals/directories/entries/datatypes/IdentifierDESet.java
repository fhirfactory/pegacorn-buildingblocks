package net.fhirfactory.pegacorn.internals.directories.entries.datatypes;

import java.util.ArrayList;

public class IdentifierDESet {
    ArrayList<IdentifierDE> identifiers;

    public IdentifierDESet(){
        this.identifiers = new ArrayList<>();
    }

    public ArrayList<IdentifierDE> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(ArrayList<IdentifierDE> identifiers) {
        this.identifiers = identifiers;
    }

    public void addIdentifier(IdentifierDE identifier){
        if(identifiers.contains(identifier)){
            return;
        } else {
            identifiers.add(identifier);
        }
    }
}
