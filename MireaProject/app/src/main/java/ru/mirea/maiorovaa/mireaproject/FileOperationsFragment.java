package ru.mirea.maiorovaa.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileOperationsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private String latestFileName;
    private TextView textViewFileName;
    private EditText editTextFileName;
    private TextView textViewFileContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_file_operations, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab_create_record);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateRecordDialog();
            }
        });

        textViewFileName = root.findViewById(R.id.text_view_file_name);
        editTextFileName = root.findViewById(R.id.edit_text_file_name);
        textViewFileContent = root.findViewById(R.id.text_view_file_content);

        sharedPreferences = requireContext().getSharedPreferences("MyFiles", Context.MODE_PRIVATE);
        latestFileName = sharedPreferences.getString("latestFileName", "");
        textViewFileName.setText(latestFileName); // Устанавливаем название файла

        Button btnGetContent = root.findViewById(R.id.btn_get_content);
        btnGetContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = editTextFileName.getText().toString();
                if (!fileName.isEmpty()) {
                    String fileContent = readFileContent(fileName);
                    if (fileContent != null) {
                        textViewFileContent.setText(fileContent);
                    } else {
                        textViewFileContent.setText("Файл не найден");
                    }
                } else {
                    Toast.makeText(requireContext(), "Введите название файла", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private String readFileContent(String fileName) {
        FileInputStream fis = null;
        StringBuilder content = new StringBuilder();
        try {
            fis = requireContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    private void showCreateRecordDialog() {
        CreateRecordDialogFragment dialogFragment = new CreateRecordDialogFragment();
        dialogFragment.setOnFileSavedListener(new CreateRecordDialogFragment.OnFileSavedListener() {
            @Override
            public void onFileSaved(String fileName, String fileContent) {
                saveToFile(fileName, fileContent);
            }
        });
        dialogFragment.show(getChildFragmentManager(), "CreateRecordDialogFragment");
    }

    private void saveToFile(String fileName, String fileContent) {
        FileOutputStream fos = null;
        try {
            fos = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(fileContent.getBytes());
            Toast.makeText(requireContext(), "Успешно", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().putString("latestFileName", fileName).apply();
            latestFileName = fileName;
            textViewFileName.setText(latestFileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
