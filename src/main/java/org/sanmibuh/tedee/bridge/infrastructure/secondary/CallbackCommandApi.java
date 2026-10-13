package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackIDsingle;

public interface CallbackCommandApi {

  CallbackIDsingle createCallback(CallbackDetailsNoId callback);
}
