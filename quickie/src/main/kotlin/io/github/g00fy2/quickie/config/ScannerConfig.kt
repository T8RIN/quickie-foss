package io.github.g00fy2.quickie.config

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Builder for ScannerConfig used in ScanBarcode ActivityResultContract.
 */
public class ScannerConfig internal constructor(
  internal val formats: IntArray,
  internal val stringRes: Int,
  internal val drawableRes: Int?,
  internal val hapticFeedback: Boolean,
  internal val showTorchToggle: Boolean
) {

  public class Builder {
    private var barcodeFormats: List<BarcodeFormat> = listOf(BarcodeFormat.FORMAT_ALL_FORMATS)
    private var overlayStringRes: Int = 0
    private var overlayDrawableRes: Int? = 0
    private var hapticSuccessFeedback: Boolean = true
    private var showTorchToggle: Boolean = false

    /**
     * Set a list of interested barcode formats. List must not be empty.
     * Reducing the number of supported formats will make the barcode scanner faster.
     */
    public fun setBarcodeFormats(formats: List<BarcodeFormat>): Builder = apply { barcodeFormats = formats }

    /**
     * Set a string resource used for the scanner overlay.
     */
    public fun setOverlayStringRes(@StringRes stringRes: Int): Builder = apply { overlayStringRes = stringRes }

    /**
     * Set a drawable resource used for the scanner overlay.
     * If null is passed, no icon will be shown.
     */
    public fun setOverlayDrawableRes(@DrawableRes drawableRes: Int?): Builder =
      apply { overlayDrawableRes = drawableRes }

    /**
     * Enable (default) or disable haptic feedback when a barcode was detected.
     */
    public fun setHapticSuccessFeedback(enable: Boolean): Builder = apply { hapticSuccessFeedback = enable }

    /**
     * Show or hide (default) torch/flashlight toggle button.
     */
    public fun setShowTorchToggle(enable: Boolean): Builder = apply { showTorchToggle = enable }

    /**
     * Build the BarcodeConfig required by the ScanBarcode ActivityResultContract.
     */
    public fun build(): ScannerConfig =
      ScannerConfig(
        barcodeFormats.map { it.value }.toIntArray(),
        overlayStringRes,
        overlayDrawableRes,
        hapticSuccessFeedback,
        showTorchToggle
      )
  }

  public companion object {
    /**
     * Kotlin friendly method to build the BarcodeConfig required by the ScanBarcode ActivityResultContract.
     */
    public fun build(func: Builder.() -> Unit): ScannerConfig = Builder().apply { func() }.build()
  }
}