package es.upm.hcid.pui.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import es.upm.hcid.pui.assignment.exceptions.AuthenticationError;
import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class MainActivity extends AppCompatActivity {

    private CountDownLatch mCountDownLatch = new CountDownLatch(1);
    private ModelManager mm = null;

    private Spinner mSpinner;
    private RecyclerView mRecyclerView;
    private ArticleAdapter mArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        try {
            initModelManager();

            getArticleList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mSpinner = findViewById(R.id.sp_article);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String data = parent.getItemAtPosition(position).toString();
                mArticleAdapter.getFilter().filter(data);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRecyclerView = findViewById(R.id.rv_content);
        mArticleAdapter = new ArticleAdapter();
        mRecyclerView.setAdapter(mArticleAdapter);

    }

    private void getArticleList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Article> dataArticles = mm.getArticles();
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if(dataArticles != null){
                                mArticleAdapter.setNewInstance(dataArticles);
                            }
                        }
                    });
                } catch (ServerCommunicationError serverCommunicationError) {
                    serverCommunicationError.printStackTrace();
                }
            }
        }).start();
    }

    private void initModelManager() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Log in
                Properties prop = new Properties();
                prop.setProperty(ModelManager.ATTR_LOGIN_USER, "DEV_TEAM_04");
                prop.setProperty(ModelManager.ATTR_LOGIN_PASS, "123704");
                prop.setProperty(ModelManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
                prop.setProperty(ModelManager.ATTR_REQUIRE_SELF_CERT, "TRUE");


                try{
                    mm = new ModelManager(prop);
                }catch (AuthenticationError e) {
                    e.printStackTrace();
                }

                mCountDownLatch.countDown();
            }
        }).start();
        mCountDownLatch.await();
    }
}