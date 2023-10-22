package com.example.pexelsapp.ui.details

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.load
import com.example.pexelsapp.R
import com.example.pexelsapp.Web.loadImage
import com.example.pexelsapp.databinding.DetailsFragmentBinding
import com.example.pexelsapp.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG="DETAILS_FRAGMENT"
class DetailsFragment : Fragment() {
    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private var imageUrl : String = ""
    private var authorName : String = "incognito"
    companion object{
        const val BITMAP_URL="bitmap"
        const val AUTHOR_NAME="author_name"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(BITMAP_URL).toString()
            Log.d(TAG,"$imageUrl")
            authorName=it.getString(AUTHOR_NAME)?:"incognito"

        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DetailsFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navBar= requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility=View.GONE
        val uri = imageUrl.toUri().buildUpon().scheme("https").build()
        binding.apply {
            image.load(uri) {
                placeholder(R.drawable.loading_img)
            }
            detailsFragmentTitle.text=authorName
            downloadButton.setOnClickListener{
                    val request = DownloadManager.Request(Uri.parse(imageUrl))
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                            imageUrl.substringAfter("www.pexels.com"))
                    val downloadManager = getSystemService(requireContext(), DownloadManager::class.java) as DownloadManager
                    downloadManager.enqueue(request)
            }
            saveButton.setOnClickListener{

                saveButton.setImageResource(R.drawable.icon_checked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navBar= requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility=View.VISIBLE
    }
}