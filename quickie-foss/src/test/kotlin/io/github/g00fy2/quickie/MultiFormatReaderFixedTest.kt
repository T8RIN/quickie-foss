package io.github.g00fy2.quickie

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Writer
import com.google.zxing.aztec.AztecWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.datamatrix.DataMatrixWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MultiFormatReaderFixedTest {

  @Test
  fun `preserves UTF-8 Cyrillic decoded from Aztec`() {
    assertUtf8CyrillicIsPreserved(AztecWriter(), BarcodeFormat.AZTEC)
  }

  @Test
  fun `preserves UTF-8 Cyrillic decoded from Data Matrix`() {
    assertUtf8CyrillicIsPreserved(
      DataMatrixWriter(),
      BarcodeFormat.DATA_MATRIX,
      mutableMapOf(EncodeHintType.DATA_MATRIX_COMPACT to true),
    )
  }

  @Test
  fun `reencodes explicit UTF-8 mojibake`() {
    val expected = "Привет, мир!"
    val mojibake = String(expected.toByteArray(Charsets.UTF_8), Charsets.ISO_8859_1)
    val matrix = AztecWriter().encode(mojibake, BarcodeFormat.AZTEC, 300, 300)
    val decodeHints = mutableMapOf<DecodeHintType?, Any>(
      DecodeHintType.POSSIBLE_FORMATS to mutableListOf(BarcodeFormat.AZTEC),
    )

    val result = MultiFormatReaderFixed().decode(matrix.toBinaryBitmap(), decodeHints)

    assertEquals(expected, result.text)
  }

  private fun assertUtf8CyrillicIsPreserved(
    writer: Writer,
    format: BarcodeFormat,
    additionalEncodeHints: MutableMap<EncodeHintType, Any> = mutableMapOf(),
  ) {
    val expected = "Привет, мир!"
    val encodeHints = mutableMapOf<EncodeHintType, Any>(
      EncodeHintType.CHARACTER_SET to Charsets.UTF_8.name(),
    ).apply {
      putAll(additionalEncodeHints)
    }
    val matrix = writer.encode(expected, format, 300, 300, encodeHints)
    val decodeHints = mutableMapOf<DecodeHintType?, Any>(
      DecodeHintType.POSSIBLE_FORMATS to mutableListOf(format),
    )

    val result = MultiFormatReaderFixed().decode(matrix.toBinaryBitmap(), decodeHints)

    assertEquals(expected, result.text)
  }

  private fun BitMatrix.toBinaryBitmap(): BinaryBitmap {
    val pixels = IntArray(width * height) { index ->
      if (get(index % width, index / width)) 0x000000 else 0xFFFFFF
    }
    return BinaryBitmap(HybridBinarizer(RGBLuminanceSource(width, height, pixels)))
  }
}
