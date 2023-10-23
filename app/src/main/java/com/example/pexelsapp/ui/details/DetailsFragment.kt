package com.example.pexelsapp.ui.details

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.health.connect.datatypes.units.Length
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.PexelsSize
import com.example.pexelsapp.PexelsApplication
import com.example.pexelsapp.R
import com.example.pexelsapp.Web.loadImage
import com.example.pexelsapp.databinding.DetailsFragmentBinding
import com.example.pexelsapp.databinding.FragmentHomeBinding
import com.example.pexelsapp.ui.home.HomeViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG="DETAILS_FRAGMENT"
class DetailsFragment : Fragment() {
    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private var photo: PexelsPhotoDto? = null
    private val viewModel by viewModels<DetailsViewModel> {
        DetailsViewModelFactory((activity?.application as PexelsApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = DetailsFragmentArgs.fromBundle(it)
            photo = args.photo
        }
        Log.d(TAG, "receive photo $photo")
        viewModel.setState(photo?.isLiked?:false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DetailsFragmentBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navBar= requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility=View.GONE

        val imageUrl= photo?.src?.get(PexelsSize.ORIGINAL.sizeName) ?: " "

        binding.backNavigation.setOnClickListener{
//            val navController=findNavController()
//            val previousFragment=navController.previousBackStackEntry
//            val previousFragmentId=previousFragment?.destination?.id
//            previousFragmentId?.let {
//
//            }

            val action = DetailsFragmentDirections.actionDetailsFragmentToNavigationHome()
            view.findNavController().navigate(action)
        }

        photo?.let{
           var uri = imageUrl.toUri().buildUpon().scheme("https").build()
            binding.image.load(uri) {
                placeholder(R.drawable.loading_img)
            }
        }
        binding.apply {


            viewModel.saveButtonState.observe(viewLifecycleOwner){
                if(it==false) saveButton.setImageResource(R.drawable.icon_unchecked)
                else saveButton.setImageResource(R.drawable.icon_checked)
            }

            detailsFragmentTitle.text=photo?.photographer
            downloadButton.setOnClickListener{
                val downloadManager : DownloadManager = getSystemService(requireContext(),DownloadManager::class.java)!!
                val request = DownloadManager.Request(Uri.parse(imageUrl))
                    .setTitle("${imageUrl.substringAfter("pexels.com")}")
                    .setDescription("Downloading an image from $imageUrl")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_PICTURES, "pexels_app_images")
                downloadManager.enqueue(request)
            }
            saveButton.setOnClickListener{
                Toast.makeText(requireContext(),"photo is saved to your collection",Toast.LENGTH_SHORT)
                photo?.let {
                    it.isLiked=!it.isLiked
                    viewModel.saveState(it.asEntity()) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navBar= requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility=View.VISIBLE
    }
}