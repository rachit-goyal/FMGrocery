package in.aaaos.fmgrocery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class DataAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<ResBean> studentList;
    static Context context;

    private int visibleThreshold = 5;
    private int  totalItemCount,lastVisibleItemPosition;
    private boolean loading;


    private OnLoadMoreListener onLoadMoreListener;


    public DataAdapter(ArrayList<ResBean> students, RecyclerView recyclerView) {

        studentList = students;

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {

            final StaggeredGridLayoutManager linearLayoutManager = (StaggeredGridLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = linearLayoutManager.getItemCount();
                            int[] lastVisibleItem = linearLayoutManager.findLastVisibleItemPositions(null);
                            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItem);

                        /*    lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPositions();*/
                            if (!loading
                                    && totalItemCount <= (lastVisibleItemPosition + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    @Override
    public int getItemViewType(int position) {

            return studentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_item, parent, false);

            vh = new StudentViewHolder(v);
        } else {
            if (isNetworkAvailable()) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false);

                vh = new ProgressViewHolder(v);
            }

        }
        return vh;
    }


    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            ResBean singleStudent = (ResBean) studentList.get(position);
            Picasso.with(((StudentViewHolder) holder).img.getContext()).load(singleStudent.getSrc()).placeholder(R.drawable.app_logo).resize(300,300).into(((StudentViewHolder) holder).img);
            ((StudentViewHolder) holder).name.setText(singleStudent.getName());
            ((StudentViewHolder) holder).price.setText(singleStudent.getPrice());
            ((StudentViewHolder) holder).regular.setText(singleStudent.getRegularprice());
            ((StudentViewHolder) holder).price.setText(singleStudent.getPrice());
            ((StudentViewHolder) holder).regular.setPaintFlags( ((StudentViewHolder) holder).regular.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

          if(singleStudent.getOption().equals("")){
              ((StudentViewHolder) holder).kg.setVisibility(View.INVISIBLE);
          }
          else {
              ((StudentViewHolder) holder).kg.setText(singleStudent.getOption());
          }
            ((StudentViewHolder) holder).student = singleStudent;
            String reg= String.valueOf(((StudentViewHolder) holder).regular.getText());
            String pri= String.valueOf(((StudentViewHolder) holder).price.getText());

            if(pri.equals("")){
                ((StudentViewHolder) holder).per.setVisibility(View.INVISIBLE);
                ((StudentViewHolder) holder).regular.setVisibility(View.INVISIBLE);
            }
            else{
                int perval= (Integer.parseInt(reg)-Integer.parseInt(pri));
                if(perval==0){
                    ((StudentViewHolder) holder).per.setVisibility(View.INVISIBLE);
                    ((StudentViewHolder) holder).regular.setVisibility(View.INVISIBLE);
                }
                else {
                    ((StudentViewHolder) holder).per.setVisibility(View.VISIBLE);

                    ((StudentViewHolder) holder).amt.setText(String.valueOf(perval) + " ");
                }
            }
        }
       else {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            if(studentList.size()<10){
                ((ProgressViewHolder) holder).progressBar.setVisibility(View.GONE);
            }
        }


    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView price,regular,name,kg,rs,amt;

        public ResBean student;
        LinearLayout per,pro;

        public StudentViewHolder(View v) {
            super(v);
            per=(LinearLayout)v.findViewById(R.id.per);
            pro=(LinearLayout)v.findViewById(R.id.pro);
            amt=(TextView)v.findViewById(R.id.amt);
            rs=(TextView)v.findViewById(R.id.rs);
            img = (ImageView) v.findViewById(R.id.img);
            price=(TextView)v.findViewById(R.id.price);
            regular=(TextView)v.findViewById(R.id.regular);
            name=(TextView)v.findViewById(R.id.pdtname);
            kg=(TextView)v.findViewById(R.id.kg);
            pro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent i=new Intent(context,Single_product.class);
                i.putExtra("id",student.getId());
                    i.putExtra("name",student.getName());
                context.startActivity(i);
                }
            });
        }



    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;


        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }




}
