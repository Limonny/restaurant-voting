package com.example.restaurantvoting.util;

import com.example.restaurantvoting.exception.EntityNotFoundException;
import com.example.restaurantvoting.exception.IdValidationException;
import com.example.restaurantvoting.model.BaseEntity;
import lombok.experimental.UtilityClass;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.http.HttpStatus;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.MESSAGE;

@UtilityClass
public class ValidationUtil {

    public static <E extends BaseEntity> void isEntityNew(E entity) {
        if (!isIdNull(entity)) {
            throw new IdValidationException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    entity.getClass().getSimpleName() + " must be new (id=null)",
                    ErrorAttributeOptions.of(MESSAGE));
        }
    }

    public static <E extends BaseEntity> void assureIdConsistent(E entity, Long id) {
        if (isIdNull(entity)) {
            entity.setId(id);
        }
        else {
            if (!entity.getId().equals(id)) {
                throw new IdValidationException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        entity.getClass().getSimpleName() + " must be with id=" + id,
                        ErrorAttributeOptions.of(MESSAGE));
            }
        }
    }

    public static void checkModification(Integer count, Long id) {
        if (count == 0) {
            throw new EntityNotFoundException(
                    HttpStatus.NOT_FOUND,
                    "Entity with id=" + id + " not found",
                    ErrorAttributeOptions.of(MESSAGE));
        }
    }

    private static <E extends BaseEntity> boolean isIdNull(E entity) {
        if (entity == null) {
            throw new EntityNotFoundException(
                    HttpStatus.BAD_REQUEST,
                    "Entity must not be null",
                    ErrorAttributeOptions.of(MESSAGE));
        }

        return entity.getId() == null;
    }
}