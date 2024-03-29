package com.example.pexelsapp.ui.Fragments

import android.app.DownloadManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.pexelsapp.Data.Dtos.PexelsPhotoDto
import com.example.pexelsapp.Data.Entitites.PexelsPhotoEntity
import com.example.pexelsapp.Data.Enums.PexelsSize
import com.example.pexelsapp.PexelsApplication
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.DetailsFragmentBinding
import com.example.pexelsapp.ui.ViewModels.DetailsViewModel
import com.example.pexelsapp.ui.ViewModels.DetailsViewModelFactory

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
        viewModel.saveState(photo?.asEntity() ?: PexelsPhotoEntity.empty())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val navBar= requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility= View.GONE

        val imageUrl= photo?.src?.get(PexelsSize.ORIGINAL.sizeName) ?: " "

        binding.backNavigation.setOnClickListener{
                val action =
                    DetailsFragmentDirections.actionDetailsFragmentToNavigationHome()
                view.findNavController().navigate(action)
        }

        photo?.let{
            var uri = imageUrl.toUri().buildUpon().scheme("https").build()
            val imageLoader = ImageLoader.Builder(requireContext())
                .build()
            val request= ImageRequest.Builder(requireContext())
                .data(uri)
                .placeholder(R.drawable.image_placeholder)
                .target(
                    onStart = {placeholder -> binding.image.setImageDrawable(placeholder)},
                    onSuccess = {result: Drawable -> binding.image.setImageDrawable(result) },
                    onError = {_->

                        binding.detailsExploreLayout.root.visibility= View.VISIBLE
                        binding.saveButton.visibility= View.GONE
                        binding.relativeLayout.visibility= View.GONE
                        binding.image.visibility= View.GONE
                        binding.detailsExploreLayout.explore.setOnClickListener{
                            photo?.let {
                                val action =
                                    DetailsFragmentDirections.actionDetailsFragmentToNavigationHome()
                                findNavController().navigate(action)
                            }
                            }
                              },)
                .build()
            imageLoader.enqueue(request)

        }
        binding.apply {

            viewModel.saveButtonState.observe(viewLifecycleOwner){
                if(it==false) saveButton.setImageResource(R.drawable.icon_unchecked)
                else saveButton.setImageResource(R.drawable.icon_checked)
            }

            detailsFragmentTitle.text=photo?.photographer
            downloadButton.setOnClickListener{
                val downloadManager : DownloadManager = ContextCompat.getSystemService(
                    requireContext(),
                    DownloadManager::class.java
                )!!
                val request = DownloadManager.Request(Uri.parse(imageUrl))
                    .setTitle("${imageUrl.substringAfter("pexels.com")}")
                    .setDescription("Downloading an image from $imageUrl")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_PICTURES, "pexels_app_images")
                downloadManager.enqueue(request)
            }

            val scaleAnimation = ScaleAnimation(
                1.0f, 0.9f,
                1.0f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point X
                Animation.RELATIVE_TO_SELF, 0.5f // Pivot point Y
            )
            val alphaAnimation = AlphaAnimation(
                1.0f, 0.5f

            )
            val animationSet = AnimationSet(true).apply {
                addAnimation(scaleAnimation)
                addAnimation(alphaAnimation)
                duration = 200
            }

            saveButton.setOnClickListener{

                Toast.makeText(requireContext(), "photo is saved to your collection", Toast.LENGTH_SHORT)
                    .show()

                saveButton.startAnimation(
                    animationSet
                )
                photo?.let {
                    viewModel.saveState(it.asEntity()) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navBar= requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navBar.visibility= View.VISIBLE
    }
}