package com.github.iielse.imageviewer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

class ViewerBehavior(
        context: Context, attrs: AttributeSet
) : CoordinatorLayout.Behavior<View>(context, attrs) {


    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (Config.DEBUG) Log.i(TAG, "layoutDependsOn child ${child.id} dependency ${dependency.id}")
        return true
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (Config.DEBUG) Log.i(TAG, "onDependentViewChanged child ${child.id} dependency ${dependency.id}")
        return true
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        if (Config.DEBUG) Log.i(TAG, "onStartNestedScroll child ${child.id} directTargetChild ${directTargetChild.id} target ${target.id} axes $axes type $type")
        return true
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (Config.DEBUG) Log.i(TAG, "onNestedPreScroll child ${child.id} ${child.tag} target ${target.id} ${target.tag} dx $dx dy $dy type $type")
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        if (Config.DEBUG) Log.i(TAG, "onStopNestedScroll child ${child.id} target ${target.id} type $type")
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float): Boolean {
        if (Config.DEBUG) Log.i(TAG, "onNestedPreFling child ${child.id} target ${target.id} velocityX $velocityX velocityY $velocityY")
        return false
    }

    companion object {
        private const val TAG = "ViewerBehavior"

        fun <V : View> from(view: V): ViewerBehavior? {
            val params = view.layoutParams
            if (params !is CoordinatorLayout.LayoutParams) {
                if (Config.DEBUG) Log.d(TAG, "The view is not a child of CoordinatorLayout $view $params")
            } else {
                val behavior = params.behavior
                if (behavior as? ViewerBehavior == null) {
                    if (Config.DEBUG) Log.d(TAG, "The view is not associated with LivePageBehavior $view $params")
                } else {
                    return behavior
                }
            }
            return null
        }
    }
}