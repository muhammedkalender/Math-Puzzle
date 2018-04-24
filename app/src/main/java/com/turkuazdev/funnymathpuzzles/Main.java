package com.turkuazdev.funnymathpuzzles;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Random;

public class Main extends AppCompatActivity {
    SoundPool soundPool;
    Random rand;
    View.OnClickListener listenerAnswer;

    RelativeLayout rlQuestionFather;

    boolean showedRewardedAds = false;

    int lastCount = 5;

    int[] lastId = {-1, -1, -1, -1, -1};
    int[] lastType = {-1, -1, -1, -1, -1};
    int lastIndex = 0;


    boolean questionLoaded = false;

    TextView[] answerViews = {null, null, null, null};
    int lastQuestionType = 0;
    int lastQuestionId = 0;
    int DIFFICULTY_EASY = 0;
    int correctIndex;
    int DIFFICULTY_NORMAL = 25;
    int DIFFICULTY_HARD = 45;
    int questionCount = 1;
    TextView tvQuestionCount;
    boolean resume = false;
    RelativeLayout rlQuestionArea;
    AdView adView;
    InterstitialAd mInterstitialAd;
    RewardedVideoAd rewardedVideoAd;
    int diffuculty = 0;
    //https://freesound.org/people/Bertrof/sounds/131662/
    int SOUND_ANSWER_CORRECT;
    //https://freesound.org/people/Bertrof/sounds/131657/
    int SOUND_ANSWER_WRONG;
    //https://freesound.org/people/dster777/
    int SOUND_CLICK;
    int indexQueu = 0;
    boolean gotoMainMenu = false;

    TextView tvShowAnswer;

    int[] explode(String TEXT, String REGEX) {
        try {
            String[] string = TEXT.split(REGEX);
            int[] result = new int[string.length];

            for (int i = 0; i < string.length; i++) {
                result[i] = Integer.valueOf(string[i]);
            }

            return result;
        } catch (Exception ex) {
            lib.err(369, ex);

            return new int[0];
        }
    }

    String implode(int[] ARRAY, String REGEX) {
        try {
            String result = "";

            for (int i = 0; i < ARRAY.length - 1; i++) {
                result += ARRAY[i] + REGEX;
            }

            result += ARRAY[ARRAY.length - 1];

            return result;
        } catch (Exception ex) {
            lib.err(369, ex);
            return "";
        }
    }

