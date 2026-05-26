package io.student.rcc.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;

public class JndiSuiteExtension implements SuiteExtension{
    @Override
    public void beforeSuite(ExtensionContext context) {
        System.setProperty("java.naming.factory.initial", "org.osjava.sj.memory.MemoryContextFactory");
        System.setProperty("org.osjava.sj.delimiter", "/");
        System.setProperty("org.osjava.sj.space", "java:comp/env");
    }

    @Override
    public void afterSuite() {
        System.clearProperty("java.naming.factory.initial");
        System.clearProperty("org.osjava.sj.delimiter");
        System.clearProperty("org.osjava.sj.space");
    }
}
