package net.fhirfactory.pegacorn.csv.core.validator;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.datamodel.DatamodelProperties;

/**
 * A CSV field validator to make sure the field value is one of the keys from the supplied properties class.
 * 
 * @author Brendan Douglas
 *
 */
public class PropertiesValidator extends FieldValidator {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesValidator.class);
    
    private Class<? extends DatamodelProperties> propertyClassType;

    public PropertiesValidator(String field,  Class<? extends DatamodelProperties> propertyClassType) {
        super(field);

        this.propertyClassType = propertyClassType;
    }

    @Override
    public boolean isValid(String value) {

        // If no value provided then assume the field is optional. If mandatory please
        // add @Mandatory annotation.
        if (StringUtils.isBlank(value)) {
            return true;
        }
        
        try {
            Class clazz = Class.forName(propertyClassType.getName());
            Constructor constructor = clazz.getDeclaredConstructor();
            
            DatamodelProperties properties = (DatamodelProperties)constructor.newInstance();
    
           return properties.exists(value);
        } catch (Exception e) {
            LOG.error("Error determining if the field is valid.", e);
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return field + " contains an unexpected value";
    }
}
