package com.dynatrace.integration.idea;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages {
    public static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");

    @NotNull
    public static String getMessage(String key, Object... args) {
        return MessageFormat.format(MESSAGES.getString(key), args);
    }
}
