package org.sanmibuh.tedee.lock.infrastructure.secondary;

import com.tedee.bridge.client.model.BridgeDetails;
import com.tedee.bridge.client.model.CallbackConflict;
import com.tedee.bridge.client.model.CallbackDetails;
import com.tedee.bridge.client.model.CallbackDetailsNoId;
import com.tedee.bridge.client.model.CallbackHeader;
import com.tedee.bridge.client.model.CallbackIDsingle;
import com.tedee.bridge.client.model.CallbackNotFound;
import com.tedee.bridge.client.model.DeviceBleError;
import com.tedee.bridge.client.model.DeviceDisconnected;
import com.tedee.bridge.client.model.DeviceNotFound;
import com.tedee.bridge.client.model.InvalidToken;
import com.tedee.bridge.client.model.LockDetails;
import com.tedee.bridge.client.model.LockDetailsDeviceSettings;
import com.tedee.bridge.client.model.PostUnlock403Response;
import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

class TedeeBridgeRuntimeHints implements RuntimeHintsRegistrar {

  private static final MemberCategory[] JACKSON_CATEGORIES = {
    MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS
  };

  @Override
  public void registerHints(final RuntimeHints hints, final @Nullable ClassLoader classLoader) {
    hints
        .reflection()
        .registerType(BridgeDetails.class, JACKSON_CATEGORIES)
        .registerType(BridgeDetails.IsConnectedEnum.class, JACKSON_CATEGORIES)
        .registerType(CallbackConflict.class, JACKSON_CATEGORIES)
        .registerType(CallbackDetails.class, JACKSON_CATEGORIES)
        .registerType(CallbackDetailsNoId.class, JACKSON_CATEGORIES)
        .registerType(CallbackHeader.class, JACKSON_CATEGORIES)
        .registerType(CallbackIDsingle.class, JACKSON_CATEGORIES)
        .registerType(CallbackNotFound.class, JACKSON_CATEGORIES)
        .registerType(DeviceBleError.class, JACKSON_CATEGORIES)
        .registerType(DeviceDisconnected.class, JACKSON_CATEGORIES)
        .registerType(DeviceNotFound.class, JACKSON_CATEGORIES)
        .registerType(InvalidToken.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.TypeEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.IsConnectedEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.StateEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.JammedEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.DoorStateEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetails.IsChargingEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.AutoLockEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(
            LockDetailsDeviceSettings.AutoLockImplicitEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.PullSpringEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.AutoPullSpringEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.PostponedLockEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.ButtonLockEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(LockDetailsDeviceSettings.ButtonUnlockEnabledEnum.class, JACKSON_CATEGORIES)
        .registerType(PostUnlock403Response.class, JACKSON_CATEGORIES);
  }
}
