package io.student.rcc.data.mapper.jpa;

import org.hibernate.EmptyInterceptor;

public class AssignedIdInterceptor extends EmptyInterceptor {
    @Override
    public Boolean isTransient(Object entity) {
        if (entity instanceof io.student.rcc.data.entity.auth.AuthUserEntity ||
                entity instanceof io.student.rcc.data.entity.api.UserEntity) {
            return Boolean.TRUE;
        }
        return super.isTransient(entity);
    }
}
