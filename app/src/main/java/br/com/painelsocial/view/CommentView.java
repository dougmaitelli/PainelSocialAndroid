package br.com.painelsocial.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.painelsocial.R;
import br.com.painelsocial.model.Comment;

public class CommentView extends LinearLayout {

    private Comment comment;

    private TextView commentText;

    public CommentView(Context context) {
        super(context);

        build();
    }

    public CommentView(Context context, Comment comment) {
        super(context);
        this.comment = comment;

        this.build();
    }

    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void build() {
        LinearLayout commentView = (LinearLayout) inflate(getContext(), R.layout.comment_view, this);

        commentText = (TextView) commentView.findViewById(R.id.commentText);

        this.refreshBadgeData();
    }

    public void refreshBadgeData() {
        commentText.setText(comment.getDescription());
    }
}