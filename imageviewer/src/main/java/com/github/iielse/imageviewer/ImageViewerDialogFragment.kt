package com.github.iielse.imageviewer

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.iielse.imageviewer.adapter.ImageViewerAdapter
import com.github.iielse.imageviewer.core.Components
import com.github.iielse.imageviewer.core.Components.requireInitKey
import com.github.iielse.imageviewer.core.Components.requireOverlayCustomizer
import com.github.iielse.imageviewer.core.Components.requireTransformer
import com.github.iielse.imageviewer.core.Components.requireViewerCallback
import com.github.iielse.imageviewer.databinding.FragmentImageViewerDialogBinding
import com.github.iielse.imageviewer.utils.Config
import com.github.iielse.imageviewer.utils.Config.OFFSCREEN_PAGE_LIMIT
import com.github.iielse.imageviewer.utils.TransitionEndHelper
import com.github.iielse.imageviewer.utils.TransitionStartHelper
import com.github.iielse.imageviewer.utils.findViewWithKeyTag
import kotlin.math.max

open class ImageViewerDialogFragment : BaseDialogFragment() {
    private var innerBinding: FragmentImageViewerDialogBinding? = null
    private val binding get() = innerBinding!!
    private val events by lazy { ViewModelProvider(requireActivity()).get(ImageViewerActionViewModel::class.java) }
    private val viewModel by lazy { ViewModelProvider(this).get(ImageViewerViewModel::class.java) }
    private val userCallback by lazy { requireViewerCallback() }
    private val initKey by lazy { requireInitKey() }
    private val transformer by lazy { requireTransformer() }
    private val adapter by lazy { ImageViewerAdapter(initKey) }
    private var initPosition = RecyclerView.NO_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Components.working) dismissAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        innerBinding =
            innerBinding ?: FragmentImageViewerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setListener(adapterListener)
        (binding.viewer.getChildAt(0) as? RecyclerView?)?.let {
            it.clipChildren = false
            it.itemAnimator = null
        }
        binding.viewer.orientation = Config.VIEWER_ORIENTATION
        binding.viewer.registerOnPageChangeCallback(pagerCallback)
        binding.viewer.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT
        binding.viewer.adapter = adapter

        requireOverlayCustomizer().provideView(binding.overlayView)?.let(binding.overlayView::addView)

        viewModel.dataList.observe(viewLifecycleOwner) { list ->
            if (Config.DEBUG) Log.i("viewer", "submitList ${list.size}")
            adapter.submitList(list)
            initPosition = list.indexOfFirst { it.id == initKey }
            binding.viewer.setCurrentItem(initPosition, false)
        }

        viewModel.viewerUserInputEnabled.observe(viewLifecycleOwner) {
            binding.viewer.isUserInputEnabled = it ?: true
        }

        events.actionEvent.observe(viewLifecycleOwner, Observer(::handle))
    }

    private fun handle(action: Pair<String, Any?>?) {
        when (action?.first) {
            ViewerActions.SET_CURRENT_ITEM -> binding.viewer.currentItem = max(action.second as Int, 0)
            ViewerActions.DISMISS -> onBackPressed()
        }
    }

    private val adapterListener by lazy {
        object : ImageViewerAdapterListener {
            override fun onInit(viewHolder: RecyclerView.ViewHolder) {
                TransitionStartHelper.start(this@ImageViewerDialogFragment, transformer.getView(initKey), viewHolder)
                binding.background.changeToBackgroundColor(Config.VIEWER_BACKGROUND_COLOR)
                userCallback.onInit(viewHolder)

                if (initPosition > 0) userCallback.onPageSelected(initPosition, viewHolder)
            }

            override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
                binding.background.updateBackgroundColor(fraction, Config.VIEWER_BACKGROUND_COLOR, Color.TRANSPARENT)
                userCallback.onDrag(viewHolder, view, fraction)
            }

            override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
                binding.background.changeToBackgroundColor(Config.VIEWER_BACKGROUND_COLOR)
                userCallback.onRestore(viewHolder, view, fraction)
            }

            override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                val startView = (view.getTag(R.id.viewer_adapter_item_key) as? Long?)?.let { transformer.getView(it) }
                TransitionEndHelper.end(this@ImageViewerDialogFragment, startView, viewHolder)
                binding.background.changeToBackgroundColor(Color.TRANSPARENT)
                userCallback.onRelease(viewHolder, view)
            }
        }
    }

    private val pagerCallback by lazy {
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                userCallback.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                userCallback.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                val currentKey = adapter.getItemId(position)
                val holder = binding.viewer.findViewWithKeyTag(R.id.viewer_adapter_item_key, currentKey)
                        ?.getTag(R.id.viewer_adapter_item_holder) as? RecyclerView.ViewHolder?
                        ?: return
                userCallback.onPageSelected(position, holder)
            }
        }
    }

    override fun showFailure(message: String?) {
        super.showFailure(message)
        Components.release()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.setListener(null)
        binding.viewer.unregisterOnPageChangeCallback(pagerCallback)
        binding.viewer.adapter = null
        innerBinding = null
        Components.release()
    }

    override fun onBackPressed() {
        if (TransitionStartHelper.animating || TransitionEndHelper.animating) return
        if (Config.DEBUG) Log.i("viewer", "onBackPressed ${binding.viewer.currentItem}")

        val currentKey = adapter.getItemId(binding.viewer.currentItem)
        binding.viewer.findViewWithKeyTag(R.id.viewer_adapter_item_key, currentKey)?.let { endView ->
            val startView = transformer.getView(currentKey)
            binding.background.changeToBackgroundColor(Color.TRANSPARENT)

            (endView.getTag(R.id.viewer_adapter_item_holder) as? RecyclerView.ViewHolder?)?.let {
                TransitionEndHelper.end(this, startView, it)
                userCallback.onRelease(it, endView)
            }
        }
    }

    open class Factory {
        open fun build(): ImageViewerDialogFragment = ImageViewerDialogFragment()
    }
}
