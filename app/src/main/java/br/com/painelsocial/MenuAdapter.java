package br.com.painelsocial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends BaseAdapter {

    private Context context;
    private List<AppMenu> adapter;

    public MenuAdapter(Context context) {
        super();
        this.context = context;
        this.adapter = new ArrayList<AppMenu>();
    }
    
    public void add(AppMenu item) {
		adapter.add(item);
	}
    
    public long getItemId(int position) {
        return position;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    
    public Object getItem(int position) {
        return adapter.get(position);
    }

    public int getCount() {
        return adapter.size();
    }

    public int getViewTypeCount() {
        return 1;
    }

    public int getItemViewType(int position) {
        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	AppMenu item = adapter.get(position);

        LinearLayout view = (LinearLayout) convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (LinearLayout) inflater.inflate(R.layout.menu_item, null);
        }

        view.setTag(item.name());

        TextView text = (TextView) view.findViewById(R.id.menu_text);
        text.setText(item.getText());
            
        return view;
    }
}