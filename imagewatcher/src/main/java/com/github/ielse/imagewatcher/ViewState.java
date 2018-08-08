package com.github.ielse.imagewatcher;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * ViewState包含记录了一个View的瞬时UI状态<br/>
 * 并且提供了给View新增状态，抓取View的当前状态，复制View的状态，将View还原至该状态等便捷方法
 */
class ViewState {
    static final int STATE_ORIGIN = R.id.state_origin; // 缩略图初始
    static final int STATE_THUMB = R.id.state_thumb; // 缩略图居中
    static final int STATE_DEFAULT = R.id.state_default; // 高清图初始
    static final int STATE_CURRENT = R.id.state_current; // 当前状态
    static final int STATE_TEMP = R.id.state_temp; // 临时目标
    static final int STATE_DRAG = R.id.state_touch_drag; // 高清图拖拽起点
    static final int STATE_EXIT = R.id.state_exit; // 高清图退出起点
    static final int STATE_TOUCH_SCALE = R.id.state_touch_scale; // 高清图缩放起点

    int mTag;
    int width;
    int height;
    float translationX;
    float translationY;
    float scaleX;
    float scaleY;
    float alpha;

    private ViewState(int tag) {
        mTag = tag;
    }

    static ViewState write(View view, int tag) {
        if (view == null) return null;

        ViewState vs = read(view, tag);
        if (vs == null) view.setTag(tag, vs = new ViewState(tag));

        vs.width = view.getWidth();
        vs.height = view.getHeight();
        vs.translationX = view.getTranslationX();
        vs.translationY = view.getTranslationY();
        vs.scaleX = view.getScaleX();
        vs.scaleY = view.getScaleY();
        vs.alpha = view.getAlpha();
        return vs;
    }

    static ViewState read(View view, int tag) {
        if (view == null) return null;
        return view.getTag(tag) != null ? (ViewState) view.getTag(tag) : null;
    }

    static void clear(View view, int tag) {
        if (view == null) return;
        view.setTag(tag, null);
    }

    static ViewState copy(ViewState mir, int tag) {
        ViewState vs = new ViewState(tag);
        vs.width = mir.width;
        vs.height = mir.height;
        vs.translationX = mir.translationX;
        vs.translationY = mir.translationY;
        vs.scaleX = mir.scaleX;
        vs.scaleY = mir.scaleY;
        vs.alpha = mir.alpha;
        return vs;
    }

    static void restore(View view, int tag) {
        ViewState viewState = read(view, tag);
        if (viewState != null) {
            view.setTranslationX(viewState.translationX);
            view.setTranslationY(viewState.translationY);
            view.setScaleX(viewState.scaleX);
            view.setScaleY(viewState.scaleY);
            view.setAlpha(viewState.alpha);
            if (view.getLayoutParams().width != viewState.width || view.getLayoutParams().height != viewState.height) {
                view.getLayoutParams().width = viewState.width;
                view.getLayoutParams().height = viewState.height;
                view.requestLayout();
            }
        }
    }

    static ValueAnimatorBuilder restoreByAnim(final View view, int tag) {
        ValueAnimator animator = null;
        if (view != null) {
            final ViewState vsCurrent = ViewState.write(view, ViewState.STATE_CURRENT);
            if (vsCurrent.width == 0 && vsCurrent.height == 0) {
                ViewState vsOrigin = ViewState.read(view, ViewState.STATE_ORIGIN);
                if (vsOrigin != null) vsCurrent.width(vsOrigin.width).height(vsOrigin.height);
            }
            final ViewState vsResult = read(view, tag);
            if (vsResult != null) {
                animator = ValueAnimator.ofFloat(0, 1).setDuration(200);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float p = (float) animation.getAnimatedValue();
                        view.setTranslationX(vsCurrent.translationX + (vsResult.translationX - vsCurrent.translationX) * p);
                        view.setTranslationY(vsCurrent.translationY + (vsResult.translationY - vsCurrent.translationY) * p);
                        view.setScaleX(vsCurrent.scaleX + (vsResult.scaleX - vsCurrent.scaleX) * p);
                        view.setScaleY(vsCurrent.scaleY + (vsResult.scaleY - vsCurrent.scaleY) * p);
                        view.setAlpha((vsCurrent.alpha + (vsResult.alpha - vsCurrent.alpha) * p));
                        if (vsCurrent.width != vsResult.width && vsCurrent.height != vsResult.height
                                && vsResult.width != 0 && vsResult.height != 0) {
                            view.getLayoutParams().width = (int) (vsCurrent.width + (vsResult.width - vsCurrent.width) * p);
                            view.getLayoutParams().height = (int) (vsCurrent.height + (vsResult.height - vsCurrent.height) * p);
                            view.requestLayout();
                        }
                    }
                });
            }
        }
        return new ValueAnimatorBuilder(animator);
    }

    static class ValueAnimatorBuilder {
        ValueAnimator mAnimator;

        ValueAnimatorBuilder(ValueAnimator animator) {
            mAnimator = animator;
        }

        ValueAnimatorBuilder addListener(Animator.AnimatorListener listener) {
            if (mAnimator != null) mAnimator.addListener(listener);
            return this;
        }

        ValueAnimator create() {
            return mAnimator;
        }
    }

    ViewState scaleX(float scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    ViewState scaleXBy(float value) {
        this.scaleX *= value;
        return this;
    }

    ViewState scaleY(float scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    ViewState scaleYBy(float value) {
        this.scaleY *= value;
        return this;
    }


    ViewState width(int width) {
        this.width = width;
        return this;
    }

    ViewState height(int height) {
        this.height = height;
        return this;
    }

    ViewState translationX(float translationX) {
        this.translationX = translationX;
        return this;
    }

    ViewState translationY(float translationY) {
        this.translationY = translationY;
        return this;
    }

    ViewState alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }
}