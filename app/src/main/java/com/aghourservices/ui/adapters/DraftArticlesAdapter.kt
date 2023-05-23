package com.aghourservices.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aghourservices.data.model.Article
import com.aghourservices.databinding.DraftArticleCardBinding
import com.aghourservices.ui.viewHolders.DraftArticlesViewHolder

class DraftArticlesAdapter(
    private val onItemClicked: (v: View, position: Int) -> Unit
) : RecyclerView.Adapter<DraftArticlesViewHolder>() {
    private var articleList: ArrayList<Article> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftArticlesViewHolder {
        val view =
            DraftArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DraftArticlesViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: DraftArticlesViewHolder, position: Int) {
        val article = articleList[position]
        holder.setArticlesList(article)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setArticles(articles: ArrayList<Article>) {
        articleList = articles
        notifyDataSetChanged()
    }

    fun getArticle(position: Int): Article {
        return articleList[position]
    }

    fun deleteArticle(position: Int) {
        articleList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateArticle(position: Int, updatedArticle: Article) {
        articleList[position] = updatedArticle
        notifyItemChanged(position)
    }
}