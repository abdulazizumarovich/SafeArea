package abdulaziz.umarovich.safearea;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SafeAreaFrame extends FrameLayout {

    public SafeAreaFrame(@NonNull Context context) {
        this(context, null);
    }

    public SafeAreaFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.safeAreaFrameStyle);
    }

    public SafeAreaFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try (TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SafeAreaFrame, defStyleAttr, R.style.SafeAreaFrame)) {
            int edges = a.getInt(R.styleable.SafeAreaFrame_edges, SafeArea.EDGE_ALL);
            SafeArea.apply(this, edges, SafeArea.InsetType.PADDING);
        }
    }
}
