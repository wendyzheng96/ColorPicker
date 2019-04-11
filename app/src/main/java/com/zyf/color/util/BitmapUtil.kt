package com.zyf.color.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Bitmap压缩工具
 */
object BitmapUtil {

    private val TAG = "BitmapUtil"

    /**
     * 图片压缩-质量压缩
     * @param filePath 源图片路径
     */
    fun compressImage(filePath: String) {

        //原文件
        val oldFile = File(filePath)

        //压缩文件路径 照片路径/
        val targetPath = oldFile.path
        val quality = 50//压缩比例0-100
        var bm = getSmallBitmap(filePath)//获取一定尺寸的图片
        val degree = getRotateAngle(filePath)//获取相片拍摄角度

        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bm = setRotateAngle(degree, bm)
        }
        val outputFile = File(targetPath)
        try {
            if (!outputFile.exists()) {
                val isMkdirs = outputFile.parentFile.mkdirs()
                Log.i(TAG, "compressImage: isMkdirs:$isMkdirs")
            } else {
                val isDelete = outputFile.delete()
                Log.i(TAG, "compressImage: isDelete:$isDelete")
            }
            val out = FileOutputStream(outputFile)
            bm?.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    fun getSmallBitmap(filePath: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        val reqWidth = 800
        val reqHeight = 800  //最大宽高为800*800
        try {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565// 降低图片从ARGB888到RGB565
            var bitmap = BitmapFactory.decodeFile(filePath, options)
            //旋转图片
            val mat = Matrix()
            val rotate = getRotateAngle(filePath)
            if (rotate > 0) {
                mat.postRotate(rotate.toFloat())
                bitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.width,
                    bitmap.height, mat, true
                )
            }
            return bitmap
        } catch (outOfMemoryError: OutOfMemoryError) {
            Log.e("getSmallBitmap", "OutOfMemoryError")
            return null
        }

    }

    //计算图片的缩放值
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * 获取图片的旋转角度
     */
    private fun getRotateAngle(filePath: String): Int {
        var rotateAngle = 0
        try {
            val exifInterface = ExifInterface(filePath)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateAngle = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateAngle = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateAngle = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return rotateAngle
    }

    /**
     * 旋转图片角度
     */
    private fun setRotateAngle(angle: Int, bitmap: Bitmap?): Bitmap? {
        var bitmap = bitmap
        if (bitmap != null) {
            val m = Matrix()
            m.postRotate(angle.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, m, true
            )
            return bitmap
        }
        return null
    }
}
