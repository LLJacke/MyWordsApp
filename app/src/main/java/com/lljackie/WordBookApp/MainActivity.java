package com.lljackie.WordBookApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.lljackie.WordBookApp.wordcontract.Words;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        WordItemFragment.OnFragmentInteractionListener,
        WordDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = "myTag";
    private String YouDaoBaseUrl = "http://fanyi.youdao.com/openapi.do";
    private String YouDaoKeyFrom = "haobaoshui";
    private String YouDaoKey = "1650542691";
    private String YouDaoType = "data";
    private String YouDaoDoctype = "json";
    private String YouDaoVersion = "1.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null)
            wordsDB.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                //查找
                SearchDialog();
                return true;
            case R.id.action_insert:
                //新增单词
                InsertDialog();
                return true;
            case R.id.action_YouDaoSearch:
                YouDaoSearchDialog();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * 更新单词列表
     */
    private void RefreshWordItemFragment() {
        WordItemFragment wordItemFragment = (WordItemFragment) getFragmentManager().findFragmentById(R.id.wordslist);
        wordItemFragment.refreshWordsList();
    }

    /**
     * 更新单词列表
     */
    private void RefreshWordItemFragment(String strWord) {
        WordItemFragment wordItemFragment = (WordItemFragment) getFragmentManager().findFragmentById(R.id.wordslist);
        wordItemFragment.refreshWordsList(strWord);
    }

    //新增对话框
    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")//标题
                .setView(tableLayout)//设置视图
                        //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();

                        //既可以使用Sql语句插入，也可以使用使用insert方法插入
                        // InsertUserSql(strWord, strMeaning, strSample);
                        WordsDB wordsDB=WordsDB.getWordsDB();
                        wordsDB.Insert(strWord, strMeaning, strSample);

                        //单词已经插入到数据库，更新显示列表
                        RefreshWordItemFragment();


                    }
                })
                        //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框

    }


    //删除对话框
    private void DeleteDialog(final String strId) {
        new AlertDialog.Builder(this).setTitle("删除单词").setMessage("是否真的删除单词?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //既可以使用Sql语句删除，也可以使用使用delete方法删除
                WordsDB wordsDB=WordsDB.getWordsDB();
                wordsDB.DeleteUseSql(strId);

                //单词已经删除，更新显示列表
                RefreshWordItemFragment();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    //修改对话框
    private void UpdateDialog(final String strId, final String strWord, final String strMeaning, final String strSample) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText) tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText) tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText) tableLayout.findViewById(R.id.txtSample)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")//标题
                .setView(tableLayout)//设置视图
                        //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();

                        //既可以使用Sql语句更新，也可以使用使用update方法更新
                        WordsDB wordsDB=WordsDB.getWordsDB();
                        wordsDB.UpdateUseSql(strId, strWord, strNewMeaning, strNewSample);

                        //单词已经更新，更新显示列表
                        RefreshWordItemFragment();
                    }
                })
                        //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框


    }

    //查找对话框
    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.searchterm, null);
        new AlertDialog.Builder(this)
                .setTitle("查找单词")//标题
                .setView(tableLayout)//设置视图
                        //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtSearchWord = ((EditText) tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();

                        //单词已经插入到数据库，更新显示列表
                        RefreshWordItemFragment(txtSearchWord);
                    }
                })
                        //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框

    }

    //有道查找对话框
    private void YouDaoSearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.searchterm, null);
        new AlertDialog.Builder(this)
                .setTitle("从有道查找单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtSearchWord = ((EditText) tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();

                        //单词已经插入到数据库，更新显示列表
                        YouDaoSearch(txtSearchWord);
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框

    }


    private void YouDaoSearch(String txtSearchWord) {
        String YouDaoUrl = YouDaoBaseUrl + "?keyfrom=" + YouDaoKeyFrom
                + "&key=" + YouDaoKey + "&type=" + YouDaoType + "&doctype="
                + YouDaoDoctype + "&type=" + YouDaoType + "&version="
                + YouDaoVersion + "&q=" + txtSearchWord;
        try {
            Log.d(TAG,"YouDaoUrl:"+YouDaoUrl);
            AnalyzingOfJson(YouDaoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AnalyzingOfJson(String url) throws Exception {
        String strWord = null,strMeaning = null;

        // 第一步，创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);

        // 第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);

        if (httpResponse.getStatusLine().getStatusCode() == 200) {

            // 第三步，使用getEntity方法活得返回结果
            String result = EntityUtils.toString(httpResponse.getEntity());
            JSONArray jsonArray = new JSONArray("[" + result + "]");
            String message = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    String errorCode = jsonObject.getString("errorCode");
                    if (errorCode.equals("20")) {
                        Toast.makeText(getApplicationContext(), "要翻译的文本过长",
                                Toast.LENGTH_SHORT);
                    } else if (errorCode.equals("30 ")) {
                        Toast.makeText(getApplicationContext(), "无法进行有效的翻译",
                                Toast.LENGTH_SHORT);
                    } else if (errorCode.equals("40")) {
                        Toast.makeText(getApplicationContext(), "不支持的语言类型",
                                Toast.LENGTH_SHORT);
                    } else if (errorCode.equals("50")) {
                        Toast.makeText(getApplicationContext(), "无效的key",
                                Toast.LENGTH_SHORT);
                    } else {
                        // 要翻译的内容
                        String query = jsonObject.getString("query");
                        message = query;

                        strWord = query;
                        Log.d(TAG,"query:"+query);

                        // 翻译内容
                        String translation = jsonObject
                                .getString("translation");
                        message += "\t" + translation;

                        strMeaning = translation;
                        Log.d(TAG,"translation:"+translation);

                        // 有道词典-基本词典
                        if (jsonObject.has("basic")) {
                            JSONObject basic = jsonObject
                                    .getJSONObject("basic");
                            if (basic.has("phonetic")) {
                                String phonetic = basic.getString("phonetic");
                                message += "\n\t" + phonetic;


                                Log.d(TAG,"phonetic:"+phonetic);

                            }
                            if (basic.has("explains")) {
                                String explains = basic.getString("explains");
                                message += "\n\t" + explains;

                                Log.d(TAG,"explains:"+explains);

                            }
                        }
                        // 有道词典-网络释义
                        if (jsonObject.has("web")) {
                            String web = jsonObject.getString("web");
                            JSONArray webString = new JSONArray("[" + web + "]");
                            message += "\n网络释义：";
                            JSONArray webArray = webString.getJSONArray(0);
                            int count = 0;
                            while (!webArray.isNull(count)) {

                                if (webArray.getJSONObject(count).has("key")) {
                                    String key = webArray.getJSONObject(count)
                                            .getString("key");
                                    message += "\n\t<" + (count + 1) + ">"
                                            + key;
                                }
                                if (webArray.getJSONObject(count).has("value")) {
                                    String value = webArray
                                            .getJSONObject(count).getString(
                                                    "value");
                                    message += "\n\t   " + value;
                                }
                                count++;
                            }
                        }
                    }
                }
            }
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
            builder.setMessage(message)//显示的消息内容
                    .setTitle("有道查询");//对话框标题

            builder.setPositiveButton("返回", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            final String finalStrWord = strWord;
            final String finalStrMeaning = strMeaning;
            builder.setNeutralButton("添加到本地", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if ("".equals(finalStrWord))
                        Toast.makeText(MainActivity.this, "查询失败，无法添加", Toast.LENGTH_LONG).show();
                    else {
                        WordsDB wordsDB = WordsDB.getWordsDB();
                        String Word = finalStrWord;
                        String Meaning = finalStrMeaning.substring(2,finalStrMeaning.length()-2);
                        wordsDB.Insert(Word, Meaning, "");
                        Log.d(TAG,"finalStrWord:"+finalStrWord);
                        Log.d(TAG,"finalStrMeaning:"+finalStrMeaning);

                        RefreshWordItemFragment();
                    }
                }

            });
            builder.show();
        } else {
            Toast.makeText(getApplicationContext(), "提取异常", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onWordDetailClick(Uri uri) {

    }

    /**
     * 当用户在单词列表Fragment中单击某个单词时回调此函数
     * 判断如果横屏的话，则需要在右侧单词详细Fragment中显示
     */
    @Override
    public void onWordItemClick(String id) {

        if(isLand()) {//横屏的话则在右侧的WordDetailFragment中显示单词详细信息
            ChangeWordDetailFragment(id);
        }else{
            Intent intent = new Intent(MainActivity.this,WordDetailActivity.class);
            intent.putExtra(WordDetailFragment.ARG_ID, id);
            startActivity(intent);
        }

    }

    //是否是横屏
    private boolean isLand(){

        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            return true;
        return false;
    }

    private void ChangeWordDetailFragment(String id){
        Bundle arguments = new Bundle();
        arguments.putString(WordDetailFragment.ARG_ID, id);
        Log.v(TAG, id);

        WordDetailFragment fragment = new WordDetailFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().replace(R.id.worddetail, fragment).commit();
    }

    @Override
    public void onDeleteDialog(String strId) {
        DeleteDialog(strId);
    }

    @Override
    public void onUpdateDialog(String strId) {
        Log.e("tag", "!!!!!!");

        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null && strId != null) {


            Words.WordDescription item = wordsDB.getSingleWord(strId);
            if (item != null) {
                UpdateDialog(strId, item.word, item.meaning, item.sample);
            }

        }

    }
}
