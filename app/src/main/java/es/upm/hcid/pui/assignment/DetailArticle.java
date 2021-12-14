package es.upm.hcid.pui.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DetailArticle extends AppCompatActivity {
    private TextView title_article, subtitle_article, category_article, abstract_article, body_article;

    String title_of_article;
    String subtitle_of_article;
    String category_of_article;
    String abstract_of_article;
    String body_of_article;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);
        String title_of_article = getIntent().getStringExtra("passed_article_title");
        String category_of_article = getIntent().getStringExtra("passed_article_category");
        String abstract_of_article = getIntent().getStringExtra("passed_article_abstract");
        String body_of_article = getIntent().getStringExtra("passed_article_body");
        String subtitle_of_article = getIntent().getStringExtra("passed_article_subtitle");

        title_article = findViewById(R.id.details_title);
        subtitle_article = findViewById(R.id.details_subtitle);
        category_article = findViewById(R.id.details_category);
        abstract_article = findViewById(R.id.details_abstract);
        body_article = findViewById(R.id.details_body);

        title_article.setText(title_of_article);
        subtitle_article.setText(subtitle_of_article);
        category_article.setText(category_of_article);
        abstract_article.setText(abstract_of_article);
        body_article.setText(body_of_article);

    }
}