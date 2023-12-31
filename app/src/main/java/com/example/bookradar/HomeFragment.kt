package com.example.bookradar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookradar.databinding.FragmentHomeBinding
import com.example.bookradar.model.BookListModel
import com.example.bookradar.model.BookModel
import com.example.bookradar.retrofit.RetrofitHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var adapter: MyBookRecyclerViewAdapter
    private lateinit var apiKey: String

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookList: MutableList<BookModel>
    var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (rootView != null) {
            return rootView as View
        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        rootView = binding.root

        apiKey = "KakaoAK " + BuildConfig.KAKAO_API
        val searchBar = binding.searchBook
        recyclerView = binding.listBook
        bookList = mutableListOf<BookModel>()
        adapter = MyBookRecyclerViewAdapter(bookList,
            object : MyBookRecyclerViewAdapter.OnItemClickListener {
                override fun onItemClick(item: BookModel) {
                    val action = HomeFragmentDirections.actionNavHomeToNavBookInfo(item)
                    findNavController().navigate(action)
                }
            })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        searchBar.clearFocus()
        var job: Job? = null
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
                    throwable.printStackTrace()
                }
                val prevListSize = bookList.size
                bookList.clear()
                adapter.notifyItemRangeRemoved(0, prevListSize)
                job?.cancel()
                job = lifecycleScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                    try {
                        var response: BookListModel? = null
                        var prevSize = 0
                        var page = 1
                        while (response == null || response.meta.is_end.not()) {
                            response = RetrofitHelper.getInstance().getBooks(apiKey, query, page++)
                            if (response.meta.total_count == 0) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.no_book_found),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                break
                            }
                            bookList.addAll(response.documents)
                            withContext(Dispatchers.Main) {
                                adapter.notifyItemRangeChanged(prevSize, bookList.size)
                            }
                            prevSize = bookList.size
                        }
                    } catch (_: CancellationException) {
                        Log.d("Coroutine", "Job cancelled")
                    } catch (e: Exception) {
                        Log.e("Network Error", "Error fetching data", e)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // false if no custom behavior is defined.
                return false
            }

        })

        /* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/
        return rootView as View
    }


}