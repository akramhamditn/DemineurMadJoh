package com.tn.demineurmadjoh.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.rey.material.widget.FloatingActionButton;
import com.tn.demineurmadjoh.R;
import com.tn.demineurmadjoh.helper.DmDatabaseHelper;
import com.tn.demineurmadjoh.model.DmGame;
import com.tn.demineurmadjoh.model.DmGameState;
import com.tn.demineurmadjoh.model.DmTile;
import com.tn.demineurmadjoh.worker.GameWorkerFragment;

import java.util.List;

import static com.tn.demineurmadjoh.R.id.fab_image;

//Fragment de jeux
public class GameFragment extends Fragment {
    public static final String FRAGMENT_TAG = "fragment_tag.GameFragment";
    private ArrayAdapter<DmTile> mArrayAdapterForTiles;
    private List<DmTile> mTiles;
    private GridView mGridView;
    private FloatingActionButton mButtonFlag;
    private MenuItem mButtonRestart;
    private OnFragmentInteractionListener mListener;

    public GameFragment() {
        // Required empty public constructor
    }


    public static GameFragment newInstance() {
        return new GameFragment();
    }
    //Tableuax de drawable contient les icons de button flag
    private Drawable[] mDrawables = new Drawable[2];
    private int index = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentManager fm = getFragmentManager();
        final GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mButtonFlag = (FloatingActionButton) rootView.findViewById(fab_image);
        mButtonRestart = (MenuItem) rootView.findViewById(R.id.game__action_restart);
        mGridView.setNumColumns(getDimension());
        //Methode responsable à remplire et gére l'affichage dans le GridView de jeux
        setArrayAdapterForTiles(new ArrayAdapter<DmTile>(getActivity(), 0) {
            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }

            @Override
            public int getCount() {
                return getTiles() != null ? getTiles().size() : 0;
            }

            @Override
            public DmTile getItem(int position) {
                return getTiles() != null ? getTiles().get(position) : null;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(getResources().getLayout(R.layout.grid_item_tiles), null);
                }
                //Responsable sur Item de Grid clicker
                GridItemViewHolder holder = (GridItemViewHolder) convertView.getTag();

                if (holder == null) {
                    holder = new GridItemViewHolder(convertView);
                    convertView.setTag(holder);
                }

                DmTile item = getItem(position);
                if (item != null) {
                    holder.setTileId(item.getId());
                    //Si contient une bombe
                    if (item.getIsExplored()) {
                        holder.getImageButtonMask().setVisibility(View.GONE);
                        holder.getImageButtonFlagged().setVisibility(View.GONE);
                    } else {
                        //S'il est marker avec un flag
                        boolean isFlagged = item.getIsFlagged();
                        holder.getImageButtonFlagged().setVisibility(isFlagged ? View.VISIBLE : View.GONE);
                        holder.getImageButtonMask().setVisibility(isFlagged ? View.GONE : View.VISIBLE);

                        DmGame game = getGame();
                        boolean shouldReveal = game != null && (game.getEnableCheat() || (game.getHasEnded() && (item.getHasMine() || item.getIsFlagged())));
                        float alpha = shouldReveal ? 0.7f : 1;
                        holder.getImageButtonMask().setAlpha(alpha);
                        holder.getImageButtonFlagged().setAlpha(alpha);
                    }
                    int resId;
                    if (item.getHasMine()) {
                        //si el click sur une bomb
                        resId = R.drawable.ic_bomb;
                    } else {
                        //affecter selon les bombes autour
                        switch (item.getAdjacentMines()) {
                            case 0:
                                resId = R.drawable.ic_blank;
                                break;
                            case 1:
                                resId = R.drawable.ic_number_1;
                                break;
                            case 2:
                                resId = R.drawable.ic_number_2;
                                break;
                            case 3:
                                resId = R.drawable.ic_number_3;
                                break;
                            case 4:
                                resId = R.drawable.ic_number_4;
                                break;
                            case 5:
                                resId = R.drawable.ic_number_5;
                                break;
                            case 6:
                                resId = R.drawable.ic_number_6;
                                break;
                            case 7:
                                resId = R.drawable.ic_number_7;
                                break;
                            case 8:
                                resId = R.drawable.ic_number_8;
                                break;
                            default:
                                resId = R.drawable.ic_blank;
                                break;
                        }
                    }
                    holder.getImageUnderlying().setImageResource(resId);
                }

                return convertView;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return getTiles() == null || getTiles().isEmpty();
            }
        });

        //affecter l'adapter déja rempli
        mGridView.setAdapter(getArrayAdapterForTiles());
        //remplire le tableaux pour la button flag
        mDrawables[0] = rootView.getResources().getDrawable(R.drawable.ic_flag_white);
        mDrawables[1] = rootView.getResources().getDrawable(R.drawable.ic_check);
        mButtonFlag.setIcon(mDrawables[index], true);
        mButtonFlag.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 index = (index + 1) % 2;
                 mButtonFlag.setIcon(mDrawables[index], true);
                 //si le flag ne pas selectionnée arreté flag mode
                 if(index==0){
                     if (workerFragment != null) {
                         DmGame game = workerFragment.getGame();
                         if (game != null) {
                             workerFragment.toggleFlagModeAsync(false);
                         }
                     }
                 }
                 //sinon le flag selectionnée démarrez flag mode
                 else if(index==1){
                     if (workerFragment != null) {
                         DmGame game = workerFragment.getGame();
                         if (game != null) {
                             workerFragment.toggleFlagModeAsync(true);
                         }
                     }
                 }
             }
         });



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            mListener.onGameFragmentAttached(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GameFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateViews();
    }
    //Mise à jour le view
    public void updateViews(DmGameState gameState) {
        FragmentManager fm = getFragmentManager();
        GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

        if (workerFragment != null && !workerFragment.getIsCreatingNewGame()) {
            DmDatabaseHelper databaseHelper = DmDatabaseHelper.getInstance(getActivity());
            if (gameState == null) {
                gameState = new DmGameState(DmGame.loadGame(databaseHelper), DmTile.loadTiles(databaseHelper));
            }
            DmGame game = gameState.getGame();
            if (game != null) {
                setTiles(gameState.getTiles());
                mGridView.setNumColumns(getDimension());
                getArrayAdapterForTiles().notifyDataSetChanged();

                if (game.getHasEnded() && mListener != null) {
                    mListener.onGameEnded(game);
                }
            }
        }
    }

    public void updateViews() {
        updateViews(null);
    }
    //Methode responsable sur affecté l'affichage d'une carrelage selon etat
    public void invokeMove(Integer tileId) {
        if (tileId != null) {
            List<DmTile> tiles = getTiles();
            int index = tileId - 1;
            if (index < tiles.size()) {
                DmTile tile = tiles.get(index);
                if (tile != null) {
                    FragmentManager fm = getFragmentManager();
                    GameWorkerFragment workerFragment = (GameWorkerFragment) fm.findFragmentByTag(GameWorkerFragment.FRAGMENT_TAG);

                    if (workerFragment != null) {
                        DmGame game = workerFragment.getGame();
                        if (!game.getHasEnded()) {
                            if (game.getEnableFlagMode()) {
                                workerFragment.flagTileAsync(tile.getRowIndex(), tile.getColIndex(), !tile.getIsFlagged());
                            } else {
                                workerFragment.exploreTileAsync(tile.getRowIndex(), tile.getColIndex());
                            }
                        } else if (mListener != null) {
                            mListener.onGameEnded(game);
                        }
                    }
                }
            }
        }
    }

    public DmGame getGame() {
        DmDatabaseHelper databaseHelper = DmDatabaseHelper.getInstance(getActivity());
        return DmGame.loadGame(databaseHelper);
    }

    public int getDimension() {
        DmGame game = getGame();
        return game != null ? game.getDimension() : 0;
    }

    public ArrayAdapter<DmTile> getArrayAdapterForTiles() {
        return mArrayAdapterForTiles;
    }

    public void setArrayAdapterForTiles(ArrayAdapter<DmTile> arrayAdapterForTiles) {
        mArrayAdapterForTiles = arrayAdapterForTiles;
    }

    public List<DmTile> getTiles() {
        return mTiles;
    }

    public void setTiles(List<DmTile> tiles) {
        mTiles = tiles;
    }

    public void onCreateNewGamePreExecute() {
    }

    public void onCreateNewGameCancelled() {
    }

    public void onCreateNewGamePostExecute(DmGameState result) {
        updateViews(result);
    }

    public void onExploreTilePreExecute() {
    }

    public void onExploreTileCancelled() {
    }

    public void onExploreTilePostExecute(DmGameState result) {
        updateViews(result);
    }

    public void onValidateGamePreExecute() {
    }

    public void onValidateGameCancelled() {
    }

    public void onValidateGamePostExecute(DmGameState result) {
        updateViews(result);
    }

    public void onToggleCheatPreExecute() {
    }

    public void onToggleCheatCancelled() {
    }

    public void onToggleCheatPostExecute(DmGameState result) {
        updateViews(result);
    }

    public void onToggleFlagModePreExecute() {
    }

    public void onToggleFlagModeCancelled() {
    }

    public void onToggleFlagModePostExecute(DmGame result) {
    }

    public void onFlagTilePreExecute() {
    }

    public void onFlagTileCancelled() {
    }

    public void onProvideHintPreExecute() {
    }

    public void onProvideHintCancelled() {
    }

    public void onProvideHintPostExecute(DmGameState result) {
        updateViews(result);
    }

    public void onFlagTilePostExecute(DmGameState result) {
        updateViews(result);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onGameFragmentAttached(GameFragment gameFragment);

        public void onGameEnded(DmGame game);
    }

    private class GridItemViewHolder {
        private ImageView mImageViewUnderlying = null;
        private ImageButton mImageButtonMask = null;
        private ImageButton mImageButtonFlagged = null;
        private Integer mTileId = null;

        public GridItemViewHolder(View base) {
            setImageUnderlying((ImageView) base.findViewById(R.id.game__imageview_underlying));
            setImageButtonMask((ImageButton) base.findViewById(R.id.game__imagebutton_mask));
            setImageButtonFlagged((ImageButton) base.findViewById(R.id.game__imagebutton_flagged));
            getImageButtonMask().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeMove(getTileId());
                }
            });
            getImageButtonFlagged().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    invokeMove(getTileId());
                }
            });
        }

        public ImageView getImageUnderlying() {
            return mImageViewUnderlying;
        }

        public void setImageUnderlying(ImageView imageViewUnderlying) {
            mImageViewUnderlying = imageViewUnderlying;
        }

        public ImageButton getImageButtonMask() {
            return mImageButtonMask;
        }

        public void setImageButtonMask(ImageButton imageButtonMask) {
            mImageButtonMask = imageButtonMask;
        }

        public ImageButton getImageButtonFlagged() {
            return mImageButtonFlagged;
        }

        public void setImageButtonFlagged(ImageButton imageButtonFlagged) {
            mImageButtonFlagged = imageButtonFlagged;
        }

        public Integer getTileId() {
            return mTileId;
        }

        public void setTileId(Integer tileId) {
            mTileId = tileId;
        }
    }
}
