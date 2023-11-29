package com.example.bookradar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.bookradar.databinding.FragmentBookInfoBinding
import com.example.bookradar.model.DocumentModel

@Suppress("DEPRECATION")
class BookInfoFragment : Fragment() {
    private lateinit var binding: FragmentBookInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBookInfoBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val item = arguments?.getParcelable<DocumentModel>("item")
        binding.textBookTitle.text = item?.title
        binding.textAuthor.text =
            item?.authors?.joinToString(", ") ?: getString(R.string.unknown_author)
        binding.textPublisher.text = item?.publisher
        binding.textIsbn.text = item?.isbn
        binding.textContent.text = item?.contents
        Glide.with(this)
            .load(item?.thumbnail)
            .into(binding.imageBookCover)

        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(item: DocumentModel) = BookInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("item", item)
            }
        }

    }
}