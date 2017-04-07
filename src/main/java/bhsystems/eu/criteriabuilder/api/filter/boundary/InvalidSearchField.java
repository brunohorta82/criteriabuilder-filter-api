
package bhsystems.eu.criteriabuilder.api.filter.boundary;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InvalidSearchField extends Exception {
    public InvalidSearchField(IllegalArgumentException e, String targetClass) {
        super(e.getMessage() + " : " + targetClass);
    }

    public InvalidSearchField(String message) {
        super(message);
    }
}
