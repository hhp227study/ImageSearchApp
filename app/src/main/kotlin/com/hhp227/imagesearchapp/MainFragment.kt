package com.hhp227.imagesearchapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import com.google.android.material.appbar.MaterialToolbar
import com.hhp227.imagesearchapp.adapters.ImageLoadStateAdapter
import com.hhp227.imagesearchapp.adapters.ImagePagingAdapter
import com.hhp227.imagesearchapp.data.NetworkStatus
import com.hhp227.imagesearchapp.databinding.FragmentMainBinding
import com.hhp227.imagesearchapp.databinding.MenuMainBinding
import com.hhp227.imagesearchapp.utilities.ALL
import com.hhp227.imagesearchapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainFragment : Fragment(), MenuProvider {
    private lateinit var binding: FragmentMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    private val connectivityManager by lazy {
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val adapter = ImagePagingAdapter(lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(MainLifecycleObserver())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.contentMain.spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mutableListOf<String>())
        binding.contentMain.recyclerView.adapter = adapter.withLoadStateFooter(ImageLoadStateAdapter(adapter::retry))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavAppbar(binding.toolbar)
        observeViewModelData()
        adapter.addOnPagesUpdatedListener(::onPagesUpdated)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(this)
        adapter.removeOnPagesUpdatedListener(::onPagesUpdated)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search, menu)
        if (menu.hasVisibleItems()) {
            menu.findItem(R.id.search).actionView = MenuMainBinding.inflate(layoutInflater).run {
                viewModel = mainViewModel
                lifecycleOwner = viewLifecycleOwner
                return@run root
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    private fun setNavAppbar(toolbar: MaterialToolbar) {
        toolbar.setupWithNavController(findNavController())
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).addMenuProvider(this)
    }

    private fun showAlert() {
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle(getString(R.string.connection_error_title))
            .setMessage(getString(R.string.connection_error_message))
            .setPositiveButton(getString(R.string.connection_error_positive_button)) { _, _ ->
                if (mainViewModel.networkStatus.value != NetworkStatus.Available) {
                    showAlert()
                } else {
                    mainViewModel.apply {
                        currentQuery.value?.also(::setQuery)
                    }
                }
            }
            .setNegativeButton(getString(R.string.connection_error_negative_button)) { _, _ ->
                exitProcess(0)
            }
            .create()
            .show()
    }

    private fun observeViewModelData() = with(mainViewModel) {
        networkStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                NetworkStatus.Losing,
                NetworkStatus.Lost,
                NetworkStatus.Unavailable -> showAlert()
                else -> Unit
            }
        }
        adapter.loadState.observe(viewLifecycleOwner) { state ->
            binding.contentMain.isEmpty = state.source.refresh is LoadState.NotLoading && adapter.itemCount < 1
            binding.contentMain.isLoading = state.refresh is LoadState.Loading
        }
    }

    private fun onPagesUpdated() {
        if (mainViewModel.filterKeyword.value == ALL) {
            mainViewModel.setCollections(
                listOf(ALL) + adapter.collections
            )
        }
    }

    inner class NetworkCallback : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            setStatus(NetworkStatus.Available)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            setStatus(NetworkStatus.Losing)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            setStatus(NetworkStatus.Lost)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            setStatus(NetworkStatus.Unavailable)
        }

        private fun setStatus(status: NetworkStatus) {
            lifecycleScope.launch {
                mainViewModel.setNetworkStatus(status)
            }
        }
    }

    inner class MainLifecycleObserver : DefaultLifecycleObserver {
        private val callback = NetworkCallback()

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            connectivityManager.registerDefaultNetworkCallback(callback)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            connectivityManager.unregisterNetworkCallback(callback)
            lifecycle.removeObserver(this)
        }
    }
}