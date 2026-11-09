package io.student.rcc.data.mapper.tpl;

import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;

import java.util.List;

public class JdbcConnectionHolders implements AutoCloseable {

    private final List<JdbcConnectionHolder> holders;

    public JdbcConnectionHolders(@Nonnull List<JdbcConnectionHolder> holders) {
        this.holders = holders;
    }

    @SneakyThrows
    @Override
    public void close() {
        holders.forEach(holder -> {
            try {
                holder.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
