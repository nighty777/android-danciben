package com.study.electronic_dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etSearch;
    Button btnSearch;
    TextView tvWordResult;
    TextView tvMeanResult;
    TextView tvSentenceResult;
    TextView tvSentenceCHS;
    TextView tvPhoneticsymbolUS;
    TextView tvPhoneticsymbolUK;
    Button btnAdd;
    Button button;//查询
    Button button2;//单词本
    Button button3;//帮助

    DB database;
    Dictionary dictionary;
    Dictionary new_dictionary;
    WordValue wordValue=null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        Intent intent=new Intent();
        switch (id){
            case R.id.mi_dictionary:
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.mi_wordnote:
                intent.setClass(this,WordnoteActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.mi_help:
                intent.setClass(this, Help.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch=findViewById(R.id.et_search);
        btnSearch=findViewById(R.id.btn_search);
        tvWordResult=findViewById(R.id.tv_wordresult);
        tvMeanResult=findViewById(R.id.tv_meanresult);
        tvSentenceResult=findViewById(R.id.tv_sentenceresult);
        tvSentenceCHS=findViewById(R.id.et_sentenceCHS);
        tvPhoneticsymbolUS=findViewById(R.id.tv_phoneticsymbolUS);
        tvPhoneticsymbolUK=findViewById(R.id.tv_phoneticsymbolUK);
        btnAdd=findViewById(R.id.btn_add);
        button=findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        if (isLand()==false) {


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 添加点击响应事件
                Intent intent=new Intent(MainActivity.this,MainActivity.class);
                //启动
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 添加点击响应事件
                Intent intent=new Intent(MainActivity.this,WordnoteActivity.class);
                //启动
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 添加点击响应事件
                Intent intent=new Intent(MainActivity.this, Help.class);
                //启动
                startActivity(intent);
            }
        });

        }
        dictionary=new Dictionary(this,"dict");
        new_dictionary=new Dictionary(this,"newdict");
        //页面跳转

//搜索单词
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if ("".equals(etSearch.getText().toString())) {
                            //判断是否输入
                            showToastInThread("请输入单词");
                        } else {
//getWordFromInternet函数 从网上查词 赋给wordvalue
                            wordValue = dictionary.getWordFromInternet(etSearch.getText().toString());

//javabean wordvalue 单词的所有信息
                            if (wordValue != null && wordValue.getInterpret() != null && !"".equals(wordValue.getPsA())) {
                                showResearchWordInterpret(wordValue);

                                Log.d("MainActivity", "if条件");
                                Log.d("MainActivity", "释义"+wordValue.getInterpret());
                            } else {

                                showToastInThread("未找到相关释义");
                                Log.d("MainActivity", "else条件");
                            }
                        }
                    }
                }).start();
            }
        });
//存入单词本数据库
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if ("".equals(etSearch.getText().toString())) {
                            //没做任何输入
                            showToastInThread("请输入单词");
                            //  Toast.makeText(MainActivity.this, "请输入单词", Toast.LENGTH_SHORT).show();
                        } else {
                            //输入不空
                            //从数据库中查找
                            wordValue = dictionary.getWordFromDict(etSearch.getText().toString());
                            if (wordValue == null) {
                                //若为空,说明数据库中没有此单词
                                //从网络获取
                                wordValue = dictionary.getWordFromInternet(etSearch.getText().toString());
                                if (wordValue == null) {
                                    //拼写错误
                                    showToastInThread("请检查单词是否拼写正确");
                                    // Toast.makeText(MainActivity.this, "请检查单词是否拼写正确", Toast.LENGTH_LONG).show();
                                } else {
                                    //查找完毕
                                    //插入单词
                                    dictionary.insertWordToDict(wordValue, true);
                                    showToastInThread("单词已存入数据库");
                                    // Toast.makeText(MainActivity.this, "单词已存入数据库", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                //词典（数据库）中有
                                //showToastInThread(word.getInterpret());
                                showToastInThread("该单词已存入数据库,请不要重复操作");
                                //Toast.makeText(MainActivity.this,"该单词已存入数据库,请不要重复操作",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).start();
            }
        });
    }





    /**
     * 线程中的ui操作，用于把参数显示到屏幕上
     *
     * @param wordValue

     */
    private void showResearchWordInterpret(final WordValue wordValue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在此进行ui操作
                //给textview赋值 单词和含义
                tvWordResult.setText(wordValue.getWord());
                tvMeanResult.setText(wordValue.getInterpret());
                //如果横屏，给textview例句赋值
                if (isLand()) {
                    tvSentenceResult.setText(wordValue.getSentOrig());
                    tvSentenceCHS.setText(wordValue.getSentTrans());
                }
                else {
                    tvPhoneticsymbolUS.setText("美["+wordValue.psA+"]");
                    tvPhoneticsymbolUK.setText("英["+wordValue.psE+"]");
                }
            }
        });
    }

    /**
     * 子线程中弹出toast
     *
     * @param string 需要显示的提示信息
     */
    private void showToastInThread(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
            }
        });
    }

    //判断当前是否为横屏
    private boolean isLand() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        }
        return false;
    }
}
