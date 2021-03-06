package com.brianco.fileheaven.dialogactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brianco.fileheaven.R;

import java.io.File;
import java.io.IOException;

public class NewFileActivity extends ActionBarActivity {

    public static final String EXTRA_PARENT_PATH = "EXTRA_PARENT_PATH";
    public static final String EXTRA_NEW_FILE_PATH = "EXTRA_NEW_FILE_PATH";

    private static final String KEY_PARENT_PATH = "KEY_PARENT_PATH";

    private Button mCancelButton;
    private Button mCreateButton;
    private Button mFileButton;
    private Button mFolderButton;
    private EditText mNewFileEt;
    private EditText mNewFolderEt;
    private String mParentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setResult(Activity.RESULT_CANCELED);
        mCancelButton = (Button) findViewById(R.id.cancel_new_file);
        mCreateButton = (Button) findViewById(R.id.confirm_create);
        mFileButton = (Button) findViewById(R.id.new_file);
        mFolderButton = (Button) findViewById(R.id.new_folder);
        mNewFileEt = (EditText) findViewById(R.id.new_file_et);
        mNewFolderEt = (EditText) findViewById(R.id.new_folder_et);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNewFileEt.getVisibility() == View.VISIBLE) {
                    createFile(mNewFileEt.getText().toString(), false);
                } else if (mNewFolderEt.getVisibility() == View.VISIBLE) {
                    createFile(mNewFolderEt.getText().toString(), true);
                } else {
                    throw new RuntimeException("May not create when noting is selected");
                }
            }
        });
        mFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popOpenNewFile();
            }
        });
        mFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popOpenNewFolder();
            }
        });
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            mParentPath = intent.getStringExtra(EXTRA_PARENT_PATH);
        } else {
            mParentPath = savedInstanceState.getString(KEY_PARENT_PATH);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_PARENT_PATH, mParentPath);
        super.onSaveInstanceState(outState);
    }

    private void popOpenNewFile() {
        mNewFolderEt.setVisibility(View.GONE);
        mFolderButton.setVisibility(View.VISIBLE);
        mFileButton.setVisibility(View.GONE);
        mNewFileEt.setVisibility(View.VISIBLE);
        mNewFileEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mNewFileEt, InputMethodManager.SHOW_IMPLICIT);
        mCreateButton.setVisibility(View.VISIBLE);
    }

    private void popOpenNewFolder() {
        mNewFileEt.setVisibility(View.GONE);
        mFileButton.setVisibility(View.VISIBLE);
        mFolderButton.setVisibility(View.GONE);
        mNewFolderEt.setVisibility(View.VISIBLE);
        mNewFolderEt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mNewFolderEt, InputMethodManager.SHOW_IMPLICIT);
        mCreateButton.setVisibility(View.VISIBLE);
    }

    private void createFile(final String fileName, final boolean isDirectory) {
        if (fileName.length() <= 0) {
            Toast.makeText(this, isDirectory ? R.string.empty_name_dir
                    : R.string.empty_name, Toast.LENGTH_SHORT).show();
            return;
        }
        final File newFile = new File(mParentPath + "/" + fileName);
        try {
            if (isDirectory && newFile.mkdir() || newFile.createNewFile()) {
                Toast.makeText(this, getString(R.string.file_created, fileName), Toast.LENGTH_SHORT).show();
                final Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_NEW_FILE_PATH, newFile.getPath());
                setResult(Activity.RESULT_OK, resultIntent);
            } else {
                final int res = isDirectory
                        ? R.string.failed_create_exists_folder
                        : R.string.failed_create_exists_file;
                Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_OK);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.failed_create_unknown, Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }
}
