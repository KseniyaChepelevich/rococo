package io.student.rcc.jupiter.extension;


import io.student.rcc.data.mapper.tpl.Connections;

public class DatabasesExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
