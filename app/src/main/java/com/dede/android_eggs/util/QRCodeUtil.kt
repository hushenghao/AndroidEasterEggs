package com.dede.android_eggs.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.google.zxing.qrcode.encoder.QRCode

object QRCodeUtil {

    private const val TAG = "QRCodeUtil"

    fun parseQRCodeDataArray(array: IntArray, blockSize: Int = 12): Bitmap {
        val sizeFlag = array[0]
        val width = sizeFlag shr 16
        val height = sizeFlag shl 16 ushr 16

        val bitmap = createBitmap(width * blockSize, height * blockSize)

        val blackPixels = IntArray(blockSize * blockSize) { Color.BLACK }
        val whitePixels = IntArray(blockSize * blockSize) { Color.WHITE }

        for (y in 0..<height) {
            for (x in 0..<width) {
                val o = x + y * width
                val index = o / 32
                val position = 31 - (o % 32)
                val bit = array[index + 1] ushr position and 1

//                bitmap[x, y] = if (bit == 1) Color.BLACK else Color.WHITE
                if (bit == 1) {
                    bitmap.setPixels(
                        blackPixels,
                        0,
                        blockSize,
                        x * blockSize,
                        y * blockSize,
                        blockSize,
                        blockSize
                    )
                } else {
                    bitmap.setPixels(
                        whitePixels,
                        0,
                        blockSize,
                        x * blockSize,
                        y * blockSize,
                        blockSize,
                        blockSize
                    )
                }
            }
        }
        return bitmap
    }

    fun qrCodeToDataArray(qrCode: QRCode): IntArray {
        val byteMatrix = qrCode.matrix
        val width = byteMatrix.width
        val height = byteMatrix.height

        val size = 1 + width * height / 32 + if (width * height % 32 == 0) 0 else 1
        val array = IntArray(size)
        val sizeFlag = (width shl 16) or height
        array[0] = sizeFlag

        var position = 31
        var value = 0
        var offset = 1
        for (y in 0..<height) {
            for (x in 0..<width) {
                if (byteMatrix.get(x, y) == 1.toByte()) {
                    value = value or (1 shl position)
                }
                if (position-- == 0) {
                    array[offset++] = value
                    position = 31
                    value = 0
                }
            }
        }
        array[size - 1] = value // Add the last value if there are remaining bits
        return array
    }

    fun generateQRCode(content: String): Bitmap {
//        val hints = hashMapOf<EncodeHintType, Any>(
//            EncodeHintType.CHARACTER_SET to "UTF-8",
////            EncodeHintType.MARGIN to 1, // Margin around the QR code
//        )
//        val qrCode = Encoder.encode(content, ErrorCorrectionLevel.L, hints)
//
//        val dataArray = qrCodeToDataArray(qrCode)
        val dataArray = intArrayOf(
            2162721,
            -20370369,
            -1056024880,
            1856726635,
            -1218912187,
            -609407718,
            -334343707,
            133868202,
            -33506640,
            15893716,
            1320428546,
            1765532167,
            1857954605,
            -1789532934,
            -1022113965,
            157702587,
            -1756407765,
            2076954232,
            596883006,
            1342993395,
            -527412453,
            1553097196,
            -1356536022,
            -1044777873,
            -1062102789,
            -669812049,
            -853083904,
            1882736255,
            -2013680752,
            1283445083,
            -1582526491,
            -713223582,
            -353513791,
            95929074,
            -20906198,
            0
        )
//        Log.i(TAG, "generateQRCode: " + dataArray.joinToString(", ", prefix = "[", postfix = "]"))
        val bitmap = parseQRCodeDataArray(dataArray)
        return bitmap
    }

}