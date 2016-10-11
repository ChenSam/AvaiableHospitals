package tw.blogspot.httpsamandroidapp.queryhospital;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;
import static tw.blogspot.httpsamandroidapp.queryhospital.R.id.default_activity_button;
import static tw.blogspot.httpsamandroidapp.queryhospital.R.id.spinnerLocal;
import static tw.blogspot.httpsamandroidapp.queryhospital.R.id.spinnerWeek;
import static tw.blogspot.httpsamandroidapp.queryhospital.R.id.spinnerSubLocal;

public class MainActivity extends AppCompatActivity {


    private final int MORNING_SHIFT = 0x0;
    private final int AFTERNOON_SHIFT = 0x7;
    private final int EVENING_SHIFT = 0xe;

    private final List<String[]> localCode = new ArrayList<String[]>();
    private final List<String[]> localString = new ArrayList<String[]>();
    private final String Taipei[] = {"0101", "0102", "0109", "0110", "0111", "0112",
                                    "0115", "0116", "0117", "0118", "0119", "0120"};
    private final String Keelung[] = { "1101", "1102", "1103", "1104", "1105", "1106",
                                    "1107"};
    private final String HsinchiuCity[] = {"1201", "1204", "1205"};
    private final String NewTaipeiCity [] = {"3101", "3102", "3103", "3104", "3105",
                                        "3106", "3107", "3108", "3109", "3110", "3111",
                                        "3112", "3113", "3114", "3115", "3116", "3117",
                                        "3118", "3119", "3120", "3121", "3122", "3123",
                                        "3124", "3125", "3126", "3127", "3128", "3129"};
    private final String Taoyuan [] = {"3201", "3202", "3203", "3204", "3205",
                                        "3206", "3207", "3208", "3209", "3210", "3211",
                                        "3212", "3213"};
    private final String HsinchuCounty [] = {"3301", "3302", "3303", "3305", "3306",
                                            "3307", "3308", "3309", "3310", "3311", "3312",
                                            "3313", "3314"};
    private final String Ilan[] = { "3401", "3402", "3403", "3404", "3405", "3406",
                                    "3407", "3408", "3409", "3410", "3411", "3412"};
    private final String Miaoli [] = { "3501", "3502", "3503", "3504", "3505", "3506",
                                    "3507", "3508", "3509", "3510", "3511", "3512", "3513"};
    private final String Taichung [] = {"1701", "1702", "1703", "1704", "1705", "1706",
                                        "1707", "1708", "3601", "3602", "3603", "3604",
                                        "3605", "3607", "3608", "3609", "3610", "3611",
                                        "3612", "3613", "3614", "3615", "3616", "3617",
                                        "3618", "3619"};


    private final String CodePre [] = {"01", "11", "12", "31", "32", "33", "34", "35", "36"};
    private DataBaseHelper myDBHelper;
    private Context mContext;
    private String location = "";
    private int local = 0;
    private int localSub = 0;
    private int time;
    private int week = 0;
    private int queryTime = 0;
    private ListView listResult;
    private CustomAdapter resultAdapter;
    private List<Hospital> listHos = new ArrayList<>();
    private ArrayAdapter<String> mAdapterSubLocal;
    private List<String> mListSubLocal;
    private int mNumOfCountry = 8; // Total of 22 counties
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this.getApplicationContext();

        myDBHelper = new DataBaseHelper(this);

