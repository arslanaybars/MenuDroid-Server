package tabsswipe;

/**
 * Created by renesotolira on 18/04/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import aybars.arslan.menudroid_server.TabAddCategory;
import aybars.arslan.menudroid_server.TabAddFood;
import aybars.arslan.menudroid_server.TabSearchCategory;
import aybars.arslan.menudroid_server.TabSearchFood;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            TabAddCategory tab1 = new TabAddCategory();
            return tab1;
        }
        else if(position == 1)        // As we are having 4 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            TabSearchCategory tab2 = new TabSearchCategory();
            return tab2;
        }
        else if(position == 2)            // As we are having 4 tabs if the position is now 0 it must be 1 so we are returning third tab
        {
            TabAddFood tab3 = new TabAddFood();
            return tab3;
        }
        else             // As we are having 4 tabs if the position is now 0 it must be 1 so we are returning fourth tab
        {
            TabSearchFood tab4 = new TabSearchFood();
            return tab4;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}