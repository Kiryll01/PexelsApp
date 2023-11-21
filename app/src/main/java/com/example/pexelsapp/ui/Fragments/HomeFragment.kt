package com.example.pexelsapp.ui.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.filter
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsapp.ui.recyclerview.FooterLoadStateAdapter
import com.example.pexelsapp.ui.recyclerview.ImageListAdapter
import com.example.pexelsapp.PexelsApplication
import com.example.pexelsapp.R
import com.example.pexelsapp.databinding.FragmentHomeBinding
import com.example.pexelsapp.ui.ViewModels.HomeViewModel
import com.example.pexelsapp.ui.ViewModels.HomeViewModelFactory
import com.google.android.material.radiobutton.MaterialRadioButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG="HOME_FRAGMENT"
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var layoutManager : LayoutManager

    private val viewModel by viewModels<HomeViewModel>{
        HomeViewModelFactory((activity?.application as PexelsApplication).repository)
    }

    private var curatedPhotosJob : Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        Log.d(TAG,"binding is created")

        binding.shimmerHomeList.startShimmer()

        binding.shimmerCollections.startShimmer()

        setQueryListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager= StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        setErrorLayout()

        val adapter = createAdapter(view)

        viewModel.isDataReady.observe(viewLifecycleOwner){dataState->
            Log.d(TAG,"data state : $dataState")
            if(dataState.isReady()) {
                Log.d(TAG,"data is ready")
                onDataReady()
            }
        }

        setScrollView()

        setCollections()

        Log.d(TAG,"photoNavArg ${HomeViewModel.photoNavArg}")

        var curatedPhotosJob : Job?= null

        Log.d(TAG,"isFirstLaunch ${HomeViewModel.isFirstLaunch}")

        Log.d(TAG,"isCurated ${HomeViewModel.photoNavArg.isCurated}")
        if (HomeViewModel.isFirstLaunch || HomeViewModel.photoNavArg.isCurated) {
            curatedPhotosJob=lifecycleScope.launch {
                Log.d(TAG,"curated photos coroutine is started")
                HomeViewModel.isFirstLaunch = false
                viewModel.curatedPhotosFlow.collect { data ->
                    Log.d(TAG,"curated photos collector is called")
                    adapter.submitData(data)
                }
            }
        }

        lifecycleScope.launch {
             Log.d(TAG,"photos flow coroutine has been started")
             viewModel.photosFlow.collect { data ->
                 curatedPhotosJob?.cancel()
                Log.d(TAG + "_PHOTOS_FLOW", "receive data : $data")
                val lastQueryParam = viewModel.searchQuery.replayCache.lastOrNull()
                Log.d(TAG + "_PHOTOS_FLOW", "last query param : $lastQueryParam")
                val dataByParam = data.filter { photo -> photo.queryParam == lastQueryParam }
                Log.d(TAG + "_PHOTOS_FLOW", "filtered data : $dataByParam")
                adapter.submitData(dataByParam)
                stopImagesShimmer()
            }
        }

        val concatAdapter = adapter.withLoadStateFooter(
            footer = FooterLoadStateAdapter(retry = {
                val lastQuery= viewModel.searchQuery.replayCache.lastOrNull()
                if(lastQuery!=null)viewModel.setQuery(lastQuery)
                // TODO : else submit curated photos
            })
        )

        binding.apply {
            imagesRecyclerView.adapter = concatAdapter
            imagesRecyclerView.layoutManager=layoutManager
        }
    }
    private fun onDataReady() {
        binding.apply {
            Log.d(TAG, "stopping shimmers")
            if (shimmerHomeList.isVisible) {
                shimmerHomeList.stopShimmer()
                shimmerHomeList.isVisible = false
            }
            if (shimmerCollections.isVisible) {
                shimmerCollections.stopShimmer()
                shimmerCollections.isVisible = false
            }
            collectionsScrollView.root.isVisible=true
        }
    }
    override fun onPause() {
        super.onPause()
        binding.collectionsScrollView.root.visibility=View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        curatedPhotosJob?.cancel()
        _binding = null

    }
    private fun setQueryListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //            private var job : Job?=null

            override fun onQueryTextChange(newText: String?): Boolean {
                //                job?.cancel()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                _binding?.imagesRecyclerView?.layoutManager?.scrollToPosition(0)
                if (query.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "discover something new!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return true
                }
                viewModel.setQuery(query!!)
                if(previousCollectionCheckedId!=-1)
                    firstCollectionButtonAppearance(binding.root.findViewById(previousCollectionCheckedId))
                startImagesShimmer()
                //                viewModel.setQueryParam(query!!)
                //              job = viewModel.refreshPhotos(query?:" ")

                return true
            }
        })
    }
    private var previousCollectionCheckedId = -1
    private fun setCollections() {

        val radioGroup = binding.collectionsScrollView.radioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            var previousButton : MaterialRadioButton? = null
            if (previousCollectionCheckedId != -1) {
                previousButton=binding.root.findViewById(previousCollectionCheckedId)
                firstCollectionButtonAppearance(previousButton)
            }
            val currentButton = binding.root.findViewById<MaterialRadioButton>(checkedId)

            val prevButtonLocation = IntArray(2)
            if(previousButton != null) {
                previousButton.getLocationOnScreen(prevButtonLocation)
            } else {
                prevButtonLocation[0] = 0
                prevButtonLocation[1] = 0

            }
            val currentButtonLocation = IntArray(2)
            currentButton.getLocationOnScreen(currentButtonLocation)

            val dx = currentButtonLocation[0] - prevButtonLocation[0]

            binding.collectionsScrollView.scrollView.smoothScrollBy(dx,0)

            secondCollectionButtonAppearance(currentButton)
            viewModel.setQuery(currentButton.text.toString())
            layoutManager.scrollToPosition(0)

            startImagesShimmer()

            previousCollectionCheckedId = checkedId
        }
    }
    private fun animateCollectionButton(button : Button,
                                        fromButtonTintColor: Int,
                                        toButtonTintColor: Int,
                                        fromTextColor : Int,
                                        toTextColor : Int,
                                         animationDuration : Long = 250
                                        ){
//        val tintColorAnimation = ValueAnimator.ofArgb(fromButtonTintColor, toButtonTintColor)
//        tintColorAnimation.addUpdateListener { animator->
//            button.background.setTint(animator.animatedValue as Int)
//        }
//
//        val textColorAnimation = ValueAnimator.ofArgb(fromTextColor,toTextColor)
//        tintColorAnimation.addUpdateListener { animator->
//            button.setTextColor(animator.animatedValue as Int)
//        }
//
//        val animatorSet = AnimatorSet()
//        animatorSet.playTogether(tintColorAnimation, textColorAnimation)
//        animatorSet.duration=animationDuration
//        animatorSet.start()

    }
    private fun secondCollectionButtonAppearance(button: Button) {
            animateCollectionButton(button,
                fromButtonTintColor = resources.getColor(R.color.grey),
                toButtonTintColor = resources.getColor(R.color.red),
                fromTextColor = resources.getColor(R.color.black),
                toTextColor = resources.getColor(R.color.white))

//            currentButton.setTextColor(resources.getColor(R.color.white))
//            currentButton.setBackgroundResource(R.drawable.red_round_rectangle)
    }

    private fun firstCollectionButtonAppearance(button: Button) {
        animateCollectionButton(button,
            fromButtonTintColor = resources.getColor(R.color.red),
            toButtonTintColor = resources.getColor(R.color.grey),
            fromTextColor = resources.getColor(R.color.white),
            toTextColor = resources.getColor(R.color.black))
//        previousButton.setTextColor(resources.getColor(R.color.black))
//        previousButton.setBackgroundResource(R.drawable.grey_round_rectangle)
    }

    private fun createAdapter(view: View): ImageListAdapter {
        val adapter = ImageListAdapter (onImageClickAction = {
            HomeViewModel.photoNavArg = it
            Log.d(TAG,"phototoNavArg : $it")
            val action =HomeFragmentDirections.actionNavigationHomeToDetailsFragment(it)
            view.findNavController().navigate(action)
        },
           imageLoadingListener = viewModel
        )
       return adapter
    }

    private fun setScrollView() {
        lifecycleScope.launch {
            viewModel.collections.collect {
                _binding?.let { notNullBinding->
                    notNullBinding.collectionsScrollView.apply {
                        Log.d(TAG, "$it")
                        if (it.isNotEmpty() && it.size > 6) {
                            radioButton1.text = it[0].name
                            radioButton2.text = it[1].name
                            radioButton3.text = it[2].name
                            radioButton4.text = it[3].name
                            radioButton5.text = it[4].name
                            radioButton6.text = it[5].name
                            radioButton7.text = it[6].name
                            viewModel.setScrollViewReady()
                        }
                    }

                }
            }
        }
    }


    //TODO: test
    private fun collectionsAnimation(view: View) : Animation{
        val scaleAnimation = ScaleAnimation(0f,view.scaleX,view.scaleY,view.scaleY)

        val translateAnimation = TranslateAnimation(-view.x,0f,-view.y,0f)

        val alphaAnimation=AlphaAnimation(0.8f,1.0f)

        val animationSet= AnimationSet(true)
        animationSet.interpolator=DecelerateInterpolator(5f)
        animationSet.duration=2000
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(translateAnimation)
        animationSet.addAnimation(alphaAnimation)

        return animationSet
    }

    private fun setErrorLayout() {
        binding.networkErrorLayout.root.apply {
            viewModel.launchException.observe(viewLifecycleOwner) { info ->
                if (info.launchException) {
                    visibility = View.VISIBLE
                    binding.networkErrorLayout.tryAgain.setOnClickListener {
                        viewModel.refreshPhotos(info.searchWord)
                    }
                } else {
                    if (visibility == View.VISIBLE)
                        visibility = View.GONE
                }
            }
        }
    }
    private fun startImagesShimmer(){
        binding.shimmerHomeList.isVisible=true
        binding.shimmerHomeList.startShimmer()
    }
    private fun stopImagesShimmer(){
        _binding?.let {
        if(binding.shimmerHomeList.isVisible) {
            binding.shimmerHomeList.isVisible = false
            binding.shimmerHomeList.stopShimmer()
            layoutManager.scrollToPosition(0)
        }
    }
    }
}