    void showAds() {
        try {
            if (mInterstitialAd == null) {
                mInterstitialAd = new InterstitialAd(getApplicationContext());
                mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
                mInterstitialAd.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                    }
                });
            }

            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        } catch (Exception ex) {
            lib.err(786, ex);
        }
    }

    void showAds(final View VIEW) {
        try {
            if (rewardedVideoAd == null) {

                rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
                rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoAdLoaded() {

                        try {
                            if (rewardedVideoAd.isLoaded()) {
                                tvShowAnswer.setClickable(true);
                                tvShowAnswer.setAlpha(1);
                            }

                        } catch (Exception ex) {
                            lib.err(142, ex);
                        }
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        showedRewardedAds = false;
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        try {
                            if (!showedRewardedAds) {
                                Toast.makeText(Main.this, getString(R.string.must_wath_ad), Toast.LENGTH_SHORT).show();
                                rewardedVideoAd.loadAd(getString(R.string.rewarded_video_ad_unity_id), new AdRequest.Builder().build());
                            } else {
                                answerViews[correctIndex].callOnClick();
                                rewardedVideoAd.loadAd(getString(R.string.rewarded_video_ad_unity_id), new AdRequest.Builder().build());
                            }
                        } catch (Exception ex) {
                            lib.err(439, ex);
                        }
                    }

                    @Override
                    public void onRewarded(RewardItem rewardItem) {
                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {

                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int i) {
                        try {
                            tvShowAnswer.setClickable(false);
                            tvShowAnswer.setAlpha(0.7f);
                            rewardedVideoAd.loadAd(getString(R.string.rewarded_video_ad_unity_id), new AdRequest.Builder().build());
                        } catch (Exception ex) {

                        }
                    }

                    @Override
                    public void onRewardedVideoCompleted() {
                        try {
                            showedRewardedAds = true;
                        } catch (Exception ex) {
                            lib.err(745, ex);
                        }
                    }
                });
            }

            rewardedVideoAd.loadAd(getString(R.string.rewarded_video_ad_unity_id), new AdRequest.Builder().build());
        } catch (Exception ex) {
            lib.err(368, ex);
        }
    }

    //https://stackoverflow.com/questions/19272646/game-sound-effects-in-android
    void playSound(int ID) {
        try {
            if (lib.settings("SOUND_EFFECT", 1) < 1) {
                return;
            }

            if (soundPool == null) {
                SOUND_ANSWER_CORRECT = R.raw.answer_correct;
                SOUND_ANSWER_WRONG = R.raw.answer_wrong;
                SOUND_CLICK = R.raw.button;


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();

                    soundPool = new SoundPool.Builder()
                            .setAudioAttributes(attributes)
                            .build();
                } else {
                    soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
                }

                SOUND_ANSWER_CORRECT = soundPool.load(getApplicationContext(), R.raw.answer_correct, 1);
                SOUND_ANSWER_WRONG = soundPool.load(getApplicationContext(), R.raw.answer_wrong, 1);
                SOUND_CLICK = soundPool.load(getApplicationContext(), R.raw.button, 1);
            }

            soundPool.play(ID, 1, 1, 1, 0, 1.0f);
        } catch (Exception ex) {
            lib.err(950, ex);
        }
    }

    boolean beforeCheck(int QUESTION_CODE, int QUESTION_TYPE) {
        try {
            return false;

            /*
            for (int i = 0; i < 4; i++) {
                if (lastId[i] == QUESTION_CODE && lastType[i] == QUESTION_TYPE) {
                    return true;
                }
            }

            return false;*/
        } catch (Exception ex) {
            lib.err(965, ex);
            return true;
        }
    }

    void addBefore(int QUESTION_CODE, int QUESTION_TYPE) {
        try {
            lastId[lastIndex % lastCount] = QUESTION_CODE;
            lastType[lastIndex % lastCount] = QUESTION_TYPE;
            lastIndex++;
        } catch (Exception ex) {
            lib.err(486, ex);
        }
    }

    boolean loadQuestion() {
        try {
            int questionType = indexQueu % 4; //rand.nextInt(5);

            rlQuestionArea.removeAllViews();

            //questionType = follow[questionCount % follow.length][0];

            View view;

            if (resume) {
                questionType = lastQuestionId;
            } else {
                lastQuestionId = questionType;
            }
            switch (questionType) {
                case 0:
                    if ((view = completeQuestion()) == null) {
                        loadQuestion();
                        return false;
                    } else {
                        rlQuestionArea.addView(view);
                    }
                    break;
                case 1:
                    if ((view = imageQuestion()) == null) {
                        loadQuestion();
                        return false;
                    } else {
                        rlQuestionArea.addView(view);
                    }
                    break;
                case 2:
                    if ((view = linearQuestion()) == null) {
                        loadQuestion();
                        return false;
                    } else {
                        rlQuestionArea.addView(view);
                    }
                    break;

                case 3:
                    if ((view = triangleQuestion()) == null) {
                        loadQuestion();
                        return false;
                    } else {
                        rlQuestionArea.addView(view);
                    }
                    break;
                case 4:
                    if ((view = sudokuQuestion()) == null) {
                        loadQuestion();
                        return false;
                    } else {
                        rlQuestionArea.addView(view);
                    }
                    break;
            }

            tvQuestionCount.setText(getResources().getString(R.string.question_count, questionCount));

            lib.setSettings("LAST_QUESTION", questionCount);
            lib.setSettings("LAST_QUESTION_TYPE", lastQuestionType);
            lib.setSettings("LAST_QUESTION_ID", lastQuestionId);
            indexQueu++;
            questionLoaded = true;
            return true;
        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.errorReset), Toast.LENGTH_SHORT).show();
            lib.err(100, ex);
            return false;
        }
    }

    boolean buildAnswers(int[] ANSWERS, int CORRECT_INDEX) {
        try {
            if (answerViews[0] == null) {
                answerViews[0] = findViewById(R.id.tvAnswerA);
                answerViews[0].setOnClickListener(listenerAnswer);

                answerViews[1] = findViewById(R.id.tvAnswerB);
                answerViews[1].setOnClickListener(listenerAnswer);

                answerViews[2] = findViewById(R.id.tvAnswerC);
                answerViews[2].setOnClickListener(listenerAnswer);

                answerViews[3] = findViewById(R.id.tvAnswerD);
                answerViews[3].setOnClickListener(listenerAnswer);
            }

            answerViews[0].setText("A) " + ANSWERS[0]);
            answerViews[0].setTag("0," + CORRECT_INDEX);
            answerViews[0].setBackground(getResources().getDrawable(R.drawable.button_blue));

            answerViews[1].setText("B) " + ANSWERS[1]);
            answerViews[1].setTag("1," + CORRECT_INDEX);
            answerViews[1].setBackground(getResources().getDrawable(R.drawable.button_blue));

            answerViews[2].setText("C) " + ANSWERS[2]);
            answerViews[2].setTag("2," + CORRECT_INDEX);
            answerViews[2].setBackground(getResources().getDrawable(R.drawable.button_blue));

            answerViews[3].setText("D) " + ANSWERS[3]);
            answerViews[3].setTag("3," + CORRECT_INDEX);
            answerViews[3].setBackground(getResources().getDrawable(R.drawable.button_blue));

            return true;
        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.errorReset), Toast.LENGTH_SHORT).show();
            lib.err(101, ex);
            return false;
        }

    }

    //   Bir Sayı türetiriz, bu tamamlalma çeşidini gösterir [ 0 = +, 1 = Üs, 2 = Bir ekleyip, bir çıkartma
    View completeQuestion() {
        try {
            final View questionView = View.inflate(getApplicationContext(), R.layout.complete_question, null);

            String question = "";

            int startPoint;
            int answer = 0;
            int range;
            int[] answers = {-11111, -11111, -11111, -11111};
            int answerRange;
            int correctAnswer;
            int sumPoint;

            int questionType = rand.nextInt(4);

            if (resume) {
                questionType = lastQuestionType;
            } else {
                while (questionCount < DIFFICULTY_NORMAL && questionType == 3) {
                    questionType = rand.nextInt(4);
                }

                if (beforeCheck(0, questionType)) {
                    return null;
                }

                lastQuestionType = questionType;
            }

            addBefore(0, questionType);

            //questionType = follow[questionCount % follow.length][1];

            switch (questionType) {
                case 0:
                    startPoint = rand.nextInt(19);

                    int addPoint = rand.nextInt(15) + 1;

                    for (int i = 0; i < 6; i++) {
                        question += startPoint + ", ";
                        startPoint += addPoint;
                    }

                    answer = startPoint;

                    break;
                case 1:
                    startPoint = rand.nextInt(4) + 1;

                    for (int i = startPoint; i < startPoint + 6; i++) {
                        question += ((startPoint + i) * (startPoint + i)) + ", ";
                    }

                    answer = ((startPoint + startPoint + 6) * (startPoint + startPoint + 6));
                    break;

                case 2:
                    sumPoint = rand.nextInt(19) + 1;
                    int minPoint = rand.nextInt(19) + 1;

                    startPoint = rand.nextInt(19) + 1;

                    boolean firstDo = rand.nextBoolean();

                    for (int i = 0; i < 8; i++) {
                        if (i % 2 == 0) {
                            if (firstDo) {
                                startPoint += sumPoint;
                            } else {
                                startPoint -= minPoint;
                            }
                        } else {
                            if (!firstDo) {
                                startPoint -= minPoint;
                            } else {
                                startPoint += sumPoint;
                            }
                        }

                        if (i != 7) {
                            question += startPoint + ", ";
                        }
                    }

                    answer = startPoint;
                    break;
                case 3:
                    startPoint = rand.nextInt(20) + 2;
                    sumPoint = rand.nextInt(9) + 1;

                    answer = startPoint;

                    int multiple = rand.nextInt(3) + 2;

                    for (int i = 0; i < 7; i++) {
                        question += answer + ", ";

                        answer += startPoint * multiple + sumPoint;
                    }
                    break;
            }

            question += " ?";
            int index = 0;

            answerRange = (int) (answer * 0.25) + 4;

            if (answer < 0) {
                answerRange = (int) ((answer * -1) * 0.25) + 4;
            }

            while (index < 4) {
                int tempAnswer;
                boolean result = true;

                if (rand.nextBoolean()) {
                    tempAnswer = answer + (rand.nextInt(answerRange));
                } else {
                    tempAnswer = answer - (rand.nextInt(answerRange));
                }

                for (int i = 0; i < 4; i++) {
                    if (answers[i] == tempAnswer || answer == tempAnswer) {
                        result = false;
                    }
                }

                if (result) {
                    answers[index] = tempAnswer;
                    index++;
                }
            }

            correctAnswer = answer;
            correctIndex = rand.nextInt(4);

            answers[correctIndex] = correctAnswer;

            if (resume) {
                question = lib.settings("AREA_1", question);
                answers = explode(lib.settings("ANSWERS", ""), "__");
                correctIndex = lib.settings("CORRECT_INDEX", 0);
                answers[correctIndex] = lib.settings("CORRECT_ANSWER", 0);
            } else {
                lib.setSettings("AREA_1", question);
                lib.setSettings("ANSWERS", implode(answers, "__"));
                lib.setSettings("CORRECT_INDEX", correctIndex);
                lib.setSettings("CORRECT_ANSWER", answers[correctIndex]);
            }


            ((TextView) questionView.findViewById(R.id.tvComplete)).setText(question);

            buildAnswers(answers, correctIndex);

            return questionView;

        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.errorReset), Toast.LENGTH_SHORT).show();
            lib.err(102, ex);
            return null;
        }
    }

    View linearQuestion() {
        try {
            final View questionView = View.inflate(getApplicationContext(), R.layout.linear_question, null);

            int[] points = new int[12];
            int[] answers = {-128, -128, -128, -128};

            int answer = 0;
            int answerRange;
            int correctAnswer;
            int firstCharAt;
            int secondCharAt;
            int questionType = rand.nextInt(8);
            if (resume) {
                questionType = lastQuestionType;
            } else {
                while ((questionCount < DIFFICULTY_NORMAL && (questionType >= 1)) || (questionCount < DIFFICULTY_HARD && questionType >= 4)) {
                    questionType = rand.nextInt(8);
                }

                if (beforeCheck(2, questionType)) {
                    return null;
                }


                lastQuestionType = questionType;
            }

            addBefore(2, questionType);
//            questionType = follow[questionCount % follow.length][1];


            int startPoint;

        /*
            0 = Bütün Sayıların Toplamı
            1 = x Karakterliern Toplamı
            2 = x Karaterlerin Çarpımı ( Leng 2, 3 )
            3 = x Karakterlerin Yanayan Yazımı
            4 = x Karakterlerini Çarğımımının Yanyana yazımı
            5 = x Karakterlerinin toplamının yanayan yazımı
            6 = Bütün sayıların tersten yanayan yazılımı 29 98 8992
            7 = Sayıların ayrı ayrı otplaı

         */

            switch (questionType) {
                case 0:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(89) + 10;
                    }

                    for (int i = 2; i < 12; i += 3) {
                        points[i] = lib.sumChars(points[i - 1]) + lib.sumChars(points[i - 2]);
                    }
                    break;
                case 1:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(8999) + 1000;
                    }

                    firstCharAt = rand.nextInt(4);
                    secondCharAt = rand.nextInt(4);

                    while (firstCharAt == secondCharAt) {
                        secondCharAt = rand.nextInt(4);
                    }

                    for (int i = 2; i < 12; i += 3) {
                        int x = lib.charAt(points[i - 1], firstCharAt) + lib.charAt(points[i - 1], secondCharAt);
                        int y = lib.charAt(points[i - 2], firstCharAt) + lib.charAt(points[i - 2], secondCharAt);

                        points[i] = x + y;
                    }
                    break;
                case 2:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(899) + 100;
                    }

                    int charAt = rand.nextInt(3);

                    for (int i = 2; i < 12; i += 3) {
                        points[i] = lib.charAt(points[i - 1], charAt) + lib.charAt(points[i - 2], charAt);
                    }
                    break;
                case 3:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(8999) + 1000;
                    }

                    firstCharAt = rand.nextInt(4);
                    secondCharAt = rand.nextInt(4);

                    for (int i = 2; i < 12; i += 3) {
                        String first = lib.charAt(points[i - 1], firstCharAt) + "" + lib.charAt(points[i - 2], firstCharAt);
                        String second = lib.charAt(points[i - 1], secondCharAt) + "" + lib.charAt(points[i - 2], secondCharAt);

                        points[i] = Integer.parseInt(first + second);
                    }
                    break;
                case 4:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(8999) + 1000;
                    }

                    firstCharAt = rand.nextInt(4);
                    secondCharAt = rand.nextInt(4);

                    for (int i = 2; i < 12; i += 3) {
                        int first = lib.charAt(points[i - 1], firstCharAt) * lib.charAt(points[i - 2], firstCharAt);
                        int second = lib.charAt(points[i - 1], secondCharAt) * lib.charAt(points[i - 2], secondCharAt);

                        points[i] = Integer.parseInt(String.valueOf(first) + String.valueOf(second));
                    }
                    break;
                case 5:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(8999) + 1000;
                    }

                    firstCharAt = rand.nextInt(4);
                    secondCharAt = rand.nextInt(4);

                    for (int i = 2; i < 12; i += 3) {
                        int first = lib.charAt(points[i - 1], firstCharAt) + lib.charAt(points[i - 2], firstCharAt);
                        int second = lib.charAt(points[i - 1], secondCharAt) + lib.charAt(points[i - 2], secondCharAt);

                        points[i] = Integer.parseInt(String.valueOf(first) + String.valueOf(second));
                    }
                    break;
                case 6:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(89) + 10;
                    }

                    for (int i = 2; i < 12; i += 3) {
                        String temp = "";

                        for (int j = 2; j >= 0; j--) {
                            temp += lib.charAt(points[i - 1], j);
                        }

                        for (int j = 2; j >= 0; j--) {
                            temp += lib.charAt(points[i - 2], j);
                        }
                    }
                    break;
                case 7:
                    for (int i = 0; i < 12; i++) {
                        points[i] = rand.nextInt(89) + 10;
                    }

                    for (int i = 2; i < 12; i++) {
                        points[i] = (points[i - 1] + points[i - 2]) * 2;
                    }
                    break;
            }

            correctIndex = rand.nextInt(4);
            correctAnswer = points[11];
            answer = points[11];

            int index = 0;

            answerRange = (int) (answer * 0.25) + 4;

            while (index < 4) {
                int tempAnswer;
                int answerMove = 1;
                boolean result = true;

                if (!rand.nextBoolean()) {
                    answerMove = -1;
                }

                tempAnswer = answer + (rand.nextInt(answerRange) * answerMove);

                for (int i = 0; i < 4; i++) {
                    if (answers[i] == tempAnswer || answer == tempAnswer) {
                        result = false;
                    }
                }

                if (result) {
                    answers[index] = tempAnswer;
                    index++;
                }
            }

            answers[correctIndex] = correctAnswer;

            if (resume) {
                points = explode(lib.settings("POINTS", ""), "__");
                answers = explode(lib.settings("ANSWERS", ""), "__");
                correctIndex = lib.settings("CORRECT_INDEX", 0);
                answers[correctIndex] = lib.settings("CORRECT_ANSWER", 0);
            } else {
                lib.setSettings("POINTS", implode(points, "__"));
                lib.setSettings("ANSWERS", implode(answers, "__"));
                lib.setSettings("CORRECT_INDEX", correctIndex);
                lib.setSettings("CORRECT_ANSWER", answers[correctIndex]);
            }

            ((TextView) questionView.findViewById(R.id.tvArea1)).setText(points[0] + " & " + points[1]);
            ((TextView) questionView.findViewById(R.id.tvArea2)).setText(points[2] + "");
            ((TextView) questionView.findViewById(R.id.tvSign1)).setText(" = ");

            ((TextView) questionView.findViewById(R.id.tvArea3)).setText(points[3] + " & " + points[4]);
            ((TextView) questionView.findViewById(R.id.tvArea4)).setText(points[5] + "");
            ((TextView) questionView.findViewById(R.id.tvSign2)).setText(" = ");

            ((TextView) questionView.findViewById(R.id.tvArea5)).setText(points[6] + " & " + points[7]);
            ((TextView) questionView.findViewById(R.id.tvArea6)).setText(points[8] + "");
            ((TextView) questionView.findViewById(R.id.tvSign3)).setText(" = ");

            ((TextView) questionView.findViewById(R.id.tvArea7)).setText(points[9] + " & " + points[10]);
            ((TextView) questionView.findViewById(R.id.tvArea8)).setText("?");
            ((TextView) questionView.findViewById(R.id.tvSign4)).setText(" = ");


            buildAnswers(answers, correctIndex);


            return questionView;
        } catch (Exception ex) {
            lib.err(104, ex);
            Toast.makeText(this, getResources().getString(R.string.errorReset), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    View imageQuestion() {
        try {
            int resId[] = {
                    R.mipmap.image1,
                    R.mipmap.image2,
                    R.mipmap.image3,
                    R.mipmap.image4,
                    R.mipmap.image5,
                    R.mipmap.image6,
                    R.mipmap.image7,
                    R.mipmap.image8,
                    R.mipmap.image9,
                    R.mipmap.image10,
                    R.mipmap.image11,
                    R.mipmap.image12
            };

            int resPoint[] = {
                    7,
                    9,
                    5,
                    12,
                    18,
                    3,
                    4,
                    6,
                    4,
                    7,
                    9,
                    2
            };
            int alternativeResId[] = {
                    R.mipmap.alternative_image1,
                    R.mipmap.alternative_image2,
                    R.mipmap.alternative_image3,
                    R.mipmap.alternative_image4,
                    R.mipmap.alternative_image5,
                    R.mipmap.alternative_image6,
                    R.mipmap.alternative_image7,
                    R.mipmap.alternative_image8,
                    R.mipmap.alternative_image9,
                    R.mipmap.alternative_image10,
                    R.mipmap.alternative_image11,
                    R.mipmap.alternative_image12
            };

            int alternativePoint[] = {
                    14,
                    0,
                    10,
                    6,
                    9,
                    6,
                    8,
                    12,
                    12,
                    14,
                    18,
                    1
            };

            final int correctAnswer;
            final int answerRange;


            final View questionView = View.inflate(getApplicationContext(), R.layout.image_question, null);

            int index1 = rand.nextInt(resId.length);

            int index2 = rand.nextInt(resId.length);

            int index3 = rand.nextInt(resId.length);

            while (index1 == index2) {
                index2 = rand.nextInt(resId.length);
            }

            while (index2 == index3 || index1 == index3) {
                index3 = rand.nextInt(resId.length);
            }

            int answer = alternativePoint[index3];

            int answers[] = {-128, -128, -128, -128};

            int questionType = rand.nextInt(1);


            if (resume) {
                questionType = lastQuestionType;
            } else {
                if (beforeCheck(1, questionType)) {
                    return null;
                }

                lastQuestionType = questionType;
            }

            addBefore(1, questionType);

            correctIndex = rand.nextInt(4);
            correctAnswer = answer;

            int index = 0;

            answerRange = (int) (answer * 0.25) + 4;

            while (index < 4) {
                int tempAnswer;
                int answerMove = 1;
                boolean result = true;

                if (!rand.nextBoolean()) {
                    answerMove = -1;
                }

                tempAnswer = answer + (rand.nextInt(answerRange) * answerMove);

                for (int i = 0; i < 4; i++) {
                    if (answers[i] == tempAnswer || answer == tempAnswer) {
                        result = false;
                    }
                }

                if (result) {
                    answers[index] = tempAnswer;
                    index++;
                }
            }

            answers[correctIndex] = correctAnswer;

            if (resume) {
                index1 = lib.settings("INDEX1", 0);
                index3 = lib.settings("INDEX3", 1);
                answers = explode(lib.settings("ANSWERS", ""), "__");
                correctIndex = lib.settings("CORRECT_INDEX", 0);
                answers[correctIndex] = lib.settings("CORRECT_ANSWER", 0);
            } else {
                lib.setSettings("INDEX1", index1);
                lib.setSettings("INDEX3", index3);
                lib.setSettings("ANSWERS", implode(answers, "__"));
                lib.setSettings("CORRECT_INDEX", correctIndex);
                lib.setSettings("CORRECT_ANSWER", answers[correctIndex]);
            }

            //questionType = follow[questionCount % follow.length][1];

            Glide.with(getApplicationContext()).load(resId[index1]).into(((ImageView) questionView.findViewById(R.id.iArea1)));

            ((TextView) questionView.findViewById(R.id.signArea1)).setText("+");

            Glide.with(getApplicationContext()).load(resId[index1]).into(((ImageView) questionView.findViewById(R.id.iArea2)));

            ((TextView) questionView.findViewById(R.id.signArea2)).setText("+");

            Glide.with(getApplicationContext()).load(resId[index1]).into(((ImageView) questionView.findViewById(R.id.iArea3)));

            ((TextView) questionView.findViewById(R.id.signArea3)).setText("" + ((resPoint[index1] * 3)));

            Glide.with(getApplicationContext()).load(resId[index1]).into(((ImageView) questionView.findViewById(R.id.iArea4)));

            ((TextView) questionView.findViewById(R.id.signArea4)).setText("+");

            Glide.with(getApplicationContext()).load(resId[index3]).into(((ImageView) questionView.findViewById(R.id.iArea5)));

            ((TextView) questionView.findViewById(R.id.signArea5)).setText("+");

            Glide.with(getApplicationContext()).load(resId[index3]).into((ImageView) questionView.findViewById(R.id.iArea6));


            ((TextView) questionView.findViewById(R.id.signArea6)).setText("" + ((resPoint[index1] + (2 * resPoint[index3]))));

            Glide.with(getApplicationContext()).load(alternativeResId[index3]).into(((ImageView) questionView.findViewById(R.id.iArea7)));

            ((TextView) questionView.findViewById(R.id.signArea7)).setText("?");

            answer = alternativePoint[index3];


            buildAnswers(answers, correctIndex);


            return questionView;
        } catch (Exception ex) {
            lib.err(105, ex);
            Toast.makeText(this, getResources().getString(R.string.errorReset), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    View sudokuQuestion() {
        try {
            final View questionView = View.inflate(getApplicationContext(), R.layout.sudoku_question, null);
            int[] points = new int[12];
            int correctAnswer = 0;
            int answerRange;
            int[] answers = new int[4];
            int answer = -1;
            int temp;
            int sum;
            int index = 0;
            int multipler;
            boolean check = true;

            /*
                0 = Sıralama ...
                1 = Belli bir toplamaay göre sıralama
                2 = Belli bir sayının katına göre sıralama
                Ayrı bir randomla karıştır
                3 = Karışık sıralam
                4 = Belli bir toplamaya göre sıralama karışık
                5 = Belli bir sayının katına göre sıralam karışık
             */

            int questionType = rand.nextInt(6);

            if (resume) {
                questionType = lastQuestionType;
            } else {
                if (beforeCheck(4, questionType)) {
                    return null;
                }


                lastQuestionType = questionType;
            }

            addBefore(4, questionType);

            boolean special = false;

            if (questionType > 3) {
                special = true;
            }

            questionType = questionType % 3;


            switch (questionType) {
                case 0:
                    temp = rand.nextInt(989) + 1;

                    for (int i = 0; i < 9; i++) {
                        points[i] = temp + (i * 1);
                    }
                    break;
                case 1:
                    temp = rand.nextInt(889) + 1;
                    sum = rand.nextInt(16) + 2;

                    for (int i = 0; i < 9; i++) {
                        points[i] = temp + (i * sum);
                    }
                    break;
                case 2:
                    temp = rand.nextInt(25) + 1;
                    multipler = rand.nextInt(16) + 2;

                    points[0] = temp * multipler;

                    for (int i = 1; i < 9; i++) {
                        points[i] = points[i - 1] + multipler;
                    }
                    break;
            }

            if (special && questionCount > DIFFICULTY_NORMAL) {
                for (int i = 0; i < 9; i++) {
                    index = rand.nextInt(9);
                    temp = points[i];
                    points[i] = points[index];
                    points[index] = temp;
                }
            }

            int indexQuestion = rand.nextInt(9);
            correctAnswer = points[indexQuestion];
            GridLayout grid = questionView.findViewById(R.id.viewGrid);
            answer = correctAnswer;
            answerRange = (int) (answer * 0.25) + 4;

            index = 0;

            while (index < 4) {
                int tempAnswer;
                int answerMove = 1;
                boolean result = true;

                if (!rand.nextBoolean()) {
                    answerMove = -1;
                }

                tempAnswer = answer + (rand.nextInt(answerRange) * answerMove);

                for (int i = 0; i < 4; i++) {
                    if (answers[i] == tempAnswer || answer == tempAnswer) {
                        result = false;
                    }
                }

                if (result) {
                    answers[index] = tempAnswer;
                    index++;
                }
            }


            answers[correctIndex] = correctAnswer;


            if (resume) {
                indexQuestion = lib.settings("INDEX", 1);
                points = explode(lib.settings("POINTS", ""), "__");
                answers = explode(lib.settings("ANSWERS", ""), "__");
                correctIndex = lib.settings("CORRECT_INDEX", 0);
                answers[correctIndex] = lib.settings("CORRECT_ANSWER", 0);
            } else {
                lib.setSettings("INDEX", indexQuestion);
                lib.setSettings("POINTS", implode(points, "__"));
                lib.setSettings("ANSWERS", implode(answers, "__"));
                lib.setSettings("CORRECT_INDEX", correctIndex);
                lib.setSettings("CORRECT_ANSWER", answers[correctIndex]);
            }

            for (int i = 0; i < 9; i++) {
                if (indexQuestion == i) {
                    ((TextView) grid.getChildAt(i)).setText("?");
                } else {
                    ((TextView) grid.getChildAt(i)).setText(points[i] + "");
                }

            }


            buildAnswers(answers, correctIndex);


            return questionView;
        } catch (Exception ex) {
            lib.err(412, ex);
            return null;
        }
    }

    View triangleQuestion() {
        try {
            final View questionView = View.inflate(getApplicationContext(), R.layout.triangle_question, null);
            int[] points = new int[12];
            int correctAnswer;
            int answerRange;
            int[] answers = new int[4];
            int answer = -1;
            int sum;
            int[] indexQueue = {-9999, -9999, -9999, -9999, -9999, -9999, -9999, -9999, -9999, -9999, -9999, -9999};

        /*
            0 = Toplama
            1 = Çarpım
            2 = Belli Artış

         */

            int index = 0;

            int questionType = rand.nextInt(2);

            if (resume) {
                questionType = lastQuestionType;
            } else {
                while ((questionCount < DIFFICULTY_NORMAL && (questionType >= 1)) || (questionCount < DIFFICULTY_HARD && questionType >= 4)) {
                    questionType = rand.nextInt(8);
                }

                if (beforeCheck(3, questionType)) {
                    return null;
                }


                lastQuestionType = questionType;
            }

            index = 0;
            while (index != 4) {
                int temp = rand.nextInt(4);

                boolean check = true;
                for (int i = 0; i < 4; i++) {
                    if (indexQueue[i] == temp) {
                        check = false;
                    }
                }

                if (check) {
                    indexQueue[index] = temp;
                    indexQueue[index + 4] = temp + 4;
                    indexQueue[index + 8] = temp + 8;
                    index++;
                }

            }

            switch (questionType) {
                case 0:
                    sum = rand.nextInt(15) + 1;

                    for (int i = 0; i < 12; i += 4) {
                        points[indexQueue[i]] = rand.nextInt(15) + 1;
                        points[indexQueue[i + 1]] += points[indexQueue[i]] + sum;
                        points[indexQueue[i + 2]] += points[indexQueue[i + 1]] + sum;
                        points[indexQueue[i + 3]] += points[indexQueue[i + 2]] + sum;
                    }

                    break;
                case 1:
                    for (int i = 0; i < 12; i++) {
                        points[indexQueue[i % 4]] = rand.nextInt(5) + 1;
                    }

                    for (int i = 0; i < 12; i += 4) {
                        points[indexQueue[i + 2]] = points[indexQueue[i + 1]] * points[indexQueue[i]];
                        points[indexQueue[i + 3]] = points[indexQueue[i + 2]] * points[indexQueue[i + 1]];
                    }
                    break;
            }

            correctIndex = rand.nextInt(4);
            correctAnswer = points[11];
            answer = points[11];

            index = 0;

            answerRange = (int) (answer * 0.25) + 4;

            while (index < 4) {
                int tempAnswer;
                int answerMove = 1;
                boolean result = true;

                if (!rand.nextBoolean()) {
                    answerMove = -1;
                }

                tempAnswer = answer + (rand.nextInt(answerRange) * answerMove);

                for (int i = 0; i < 4; i++) {
                    if (answers[i] == tempAnswer || answer == tempAnswer) {
                        result = false;
                    }
                }

                if (result) {
                    answers[index] = tempAnswer;
                    index++;
                }
            }

            answers[correctIndex] = correctAnswer;

            if (resume) {
                points = explode(lib.settings("POINTS", ""), "__");
                answers = explode(lib.settings("ANSWERS", ""), "__");
                correctIndex = lib.settings("CORRECT_INDEX", 0);
                answers[correctIndex] = lib.settings("CORRECT_ANSWER", 0);
            } else {
                lib.setSettings("POINTS", implode(points, "__"));
                lib.setSettings("ANSWERS", implode(answers, "__"));
                lib.setSettings("CORRECT_INDEX", correctIndex);
                lib.setSettings("CORRECT_ANSWER", answers[correctIndex]);
            }

            ((TextView) questionView.findViewById(R.id.tvArea1)).setText(points[0] + "");
            ((TextView) questionView.findViewById(R.id.tvArea2)).setText(points[1] + "");
            ((TextView) questionView.findViewById(R.id.tvArea3)).setText(points[2] + "");
            ((TextView) questionView.findViewById(R.id.tvArea4)).setText(points[3] + "");

            ((TextView) questionView.findViewById(R.id.tvArea5)).setText(points[4] + "");
            ((TextView) questionView.findViewById(R.id.tvArea6)).setText(points[5] + "");
            ((TextView) questionView.findViewById(R.id.tvArea7)).setText(points[6] + "");
            ((TextView) questionView.findViewById(R.id.tvArea8)).setText(points[7] + "");

            ((TextView) questionView.findViewById(R.id.tvArea9)).setText(points[8] + "");
            ((TextView) questionView.findViewById(R.id.tvArea10)).setText(points[9] + "");
            ((TextView) questionView.findViewById(R.id.tvArea11)).setText(points[10] + "");
            ((TextView) questionView.findViewById(R.id.tvArea12)).setText("?");


            buildAnswers(answers, correctIndex);


            return questionView;
        } catch (Exception ex) {
            lib.err(110, ex);
            Toast.makeText(this, getResources().getString(R.string.errorReset), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlQuestionArea = findViewById(R.id.viewQuestion);
        rlQuestionFather = findViewById(R.id.questionFather);

        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvShowAnswer = findViewById(R.id.bShowAnswer);
        tvShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (rewardedVideoAd.isLoaded()) {
                        rewardedVideoAd.show();
                    } else {
                        tvShowAnswer.setAlpha(0.7f);
                        tvShowAnswer.setClickable(false);
                    }
                } catch (Exception ex) {
                    lib.err(753, ex);
                }
            }
        });
        tvShowAnswer.setClickable(false);
        tvShowAnswer.setAlpha(0.7f);
        showAds(null);

        lib.context = getApplicationContext();

        TextView tvContinue = findViewById(R.id.bResume);
        TextView tvNewGame = findViewById(R.id.bNewGame);
        TextView tvSettings = findViewById(R.id.bSettings);

        if (lib.settings("SOUND_EFFECT", 1) < 1) {
            tvSettings.setText(getResources().getText(R.string.sound_unmute));
        } else {
            tvSettings.setText(getResources().getText(R.string.sound_mute));
        }


        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lib.settings("SOUND_EFFECT", 1) < 1) {
                    lib.setSettings("SOUND_EFFECT", 1);
                    ((TextView) v).setText(getResources().getText(R.string.sound_mute));
                    playSound(SOUND_CLICK);
                } else {
                    ((TextView) v).setText(getResources().getText(R.string.sound_unmute));
                    lib.setSettings("SOUND_EFFECT", 0);
                }
            }
        });

        findViewById(R.id.headerBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(SOUND_CLICK);
                onBackPressed();
            }
        });


        tvNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playSound(SOUND_CLICK);
                    questionCount = 1;

                    findViewById(R.id.viewMenu).setVisibility(View.INVISIBLE);
                    //tvQuestionCount.setVisibility(View.VISIBLE);
                    tvQuestionCount.setText(getResources().getString(R.string.app_name));
                    rlQuestionFather.setVisibility(View.VISIBLE);
                    loadQuestion();
                } catch (Exception ex) {
                    lib.err(148, ex);
                }
            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playSound(SOUND_CLICK);

                    if (lib.settings("LAST_QUESTION", -1) < 1) {
                        return;
                    }

                    questionCount = lib.settings("LAST_QUESTION", -1);
                    lastQuestionId = lib.settings("LAST_QUESTION_ID", -1);
                    lastQuestionType = lib.settings("LAST_QUESTION_TYPE", -1);

                    findViewById(R.id.viewMenu).setVisibility(View.INVISIBLE);
                    //   tvQuestionCount.setVisibility(View.VISIBLE);
                    rlQuestionFather.setVisibility(View.VISIBLE);
                    resume = true;
                    loadQuestion();
                    resume = false;
                } catch (Exception ex) {
                    lib.err(369, ex);
                }
            }
        });


        findViewById(R.id.bCredits).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String credits = getString(R.string.credits1);
                    credits += getString(R.string.credits2);
                    credits += getString(R.string.credits3);
                    credits += getString(R.string.credits4);
                    credits += getString(R.string.credits5);
                    credits += getString(R.string.credits6);

                    ((TextView) findViewById(R.id.tvCredits)).setText(credits);

                    rlQuestionFather.setVisibility(View.INVISIBLE);
                    findViewById(R.id.viewMenu).setVisibility(View.INVISIBLE);
                    findViewById(R.id.viewSettings).setVisibility(View.INVISIBLE);
                    findViewById(R.id.viewCredits).setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    lib.err(586, ex);
                }
            }
        });

        answerViews = new TextView[4];

        listenerAnswer = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!questionLoaded) {
                        return;
                    }

                    questionLoaded = false;

                    String tag[] = v.getTag().toString().split(",");


                    answerViews[Integer.parseInt(tag[1])].setBackground(getResources().getDrawable(R.drawable.button_green));

                    if (tag[0].equals(tag[1])) {
                        questionCount++;
                        playSound(SOUND_ANSWER_CORRECT);
                    } else {
                        answerViews[Integer.parseInt(tag[0])].setBackground(getResources().getDrawable(R.drawable.button_red));
                        questionCount = 1;
                        playSound(SOUND_ANSWER_WRONG);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadQuestion();
                        }
                    }, 1000);
                } catch (Exception ex) {
                    lib.err(256, ex);
                }
            }
        };

        tvContinue = findViewById(R.id.bResume);

        if (lib.settings("LAST_QUESTION", -1) > 1) {
            tvContinue.setClickable(true);
            tvContinue.setAlpha(1);
            questionCount = lib.settings("LAST_QUESTION", -1);
            lastQuestionId = lib.settings("LAST_QUESTION_ID", -1);
            lastQuestionType = lib.settings("LAST_QUESTION_TYPE", -1);

        } else {
            tvContinue.setClickable(false);
            tvContinue.setAlpha(0.7f);
            lastQuestionId = -1;
            lastQuestionType = -1;
        }


        rand = new Random();

        try {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        } catch (Exception ex) {
            lib.err(475, ex);
        }

        showAds();

    }

    //https://stackoverflow.com/a/15021185
    void showExitDialog() {
        try {
            findViewById(R.id.popupView).setVisibility(View.VISIBLE);
            findViewById(R.id.mainView).setEnabled(false);
            findViewById(R.id.headerView).setEnabled(false);
            findViewById(R.id.viewQuestion).setEnabled(false);

            findViewById(R.id.popupButtonYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.popupView).setVisibility(View.INVISIBLE);
                    findViewById(R.id.mainView).setEnabled(true);
                    findViewById(R.id.viewQuestion).setEnabled(true);
                    findViewById(R.id.headerView).setEnabled(true);
                    playSound(SOUND_CLICK);
                    gotoMainMenu = true;
                    onBackPressed();
                }
            });


            findViewById(R.id.popupButtonNo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.popupView).setVisibility(View.INVISIBLE);
                    playSound(SOUND_CLICK);
                    gotoMainMenu = false;
                    findViewById(R.id.mainView).setEnabled(true);
                    findViewById(R.id.headerView).setEnabled(true);
                }
            });

        } catch (Exception ex) {
            lib.err(750, ex);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (rlQuestionFather.getVisibility() == View.VISIBLE) {


                if (!gotoMainMenu) {
                    showExitDialog();
                    return;
                }

                rlQuestionFather.setVisibility(View.INVISIBLE);
                //  tvQuestionCount.setVisibility(View.INVISIBLE);
                tvQuestionCount.setText(getResources().getString(R.string.app_name));

                findViewById(R.id.viewMenu).setVisibility(View.VISIBLE);
                TextView tvContinue = findViewById(R.id.bResume);

                if (lib.settings("LAST_QUESTION", -1) > 1) {
                    tvContinue.setClickable(true);
                    tvContinue.setAlpha(1);
                    questionCount = lib.settings("LAST_QUESTION", -1);
                    lastQuestionId = lib.settings("LAST_QUESTION_ID", -1);
                    lastQuestionType = lib.settings("LAST_QUESTION_TYPE", -1);

                } else {
                    tvContinue.setClickable(false);
                    tvContinue.setAlpha(0.7f);
                    lastQuestionId = -1;
                    lastQuestionType = -1;
                }

                gotoMainMenu = false;
            } else if (findViewById(R.id.viewSettings).getVisibility() == View.VISIBLE || findViewById(R.id.viewCredits).getVisibility() == View.VISIBLE) {
                findViewById(R.id.viewSettings).setVisibility(View.INVISIBLE);
                findViewById(R.id.viewCredits).setVisibility(View.INVISIBLE);

                findViewById(R.id.viewMenu).setVisibility(View.VISIBLE);

                TextView tvContinue = findViewById(R.id.bResume);

                if (lib.settings("LAST_QUESTION", -1) > 1) {
                    tvContinue.setClickable(true);
                    tvContinue.setAlpha(1);

                } else {
                    tvContinue.setClickable(false);
                    tvContinue.setAlpha(0.7f);
                }
            } else if (findViewById(R.id.viewMenu).getVisibility() == View.VISIBLE) {
                showExitDialog();

                if (!gotoMainMenu) {
                    return;
                }

                gotoMainMenu = false;
                finish();
            }
        } catch (Exception ex) {
            lib.err(478, ex);
        }
    }
}

