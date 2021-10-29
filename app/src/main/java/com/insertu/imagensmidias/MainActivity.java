package com.insertu.imagensmidias;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button btnGaleria, btnCamera;
    ImageView imgFoto;
    Bitmap imgBitmap;

    final int GALERIA_IMAGENS = 1;
    final int CAMERA = 2;
    final int PERMISSAO_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Verificação de permissões para acessar a galeria e a camera
        btnGaleria = findViewById(R.id.btnGaleria);
        btnCamera = findViewById(R.id.btnCamera);
        imgFoto = findViewById(R.id.imgFoto);

        permissoes();


    }

    @Override
    protected void onStart() {
        super.onStart();

        btnGaleria.setOnClickListener(view -> {

            Intent itGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(itGaleria, GALERIA_IMAGENS);

        });


        btnCamera.setOnClickListener(view -> {

            Intent itCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(itCamera, CAMERA);

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGENS) {

            //pegar o caminho onde a imagem está salva
            Uri imagemSelecionada = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(imagemSelecionada, filePath, null, null, null);
            c.moveToFirst();
            int indiceColuna = c.getColumnIndex(filePath[0]);

            //guardar o indice da galeria
            String caminhoImagem = c.getString(indiceColuna);
            c.close();

            //decodificar para envio
            imgBitmap= BitmapFactory.decodeFile(caminhoImagem);

            //mostrar na imageView
            imgFoto.setImageBitmap(imgBitmap);

        } else if (resultCode == RESULT_OK && requestCode == CAMERA) {

            imgBitmap = (Bitmap) data.getExtras().get("data");
            imgFoto.setImageBitmap(imgBitmap);

        }
    }

    public void permissoes() {
        //Primeira pergunta: Se as seguintes permissões estão diferente de permitido
        if (
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {


            //se alguma das permissões não está liberada - Requisita a Permissão
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, PERMISSAO_REQUEST);

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSAO_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                btnGaleria.setEnabled(true);
                btnCamera.setEnabled(true);

            } else {

                btnGaleria.setEnabled(false);
                btnCamera.setEnabled(false);
            }

        }
    }
}