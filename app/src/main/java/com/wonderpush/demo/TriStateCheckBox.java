package com.wonderpush.demo;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

public class TriStateCheckBox extends AppCompatCheckBox {

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a tri state checkbox changed.
     */
    public static interface OnCheckedTriStateChangeListener {
        /**
         * Called when the checked state of a tri state checkbox has changed.
         *
         * @param triStateCheckBox The tri state checkbox view whose state has changed.
         * @param state            The new checked state of triStateCheckBox.
         */
        void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, State state);
    }

    public enum State {
        INDETERMINATE(null),
        UNCHECKED(Boolean.FALSE),
        CHECKED(Boolean.TRUE);

        private Boolean mBoolValue;

        State(Boolean boolValue) {
            mBoolValue = boolValue;
        }

        public Boolean getBoolean() {
            return mBoolValue;
        }

        public static State fromBoolean(Boolean value) {
            if (value == null) return INDETERMINATE;
            return value ? CHECKED : UNCHECKED;
        }

    }

    private State mState = State.INDETERMINATE;

    private boolean mBroadcasting;
    private OnCheckedTriStateChangeListener mOnCheckedTriStateChangeListener;

    public TriStateCheckBox(Context context) {
        super(context);
        init(null, 0);
    }

    public TriStateCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TriStateCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        setState(mState);
    }

    public State getState() {
        return mState;
    }

    public TriStateCheckBox setState(State state) {
        mState = state;
        switch (mState) {
            case INDETERMINATE:
                setButtonDrawable(R.drawable.ic_indeterminate_check_box_black_24dp);
                break;
            case CHECKED:
                setButtonDrawable(R.drawable.ic_check_box_black_24dp);
                break;
            case UNCHECKED:
                setButtonDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
                break;
        }

        // Avoid infinite recursions if setChecked() is called from a listener
        if (mBroadcasting) {
            return this;
        }

        mBroadcasting = true;
        if (mOnCheckedTriStateChangeListener != null) {
            mOnCheckedTriStateChangeListener.onCheckedTriStateChanged(this, mState);
        }
        mBroadcasting = false;

        return this;
    }

    /**
     * Register a callback to be invoked when the checked state of this button
     * changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedTriStateChangeListener(OnCheckedTriStateChangeListener listener) {
        mOnCheckedTriStateChangeListener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        setState(checked ? State.CHECKED : State.UNCHECKED);
    }

    public void setCheckedBoolean(Boolean checked) {
        setState(State.fromBoolean(checked));
    }

    @Override
    public boolean isChecked() {
        return mState == State.CHECKED;
    }

    public Boolean getCheckedBoolean() {
        return mState.getBoolean();
    }

    @Override
    public void toggle() {
        switch (mState) {
            case INDETERMINATE:
                setState(State.CHECKED);
                break;
            case CHECKED:
                setState(State.UNCHECKED);
                break;
            case UNCHECKED:
                setState(State.INDETERMINATE);
                break;
        }
    }

}
