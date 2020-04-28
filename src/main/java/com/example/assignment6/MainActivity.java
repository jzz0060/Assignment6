package com.example.assignment6;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    HPfile myDb;
    TextView balance;
    EditText exDay;
    EditText exMonth;
    EditText exYear;
    EditText exPrice;
    EditText exItem;
    EditText filterDayFrom;
    EditText filterMonthFrom;
    EditText filterYearFrom;
    EditText filterDayTo;
    EditText filterMonthTo;
    EditText filterYearTo;
    EditText filterPriceFrom;
    EditText filterPriceTo;
    Button btnAdd;
    Button btnSub;
    Button btnFilter;
    Button btnClearFilter;
    TableLayout history;
    DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new HPfile(this);

        balance = (TextView) findViewById(R.id.balance);
        exDay = (EditText) findViewById(R.id.exDay);
        exMonth = (EditText) findViewById(R.id.exMonth);
        exYear = (EditText) findViewById(R.id.exYear);
        exPrice = (EditText) findViewById(R.id.exPrice);
        exItem = (EditText) findViewById(R.id.exItem);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnClearFilter = (Button) findViewById(R.id.btnClearFilter);
        filterDayFrom = (EditText) findViewById(R.id.filterDayFrom);
        filterMonthFrom = (EditText) findViewById(R.id.filterMonthFrom);
        filterYearFrom = (EditText) findViewById(R.id.filterYearFrom);
        filterDayTo = (EditText) findViewById(R.id.filterDayTo);
        filterMonthTo = (EditText) findViewById(R.id.filterMonthTo);
        filterYearTo = (EditText) findViewById(R.id.filterYearTo);
        filterPriceFrom = (EditText) findViewById(R.id.filterPriceFrom);
        filterPriceTo = (EditText) findViewById(R.id.filterPriceTo);
        history = (TableLayout) findViewById(R.id.tableHistory);
        GetAllHistory();
        AddTransaction();
        Filter();
        ClearFilter();
    }

    public void AddTransaction(){
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = Double.parseDouble(exPrice.getText().toString());
                        model model = new model();
                        String day = exDay.getText().toString();
                        String month = exMonth.getText().toString();
                        String year = exYear.getText().toString();
                        model.aDate =  CreateDate(day, month, year);
                        model.aItem = exItem.getText().toString();
                        model.aPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetAllHistory();
                        ClearText();
                    }
                }
        );

        btnSub.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double price = -1 * Double.parseDouble(exPrice.getText().toString());
                        model model = new model();
                        String day = exDay.getText().toString();
                        String month = exMonth.getText().toString();
                        String year = exYear.getText().toString();
                        model.aDate =  CreateDate(day, month, year);
                        model.aItem = exItem.getText().toString();
                        model.aPrice = price;
                        boolean result = myDb.createTransaction(model);
                        if (result)
                            Toast.makeText(MainActivity.this, "Transaction Created", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MainActivity.this, "Transaction Not Created", Toast.LENGTH_LONG).show();
                        GetAllHistory();
                        ClearText();
                    }
                }
        );
    }

    public void GetAllHistory(){
        Cursor result = myDb.getAllData();
        DisplayHistory(result, false);
    }

    public void DisplayHistory(Cursor result, boolean filtered){
        if (result == null){
            return;
        }

        StringBuffer buffer = new StringBuffer();
        Double balance = 0.0;
        ClearTable();
        while(result.moveToNext()){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams columnLayout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            columnLayout.weight = 1;

            TextView date = new TextView(this);
            date.setLayoutParams(columnLayout);
            date.setText(result.getString(2));
            tr.addView(date);

            TextView priceView = new TextView(this);
            priceView.setLayoutParams(columnLayout);
            priceView.setText(result.getString(3));
            tr.addView(priceView);

            TextView item = new TextView(this);
            item.setLayoutParams(columnLayout);
            item.setText(result.getString(1));
            tr.addView(item);

            history.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            double price = Double.parseDouble(result.getString(3));
            balance += price;
        }
        if (!filtered){
            MainActivity.this.balance.setText("Current Balance: $" + df.format(balance));
        }
    }

    public void ClearText(){
        MainActivity.this.exDay.setText("");
        MainActivity.this.exMonth.setText("");
        MainActivity.this.exYear.setText("");
        MainActivity.this.exPrice.setText("");
        MainActivity.this.exItem.setText("");
        MainActivity.this.filterDayFrom.setText("");
        MainActivity.this.filterMonthFrom.setText("");
        MainActivity.this.filterYearFrom.setText("");
        MainActivity.this.filterDayTo.setText("");
        MainActivity.this.filterMonthTo.setText("");
        MainActivity.this.filterYearTo.setText("");
        MainActivity.this.filterPriceFrom.setText("");
        MainActivity.this.filterPriceTo.setText("");
    }

    public void ClearTable(){
        int count = history.getChildCount();
        for (int i = 1; i < count; i++) {
            history.removeViewAt(1);
        }
    }

    public void Filter(){
        btnFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String priceFromString = filterPriceFrom.getText().toString();
                        String priceToString = filterPriceTo.getText().toString();
                        String day = filterDayFrom.getText().toString();
                        String month = filterMonthFrom.getText().toString();
                        String year = filterYearFrom.getText().toString();
                        String dateFrom = CreateDate(day, month, year);
                        day = filterDayTo.getText().toString();
                        month = filterMonthTo.getText().toString();
                        year = filterYearTo.getText().toString();
                        String dateTo = CreateDate(day, month, year);


                        Cursor result = myDb.getFilteredData(priceFromString, priceToString, dateFrom, dateTo);
                        DisplayHistory(result, true);
                    }
                }
        );
    }

    public void ClearFilter(){
        btnClearFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClearText();
                        GetAllHistory();
                    }
                }
        );
    }

    public String CreateDate(String day, String month, String year){
        if (month.isEmpty() || day.isEmpty() || year.isEmpty()) {
            return "";
        }
        else {
            int dayIn = Integer.parseInt(day);
            int monthIn = Integer.parseInt(month);
            if (dayIn < 10 && monthIn >= 10) {
                return year + "-" + month + "-0" + day;
            }
            else if (dayIn >= 10 && monthIn < 10){
                return year + "-0" + month + "-" + day;
            }
            else if (dayIn < 10 && monthIn < 10){
                return year + "-0" + month + "-0" + day;
            }
            else {
                return year + "-" + month + "-" + day;
            }
        }
    }
}