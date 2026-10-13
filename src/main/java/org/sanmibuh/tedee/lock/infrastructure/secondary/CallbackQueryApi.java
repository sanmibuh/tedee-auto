package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.model.CallbackDetails;
import java.util.List;

public interface CallbackQueryApi {

  List<CallbackDetails> getCallbacks();
}
