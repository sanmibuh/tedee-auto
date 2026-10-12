package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackIDsingle;

interface CallbackCommandApi {

  CallbackIDsingle createCallback(CallbackDetailsNoId callback);

  void deleteCallback(int callbackId);
}
