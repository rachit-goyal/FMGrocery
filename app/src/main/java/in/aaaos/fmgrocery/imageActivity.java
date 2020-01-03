package in.aaaos.fmgrocery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class imageActivity extends AppCompatActivity {
    int position;
    ArrayList<String> listImageURLs;
    ImageView cross;
    private ViewPager viewPager;
    private List<Fragment> listFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        cross=(ImageView)findViewById(R.id.cross);
        viewPager = (ViewPager) findViewById(R.id.view);
        getArguments();
        createFragments();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getArguments() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            listImageURLs = bundle.getStringArrayList("imageURLs");
            position = bundle.getInt("position");
        }
    }

    private void createFragments() {
        for(int i=0;i<listImageURLs.size();i++){
            Bundle bundle = new Bundle();
            bundle.putString("imageURL", listImageURLs.get(i));
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.setArguments(bundle);
            listFragments.add(imageFragment);
        }
    }

}
