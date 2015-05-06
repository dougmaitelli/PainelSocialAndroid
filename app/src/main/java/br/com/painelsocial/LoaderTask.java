package br.com.painelsocial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public abstract class LoaderTask<C extends Context> extends AsyncTask<C, Object, Object> {
    
    protected C context;
    protected ProgressDialog dialog;
    
    public LoaderTask(C context) {
        this(context, true);
    }

    public LoaderTask(C context, boolean dialog) {
        this.context = context;
        
        if (dialog) {
        	this.dialog = new ProgressDialog(context);
        	this.dialog.setMessage("Loading...");
        	this.dialog.setCancelable(false);
        	this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					onCancelTriggered();
				}
			});
        	this.dialog.show();
        }
        
        execute(this.context);
    }
    
    public C getContext() {
        return context;
    }

    @Override
    protected final void onProgressUpdate(Object... values) {
    	onUpdate(values);
    }
    
    public void onUpdate(Object... values) {
    }
    
    public final void doUpdate(Object... values) {
    	publishProgress(values);
    }
    
    public final void setCancellable(boolean cancellable) {
    	if (this.dialog != null) {
    		this.dialog.setCancelable(cancellable);
    	}
    }

    @SafeVarargs
    public final C doInBackground(C... params) {
        process();
        return null;
    }
    
    public abstract void process();

    @Override
    protected final void onPostExecute(Object result) {
        onComplete();
        if (dialog != null) {
        	dialog.dismiss();
    	}
    }
    
    public void onComplete() {
    }
    
    public void onCancelTriggered() {
    }
}