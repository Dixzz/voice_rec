package com.example.cameractivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class MainActivity2 extends AppCompatActivity {
    @BindView(R.id.textViewQ)
    MaterialTextView layoutWel;

    @BindView(R.id.indi)
    LinearProgressIndicator indicator;

    /*@BindView(R.id.appbar)
    AppBarLayout appBarLayout;


    ArrayList<Pojo> arrayList = new ArrayList<>();
    ArrayList<Pojo> fullList = new ArrayList<>();

    adapter adapter;
*/

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.results)
    EditText editText;

    @BindView(R.id.mic)
    View mic;
    SpeechRecognizer speechRecognizer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        boolean isPlayed = getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean("is_played", false);

        if (!isPlayed) {
            FancyShowCaseQueue fancyShowCaseQueue = new FancyShowCaseQueue();
            fancyShowCaseQueue.add(
                    new FancyShowCaseView.Builder(this).focusOn(editText).focusShape(FocusShape.ROUNDED_RECTANGLE).fitSystemWindows(true).title("Results are shown here").roundRectRadius(24).titleGravity(Gravity.BOTTOM).build()
            ).add(new FancyShowCaseView.Builder(this).focusOn(mic).title("Hold mic to speak and release to get results").fitSystemWindows(true).titleGravity(Gravity.BOTTOM).build()).show();
            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putBoolean("is_played", true).apply();
        }
        String[] items = new String[]{"Hindi", "English"};
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.forLanguageTag("hin-IND"));
        //String languagePref = Locale.forLanguageTag("hin").toString();
        String languagePref = new Locale("hi", "IN").toString();

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        speechRecognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {
                this.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                this.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                return true;
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                layoutWel.setText("Listening...");
                indicator.setIndeterminate(true);
                indicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                layoutWel.setText(MainActivity2.this.getString(R.string.app_name));
                indicator.setIndeterminate(false);
                indicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int i) {
                String message;
                if (indicator.getVisibility() == View.VISIBLE) {
                    indicator.setIndeterminate(false);
                    indicator.setVisibility(View.INVISIBLE);
                }
                switch (i) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default:
                        message = "Didn't understand, please try again.";
                        break;
                }
                Toast.makeText(MainActivity2.this, "" + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d("AAA", "" + matches.toString());
                editText.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                    default:
                        String languagePref = new Locale("hi", "IN").toString();
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);
                        editText.setText("");
                        break;
                    case 1:
                        String languagePref2 = new Locale("en", "IN").toString();
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref2);
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref2);
                        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref2);
                        editText.setText("");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*RecyclerView recyclerView = findViewById(R.id.rec3);
        adapter = new adapter(arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout1, int i) {

                float abs = ((float) Math.abs(i)) / ((float) appBarLayout.getTotalScrollRange());
                float f = 2.0f - abs;
                float f2 = 1.0f - abs;
                //Log.d("AAA", "" + i);
                //Main title
                //Log.d("EAA", "onOffsetChanged: " + f2);
                layoutWel.setScaleX(f2);
                layoutWel.setScaleY(f2);
                layoutWel.setTranslationY(getApplicationContext().getResources().getDimensionPixelSize(R.dimen.start_margin) * abs);
            }
        });

        new JsonTask().execute("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.atom");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    new Handler().postDelayed(() -> {
                        if (arrayList.size() <= fullList.size() / 2) {
                            indicator.setIndeterminate(true);
                            indicator.setVisibility(View.VISIBLE);
                            for (int i = arrayList.size(); i < fullList.size(); i++) {
                                arrayList.add(fullList.get(i));
                            }
                            new Handler().postDelayed(() -> {
                                adapter.notifyDataSetChanged();
                                indicator.setIndeterminate(false);
                                indicator.setVisibility(View.INVISIBLE);
                            }, 1000);
                            recyclerView.smoothScrollBy(0, arrayList.size() * 25, new OvershootInterpolator(), 2000);
                        }
                    }, 2000);
                }
            }
        });
    }

    private static class adapter extends RecyclerView.Adapter<adapter.holder> {
        ArrayList<Pojo> arrayList;

        public adapter(ArrayList<Pojo> arrayList) {
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.web_view, parent, false));
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onBindViewHolder(@NonNull holder holder, int position) {
            Log.d("AAA", "" + position);
            //((TextView) holder.itemView).setText(Html.fromHtml(arrayList.get(position).regionTitle));
            ((WebView) holder.itemView.findViewById(R.id.webview)).loadDataWithBaseURL(null, arrayList.get(position).regionTitle, "text/html", "utf-8", null);
            ((TextView) holder.itemView.findViewById(R.id.titleText)).setText(arrayList.get(position).summary);

            WebView mWebView = (WebView)holder.itemView.findViewById(R.id.webview);
            WebSettings settings = mWebView.getSettings();
            settings.setJavaScriptEnabled(false);
            settings.setLoadWithOverviewMode(false);
            settings.setUseWideViewPort(false);
            settings.setSupportZoom(false);
            settings.setBuiltInZoomControls(false);
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            settings.setLoadWithOverviewMode(false);
            settings.setDomStorageEnabled(false);
            mWebView.setScrollbarFadingEnabled(true);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

            /*holder.pos.setText(position + 1 + "");
            holder.title.setText(itemHolder.get(position).name);
            holder.desc.setText(itemHolder.get(position).shopeImage);
            holder.price.setText("₹" + itemHolder.get(position).offer);

            holder.itemView.setScaleX(.95f);
            ViewPropertyAnimator v = holder.itemView.animate();
            v.translationY(.95f);
            v.setDuration((long) (15 * (position + 1) * .5));
            v.setStartDelay((long) (15 * (position + 1) * 1.25));
            v.setInterpolator(new BounceInterpolator());
            v.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    v.scaleX(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300).start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            v.start();
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        static class holder extends RecyclerView.ViewHolder {
            TextView pos, title, desc, price;

            public holder(@NonNull View itemView) {
                super(itemView);
                /*pos = itemView.findViewById(R.id.positionI);
                title = itemView.findViewById(R.id.titlee);
                desc = itemView.findViewById(R.id.desc);
                price = itemView.findViewById(R.id.price);
            }
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            indicator.setIndeterminate(true);
            indicator.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");

                }

                return buffer.toString();


            } catch (IOException e) {

                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new Handler().postDelayed(() -> {
                if (indicator.getVisibility() == View.VISIBLE) {
                    indicator.setIndeterminate(false);
                    indicator.setVisibility(View.INVISIBLE);
                }
            }, 1000);

            if (result != null) {
                XmlToJson xmlToJson = new XmlToJson.Builder(result).build();
                JSONObject jsonObject = xmlToJson.toJson();
                JSONArray jsonArray;
                try {
                    jsonArray = jsonObject.getJSONObject("feed").getJSONArray("entry");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        fullList.add(new Pojo(jsonArray.getJSONObject(i).getJSONObject("summary").getString("content"),
                                jsonArray.getJSONObject(i).getString("title")));
                    }
                    if (fullList.size() > 0) {
                        fullList.get(0).setTitle(jsonObject.getJSONObject("feed").getString("title"));
                        layoutWel.setText(fullList.get(0).title);
                        for (int i = 0; i < fullList.size() / 2; i++) {
                            arrayList.add(fullList.get(i));
                            adapter.notifyItemInserted(i);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(indicator, "Error", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        }
    }

    class Pojo {
        String title;
        String regionTitle;
        String summary;

        public Pojo(String regionTitle, String summary) {
            this.regionTitle = regionTitle;
            this.summary = summary;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }*/
    }
}