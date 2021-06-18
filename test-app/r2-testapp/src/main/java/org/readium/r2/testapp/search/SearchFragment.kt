/*
 * Copyright 2021 Readium Foundation. All rights reserved.
 * Use of this source code is governed by the BSD-style license
 * available in the top-level LICENSE file of the project.
 */

package org.readium.r2.testapp.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.readium.r2.shared.publication.Locator
import org.readium.r2.testapp.R
import org.readium.r2.testapp.databinding.FragmentSearchBinding
import org.readium.r2.testapp.reader.ReaderViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: ReaderViewModel by activityViewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewScope = viewLifecycleOwner.lifecycleScope

        val adapter =  SearchResultAdapter(object : SearchResultAdapter.Listener {
            override fun onItemClicked(v: View, locator: Locator) {
                val result = Bundle().apply {
                    putParcelable(SearchFragment::class.java.name, locator)
                }
                setFragmentResult(SearchFragment::class.java.name, result)
            }
        })

        viewModel.searchResult
            .onEach { adapter.submitData(it) }
            .launchIn(viewScope)

        binding.searchListView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
