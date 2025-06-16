package com.example.catnicwarehouse.scan.presentation.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.shared.networking.network.article.ArticlesForDeliveryResponseDTO

class ArticleAutoCompleteAdapter(
    context: Context,
    private val articles: List<ArticlesForDeliveryResponseDTO>
) : ArrayAdapter<ArticlesForDeliveryResponseDTO>(context, android.R.layout.simple_dropdown_item_1line, articles) {


    override fun getCount(): Int {
        return articles.size
    }

    override fun getItem(position: Int): ArticlesForDeliveryResponseDTO? {
        return articles[position]
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val article = getItem(position)
        if (article != null) {
            (view as TextView).text = article.articleId
        }
        return view
    }
}
