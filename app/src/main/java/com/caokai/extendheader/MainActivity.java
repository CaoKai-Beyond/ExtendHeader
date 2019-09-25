package com.caokai.extendheader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private PullExtendLayout mPullExtendLayout;
    private ExtendListHeader mHeader;
    private ExtendListView   mListView;

    private ItemAdapter itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullExtendLayout=findViewById(R.id.pull_extend);
        mHeader=findViewById(R.id.extend_header);
        mListView=findViewById(R.id.listview);
        itemAdapter=new ItemAdapter(this);
        mListView.setAdapter(itemAdapter);


        mPullExtendLayout.setPullLoadEnabled(false);
        mListView.setPullExtendLayout(this.mPullExtendLayout);


    }
}
