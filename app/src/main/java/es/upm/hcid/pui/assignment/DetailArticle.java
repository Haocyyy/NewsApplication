package es.upm.hcid.pui.assignment;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;


public class DetailArticle extends AppCompatActivity {
    private static final int REQUEST_CODE_OPEN_IMAGE = 1;
    private ModelManager mm = null;

    Integer article_id;
    Article article_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);
        this.article_id = getIntent().getIntExtra("passed_article_id", -1);
        getArticle();
        initiateImageChange();
    }

    private void initiateImageChange() {
        ((Button)findViewById(R.id.btn_select_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(i, REQUEST_CODE_OPEN_IMAGE);
            }
        });
    }

    void getArticle(){
        new Thread(() -> {
            try {
                // calls something from the internet, so do it in a different thread
                article_object = MainActivity.mm.getArticle(this.article_id);
                // this.article_id can be called anywhere

                // send the actions back to the UI, because this is on a different thread
                this.runOnUiThread(() -> {
                    try {
                        this.receive_article(article_object);
                    } catch (ServerCommunicationError serverCommunicationError) {
                        serverCommunicationError.printStackTrace();
                    }
                });

            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        }).start();


    }

    private void receive_article(Article article_object) throws ServerCommunicationError {
        this.article_object = article_object;
        TextView title_article = findViewById(R.id.details_title);
        TextView category_article = findViewById(R.id.details_category);
        TextView abstract_article = findViewById(R.id.details_abstract);
        TextView body_article = findViewById(R.id.details_body);
        ImageView image_article = findViewById(R.id.details_image);

        title_article.setText(article_object.getTitleText());
        category_article.setText(article_object.getCategory());
        abstract_article.setText(article_object.getAbstractText());
        body_article.setText(article_object.getBodyText());
        image_article.setImageBitmap(Utils.base64StringToImg(article_object.getImage().getImage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_OPEN_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    InputStream stream = null;
                    try {
                        Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
                        stream = getContentResolver().openInputStream(data.getData());
                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                        // set image to article and reload article (save article)
                        saveImageToArticle(bitmap);
                    } catch (FileNotFoundException | ServerCommunicationError e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else {
                    Toast.makeText(this, "User cancelled the selection", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        }
    }


    void saveImageToArticle(Bitmap bitmap) throws ServerCommunicationError {
        String new_image = Utils.encodeImage(bitmap);

        article_object.addImage(new_image, "new_image");

        try {
            Image img = article_object.getImage();
            if (img != null) {
                String str = img.getImage();
                if (str != null) {
                    bitmap = Utils.base64StringToImg(new_image);
                }
            }
        } catch (ServerCommunicationError serverCommunicationError) {
            System.out.println("oh no");
        }
        if (bitmap == null) {
            ((ImageView) findViewById(R.id.details_image)).setImageResource(R.drawable.brvah_sample_footer_loading);

        } else {
            ((ImageView) findViewById(R.id.details_image)).setImageBitmap(bitmap);

        }

        new Thread(() -> {
            try {
                MainActivity.mm.saveArticle(this.article_object);

            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        }).start();

    }
}