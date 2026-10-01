package org.sanmibuh.framework.notifier.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

record TelegramMessageRequest(@JsonProperty("chat_id") String chatId, String text) {}
