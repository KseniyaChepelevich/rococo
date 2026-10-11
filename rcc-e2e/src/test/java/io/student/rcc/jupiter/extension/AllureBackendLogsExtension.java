package io.student.rcc.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension{

    public static final String caseName = "Rococo backend logs";

    private static final String[] SERVICES = {"rcc-auth", "rcc-api"};


    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);
        int attachedCount = 0;

        for (String service : SERVICES) {
            Path logFile = findLogFile(service);

            if (logFile == null) {
                System.out.println("[AllureBackendLogsExtension] Лог для " + service
                        + " не найден, пропускаем.");
                continue;
            }

            try (InputStream is = Files.newInputStream(logFile)) {
                allureLifecycle.addAttachment(
                        service + " log",
                        "text/html",
                        ".log",
                        is
                );
                attachedCount++;
                System.out.println("[AllureBackendLogsExtension] Прикреплён лог: "
                        + logFile.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("[AllureBackendLogsExtension] Ошибка чтения лога "
                        + service + ": " + e.getMessage());
            }
        }

        if (attachedCount == 0) {
            System.out.println("[AllureBackendLogsExtension] "
                    + "Не было прикреплено ни одного лога.");
        }
        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    private Path findLogFile(String service) {
        String[] candidates = {
                "../" + service + "/logs/" + service + "/app.log",
                "./" + service + "/logs/" + service + "/app.log",
                "./logs/" + service + "/app.log",
                "../logs/" + service + "/app.log"
        };

        for (String candidate : candidates) {
            Path path = Path.of(candidate);
            if (Files.exists(path)) {
                return path;
            }
        }
        return null;
    }
}
