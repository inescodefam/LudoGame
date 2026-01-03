package com.projectkamberinesludogame.ludogame.jndi;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class InitialDirContextClosable extends InitialDirContext  implements AutoCloseable {
    public InitialDirContextClosable(Hashtable<?, ?> env) throws NamingException {
        super(env);
    }
}
