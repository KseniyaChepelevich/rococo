package io.student.rcc.jupiter.extension;


import io.student.rcc.data.tpl.Connections;

public class DatabasesExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
