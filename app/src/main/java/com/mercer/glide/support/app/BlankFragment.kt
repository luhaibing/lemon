package com.mercer.glide.support.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.mercer.glide.support.app.databinding.FragmentBlankBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BlankFragment : Fragment() {

    lateinit var binding: FragmentBlankBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlankBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    val viewModel: BlankViewModel by viewModels(factoryProducer = {
        val factory = (this as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory ?: defaultViewModelProviderFactory
        IntentFallbackViewModelFactory(factory, requireActivity().intent.extras)
    })

    /*
    val viewModel: BlankViewModel by viewModels(extrasProducer = {
        val extras = defaultViewModelCreationExtras
        val defaultArgs = extras[DEFAULT_ARGS_KEY]
        val newDefaultArgs = bundleOf().apply {
            putAll(requireActivity().intent?.extras ?: bundleOf())
            putAll(defaultArgs ?: Bundle.EMPTY)
        }
        MutableCreationExtras(extras).apply {
            set(DEFAULT_ARGS_KEY, newDefaultArgs)
        }
    })
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tsAsFlow.onEach {
            Log.e("TAG", "时间戳 >>> $it.")
            binding.tv.text = it
        }.launchIn(lifecycleScope)
    }

}