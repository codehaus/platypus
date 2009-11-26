package org.pz.platypus.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: Kalyani
 * Date: Nov 26, 2009
 * Time: 10:32:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class StopExecutionExecption extends RuntimeException {
    public StopExecutionExecption(String errMsg) {
        super(errMsg);
    }
}
