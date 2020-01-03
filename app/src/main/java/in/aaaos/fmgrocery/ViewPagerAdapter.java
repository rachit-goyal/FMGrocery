package in.aaaos.fmgrocery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by RACHIT GOYAL on 5/5/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> listFragment;

    public ViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> listFragment) {
        super(fragmentManager);
        this.listFragment = listFragment;
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }
}
