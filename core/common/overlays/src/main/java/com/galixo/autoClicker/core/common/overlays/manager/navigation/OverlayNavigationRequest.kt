package com.galixo.autoClicker.core.common.overlays.manager.navigation

import com.galixo.autoClicker.core.common.overlays.base.Overlay

internal sealed class OverlayNavigationRequest {

    data object NavigateUp : OverlayNavigationRequest()

    data class NavigateTo(
        val overlay: Overlay,
        val hideCurrent: Boolean = false,
    ) : OverlayNavigationRequest()
}