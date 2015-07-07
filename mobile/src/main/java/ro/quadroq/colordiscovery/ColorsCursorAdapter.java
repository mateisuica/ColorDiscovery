package ro.quadroq.colordiscovery;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ro.quadroq.commonclasses.Utils;

/**
 * Created by mateisuica on 21/06/15.
 */
public class ColorsCursorAdapter extends RecyclerView.Adapter<ColorsCursorAdapter.ViewHolder> {

    private static final int layout = R.layout.color_item;
    private Cursor mCursor;

    public ColorsCursorAdapter(Cursor c) {
        super();
        this.mCursor = c;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        TextView name = (TextView) v.findViewById(R.id.colorName);
        ImageView image = (ImageView) v.findViewById(R.id.colorPreview);
        return new ViewHolder(v, name, image);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int color = mCursor.getInt(mCursor.getColumnIndex(ColorItem.COLUMN_COLOR));
        String customName = mCursor.getString(mCursor.getColumnIndex(ColorItem.COLUMN_NAME));
        String colorName = Utils.getColorString(color);
        if(!TextUtils.isEmpty(customName)) {
            colorName = colorName + " - " + customName;
        }
        holder.colorName.setText(colorName);
        holder.colorPreview.setImageDrawable(new ColorDrawable(color));
    }

    @Override
    public int getItemCount() {
        if(mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor c) {
        mCursor = c;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView colorName;
        public ImageView colorPreview;

        public ViewHolder(View root, TextView colorName, ImageView colorPreview) {
            super(root);
            this.colorName = colorName;
            this.colorPreview = colorPreview;
        }
    }
}
