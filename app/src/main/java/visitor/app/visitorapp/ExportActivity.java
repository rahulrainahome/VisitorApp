package visitor.app.visitorapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import visitor.app.utils.Constants;

/**
 * @class: ExportActivity
 * @desc: Class responsible for handling the data to export in excel sheet.
 */

public class ExportActivity extends AppCompatActivity {

    ProgressBar progressBar;
    File file;
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        int check1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int check2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if((check1 != PackageManager.PERMISSION_GRANTED) || (check2 != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getApplicationContext(), "Go to permissions and \nEnable the Storage permission", Toast.LENGTH_LONG).show();
            final Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            ExportActivity.this.startActivity(i);
        }
        else
        {
            exportExcel();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        int check1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int check2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if((check1 != PackageManager.PERMISSION_GRANTED) || (check2 != PackageManager.PERMISSION_GRANTED)) {
           //No permission granted for: STORAGE
        }
        else
        {
            exportExcel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_visitor:

                if(!flag)
                {
                    Snackbar.make(progressBar, "File not exported.", Snackbar.LENGTH_LONG)
                            .setAction("Try Again ?", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    exportExcel();

                                }

                            }).show();
                    return true;
                }
                ArrayList<Uri> sendDoc = new ArrayList<Uri>();
                File fileIn = new File(file.getPath());
                Uri u = Uri.fromFile(fileIn);
                sendDoc.add(u);

                try
                {
                    final Intent ei = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    ei.setType("plain/text");
                    ei.putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendDoc);
                    startActivityForResult(Intent.createChooser(ei, "Sending multiple attachment"), 12345);

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "No package to share file.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    /**
     * @method: exportExcel
     * @desc: Method for exporting data to excel sheet
     */
    private void exportExcel()
    {
        progressBar.setVisibility(View.VISIBLE);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Visitors");

        HSSFRow rowA = sheet.createRow(0);

        HSSFCell cellA = rowA.createCell(0);
        cellA.setCellValue(new HSSFRichTextString("MY FIRST Data"));

        HSSFCell cellB = rowA.createCell(1);
        cellB.setCellValue(new HSSFRichTextString("MY SECOND Data"));

        FileOutputStream fos = null;
        try
        {
            String str_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "";
            File dirs = new File(str_path);
            dirs.mkdirs();
            file = new File(str_path, getString(R.string.app_name) + ".xls");
            System.out.println("" + str_path);
            fos = new FileOutputStream(file);
            workbook.write(fos);
            Snackbar.make(progressBar, "File exported successfully.", Snackbar.LENGTH_LONG)
                    .setAction("OPEN ?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Uri uri = Uri.fromFile(file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri , "application/vnd.ms-excel");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                            try {
                                startActivity(intent);
                            }
                            catch (ActivityNotFoundException e) {
                                Toast.makeText(getApplicationContext(), "No application available to view Excel", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }).show();
            flag = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(ExportActivity.this, "Data Export Error.", Toast.LENGTH_SHORT).show();
        }
        finally
        {
            progressBar.setVisibility(View.GONE);
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
