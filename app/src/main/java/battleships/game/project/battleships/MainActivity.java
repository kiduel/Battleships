package battleships.game.project.battleships;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import battleships.game.project.battleships.comunication.HttpHelper;
import battleships.game.project.battleships.game.GameLogicManager;
import battleships.game.project.battleships.game.GameMoveListener;
import battleships.game.project.battleships.game.HttpEventListener;
import battleships.game.project.battleships.game.ID;
import battleships.game.project.battleships.ui.Board;

public class MainActivity extends AppCompatActivity implements HttpEventListener, GameMoveListener {

    Board player_board,opponentBoard;
    GameLogicManager gameLogicManager;
    Button play_button;
    Button new_game_button;
    EditText magic_word_editText;
    TextView status_textView;

    HttpHelper helper;

    boolean registration_sent = false;
    boolean waitConditionSatisfied = false;
    String currentMagicWord="";
    boolean result_received = true;

    int unique_id;

    boolean player_turn= false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setClickListener();

        gameLogicManager = new GameLogicManager((Vibrator) getSystemService(VIBRATOR_SERVICE),
                player_board,opponentBoard, this);

        helper = new HttpHelper(getApplicationContext(),this);
    }

    @Override
    protected void onDestroy() {
        helper.setMagicWord(currentMagicWord,"!");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        helper.setMagicWord(currentMagicWord,"!");
        super.onStop();
    }
    @Override
    protected void onPause() {
        helper.setMagicWord(currentMagicWord,"!");
        super.onPause();
    }

    void registerKey(String key)
    {
        helper.getResponse(key);
        registration_sent = true;

    }
    void setClickListener()
    {
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.play_button:
                    {
                        // hide keyboard
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


                        if(gameLogicManager.areAllBattleshipsInRightPlace()) {
                            new_game_button.setText("close game");
                            currentMagicWord = magic_word_editText.getText().toString();
                            if (currentMagicWord.equals("")) {
                                status_textView.setText("You must enter a key");
                            } else {
                                gameLogicManager.copyPlayerBoardToOpponent();
                                registerKey(currentMagicWord);
                            }
                        }
                        else
                            status_textView.setText("Some battleships are not in the right position");
                        break;

                    }

                    case R.id.new_game_button:
                    {

                        if(player_turn)
                        {
                            helper.setMagicWord(currentMagicWord,"!");
                            setNewGame();
                        }
                        else
                            status_textView.setText("wait your turn");

                        if(gameLogicManager.game_state==gameLogicManager.GAME_BATTLESHIPS_POSITIONING)
                        {
                            helper.setMagicWord(currentMagicWord,"!");
                            setNewGame();
                        }


                        break;
                    }
                }
            }
        };
        new_game_button.setOnClickListener(l);
        play_button.setOnClickListener(l);
    }
    void setNewGame()
    {
        magic_word_editText.setText("");
        status_textView.setText(" please enter magic word  !");
        gameLogicManager.setGameState(GameLogicManager.GAME_BATTLESHIPS_POSITIONING);
        registration_sent = false;
        player_turn = false;
        waitConditionSatisfied = false;
        result_received = false;
    }
    void initializeViews()
    {
        player_board = (Board) findViewById(R.id.player_board);
        opponentBoard = (Board) findViewById(R.id.opponent_board);
        play_button = (Button) findViewById(R.id.play_button);
        new_game_button = (Button) findViewById(R.id.new_game_button);
        status_textView = (TextView) findViewById(R.id.status_textView);
        magic_word_editText = (EditText) findViewById(R.id.magic_word_editText);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float width = metrics.widthPixels;
        float height = metrics.heightPixels;
        float s = width/1.5f;
        resizeView(player_board, s, s);
        resizeView(opponentBoard,s, s);
        s = width/5;
        resizeView(new_game_button,s,s);
        resizeView(play_button,s,s);
    }
    void resizeView(View v, float width, float height)
    {
        v.getLayoutParams().height = (int)height;
        v.getLayoutParams().width = (int)width;
        v.requestLayout();
    }

    @Override
    public void onResponse(String result) {
        String[] r = result.split(helper.separator);
        int a = r.length;
        if(gameLogicManager.game_state == gameLogicManager.GAME_BATTLESHIPS_POSITIONING)
            if(registration_sent)
            {
                registration_sent = false;

                if(a > 1)
                {
                    if(Integer.parseInt(r[1])==19)
                    {
                        int[] dataa = new int[24];
                        if(a>23)
                            for(int i=0; i<21; i++)
                                dataa[i] = Integer.parseInt(r[i+2]);
                        helper.setMagicWord(currentMagicWord, helper.formatMessage(20,
                                gameLogicManager.getPlayerBattleships()));
                        gameLogicManager.setReceivedBattleships(dataa);
                        gameLogicManager.setGameState(GameLogicManager.GAME_RUNNING);
                        status_textView.setText(" game started ! \n opponent turn");
                        player_turn = false;
                        player_board.setCanTouch(false);
                        unique_id = -6;
                        startWaitingOpponentThread();
                    }
                    else
                    {
                        if(a==5||  a==4|| Integer.parseInt(r[1])==20)
                            status_textView.setText("There are already two players that are playing");
                    }
                }
                else
                {
                    helper.setMagicWord(currentMagicWord, helper.formatMessage(19,
                            gameLogicManager.getPlayerBattleships()));
                    status_textView.setText("waiting for player");
                    waitConditionSatisfied = false;
                    startWaitingOpponentThread();
                    unique_id = 6;
                }

            }
            else
            {
                if(a>1)
                {
                    if(Integer.parseInt(r[1])==20)
                    {
                        gameLogicManager.setGameState(GameLogicManager.GAME_RUNNING);
                        int[] dataa = new int[24];
                        if(a>23)
                            for(int i=0; i<21; i++)
                                dataa[i] = Integer.parseInt(r[i+2]);
                        gameLogicManager.setReceivedBattleships(dataa);
                        waitConditionSatisfied = true;
                        status_textView.setText(" game started : \n your turn ");
                        player_turn = true;
                        player_board.setCanTouch(true);
                    }
                }
            }

        String[] d = result.split("!");

        if(gameLogicManager.game_state == GameLogicManager.GAME_RUNNING)
        {
            if(d.length > 1)
            {

                gameLogicManager.setGameState(GameLogicManager.GAME_BATTLESHIPS_POSITIONING);
                status_textView.setText("your opponent left the game");
                player_board.drawBoardGrid();
                helper.setMagicWord(currentMagicWord,"//80//0//0//");
            }
            if(a>1)
            {
                if(Integer.parseInt(r[1])== -unique_id)
                {
                    ID id = new ID();
                    id.x = Integer.parseInt(r[2]);
                    id.y = Integer.parseInt(r[3]);
                    helper.setMagicWord(currentMagicWord,"//80//0//0//");
                    opponentBoard.dropGridToPlace(id);
                    waitConditionSatisfied = true;
                    player_turn = true;
                    player_board.setCanTouch(true);
                    status_textView.setText("your turn");
                    if(opponentBoard.getGrids_detected()==16)
                    {
                        opponentBoard.setGrids_detected(0);
                        player_board.setGrids_detected(0);
                        status_textView.setText("Your opponent  won !");
                        player_board.setCanTouch(false);
                    }
                }
            }

        }


        result_received = true;
    }

    void startWaitingOpponentThread()
    {
        final Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!waitConditionSatisfied)
                    if(result_received) {
                        helper.getResponse(currentMagicWord);
                        result_received = false;
                    }
                return;
            }

        });
        th.start();

    }

    @Override
    public void OnPlayerMove(int x, int y) {

        if(player_turn ) {
            player_turn = false;
            player_board.setCanTouch(false);
            status_textView.setText("opponent turn");
            waitConditionSatisfied = false;
            helper.setMagicWord(currentMagicWord,helper.formatMoveMessage(unique_id,x,y));
            startWaitingOpponentThread();
            if(player_board.getGrids_detected()==16)
            {
                opponentBoard.setGrids_detected(0);
                player_board.setGrids_detected(0);
                status_textView.setText("You  won !");
                player_board.setCanTouch(false);
            }
        }
    }
}
