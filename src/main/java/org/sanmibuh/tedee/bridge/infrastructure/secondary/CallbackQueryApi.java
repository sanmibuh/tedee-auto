package org.sanmibuh.tedee.bridge.infrastructure.secondary;

import com.tedee.bridge.client.model.CallbackDetails;
import java.util.List;

public interface CallbackQueryApi {

  List<CallbackDetails> getCallbacks();
}
