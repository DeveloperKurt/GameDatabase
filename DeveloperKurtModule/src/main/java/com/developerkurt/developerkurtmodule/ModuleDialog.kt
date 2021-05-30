package com.developerkurt.developerkurtmodule

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*


class ModuleDialog internal constructor(private val activity: Activity)
{

    companion object
    {
        val KEY_MODULE_INSTALLATION_DATE = stringPreferencesKey("installation_date")

        //Keep these as lambdas in order to prevent a bug that occurs when user changes the Locale whilst the app is running
        val installationDateFormat = { SimpleDateFormat("dd MM yyyy", Locale.getDefault()) }
        val currentDateFormat = { SimpleDateFormat("HH:mm:ss, dd MM yyyy", Locale.getDefault()) }
    }


    private val alertDialog: AlertDialog

    private val installationDateFlow: Flow<String> = activity.applicationContext.dataStore.data
        .map { preferences -> preferences[KEY_MODULE_INSTALLATION_DATE] ?: "" }

    private var installationDate: String = runBlocking { installationDateFlow.first() }


    private val currentDateFormatTv: TextView
    private val installationDateTv: TextView
    private val iconImageView: ImageView

    private val iconUrl = "https://developer.android.com/images/brand/Android_Robot.png"

    init
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity, R.style.ModuleDialog)
        val dialogView: View = activity.getLayoutInflater().inflate(R.layout.module_dialog, null)
        installationDateTv = dialogView.findViewById(R.id.module_tv_installation_date)
        currentDateFormatTv = dialogView.findViewById(R.id.module_tv_current_date)
        iconImageView = dialogView.findViewById(R.id.iv_module)

        builder.setView(dialogView)
        builder.setCancelable(true)
        updateInstallationDateIfEmpty()

        installationDateTv.text = activity.applicationContext
            .getString(R.string.library_installed_on, installationDate)


        //Note: Loading straight into an ImageView in an AlertDialog causes image to be displayed in the entire screen
        Glide.with(activity.applicationContext)
            .asBitmap()
            .load(iconUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>()
            {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?)
                {
                    iconImageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?)
                {
                }
            })


        alertDialog = builder.create()
    }

    fun displayDialog()
    {
        if (!alertDialog.isShowing)
        {
            startUpdatingCurrentTime()
            alertDialog.show()

        }
    }

    //Note: coroutines were not preferred to make the integration more convenient
    private fun startUpdatingCurrentTime()
    {
        val handler = Handler(activity.getMainLooper())
        handler.postDelayed(object : Runnable
        {
            override fun run()
            {
                if (!activity.isDestroyed && alertDialog.isShowing)
                {
                    currentDateFormatTv.text = currentDateFormat().format(Calendar.getInstance().time)
                    handler.postDelayed(this, 1000)
                }
            }
        }, 10)
    }


    private fun updateInstallationDateIfEmpty()
    {
        if (installationDate.isEmpty())
        {
            runBlocking {
                installationDate = installationDateFormat().format(Calendar.getInstance().time)
                activity.applicationContext.dataStore.edit {
                    it[KEY_MODULE_INSTALLATION_DATE] = installationDate
                }
            }
        }
    }


}