package com.ap.greenpole.usermodule.annotation;

import java.lang.annotation.*;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 12-Aug-20 01:07 AM
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuthorizePermission {
    String[] value();
}
