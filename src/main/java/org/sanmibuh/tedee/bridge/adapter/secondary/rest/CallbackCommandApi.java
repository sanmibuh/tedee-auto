package org.sanmibuh.tedee.bridge.adapter.secondary.rest;

import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackIDsingle;

public interface CallbackCommandApi {

  CallbackIDsingle createCallback(CallbackDetailsNoId callback);
}
