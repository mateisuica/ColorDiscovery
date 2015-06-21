package ro.quadroq.sexdice;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mateisuica on 21/06/15.
 */
public class ColorsCursorAdapter extends CursorAdapter {

    private static final int layout = R.layout.color_item;

    public ColorsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.colorName = (TextView) root.findViewById(R.id.colorName);
        viewHolder.colorPreview = (ImageView) root.findViewById(R.id.colorPreview);
        root.setTag(viewHolder);

        setFields(cursor, viewHolder);

        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        setFields(cursor, viewHolder);
    }

    private void setFields(Cursor cursor, ViewHolder viewHolder) {
        int color = cursor.getInt(cursor.getColumnIndex(ColorItem.COLUMN_COLOR));

        viewHolder.colorName.setText(getColorString(color));
        viewHolder.colorPreview.setImageDrawable(new ColorDrawable(color));

    }
    private String getColorString(int color) {
        return "#" + Integer.toHexString(color).substring(2).toUpperCase();
    }

    private class ViewHolder {
        public TextView colorName;
        public ImageView colorPreview;
    }
}