        try {
            myDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDBHelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        initLocalString();
        initLocalCode();
        initCheck();
        // Gets a handle to the clipboard service.
        clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        Spinner spinnerLocal = (Spinner) findViewById(R.id.spinnerLocal);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapterLocal = ArrayAdapter.createFromResource(this,
                R.array.counties, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerLocal.setAdapter(adapterLocal);


        Spinner spinnerWeek = (Spinner) findViewById(R.id.spinnerWeek);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterWeek = ArrayAdapter.createFromResource(this,
                R.array.weekdays, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterWeek.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerWeek.setAdapter(adapterWeek);

        mListSubLocal = new ArrayList<>();
        mAdapterSubLocal = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mListSubLocal);
        mAdapterSubLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinnerSubLocal = (Spinner) findViewById(R.id.spinnerSubLocal);
        spinnerSubLocal.setAdapter(mAdapterSubLocal);

        spinnerLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //Toast.makeText(mContext, "你選的是" + position, Toast.LENGTH_SHORT).show();
                //Log.d("tag", ilanPre);
                //mListSubLocal.clear();
                local = position;
                mListSubLocal = Arrays.asList(localString.get(position));
                spinnerSubLocal.setSelection(0);
                String code[] = localCode.get(local);
                location = code[0];
                mAdapterSubLocal.clear();
                mAdapterSubLocal.addAll(mListSubLocal);
                mAdapterSubLocal.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int temp;
                temp = 1 << i;
                week = temp + (temp << 7) + (temp << 14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSubLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(mContext, "你選的是222222" + i, Toast.LENGTH_SHORT).show();
                String code[] = localCode.get(local);
                location = code[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ListView listView = (ListView) findViewById(R.id.listview);
        //listHos.add(new Hospital(0, "名稱", "地址", "電話"));
        resultAdapter = new CustomAdapter(this, listHos);
        listView.setAdapter(resultAdapter);

    }

    String myStringArray[] = {"臺北市", "基隆市", "新竹市", "新北市", "桃園市", "新竹縣",
            "宜蘭縣", "苗栗縣", "臺中市", "彰化縣", "南投縣", " 臺南市", "嘉義市", "雲林縣",
            "嘉義縣", "高雄市", "屏東縣", "澎湖縣", "花蓮縣", "台東縣", "金門縣", "連江縣"};
    private void initLocalString() {
        localString.add(getResources().getStringArray(R.array.taipei));
        localString.add(getResources().getStringArray(R.array.keelung));
        localString.add(getResources().getStringArray(R.array.hsinchuCity));
        localString.add(getResources().getStringArray(R.array.newTaipeiCity));
        localString.add(getResources().getStringArray(R.array.taoyuan));
        localString.add(getResources().getStringArray(R.array.hsinchuCounty));
        localString.add(getResources().getStringArray(R.array.ilan));
        localString.add(getResources().getStringArray(R.array.miaoli));
        localString.add(getResources().getStringArray(R.array.taichung));

    }

    private String[] generateCode(int local, int subLocal)
    {
        String code [] = {};

        for (int i = 0; i < mNumOfCountry; i++) {

        }



        return code;
    }
    private void initLocalCode() {
        localCode.add(Taipei);
        localCode.add(Keelung);
        localCode.add(HsinchiuCity);
        localCode.add(NewTaipeiCity);
        localCode.add(Taoyuan);
        localCode.add(HsinchuCounty);
        localCode.add(Ilan);
        localCode.add(Miaoli);
        localCode.add(Taichung);
    }
    public void search(View view) {
        String[] selectionArgs = {"3401", "140", "140"};
        queryTime = week & time;

        if (queryTime == 0) {
            listHos.clear();
            String a = "請選擇時段";
            Toast.makeText(mContext, a, Toast.LENGTH_SHORT).show();
            resultAdapter.notifyDataSetChanged();
            return;

        }

        selectionArgs[0] = location;
        selectionArgs[1] = Integer.toString(queryTime);
        selectionArgs[2] = Integer.toString(queryTime);
        queryTime = 0;

        Cursor c = myDBHelper.getDataBase().rawQuery("SELECT * FROM hospital_table" +
                        " WHERE INSTR(medId, ?)=3 AND (avaiable & CAST(? as INTEGER))=CAST(? as INTEGER) "
                , selectionArgs);
        c.moveToFirst();
        listHos.clear();
        if (c.moveToFirst()) {
            listHos.add(new Hospital(0, "名稱", "地址", "電話"));
            while (c.isAfterLast() == false) {
                String a = "地址  " + c.getString(4);
                listHos.add(new Hospital(0, c.getString(3), c.getString(4),
                        c.getString(5) + "-" + c.getString(6)));
                //Toast.makeText(mContext, a, Toast.LENGTH_SHORT).show();
                c.moveToNext();
            }
            resultAdapter.notifyDataSetChanged();
        } else {
            String a = "找不到符合結果";
            Toast.makeText(mContext, a, Toast.LENGTH_SHORT).show();
            resultAdapter.notifyDataSetChanged();
        }
    }

    public void initCheck()
    {
        CheckBox morning = (CheckBox) findViewById(R.id.checkBox_morning);
        CheckBox afternoon = (CheckBox) findViewById(R.id.checkBox_afternoon);
        CheckBox evening = (CheckBox) findViewById(R.id.checkBox_evening);
        time = 0;

        if (morning.isChecked()) {
            time = time | 0x7F;
        }
        if (afternoon.isChecked()){
            time = time | (0x7F << 7);
        }

        if (evening.isChecked()) {
            time = time | (0x7F << 14);
        }

    }
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox_morning:
                if (checked) {
                    time = time | 0x7F;
                } else {
                    time = time & ~(0x7F);
                }
                break;
            case R.id.checkBox_afternoon:
                if (checked) {
                    time = time | (0x7F << 7);
                } else {
                    time = time & ~(0x7F << 7);
                }
                break;
            case R.id.checkBox_evening:
                if (checked) {
                    time = time | (0x7F << 14);
                } else {
                    time = time & ~(0x7F << 14);
                }
                break;
        }
        //Toast.makeText(mContext, "time = " + Integer.toHexString(time),Toast.LENGTH_SHORT).show();
    }

    public void copy(View view){
        Toast.makeText(mContext, "copy = " + view.getTag(),Toast.LENGTH_SHORT).show();
        ClipData clip = ClipData.newPlainText("simple_text", view.getTag().toString());
        clipboard.setPrimaryClip(clip);

    }

}
