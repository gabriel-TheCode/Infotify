package com.thecode.infotify.presentation.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thecode.infotify.core.domain.OnBoardingPart
import com.thecode.infotify.databinding.FragmentOnboardingBinding

class OnBoardingAdapter : RecyclerView.Adapter<OnBoardingAdapter.BaseViewHolder<*>>() {

    private var list: List<OnBoardingPart> = listOf()
    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        binding =
            FragmentOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingPagerViewHolder(binding.root)
    }

    override fun getItemCount() = list.size

    fun setItem(list: List<OnBoardingPart>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val card = list[position]
        when (holder) {
            is OnBoardingPagerViewHolder -> holder.bind(card)
            else -> throw IllegalArgumentException()
        }
    }

    inner class OnBoardingPagerViewHolder(itemView: View) :
        BaseViewHolder<OnBoardingPart>(itemView) {
        override fun bind(item: OnBoardingPart) {
            binding.imageOnboarding.setAnimation(item.image)
            binding.textOnboardingTitle.setText(item.title)
            binding.textOnboardingDescription.setText(item.description)
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }
}