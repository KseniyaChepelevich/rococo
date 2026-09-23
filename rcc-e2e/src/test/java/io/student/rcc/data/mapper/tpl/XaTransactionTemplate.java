package io.student.rcc.data.mapper.tpl;

import com.atomikos.icatch.jta.UserTransactionImp;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class XaTransactionTemplate {
    private final JdbcConnectionHolders holders;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public XaTransactionTemplate(String... jdbcUrl) {
        this.holders = Connections.holders(jdbcUrl);
    }

    public XaTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(@Nonnull Supplier<T> action) {
        UserTransaction ut = new UserTransactionImp();
        boolean transactionStarted = false;
        try {
            ut.begin();
            transactionStarted = true;
            T result = action.get();
            ut.commit();
            return result;
        } catch (Exception e) {
            if (transactionStarted) {
                try {
                    int status = ut.getStatus();
                    if (status == Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK) {
                        ut.rollback();
                    } else {
                        System.err.println("[XaTransactionTemplate] Warning: Cannot rollback. Transaction status is: " + status);
                    }
                } catch (Exception rollbackEx) {
                    System.err.println("[XaTransactionTemplate] Critical: Failed to rollback transaction: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Transaction execution failed", e);
        } finally {
            if (closeAfterAction.get()) {
                holders.close();
            }
        }
    }

    @SafeVarargs
    public final <T> T execute(@Nonnull Supplier<T>... actions) {
        UserTransaction ut = new UserTransactionImp();
        boolean transactionStarted = false;
        try {
            ut.begin();
            transactionStarted = true;
            T result = null;
            for (Supplier<T> action : actions) {
                result = action.get();
            }
            ut.commit();
            return result;
        } catch (Exception e) {
            if (transactionStarted) {
                try {
                    int status = ut.getStatus();
                    if (status == Status.STATUS_ACTIVE || status == Status.STATUS_MARKED_ROLLBACK) {
                        ut.rollback();
                    }
                } catch (Exception rollbackEx) {
                    System.err.println("[XaTransactionTemplate] Critical: Failed to rollback transaction: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Transaction execution failed", e);
        } finally {
            if (closeAfterAction.get()) {
                holders.close();
            }
        }
    }
}



