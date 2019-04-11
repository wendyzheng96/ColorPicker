package com.zyf.color

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.graphics.Palette
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Menu
import android.view.MenuItem
import com.zyf.color.adapter.GvColorAdapter
import com.zyf.color.bean.ColorBean
import org.jetbrains.anko.toast
import java.io.File

class MainActivity : AppCompatActivity() {

    //设置权限请求和活动请求的请求码requestCode
    companion object {
        private const val PERMISSION_REQUEST_IMAGE = 1
        private const val PERMISSION_REQUEST_CAMERA = 2

        private const val ACTIVITY_REQUEST_IMAGE = 101
        private const val ACTIVITY_REQUEST_CAMERA = 102
    }

    //拍照需要的两个权限
    private val permissionList: Array<String> = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    //存储用户拒绝授权的权限
    private var permissionTmp: ArrayList<String> = ArrayList()

    private val colorList = mutableListOf<ColorBean>()

    private val gvColorAdapter: GvColorAdapter by lazy {
        GvColorAdapter(this, colorList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gvList.adapter = gvColorAdapter

        ivImage.setImageResource(R.drawable.pic_example)
        val bitmap: Bitmap = (ivImage.drawable as BitmapDrawable).bitmap
        getImageColor(bitmap)
    }

    /**
     * 获取图片颜色
     */
    private fun getImageColor(bitmap: Bitmap) {
        colorList.clear()
        Palette.from(bitmap)
            .generate { palette ->
                if (palette != null) {
                    //最活跃的颜色
                    val vibrant = palette.vibrantSwatch
                    //活跃的亮色
                    val lightVibrant = palette.lightVibrantSwatch
                    //活跃的深色
                    val darkVibrant = palette.darkVibrantSwatch
                    //最柔和的颜色
                    val muted = palette.mutedSwatch
                    //柔和的亮色
                    val lightMuted = palette.lightMutedSwatch
                    //柔和的深色
                    val darkMuted = palette.darkMutedSwatch

                    if (lightMuted != null) {
                        colorList.add(ColorBean(lightMuted.rgb))
                    }
                    if (lightVibrant != null) {
                        colorList.add(ColorBean(lightVibrant.rgb))
                    }
                    if (vibrant != null) {
                        colorList.add(ColorBean(vibrant.rgb))
                    }
                    if (muted != null) {
                        colorList.add(ColorBean(muted.rgb))
                    }
                    if (darkVibrant != null) {
                        colorList.add(ColorBean(darkVibrant.rgb))
                    }
                    if (darkMuted != null) {
                        colorList.add(ColorBean(darkMuted.rgb))
                    }
                    gvList.numColumns = colorList.size
                    gvColorAdapter.notifyDataSetChanged()
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_camera -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionTmp.clear()
                    for (i in permissionList.indices) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                permissionList[i]
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionTmp.add(permissionList[i])
                        }
                    }
                    if (permissionTmp.isEmpty()) {//未授予的权限为空，表示权限都授予了，开启照相功能
                        gotoCamera()
                    } else {
                        val permissions = permissionTmp.toTypedArray()
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            permissions,
                            PERMISSION_REQUEST_CAMERA)
                    }
                } else {
                    gotoCamera()
                }
            }
            R.id.menu_album -> {
                //检查版本是否大于M
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            PERMISSION_REQUEST_IMAGE)
                    } else {
                        gotoAlbum()
                    }
                } else {
                    gotoAlbum()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var uri: Uri
    private lateinit var photoFile: File

    //跳转拍照
    private fun gotoCamera() {
        photoFile = File(Environment.getExternalStorageDirectory().path
                + "/" + System.currentTimeMillis() + ".jpg")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, "com.zyf.color.fileprovider", photoFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            uri = Uri.fromFile(photoFile)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, ACTIVITY_REQUEST_CAMERA)
    }

    //跳转相册
    private fun gotoAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, ACTIVITY_REQUEST_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_IMAGE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoAlbum()
                } else {
                    toast("你拒绝了读取相册权限！")
                }
            }
            PERMISSION_REQUEST_CAMERA -> {
                //用于判断是否有未授权权限
                var isAgree = true
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isAgree = false
                        //判断是否勾选禁止后不再询问
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionList[i])) {
                            toast("你拒绝了拍照相关权限！")
                        }
                    }
                }
                if (isAgree) {
                    gotoCamera()
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ACTIVITY_REQUEST_CAMERA ->{
                    if (photoFile.exists()) {
                        try {
                            val inputStream = contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            ivImage.setImageBitmap(bitmap)
                            getImageColor(bitmap)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                ACTIVITY_REQUEST_IMAGE ->{
                    try {
                        val inputStream = contentResolver.openInputStream(data?.data!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        ivImage.setImageBitmap(bitmap)
                        getImageColor(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
