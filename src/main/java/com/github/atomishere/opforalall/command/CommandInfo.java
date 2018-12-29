package com.github.atomishere.opforalall.command;

import com.github.atomishere.opforalall.ranks.Rank;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    Rank requiresRank();
    CommandSource source();
    String description() default "";
    String usage() default "";
}
