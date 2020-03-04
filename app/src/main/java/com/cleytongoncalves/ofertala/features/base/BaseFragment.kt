package com.cleytongoncalves.ofertala.features.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

abstract class BaseFragment : Fragment() {
    
    private var fragmentId = 0L
    
    companion object {
        private const val KEY_FRAGMENT_ID = "KEY_FRAGMENT_ID"
        private val NEXT_ID = AtomicLong(0)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create the FragmentComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        fragmentId = savedInstanceState?.getLong(KEY_FRAGMENT_ID) ?: NEXT_ID.getAndIncrement()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(layoutId(), container, false)
        return view
    }
    
    @LayoutRes
    abstract fun layoutId(): Int
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_FRAGMENT_ID, fragmentId)
    }
    
    override fun onDestroy() {
        if (!activity!!.isChangingConfigurations)
            Timber.i("Clearing ConfigPersistentComponent id=%d", fragmentId)
        
        super.onDestroy()
    }
}