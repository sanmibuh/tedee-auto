package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.model.CallbackDetails;
import java.util.List;

interface CallbackQueryApi {

  List<CallbackDetails> getCallbacks();
}
