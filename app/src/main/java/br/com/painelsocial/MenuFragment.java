package br.com.painelsocial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class MenuFragment extends Fragment {

    private MenuAdapter adp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.slide_menu, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
        ListView listView = (ListView) getView().findViewById(R.id.menu_container);

        adp = new MenuAdapter(getActivity());
        listView.setAdapter(adp);
        
        for (AppMenu menu : AppMenu.values()) {
        	adp.add(menu);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppMenu menu = (AppMenu) parent.getItemAtPosition(position);

                switch (menu) {
                    case SAIR:
                        ((MainActivity) getActivity()).loginActivity();
                        break;
                    default:
                        switchFragment(menu.getTela());
                }
            }
        });
	}

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null) {
			return;
		}
		
		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.switchContent(fragment);
	}

}
