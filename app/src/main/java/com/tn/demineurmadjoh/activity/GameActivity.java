package com.tn.demineurmadjoh.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tn.demineurmadjoh.R;
import com.tn.demineurmadjoh.fragment.GameFragment;
import com.tn.demineurmadjoh.model.DmGame;
import com.tn.demineurmadjoh.model.DmGameState;
import com.tn.demineurmadjoh.worker.GameWorkerFragment;
//Activiter de plan de jeux
public class GameActivity extends Activity
        implements GameFragment.OnFragmentInteractionListener,
        GameWorkerFragment.OnWorkerFragmentCallbacks{

    public static final int REQUEST_CODE_NEW_GAME = 1000;
    public static final String EXTRA_DIMENSION = "extra.DIMENSION";
    public static final String EXTRA_MINES = "extra.MINES";
    public static final String EXTRA_LOAD_GAME = "extra.LOAD_GAME";
    protected GameWorkerFragment mWorkerFragment;
    protected GameFragment mGameFragment;
    protected AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
        Resources resources = getResources();
        int dimension = 0, mines = 0;
        boolean loadGame = false;
        if (mWorkerFragment == null) {
            Intent intent = getIntent();
            if (intent != null) {
                dimension = intent.getIntExtra(EXTRA_DIMENSION, resources.getInteger(R.integer.ms_default_dimension));
                mines = intent.getIntExtra(EXTRA_MINES, resources.getInteger(R.integer.ms_default_dimension));
                loadGame = intent.getBooleanExtra(EXTRA_LOAD_GAME, false);
            } else {
                dimension = resources.getInteger(R.integer.ms_default_dimension);
                mines = resources.getInteger(R.integer.ms_default_mines);
            }

            mWorkerFragment = GameWorkerFragment.newInstance(dimension, mines, loadGame);
            fm.beginTransaction().add(mWorkerFragment, GameWorkerFragment.FRAGMENT_TAG).commit();
        } else {
            dimension = mWorkerFragment.getDimension();
            mines = mWorkerFragment.getMines();
        }
        //Afficher button retour en action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            updateActionBarTitle(actionBar, dimension, mines);
        }
        //Affecter la fragment au content
        fm.beginTransaction()
                .replace(android.R.id.content, GameFragment.newInstance())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    //Méthode
    public void updateActionBarTitle(ActionBar actionBar, int dimension, int mines) {
        if (actionBar != null) {
            actionBar.setTitle(String.format(getString(R.string.game__activity_title), dimension, dimension, mines));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Injecter le menu dans l'activiter
        getMenuInflater().inflate(R.menu.menu_game, menu);
        boolean outcome = super.onCreateOptionsMenu(menu);
        FragmentManager fm = getFragmentManager();
        mWorkerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
        if (mWorkerFragment != null) {
            DmGame game = mWorkerFragment.getGame();
            if (game != null) {
            }
        }
        return outcome;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
        //Choisir selon l'item selectionée la methode à faire (validé ou réinitialiser)
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.game__action_validate:
                if (workerFragment != null) {
                    DmGame game = workerFragment.getGame();
                    if (game != null) {
                        if (game.getHasEnded()) {
                            onGameEnded(game);
                        } else if (game.getHasStarted()) {
                            mAlertDialog = new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.game__validate_confirmation_dialog___title))
                                    .setMessage(getString(R.string.game__validate_confirmation_dialog___prompt))
                                    .setPositiveButton(getString(R.string.game__validate_confirmation_dialog___button_positive), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FragmentManager fm = getFragmentManager();
                                            GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
                                            if (mWorkerFragment != null) {
                                                workerFragment.validateGameAsync();
                                            }
                                        }

                                    })
                                    .setNegativeButton(getString(R.string.dialog___button_cancel), null)
                                    .show();
                        } else {
                            Toast.makeText(this, getString(R.string.game__validate_disallowed_first_move_required), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                return true;
            case R.id.game__action_restart:
                if (workerFragment != null) {
                    DmGame game = workerFragment.getGame();
                    if (game != null) {
                        if (game.getHasStarted()) {
                            if (game.getHasEnded()) {
                                workerFragment.createNewGameAsync(game.getDimension(), game.getMines());
                            } else {
                                mAlertDialog = new AlertDialog.Builder(this)
                                        .setTitle(getString(R.string.game__restart_confirmation_dialog___title))
                                        .setMessage(getString(R.string.game__restart_confirmation_dialog___prompt))
                                        .setPositiveButton(getString(R.string.game__restart_confirmation_dialog___button_positive), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FragmentManager fm = getFragmentManager();
                                                GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
                                                if (mWorkerFragment != null) {
                                                    DmGame game = workerFragment.getGame();
                                                    if (game != null) {
                                                        workerFragment.createNewGameAsync(game.getDimension(), game.getMines());
                                                    }
                                                }
                                            }

                                        })
                                        .setNegativeButton(getString(R.string.dialog___button_cancel), null)
                                        .show();
                            }
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Getter et Setter de fragment
    public GameFragment getGameFragment() {
        if (mGameFragment == null) {
            FragmentManager fm = getFragmentManager();
            GameFragment gameFragment = (GameFragment) fm.findFragmentByTag(GameFragment.FRAGMENT_TAG);
            if (gameFragment != null) {
                mGameFragment = gameFragment;
            }
        }
        return mGameFragment;
    }

    public void setGameFragment(GameFragment gameFragment) {
        mGameFragment = gameFragment;
    }

    @Override
    public void onCreateNewGamePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onCreateNewGamePreExecute();
        }
    }

    @Override
    public void onCreateNewGameCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onCreateNewGameCancelled();
        }
    }

    @Override
    public void onCreateNewGamePostExecute(DmGameState result) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (result != null) {
                DmGame game = result.getGame();
                if (game != null) {
                    updateActionBarTitle(actionBar, game.getDimension(), game.getMines());
                }
            }
        }
        invalidateOptionsMenu();

        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onCreateNewGamePostExecute(result);
        }
    }

    @Override
    public void onExploreTilePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreTilePreExecute();
        }
    }

    @Override
    public void onExploreTileCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreTileCancelled();
        }
    }

    @Override
    public void onExploreTilePostExecute(DmGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onExploreTilePostExecute(result);
        }
    }

    @Override
    public void onFlagTilePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onFlagTilePreExecute();
        }
    }

    @Override
    public void onFlagTileCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onFlagTileCancelled();
        }
    }

    @Override
    public void onFlagTilePostExecute(DmGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onFlagTilePostExecute(result);
        }
    }

    @Override
    public void onValidateGamePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onValidateGamePreExecute();
        }
    }

    @Override
    public void onValidateGameCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onValidateGameCancelled();
        }
    }

    @Override
    public void onValidateGamePostExecute(DmGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onValidateGamePostExecute(result);
        }
    }

    @Override
    public void onToggleCheatPreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleCheatPreExecute();
        }
    }

    @Override
    public void onToggleCheatCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleCheatCancelled();
        }
    }

    @Override
    public void onToggleCheatPostExecute(DmGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleCheatPostExecute(result);
        }
    }

    @Override
    public void onToggleFlagModePreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleFlagModePreExecute();
        }
    }

    @Override
    public void onToggleFlagModeCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleFlagModeCancelled();
        }
    }

    @Override
    public void onToggleFlagModePostExecute(DmGame result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onToggleFlagModePostExecute(result);
        }
    }

    @Override
    public void onProvideHintPreExecute() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onProvideHintPreExecute();
        }
    }

    @Override
    public void onProvideHintCancelled() {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onProvideHintCancelled();
        }
    }

    @Override
    public void onProvideHintPostExecute(DmGameState result) {
        GameFragment gameFragment = getGameFragment();
        if (gameFragment != null) {
            gameFragment.onProvideHintPostExecute(result);
        }
    }

    @Override
    public void onGameFragmentAttached(GameFragment gameFragment) {
        setGameFragment(gameFragment);
    }

    //Méthode responsable si la jeux est demander de terminer elle affiche dialog selon resultat (gagnée ou perdre)
    @Override
    public void onGameEnded(DmGame game) {
        if (game.getHasEnded()) {
            int resIdTitle, resIdMessage;
            if (game.getHasWon()) {
                resIdTitle = R.string.game__end_of_game_dialog___title_won;
                resIdMessage = R.string.game__end_of_game_dialog___message_won;
            } else {
                resIdTitle = R.string.game__end_of_game_dialog___title_lost;
                resIdMessage = R.string.game__end_of_game_dialog___message_lost;
            }
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(resIdTitle))
                    .setMessage(getString(resIdMessage))
                    .setPositiveButton(getString(R.string.game__end_of_game_dialog___button_restart), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentManager fm = getFragmentManager();
                            GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
                            if (workerFragment != null) {
                                DmGame game = workerFragment.getGame();
                                if (game != null) {
                                    workerFragment.createNewGameAsync(game.getDimension(), game.getMines());
                                }
                            }
                        }

                    })
                    .setNegativeButton(getString(R.string.game__end_of_game_dialog___button_new_game), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GameActivity.this.finish();
                        }
                    })
                    .show();
        }
    }

}
