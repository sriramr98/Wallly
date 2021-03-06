package com.sriram.wally.ui.collections

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sriram.wally.R
import com.sriram.wally.adapters.CollectionsListAdapter
import com.sriram.wally.models.NetworkStatus
import com.sriram.wally.models.response.Collection
import com.sriram.wally.utils.EndlessScrollRvListener
import com.sriram.wally.utils.isConnectedToNetwork
import kotlinx.android.synthetic.main.fragment_collections.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

class CollectionsFragment : Fragment() {

    private val mAdpater: CollectionsListAdapter by inject()
    private val mViewModel: CollectionsViewModel by viewModel()

    companion object {
        fun instantiate(): CollectionsFragment {
            return CollectionsFragment()
        }

        const val TAG = "collections-fragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.VERTICAL, false)

        rv_collections.layoutManager = layoutManager
        rv_collections.adapter = mAdpater

        mViewModel.getCollections().observe(this, Observer {
            if (it == null || it.status == NetworkStatus.FAILURE) {
                showFailure()
            } else if (it.status == NetworkStatus.LOADING) {
                showLoading()
            } else {
                // success
                showSuccess(it.items)
            }
        })

        mAdpater.onItemClickListener(object : CollectionsListAdapter.PhotoListener {
            override fun onPhotoClicked(photo: Collection) {
                startActivity<CollectionsDetailActivity>(CollectionsDetailActivity.TAG to photo)
            }

        })

        rv_collections.addOnScrollListener(object : EndlessScrollRvListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                mViewModel.loadMore()
            }

        })

    }

    private fun showSuccess(images: ArrayList<Collection>) {
        toast(images.size.toString())
        layout_refresh.isRefreshing = false
        mAdpater.setImages(images)
    }

    private fun showLoading() {
        layout_refresh.isRefreshing = true
    }

    private fun showFailure() {
        layout_refresh.isRefreshing = true
        if (isConnectedToNetwork(requireContext())) {
            tv_error.text = "Error retrieving data. Please try again"
        } else {
            tv_error.text = "No Internet detected. Please try again"
        }
    }
}