package es.upm.hcid.pui.assignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import com.scrat.app.richtext.RichEditText;

import java.util.ArrayList;
import java.util.List;

import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class ArticleAdapter extends BaseQuickAdapter<Article, BaseViewHolder> implements Filterable {
    private List<Article> mSourceArticleList;
    private List<Article> mFilterArticleList;
    private static Context context;
    public ArticleAdapter(Context context) {
        super(R.layout.item_article);
        mSourceArticleList = new ArrayList<>();
        mFilterArticleList = new ArrayList<>();

    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, Article article){
        try {
            baseViewHolder.setImageBitmap(R.id.iv_bg,
                    getBitmap(article.getImage().getImage()));
        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }

        baseViewHolder.setText(R.id.tv_title, article.getTitleText());
        baseViewHolder.setText(R.id.tv_category, article.getCategory());
        ((RichEditText)(baseViewHolder.getView(R.id.tv_abstract))).fromHtml(article.getAbstractText());


        RelativeLayout detailed_layout = baseViewHolder.getView(R.id.detailed_layout);
        detailed_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view1) {
                // with context you take the parent of the function where it's being runned in

                Intent intent1 = new Intent (view1.getContext(), DetailArticle.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//because we use Application context

                intent1.putExtra("passed_article_id", article.getId());
                view1.getContext().startActivity(intent1);
            }
        });
    }

    @Override
    public void setNewInstance(List<Article> list) {
        super.setNewInstance(list);
        mFilterArticleList = list;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if(mSourceArticleList.isEmpty() && !getData().isEmpty()){
                    mSourceArticleList.addAll(getData());
                }
                List<Article> articleFilterList = new ArrayList<>();
                for (int i = 0; i < mSourceArticleList.size(); i++) {
                    if (charString.equalsIgnoreCase("All")) {
                        articleFilterList.add(mSourceArticleList.get(i));
                        continue;
                    }
                    if(mSourceArticleList.get(i).getCategory().contains(charString)){
                        articleFilterList.add(mSourceArticleList.get(i));
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = articleFilterList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.e("test1210", "s:" + filterResults.values);
                setNewInstance((List<Article>) filterResults.values);
                //refresh
                notifyDataSetChanged();
            }
        };
    }

    private Bitmap getBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
