package in.aaaos.fmgrocery;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by RACHIT GOYAL on 7/20/2017.
 */

public class URLImageParser implements Html.ImageGetter {
    Context context;
    TextView container;
     ArrayList<String> img=new ArrayList<>();

    public URLImageParser(TextView container, Context context) {
        this.context = context;
        this.container = container;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();


        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);


        return urlDrawable;
    }



    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;


        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if(result == null)
            {
                urlDrawable.drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();
                return;
            }
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, result.getIntrinsicWidth(),
                    result.getIntrinsicHeight());
            urlDrawable.setGravity(Gravity.CENTER);
            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            URLImageParser.this.container.invalidate();

            // For ICS
            URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight() + result.getIntrinsicHeight()));

            // Pre ICS
            URLImageParser.this.container.setEllipsize(null);

            URLImageParser.this.container.setText(URLImageParser.this.container.getText());

        }


        Drawable fetchDrawable(String urlString) {
            img.add(urlString);
            try {
                Drawable drawable = fetch(urlString);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }


        private Drawable fetch(String urlString) throws IOException {
            return new BitmapDrawable(context.getResources(), Picasso.with(context).load(urlString).resize(500,400).centerCrop().get());
        }
    }
  /*  public List<String> getList() {
        return img;
    }*/
}
