package abdulaziz.umarovich.safearea;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SafeArea {

    public static final int EDGE_NONE = 0x00;
    public static final int EDGE_LEFT = 0x01; // 00000001
    public static final int EDGE_TOP = 0x02; // 00000010
    public static final int EDGE_RIGHT = 0x04; // 00000100
    public static final int EDGE_BOTTOM = 0x08; // 00001000

    public static final int EDGE_HORIZONTAL = EDGE_LEFT | EDGE_RIGHT;  // 0x05 - 00000101
    public static final int EDGE_VERTICAL = EDGE_TOP | EDGE_BOTTOM;  // 0x0A - 00001010
    public static final int EDGE_ALL = EDGE_HORIZONTAL | EDGE_VERTICAL; // 0x0F - 00001111

    public enum InsetType {
        PADDING,
        MARGIN
    }

    public static class InsetSettings {
        private final Insets baseInset;
        private final int edges;
        private final InsetType type;

        public InsetSettings(@NonNull View view, int edges, InsetType type) {
            this.edges = edges;
            this.type = type;
            this.baseInset = initBaseInset(view, type);
        }

        private Insets initBaseInset(@NonNull View view, InsetType type) {
            if (type == InsetType.PADDING) {
                return Insets.of(
                        view.getPaddingLeft(),
                        view.getPaddingTop(),
                        view.getPaddingRight(),
                        view.getPaddingBottom()
                );
            }

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            return Insets.of(
                    lp.leftMargin,
                    lp.topMargin,
                    lp.rightMargin,
                    lp.bottomMargin
            );
        }

        public boolean has(int edge) {
            if (edge == EDGE_NONE)
                return edges == EDGE_NONE;
            return (edges & edge) == edge;
        }

        private Insets computeOverlaps(Insets inset, boolean isLTR) {
            if (Insets.NONE.equals(inset))
                return Insets.NONE;

            int left = has(SafeArea.EDGE_LEFT) ? inset.left : 0;
            int top = has(SafeArea.EDGE_TOP) ? inset.top : 0;
            int right = has(SafeArea.EDGE_RIGHT) ? inset.right : 0;
            int bottom = has(SafeArea.EDGE_BOTTOM) ? inset.bottom : 0;

            // Handle RTL layout: swap left/right edges when only one horizontal edge is specified
            if (!isLTR && (edges & EDGE_HORIZONTAL) != EDGE_HORIZONTAL) {
                left = has(SafeArea.EDGE_RIGHT) ? inset.left : 0;
                right = has(SafeArea.EDGE_LEFT) ? inset.right : 0;
            }

            return Insets.of(baseInset.left + left,
                    baseInset.top + top,
                    baseInset.right + right,
                    baseInset.bottom + bottom);
        }

        public void updateInsets(@NonNull View view, Insets inset) {
            boolean isLtr = view.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
            Insets computed = computeOverlaps(inset, isLtr);
            if (type == InsetType.PADDING) {
                view.setPadding(computed.left, computed.top, computed.right, computed.bottom);
            } else {
                ViewGroup.MarginLayoutParams lp =
                        (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                lp.leftMargin = computed.left;
                lp.topMargin = computed.top;
                lp.rightMargin = computed.right;
                lp.bottomMargin = computed.bottom;
                view.setLayoutParams(lp);
            }
        }
    }

    public static void apply(@NonNull View view) {
        apply(view, SafeArea.EDGE_ALL, InsetType.PADDING);
    }

    public static void apply(@NonNull View view, @NonNull InsetType type) {
        apply(view, SafeArea.EDGE_ALL, type);
    }

    public static void apply(@NonNull View view, int edge) {
        apply(view, edge, InsetType.PADDING);
    }

    public static void apply(@NonNull View view, int edge, @NonNull InsetType insetType) {
        InsetSettings settings = new InsetSettings(view, edge, insetType);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets inset = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout()
                    | WindowInsetsCompat.Type.ime());
            settings.updateInsets(view, inset);
            return WindowInsetsCompat.CONSUMED;
        });
        requestApplyInsetsWhenAttached(view);
    }

    private static void requestApplyInsetsWhenAttached(@NonNull View view) {
        if (view.isAttachedToWindow()) {
            // We're already attached, just request as normal.
            ViewCompat.requestApplyInsets(view);
            return;
        }

        // We're not attached to the hierarchy, add a listener to request when we are.
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                v.removeOnAttachStateChangeListener(this);
                ViewCompat.requestApplyInsets(v);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
            }
        });
    }
}
