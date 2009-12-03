package org.pz.platypus.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Kalyani
 * Date: Dec 3, 2009
 * Time: 5:06:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidInputException extends IllegalArgumentException {

    final int status;

    public InvalidInputException(String s, int status) {
        super(s);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
