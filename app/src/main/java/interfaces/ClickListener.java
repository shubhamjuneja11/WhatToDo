package interfaces;

import android.view.View;

/**
 * Created by junejaspc on 25/11/16.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
