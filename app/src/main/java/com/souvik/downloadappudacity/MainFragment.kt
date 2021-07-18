package com.souvik.downloadappudacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.souvik.customloader.CustomLoader
import com.souvik.downloadappudacity.databinding.FragmentMainBinding
import com.souvik.downloadappudacity.databinding.FragmentMainBindingImpl


class MainFragment : Fragment() {
    private val CHANNEL_ID: String = "DownloadApp"
    private lateinit var downloadManager: DownloadManager
    private val PERMISSIONCODE: Int = 12
    private val NOTIFICATION_ID: Int = 13
    var list: ArrayList<Long> = ArrayList()
    var counter: Int = 1
    lateinit var status: String
    lateinit var title: String
    var binding : FragmentMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main, container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding?.download?.onButtonFilled ={ binding?.download?.showFillButtonToast() }
        binding?.download?._drawProgress?.observe(viewLifecycleOwner, Observer {
            if(it)
                binding?.download!!.text = "Download Started"
            else
                binding?.download!!.text = "Download"
        })
    }
    private fun initView() {
        createNotificationChannel()
    }
    private fun checkRadioButton() {
        val selectedRadioButtonId: Int = binding?.rgSelector?.checkedRadioButtonId!!
        if (selectedRadioButtonId != -1) {
            val selectedRadioButton: RadioButton =
                binding?.rgSelector?.findViewById(selectedRadioButtonId)!!
            val selectedRbText: String = selectedRadioButton.getText().toString()
            downloadCondition(selectedRbText)
        } else {
            Toast.makeText(
                requireContext(),
                "Please select an option to download",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun downloadCondition(select: String) {
        if (select.contains("Glide")) {
            Log.d("TAG", "first is Selected")
            status = "Sucees"
            title = resources.getString(R.string.radio_txt1)
            checkPermissionApp(true)
        } else if (select.contains("LoadApp")) {
            Log.d("TAG", "second is Selected")
            status = "Failure"
            title = resources.getString(R.string.radio_txt2)
            checkPermissionApp(false)
        } else if (select.contains("Retrofit")) {
            Log.d("TAG", "third is Selected")
            status = "Sucees"
            title = resources.getString(R.string.radio_txt3)
            checkPermissionApp(true)
        }
    }

    private fun checkPermissionApp(status: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_DENIED && (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.INTERNET
                ) != PackageManager.PERMISSION_DENIED)
            ) {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSIONCODE
                )
            } else {
                if (status)
                    download()
                else
                    createAppNotification()
            }
        }
    }
    private fun CustomLoader.showFillButtonToast(){
        checkRadioButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONCODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download()
                } else {
                    Snackbar.make(binding?.root!!, "Permission is not granted", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    private fun download() {
        downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val Download_Uri =
            Uri.parse("https://i.pinimg.com/564x/75/95/d3/7595d37ebb81c6c2d0b7160b597b082c.jpg")
        val request = DownloadManager.Request(Download_Uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setAllowedOverRoaming(false)
        request.setTitle(resources.getString(R.string.app_name))
        request.setDescription("Downloading resource")
        request.setVisibleInDownloadsUi(true)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "/DownloadApp//SampleApp.png"
        )
        var refid: Long = downloadManager.enqueue(request)
        list.add(refid)

    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.e("IN", "" + referenceId)
            list.remove(referenceId)
            if (list.isEmpty()) {
                Log.e("INSIDE", "" + referenceId)
                createAppNotification()
            }
        }
    }
    private fun createAppNotification() {
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("DownloadApp")
                .setContentText("All Download completed")

        val notificationIntent =
            Intent(requireActivity().applicationContext, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        notificationIntent.putExtra("destination", "DetailsFragment")
        notificationIntent.putExtra("status", status)
        notificationIntent.putExtra("title", title)

        val pendingIntent = PendingIntent.getActivity(
            requireActivity(), 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setAutoCancel(true)

        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(onComplete)
    }

}