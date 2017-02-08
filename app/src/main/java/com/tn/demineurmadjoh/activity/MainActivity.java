package com.tn.demineurmadjoh.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tn.demineurmadjoh.R;
import com.tn.demineurmadjoh.fragment.MainFragment;

//Activiter principale
public class MainActivity extends Activity
        implements MainFragment.OnFragmentInteractionListener {



    public static void setDifficulty(String difficulty) {
        MainActivity.difficulty = difficulty;
    }

    private static String difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, MainFragment.newInstance())
                .commit();
    }
    //Appler au menu de activiter principale
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Implémentation de la methode de lancement de nouvelle plan de jeux
    @Override
    public void onRequestToCreateNewGame() {
        //choisir selon difficulté
        switch (difficulty){

            //difficulté facile : 10 bombes
            case "easy":
                Intent intent = new Intent(this, GameActivity.class);
                Bundle extras = new Bundle();
                extras.putInt(GameActivity.EXTRA_DIMENSION, 10);
                extras.putInt(GameActivity.EXTRA_MINES, 10);
                extras.putBoolean(GameActivity.EXTRA_LOAD_GAME, false);
                intent.putExtras(extras);
                startActivityForResult(intent, GameActivity.REQUEST_CODE_NEW_GAME);
                break;

            //difficulté moyenne : 15 bombes
            case "normal":
                Intent intentNormal = new Intent(this, GameActivity.class);
                Bundle extrasNormal = new Bundle();
                extrasNormal.putInt(GameActivity.EXTRA_DIMENSION, 10);
                extrasNormal.putInt(GameActivity.EXTRA_MINES, 15);
                extrasNormal.putBoolean(GameActivity.EXTRA_LOAD_GAME, false);
                intentNormal.putExtras(extrasNormal);
                startActivityForResult(intentNormal, GameActivity.REQUEST_CODE_NEW_GAME);
                break;

            //difficulté difficile  : 20 bombes
            case "difficult":
                Intent intentDifficult = new Intent(this, GameActivity.class);
                Bundle extrasDifficult = new Bundle();
                extrasDifficult.putInt(GameActivity.EXTRA_DIMENSION, 10);
                extrasDifficult.putInt(GameActivity.EXTRA_MINES, 20);
                extrasDifficult.putBoolean(GameActivity.EXTRA_LOAD_GAME, false);
                intentDifficult.putExtras(extrasDifficult);
                startActivityForResult(intentDifficult, GameActivity.REQUEST_CODE_NEW_GAME);
                break;
        }

    }




}
