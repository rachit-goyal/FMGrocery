package in.aaaos.fmgrocery;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.List;


public class MyOderAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<MyOrderBean> studentList;
    static Context context;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;


    private OnLoadMoreListener onLoadMoreListener;


    public MyOderAdapter(ArrayList<MyOrderBean> students, RecyclerView recyclerView) {

        studentList = students;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
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
                    R.layout.myoder_lay, parent, false);

            vh = new StudentViewHolder(v);
        }

        else {
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

            final MyOrderBean singleStudent = (MyOrderBean) studentList.get(position);
            ((StudentViewHolder) holder).id.setText(singleStudent.getId());
            ((StudentViewHolder) holder).status.setText(singleStudent.getStatus());
            ((StudentViewHolder) holder).total.setText(singleStudent.getTotal());
            ((StudentViewHolder) holder).viewmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isNetworkAvailable()){
                        Intent i=new Intent(context,OrderSummary.class);
                        i.putExtra("orderid",singleStudent.getId());
                        context.startActivity(i);
                    }
                }
            });


        }
        else {
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



    //
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView id,status,total,viewmore;

        public StudentViewHolder(View v) {
            super(v);
            id = (TextView) v.findViewById(R.id.orderid);
            status = (TextView) v.findViewById(R.id.orderstatus);
            total = (TextView) v.findViewById(R.id.ordertotal);
            viewmore = (TextView) v.findViewById(R.id.viewmore);


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

