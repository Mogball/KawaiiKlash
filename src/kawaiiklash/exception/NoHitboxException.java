/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kawaiiklash.exception;

import java.util.logging.Logger;

/**
 *
 * @author Jeff Niu
 */
public class NoHitboxException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(NoHitboxException.class.getName());

    public NoHitboxException(String message) {
        super(message);
    }
}
