package zetbrush.generatingmain;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

/**
 * Created by Arman on 5/6/15.
 */
public  class MergeVidsWorker extends ModernAsyncTask<Integer, Integer, Integer> implements ICommandProvider {
    Context ctx;
    String stillPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/req_images/transitions/still_";
    String transVidpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/req_images/transitions/trans";
    String outputVidName="";
    String audioPath="";
    IThreadCompleteListener listener;
    ProgressHandler prgs;
    IProgress progress;
    int imageCount;

    public MergeVidsWorker(Context ctx,String outputnm,String audiopath){
        this.ctx = ctx;
        this.outputVidName=outputnm;
        this.audioPath=audiopath;
    }

    public void setListener(IThreadCompleteListener listener, ProgressHandler prgs, IProgress prgress) {
        this.listener = listener;

        this.progress=prgress;
        this.prgs = prgs;
    }

    @Override
    protected Integer doInBackground(final Integer... params) {

         FFmpeg mmpg = new FFmpeg(ctx);
        imageCount=params[0];
        try {
            final int[] imgc = {imageCount};
            mmpg.execute(getCommand(params[0].toString(), outputVidName), new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {

                    Log.d("Merging.....",message);
                }

                @Override
                public void onProgress(String message) {
                    publishProgress(--imgc[0]);
                    Log.d("Merging.....", message);
                }

                @Override
                public void onFailure(String message)
                {
                    Log.d("Merging....Failure",message);

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    if(audioPath==null || audioPath==""){

                        listener.notifyOfThreadComplete(666);
                        Toast.makeText(ctx,"Video is ready!",Toast.LENGTH_SHORT).show();
                    }
                        else {

                        listener.notifyOfThreadComplete(555);
                    }

                    Log.d("Merging.....", "Finished!");

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.d("Merging||CreatingTrans", "progress.progress( " + prgs.updateProgress((int)(imageCount/2.0)) + "  imageCount " + imageCount);
        progress.progress(prgs.updateProgress((int)(imageCount/2.0)),"Creating Transitions");

    }

    @Override
    public String getCommand(String... param) {
        Log.d("Merge Command","-i " + "concat:" +videosPathBuilder(Integer.valueOf(param[0])) + " -preset ultrafast "+ "-c:v copy "+param[1]+".mp4");
        return "-i " + "concat:" +videosPathBuilder(Integer.valueOf(param[0])) + " -preset ultrafast "+ "-c:v copy "+param[1]+".mp4";
    }


    private String videosPathBuilder(int count){

        StringBuilder sb = new StringBuilder("");
        int counter =1;
        while(count>0 ) {
            count--;

            sb.append(stillPath+(counter-1)+".ts");
            sb.append("|");
            sb.append(transVidpath+counter+".ts");
            counter++;
            if(count>0) sb.append("|");

        }
    Log.d("MERGE_INFO COMMAND", sb.toString());
        return sb.toString();
    }

}
